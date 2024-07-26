package com.nudge.syncmanager.workers

import android.annotation.SuppressLint
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.facebook.network.connectionclass.ConnectionClassManager
import com.facebook.network.connectionclass.DeviceBandwidthSampler
import com.nudge.core.BATCH_DEFAULT_LIMIT
import com.nudge.core.BLANK_STRING
import com.nudge.core.EventSyncStatus
import com.nudge.core.IMAGE_EVENT_STRING
import com.nudge.core.MULTIPART_FORM_DATA
import com.nudge.core.MULTIPART_IMAGE_PARAM_NAME
import com.nudge.core.RETRY_DEFAULT_COUNT
import com.nudge.core.SOMETHING_WENT_WRONG
import com.nudge.core.SYNC_DATE_TIME_FORMAT
import com.nudge.core.database.entities.Events
import com.nudge.core.database.entities.ImageStatusEntity
import com.nudge.core.datamodel.SyncImageUploadPayload
import com.nudge.core.enums.SyncException
import com.nudge.core.getBatchSize
import com.nudge.core.json
import com.nudge.core.model.request.EventConsumerRequest
import com.nudge.core.model.response.EventResult
import com.nudge.core.model.response.SyncEventResponse
import com.nudge.core.model.response.SyncImageStatusResponse
import com.nudge.core.utils.CoreLogger
import com.nudge.core.utils.SyncType
import com.nudge.syncmanager.SyncApiRepository
import com.nudge.syncmanager.utils.SUCCESS
import com.nudge.syncmanager.utils.WORKER_ARG_SYNC_TYPE
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

@HiltWorker
class SyncUploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val syncApiRepository: SyncApiRepository
) : CoroutineWorker(appContext, workerParams) {
    private val TAG = SyncUploadWorker::class.java.simpleName
    private var batchLimit = BATCH_DEFAULT_LIMIT
    private val retryCount = RETRY_DEFAULT_COUNT
    override suspend fun doWork(): Result {
        var mPendingEventList = listOf<Events>()


        val selectedSyncType = inputData.getInt(WORKER_ARG_SYNC_TYPE, SyncType.SYNC_ALL.ordinal)

        return try {
            val connectionQuality = ConnectionClassManager.getInstance().currentBandwidthQuality
            DeviceBandwidthSampler.getInstance().startSampling()

            if (runAttemptCount > 0) {
                batchLimit = getBatchSize(connectionQuality)
            }

            CoreLogger.d(
                applicationContext,
                TAG,
                "doWork Started: batchLimit: $batchLimit  runAttemptCount: $runAttemptCount"
            )

            var totalPendingEventCount =
                syncApiRepository.getPendingEventCount(syncType = selectedSyncType)
            CoreLogger.d(
                applicationContext,
                TAG,
                "doWork: totalPendingEventCount: $totalPendingEventCount"
            )

            while (totalPendingEventCount > 0) {
                mPendingEventList = syncApiRepository.getPendingEventFromDb(
                    batchLimit = batchLimit,
                    retryCount = retryCount,
                    syncType = selectedSyncType
                )

                if (mPendingEventList.isEmpty()) {
                    return Result.success(
                        workDataOf(
                            WorkerKeys.SUCCESS_MSG to "Success: All Producer Completed"
                        )
                    )
                }

                CoreLogger.d(
                    applicationContext,
                    TAG,
                    "doWork: pendingEvents List: ${mPendingEventList.json()}"
                )

                val apiResponse = syncApiRepository.syncProducerEventToServer(mPendingEventList)
                if (apiResponse.status == SUCCESS) {
                    apiResponse.data?.let { eventList ->
                        if (eventList.isNotEmpty()) {
                            processEventList(eventList)
                            totalPendingEventCount = syncApiRepository.getPendingEventCount(
                                syncType = selectedSyncType
                            )
                            CoreLogger.d(
                                applicationContext,
                                TAG,
                                "doWork: After totalPendingEventCount: $totalPendingEventCount"
                            )
                        } else handleEmptyEventListResponse(mPendingEventList)
                    } ?: handleNullApiResponse(mPendingEventList)
                } else handleFailedApiResponse(mPendingEventList)
            }

            fetchConsumerStatus(
                context = applicationContext,
                syncApiRepository = syncApiRepository,
                mobileNumber = syncApiRepository.getLoggedInMobileNumber()
            )
            CoreLogger.d(
                applicationContext,
                TAG,
                "doWork: success totalPendingEventCount: $totalPendingEventCount"
            )
            Result.success(
                workDataOf(
                    WorkerKeys.SUCCESS_MSG to "Success: All Producer Completed and Count 0"
                )
            )
        } catch (ex: Exception) {
            handleException(ex, mPendingEventList)
        } finally {
            DeviceBandwidthSampler.getInstance().stopSampling()
        }
    }

    private suspend fun processEventList(eventList: List<SyncEventResponse>) {
        val eventSuccessList =
            eventList.filter { it.status == EventSyncStatus.PRODUCER_SUCCESS.eventSyncStatus }
        val eventFailedList =
            eventList.filter { it.status == EventSyncStatus.PRODUCER_FAILED.eventSyncStatus }

        if (eventSuccessList.isNotEmpty()) {
            CoreLogger.d(
                applicationContext,
                TAG,
                "doWork: eventSuccessList List: ${eventSuccessList.json()}"
            )
            syncApiRepository.updateSuccessEventStatus(
                context = applicationContext,
                eventList = eventSuccessList
            )

            eventSuccessList.forEach { successEvent ->
                if (successEvent.eventName.contains(IMAGE_EVENT_STRING)) {
                    findImageEventAndImage(successEvent.clientId)
                }
            }
        }

        if (eventFailedList.isNotEmpty()) {
            CoreLogger.d(
                applicationContext,
                TAG,
                "doWork: eventFailedList List: ${eventFailedList.json()}"
            )
            syncApiRepository.updateFailedEventStatus(
                context = applicationContext,
                eventList = eventFailedList
            )
        }
    }

    private suspend fun handleEmptyEventListResponse(mPendingEventList: List<Events>) {
        CoreLogger.d(applicationContext, TAG, "doWork: Producer Response list Empty error")
        syncApiRepository.updateFailedEventStatus(
            context = applicationContext,
            eventList = createEventResponseList(
                mPendingEventList,
                SyncException.RESPONSE_DATA_LIST_IS_EMPTY_EXCEPTION.message
            )
        )
    }

    private suspend fun handleNullApiResponse(mPendingEventList: List<Events>) {
        CoreLogger.d(applicationContext, TAG, "doWork: Getting API response Null")
        syncApiRepository.updateFailedEventStatus(
            context = applicationContext,
            eventList = createEventResponseList(
                mPendingEventList,
                SyncException.RESPONSE_DATA_IS_NULL_EXCEPTION.message
            )
        )
    }

    private suspend fun handleFailedApiResponse(mPendingEventList: List<Events>) {
        CoreLogger.d(applicationContext, TAG, "doWork: Getting API Failed")
        syncApiRepository.updateFailedEventStatus(
            context = applicationContext,
            eventList = createEventResponseList(
                mPendingEventList,
                SyncException.RESPONSE_STATUS_FAILED_EXCEPTION.message
            )
        )
    }

    private suspend fun handleException(ex: Exception, mPendingEventList: List<Events>): Result {
        CoreLogger.e(
            applicationContext,
            TAG,
            "doWork: Exception: ${ex.message} :: ${mPendingEventList.json()}",
            ex,
            true
        )

        return if (runAttemptCount < RETRY_DEFAULT_COUNT) {
            if (mPendingEventList.isNotEmpty()) {
                mPendingEventList.forEach {
                    syncApiRepository.findEventAndUpdateRetryCount(it.id)
                }
            }
            Result.retry()
        } else {
            if (mPendingEventList.isNotEmpty()) {
                syncApiRepository.updateFailedEventStatus(
                    context = applicationContext,
                    eventList = createEventResponseList(
                        mPendingEventList,
                        "${SyncException.PRODUCER_RETRY_COUNT_EXCEEDED_EXCEPTION} :: ${ex.message ?: SOMETHING_WENT_WRONG}"
                    )
                )
            }
            Result.failure(
                workDataOf(
                    WorkerKeys.ERROR_MSG to "Failed: Producer Failed with Exception: ${ex.message}"
                )
            )
        }
    }


    private suspend fun syncImageToServerAPI(
        imageFile: File,
        imageStatusEvent: ImageStatusEntity
    ) {

        try {
            val imageRequest = imageFile
                .asRequestBody(MULTIPART_FORM_DATA.toMediaTypeOrNull())
            val multipartRequest = MultipartBody.Part.createFormData(
                MULTIPART_IMAGE_PARAM_NAME,
                imageStatusEvent.fileName,
                imageRequest
            )
            val imagePayloadRequest = listOf(
                SyncImageUploadPayload(
                createdBy = imageStatusEvent.createdBy,
                    fileEventClientId = imageStatusEvent.imageEventId ?: BLANK_STRING,
                eventTopic = imageStatusEvent.type,
                clientId = imageStatusEvent.id,
                fileName = imageStatusEvent.fileName,
                filePath = imageStatusEvent.filePath,
                eventName = imageStatusEvent.name,
                mobileNo = imageStatusEvent.mobileNumber
                )
            ).json()

            val multipartData =
                imagePayloadRequest.toRequestBody(MULTIPART_FORM_DATA.toMediaTypeOrNull())
            CoreLogger.d(
                context = applicationContext,
                TAG,
                "syncImageToServerAPI: SyncImageAPI Request: ${imagePayloadRequest.json()} :: $multipartData",
            )
            val response = syncApiRepository.syncImageToServer(
                image = multipartRequest,
                imagePayload = multipartData
            )
            if (response.status == SUCCESS) {
                response.data?.let { imageEventList ->
                    if (imageEventList.isNotEmpty()) {
                        handleSuccessImageStatus(imageEventList[0])
                    } else handleFailedImageStatus(
                        event = imageStatusEvent,
                        errorMessage = SyncException.RESPONSE_DATA_LIST_IS_EMPTY_EXCEPTION.message
                    )
                } ?: handleFailedImageStatus(
                    event = imageStatusEvent,
                    errorMessage = SyncException.RESPONSE_DATA_IS_NULL_EXCEPTION.message
                )
            } else handleFailedImageStatus(
                event = imageStatusEvent,
                errorMessage = SyncException.RESPONSE_STATUS_FAILED_EXCEPTION.message
            )
        } catch (ex: Exception) {
            CoreLogger.e(
                applicationContext,
                TAG,
                "syncImageToServerAPI: Exception: ${ex.message} :: ${imageStatusEvent.json()}",
                ex,
                true
            )
            handleFailedImageStatus(
                event = imageStatusEvent,
                errorMessage = ex.message ?: SOMETHING_WENT_WRONG
            )
        }
    }


    private suspend fun handleFailedImageStatus(event: ImageStatusEntity, errorMessage: String) {
        CoreLogger.d(
            applicationContext,
            TAG,
            "handleFailedImageStatus: ${event.json()} ::Message: $errorMessage"
        )
        syncApiRepository.updateImageEventStatus(
            eventId = event.id,
            errorMessage = errorMessage,
            status = EventSyncStatus.PRODUCER_FAILED.eventSyncStatus
        )
    }

    private suspend fun handleSuccessImageStatus(imageResponse: SyncImageStatusResponse) {
        CoreLogger.d(applicationContext, TAG, "handleSuccessImageStatus: ${imageResponse.json()} ")
        syncApiRepository.updateImageEventStatus(
            eventId = imageResponse.clientId,
            errorMessage = BLANK_STRING,
            status = imageResponse.status
        )

    }

    private suspend fun findImageEventAndImage(
        eventId: String
    ) {
        val imageStatusEvent =
            syncApiRepository.fetchImageStatusFromEventId(eventId = eventId)
        imageStatusEvent?.let {
            if (it.status != EventSyncStatus.PRODUCER_IN_PROGRESS.name
                && it.status != EventSyncStatus.PRODUCER_SUCCESS.name
                && it.status != EventSyncStatus.CONSUMER_IN_PROGRESS.name
                && it.status != EventSyncStatus.CONSUMER_SUCCESS.name
            ) {
                CoreLogger.d(applicationContext, TAG, "findImageEventAndImage: ${it.json()} ")
                try {
                    val imageFile = File(it.filePath)
                    if (imageFile.exists() && imageFile.isFile) {
                        syncImageToServerAPI(imageFile = imageFile, imageStatusEvent = it)
                    } else
                        handleFailedImageStatus(
                            event = imageStatusEvent,
                            errorMessage = SyncException.IMAGE_FILE_IS_NOT_EXIST_EXCEPTION.message
                        )

                } catch (e: Exception) {
                    e.printStackTrace()
                    handleFailedImageStatus(
                        event = imageStatusEvent,
                        errorMessage = e.message
                            ?: SyncException.EXCEPTION_WHILE_FINDING_IMAGE.message
                    )
                }
            }
        }
    }


}

fun createEventResponseList(
    eventList: List<Events>,
    errorMessage: String
): List<SyncEventResponse> {
    val failedEventList = arrayListOf<SyncEventResponse>()
    eventList.forEach {
        failedEventList.add(
            SyncEventResponse(
                clientId = it.id,
                status = EventSyncStatus.PRODUCER_FAILED.eventSyncStatus,
                type = it.id,
                mobileNumber = it.mobile_number,
                errorMessage = errorMessage,
                eventName = it.name,
                eventResult = EventResult(
                    eventId = BLANK_STRING,
                    status = EventSyncStatus.PRODUCER_FAILED.eventSyncStatus,
                    message = errorMessage
                ),
                requestId = BLANK_STRING
            )
        )
    }
    return failedEventList
}


@SuppressLint("SimpleDateFormat")
suspend fun fetchConsumerStatus(
    context: Context,
    syncApiRepository: SyncApiRepository,
    mobileNumber: String
) {
    val date = SimpleDateFormat(SYNC_DATE_TIME_FORMAT).format(Date())
    val eventConsumerRequest = EventConsumerRequest(
        requestId = BLANK_STRING,
        mobile = mobileNumber,
        endDate = date,
        startDate = date
    )
    val consumerAPIResponse = syncApiRepository.fetchConsumerEventStatus(eventConsumerRequest)
    if (consumerAPIResponse.status == SUCCESS) {
        consumerAPIResponse.data?.let {
            if (it.isNotEmpty()) {
                syncApiRepository.updateEventConsumerStatus(context = context, eventList = it)
            }
        }
    }
}


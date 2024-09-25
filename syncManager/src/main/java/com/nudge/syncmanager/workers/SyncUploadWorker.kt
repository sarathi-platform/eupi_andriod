package com.nudge.syncmanager.workers

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
import com.nudge.core.FORM_C_TOPIC
import com.nudge.core.FORM_D_TOPIC
import com.nudge.core.IMAGE_EVENT_STRING
import com.nudge.core.PRODUCER
import com.nudge.core.RETRY_DEFAULT_COUNT
import com.nudge.core.SOMETHING_WENT_WRONG
import com.nudge.core.SYNC_POST_SELECTION_DRIVE
import com.nudge.core.SYNC_SELECTION_DRIVE
import com.nudge.core.UPCM_USER
import com.nudge.core.database.entities.Events
import com.nudge.core.datamodel.Data
import com.nudge.core.datamodel.ImageEventDetailsModel
import com.nudge.core.datamodel.SyncImageMetadataRequest
import com.nudge.core.enums.EventName
import com.nudge.core.enums.SyncException
import com.nudge.core.getBatchSize
import com.nudge.core.getFileMimeType
import com.nudge.core.getImagePathFromPicture
import com.nudge.core.json
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.response.EventResult
import com.nudge.core.model.response.SyncEventResponse
import com.nudge.core.toDate
import com.nudge.core.utils.CoreLogger
import com.nudge.core.utils.SyncType
import com.nudge.syncmanager.domain.usecase.SyncManagerUseCase
import com.nudge.syncmanager.utils.SUCCESS
import com.nudge.syncmanager.utils.WORKER_ARG_SYNC_TYPE
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File

@HiltWorker
class SyncUploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val syncManagerUseCase: SyncManagerUseCase
) : CoroutineWorker(appContext, workerParams) {
    private val TAG = SyncUploadWorker::class.java.simpleName
    private var batchLimit = BATCH_DEFAULT_LIMIT
    private var retryCount = RETRY_DEFAULT_COUNT
    override suspend fun doWork(): Result {
        var mPendingEventList = listOf<Events>()

        val selectedSyncType = inputData.getInt(WORKER_ARG_SYNC_TYPE, SyncType.SYNC_ALL.ordinal)

        return try {
            val connectionQuality = ConnectionClassManager.getInstance().currentBandwidthQuality
            batchLimit = syncManagerUseCase.syncAPIUseCase.getSyncBatchSize()
            retryCount = syncManagerUseCase.syncAPIUseCase.getSyncRetryCount()
            CoreLogger.d(
                applicationContext,
                TAG,
                "doWork Started: batchLimit: $batchLimit  retryCount: $retryCount"
            )
            if (runAttemptCount > 0) {
                batchLimit = getBatchSize(connectionQuality)
            }

            CoreLogger.d(
                applicationContext,
                TAG,
                "doWork Started: batchLimit: $batchLimit  runAttemptCount: $runAttemptCount"
            )

            var totalPendingEventCount =
                syncManagerUseCase.fetchEventsFromDBUseCase.getPendingEventCount(syncType = selectedSyncType)
            CoreLogger.d(
                applicationContext,
                TAG,
                "doWork: totalPendingEventCount: $totalPendingEventCount"
            )

            while (totalPendingEventCount > 0) {
                mPendingEventList =
                    syncManagerUseCase.fetchEventsFromDBUseCase.getPendingEventFromDb(
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
                DeviceBandwidthSampler.getInstance().startSampling()
                val dataEventList =
                    mPendingEventList.filter { !it.name.contains(IMAGE_EVENT_STRING) && it.name != FORM_C_TOPIC && it.name != FORM_D_TOPIC }
                if ((selectedSyncType == SyncType.SYNC_ONLY_DATA.ordinal || selectedSyncType == SyncType.SYNC_ALL.ordinal) && dataEventList.isNotEmpty()) {
                    val apiResponse =
                        syncManagerUseCase.syncAPIUseCase.syncProducerEventToServer(dataEventList)
                    totalPendingEventCount =
                        handleAPIResponse(
                            apiResponse,
                            totalPendingEventCount,
                            selectedSyncType,
                            mPendingEventList
                        )
                }

                val imageEventIdsList =
                    mPendingEventList.filter { it.name.contains(IMAGE_EVENT_STRING) || it.name == FORM_C_TOPIC || it.name == FORM_D_TOPIC }
                        .map { it.id }
                CoreLogger.d(
                    applicationContext,
                    TAG,
                    "doWork: imageEventIdsList List: ${imageEventIdsList.json()}"
                )
                if ((selectedSyncType == SyncType.SYNC_ONLY_IMAGES.ordinal || selectedSyncType == SyncType.SYNC_ALL.ordinal) && imageEventIdsList.isNotEmpty()) {
                    val imageEventList =
                        syncManagerUseCase.fetchEventsFromDBUseCase.fetchAllImageEventDetails(
                            eventIds = imageEventIdsList
                        )
                    if (imageEventList.isNotEmpty()) {
                        findImageEventAndImage(imageEventList, batchLimit) { response ->
                            totalPendingEventCount =
                                handleAPIResponse(
                                    response,
                                    totalPendingEventCount,
                                    selectedSyncType,
                                    mPendingEventList
                                )
                        }
                    }
                }

                batchLimit = getBatchSize(connectionQuality)
                CoreLogger.d(
                    applicationContext,
                    TAG,
                    "doWork: Next batchLimit: $batchLimit"
                )

            }

            syncManagerUseCase.syncAPIUseCase.fetchConsumerEventStatus()
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

    private suspend fun SyncUploadWorker.handleAPIResponse(
        apiResponse: ApiResponseModel<List<SyncEventResponse>>,
        totalPendingEventCount: Int,
        selectedSyncType: Int,
        mPendingEventList: List<Events>
    ): Int {
        var totalPendingEventCount1 = totalPendingEventCount
        if (apiResponse.status == SUCCESS) {
            apiResponse.data?.let { eventList ->
                if (eventList.isNotEmpty()) {
                    processEventList(eventList)
                    totalPendingEventCount1 =
                        syncManagerUseCase.fetchEventsFromDBUseCase.getPendingEventCount(
                        syncType = selectedSyncType
                    )
                    CoreLogger.d(
                        applicationContext,
                        TAG,
                        "doWork: After totalPendingEventCount: $totalPendingEventCount1"
                    )
                } else handleEmptyEventListResponse(mPendingEventList)
            } ?: handleNullApiResponse(mPendingEventList)
        } else handleFailedApiResponse(mPendingEventList)
        DeviceBandwidthSampler.getInstance().stopSampling()
        return totalPendingEventCount1
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
            syncManagerUseCase.addUpdateEventUseCase.updateSuccessEventStatus(
                eventList = eventSuccessList
            )
        }

        if (eventFailedList.isNotEmpty()) {
            CoreLogger.d(
                applicationContext,
                TAG,
                "doWork: eventFailedList List: ${eventFailedList.json()}"
            )
            syncManagerUseCase.addUpdateEventUseCase.updateFailedEventStatus(
                eventList = eventFailedList
            )
        }

        if (eventList.isNotEmpty()) {
            syncManagerUseCase.fetchEventsFromDBUseCase.findRequestEvents(eventList, PRODUCER)
        }

    }

    private suspend fun handleEmptyEventListResponse(mPendingEventList: List<Events>) {
        CoreLogger.d(applicationContext, TAG, "doWork: Producer Response list Empty error")
        syncManagerUseCase.addUpdateEventUseCase.updateFailedEventStatus(
            eventList = createEventResponseList(
                mPendingEventList,
                SyncException.RESPONSE_DATA_LIST_IS_EMPTY_EXCEPTION.message
            )
        )
    }

    private suspend fun handleNullApiResponse(mPendingEventList: List<Events>) {
        CoreLogger.d(applicationContext, TAG, "doWork: Getting API response Null")
        syncManagerUseCase.addUpdateEventUseCase.updateFailedEventStatus(
            eventList = createEventResponseList(
                mPendingEventList,
                SyncException.RESPONSE_DATA_IS_NULL_EXCEPTION.message
            )
        )
    }

    private suspend fun handleFailedApiResponse(mPendingEventList: List<Events>) {
        CoreLogger.d(applicationContext, TAG, "doWork: Getting API Failed")
        syncManagerUseCase.addUpdateEventUseCase.updateFailedEventStatus(
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
                    syncManagerUseCase.addUpdateEventUseCase.findEventAndUpdateRetryCount(it.id)
                }
            }
            Result.retry()
        } else {
            if (mPendingEventList.isNotEmpty()) {
                syncManagerUseCase.addUpdateEventUseCase.updateFailedEventStatus(
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

    private suspend fun handleFailedImageStatus(
        imageEventDetail: ImageEventDetailsModel,
        errorMessage: String
    ) {
        CoreLogger.d(
            applicationContext,
            TAG,
            "handleFailedImageStatus: ${imageEventDetail.json()} ::Message: $errorMessage"
        )
        syncManagerUseCase.addUpdateEventUseCase.updateImageDetailsEventStatus(
            eventId = imageEventDetail.id,
            errorMessage = errorMessage,
            status = EventSyncStatus.IMAGE_NOT_EXIST.eventSyncStatus,
            requestId = imageEventDetail.requestId ?: BLANK_STRING
        )
    }

    private suspend fun findImageEventAndImage(
        imageEventList: List<ImageEventDetailsModel>,
        batchLimit: Int,
        onAPIResponse: suspend (ApiResponseModel<List<SyncEventResponse>>) -> Unit
    ) {
        try {
            if (imageEventList.isNotEmpty()) {
                CoreLogger.d(
                    applicationContext,
                    TAG,
                    "findImageEventAndImageList: ${imageEventList.json()} "
                )
                imageEventList.forEach { imageDetail ->
                    val picturePath = getImagePathFromPicture() + "/${imageDetail.fileName}"
                    var uploadedBlobUrl = BLANK_STRING
                    try {
                        syncManagerUseCase.syncBlobUploadUseCase.uploadImageOnBlob(
                            filePath = picturePath,
                            fileName = imageDetail.fileName ?: BLANK_STRING
                        ) { message, isExceptionOccur ->
                            if (!isExceptionOccur) {
                                uploadedBlobUrl = message
                            }
                            syncManagerUseCase.syncBlobUploadUseCase.updateImageBlobStatus(
                                imageStatusId = imageDetail.imageStatusId ?: BLANK_STRING,
                                isBlobUploaded = isExceptionOccur,
                                blobUrl = if (isExceptionOccur) BLANK_STRING else message,
                                errorMessage = if (isExceptionOccur) message else BLANK_STRING,
                                eventId = imageDetail.eventId ?: BLANK_STRING,
                                requestId = BLANK_STRING,
                                status = if (isExceptionOccur) EventSyncStatus.BLOB_UPLOAD_FAILED.eventSyncStatus
                                else EventSyncStatus.OPEN.eventSyncStatus
                            )
                        }
                    } catch (e: Exception) {
                        handleFailedImageStatus(
                            imageEventDetail = imageDetail,
                            errorMessage = e.message
                                ?: SyncException.EXCEPTION_WHILE_FINDING_IMAGE.message
                        )
                    }
                    val file = File(picturePath)
                    if (uploadedBlobUrl.isNotEmpty()) {
                        val imageEvent = Events(
                            id = imageDetail.id,
                            name = imageDetail.name,
                            type = EventName.BLOB_UPLOAD_TOPIC.topicName,
                            createdBy = imageDetail.createdBy,
                            modified_date = System.currentTimeMillis().toDate(),
                            request_payload = imageDetail.request_payload,
                            status = imageDetail.status,
                            metadata = SyncImageMetadataRequest(
                                data = Data(
                                    filePath = picturePath,
                                    contentType = getFileMimeType(file),
                                    isOnlyData = true,
                                    blobUrl = uploadedBlobUrl,
                                    driveType = if (syncManagerUseCase.getUserDetailsSyncUseCase.getLoggedInUserType() == UPCM_USER)
                                        SYNC_POST_SELECTION_DRIVE else SYNC_SELECTION_DRIVE,
                                ),
                                dependsOn = emptyList()
                            ).json(),
                            mobile_number = imageDetail.mobile_number,
                            payloadLocalId = imageDetail.payloadLocalId
                        )

                        val apiResponse =
                            syncManagerUseCase.syncAPIUseCase.syncProducerEventToServer(
                                events = listOf(imageEvent),
                            )
                        onAPIResponse(apiResponse)
                    } else {
                        handleFailedImageStatus(
                            imageEventDetail = imageDetail,
                            errorMessage = SyncException.BLOB_URL_NOT_FOUND_EXCEPTION.message
                        )
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
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


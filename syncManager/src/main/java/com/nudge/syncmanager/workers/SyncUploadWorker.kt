package com.nudge.syncmanager.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.facebook.network.connectionclass.ConnectionClassManager
import com.facebook.network.connectionclass.ConnectionQuality
import com.facebook.network.connectionclass.DeviceBandwidthSampler
import com.nudge.core.BATCH_DEFAULT_LIMIT
import com.nudge.core.BLANK_STRING
import com.nudge.core.BLOB_URL
import com.nudge.core.CONTENT_TYPE
import com.nudge.core.DRIVE_TYPE
import com.nudge.core.EMPTY_EVENT_LIST_FAILURE
import com.nudge.core.EventSyncStatus
import com.nudge.core.FAILED_RESPONSE_FAILURE
import com.nudge.core.FILE_NAME
import com.nudge.core.FILE_PATH
import com.nudge.core.FORM_C_TOPIC
import com.nudge.core.FORM_D_TOPIC
import com.nudge.core.IMAGE_EVENT_STRING
import com.nudge.core.IS_ONLY_DATA
import com.nudge.core.MULTIPART_FORM_DATA
import com.nudge.core.NULL_RESPONSE_FAILURE
import com.nudge.core.PRODUCER
import com.nudge.core.RETRY_DEFAULT_COUNT
import com.nudge.core.SOMETHING_WENT_WRONG
import com.nudge.core.SYNC_POST_SELECTION_DRIVE
import com.nudge.core.SYNC_SELECTION_DRIVE
import com.nudge.core.UPCM_USER
import com.nudge.core.analytics.mixpanel.CommonEventParams
import com.nudge.core.convertFileIntoMultipart
import com.nudge.core.database.entities.Events
import com.nudge.core.datamodel.ImageEventDetailsModel
import com.nudge.core.datamodel.SyncImageMetadataRequest
import com.nudge.core.datamodel.SyncImageUploadPayload
import com.nudge.core.enums.AppConfigKeysEnum
import com.nudge.core.enums.EventName
import com.nudge.core.enums.SyncException
import com.nudge.core.getBatchSize
import com.nudge.core.getFileMimeType
import com.nudge.core.getImagePathFromPicture
import com.nudge.core.getSizeInLong
import com.nudge.core.json
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.getMetaDataDtoFromString
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


@HiltWorker
class SyncUploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val syncManagerUseCase: SyncManagerUseCase,
) : CoroutineWorker(appContext, workerParams) {
    private val TAG = SyncUploadWorker::class.java.simpleName
    private var batchLimit = BATCH_DEFAULT_LIMIT
    private var retryCount = RETRY_DEFAULT_COUNT
    private var connectionQuality = ConnectionQuality.UNKNOWN
    override suspend fun doWork(): Result {
        var mPendingEventList = listOf<Events>()
        val selectedSyncType = inputData.getInt(WORKER_ARG_SYNC_TYPE, SyncType.SYNC_ALL.ordinal)
        return try {
            connectionQuality = ConnectionClassManager.getInstance().currentBandwidthQuality
            batchLimit =
                syncManagerUseCase.fetchAppConfigFromCacheOrDbUsecase.invoke(AppConfigKeysEnum.SYNC_BATCH_SIZE.name)
                    .toInt()
            retryCount =
                syncManagerUseCase.fetchAppConfigFromCacheOrDbUsecase.invoke(AppConfigKeysEnum.SYNC_RETRY_COUNT.name)
                    .toInt()
            val isBlobImageUploadEnable =
                syncManagerUseCase.fetchAppConfigFromCacheOrDbUsecase.invoke(AppConfigKeysEnum.BLOB_IMAGE_UPLOAD_ENABLED.name)
                    .toBoolean()
            var preSelectionContainerName = BLANK_STRING
            var postSelectionContainerName = BLANK_STRING
            var azureConnectionString = BLANK_STRING
            if (isBlobImageUploadEnable) {
                azureConnectionString =
                    syncManagerUseCase.fetchAppConfigFromCacheOrDbUsecase.invoke(AppConfigKeysEnum.ENCODED_KEY.name)
                preSelectionContainerName =
                    syncManagerUseCase.fetchAppConfigFromCacheOrDbUsecase.invoke(AppConfigKeysEnum.SELECTION_CONTAINER_NAME.name)
                postSelectionContainerName =
                    syncManagerUseCase.fetchAppConfigFromCacheOrDbUsecase.invoke(AppConfigKeysEnum.POST_SELECTION_CONTAINER_NAME.name)

            }
            CoreLogger.d(
                applicationContext,
                TAG,
                "doWork Started: batchLimit: $batchLimit  retryCount: $retryCount"
            )
            if (runAttemptCount > 0) {
                batchLimit = getBatchSize(connectionQuality).batchSize
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
            syncManagerUseCase.syncAnalyticsEventUseCase.sendSyncStartedAnalyticEvent(
                selectedSyncType,
                CommonEventParams(batchLimit, retryCount, connectionQuality.name),
                totalPendingEventCount
            )
            DeviceBandwidthSampler.getInstance().startSampling()
// Reset retry count to 0 if producer failed
            syncManagerUseCase.addUpdateEventUseCase.resetFailedEventStatusForProducerFailed()

            while (totalPendingEventCount > 0) {
                mPendingEventList =
                    syncManagerUseCase.fetchEventsFromDBUseCase.getPendingEventFromDb(
                        batchLimit = batchLimit,
                        retryCount = retryCount,
                        syncType = selectedSyncType
                )

                if (mPendingEventList.isEmpty()) {
                    syncManagerUseCase.syncAnalyticsEventUseCase.sendSyncProducerSuccessEvent(
                        selectedSyncType
                    )
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
                val dataEventList =
                    mPendingEventList.filter { !it.name.contains(IMAGE_EVENT_STRING) && it.name != FORM_C_TOPIC && it.name != FORM_D_TOPIC }



                if ((selectedSyncType == SyncType.SYNC_ONLY_DATA.ordinal || selectedSyncType == SyncType.SYNC_ALL.ordinal) && dataEventList.isNotEmpty()) {
                    val eventListAfterPayloadCheck =
                        getEventListAccordingToPayloadSize(dataEventList, connectionQuality)
                    val apiResponse =
                        syncManagerUseCase.syncAPIUseCase.syncProducerEventToServer(
                            eventListAfterPayloadCheck
                        )
                    totalPendingEventCount =
                        handleAPIResponse(
                            apiResponse,
                            totalPendingEventCount,
                            selectedSyncType,
                            eventListAfterPayloadCheck
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
                        if (isBlobImageUploadEnable) {
                            findImageEventAndImage(
                                imageEventList,
                                postSelectionContainerName = postSelectionContainerName,
                                selectionContainerName = preSelectionContainerName,
                                blobConnectionUrl = azureConnectionString
                            ) { response ->
                                totalPendingEventCount =
                                    handleAPIResponse(
                                        response,
                                        totalPendingEventCount,
                                        selectedSyncType,
                                        mPendingEventList
                                    )
                            }
                        } else {
                            findImageEventAndImageForMultipart(imageEventList) { response ->
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
                }



                DeviceBandwidthSampler.getInstance().stopSampling()
                batchLimit =
                    getBatchSize(ConnectionClassManager.getInstance().currentBandwidthQuality).batchSize
                CoreLogger.d(
                    applicationContext,
                    TAG,
                    "doWork: Next batchLimit: $batchLimit"
                )
            }

            syncManagerUseCase.syncAPIUseCase.fetchConsumerEventStatus { success: Boolean, message: String, requestIds: Int, ex: Throwable? ->
//                syncManagerUseCase.syncAnalyticsEventUseCase.sendConsumerEvents(
//                    selectedSyncType,
//                    CommonEventParams(batchLimit, retryCount, connectionQuality.name),
//                    success,
//                    message,
//                    requestIds,
//                    ex
//                )
            }
            CoreLogger.d(
                applicationContext,
                TAG,
                "doWork: success totalPendingEventCount: $totalPendingEventCount"
            )

            syncManagerUseCase.syncAnalyticsEventUseCase.sendSyncSuccessEvent(selectedSyncType)

            Result.success(
                workDataOf(
                    WorkerKeys.SUCCESS_MSG to "Success: All Producer Completed and Count 0"
                )
            )
        } catch (ex: Exception) {
            handleException(ex, mPendingEventList, selectedSyncType)
        } finally {
            DeviceBandwidthSampler.getInstance().stopSampling()
        }
    }

    private fun getEventListAccordingToPayloadSize(
        dataEventList: List<Events>,
        connectionQuality: ConnectionQuality
    ): List<Events> {
        var eventPayloadSize = dataEventList.json().getSizeInLong() / 1000

        CoreLogger.d(
            applicationContext,
            TAG,
            "doWork: Event Payload size: ${dataEventList.json().getSizeInLong()}"
        )
        var eventListAccordingToPayload: List<Events> = dataEventList
        while (eventPayloadSize > getBatchSize(connectionQuality).maxPayloadSize && eventListAccordingToPayload.size > 1) {
            eventListAccordingToPayload =
                eventListAccordingToPayload.subList(0, (eventListAccordingToPayload.size / 2))
            eventPayloadSize = eventListAccordingToPayload.json().getSizeInLong() / 1000
            CoreLogger.d(
                applicationContext,
                TAG,
                "doWork: Event Payload size in loop: ${
                    eventListAccordingToPayload.json().getSizeInLong()
                }"
            )
        }
        return eventListAccordingToPayload
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
                } else handleAPIResponseFailure(
                    mPendingEventList,
                    EMPTY_EVENT_LIST_FAILURE,
                    selectedSyncType = selectedSyncType
                )
            } ?: handleAPIResponseFailure(
                mPendingEventList,
                NULL_RESPONSE_FAILURE,
                apiResponse.message,
                selectedSyncType = selectedSyncType
            )
        } else handleAPIResponseFailure(
            mPendingEventList,
            FAILED_RESPONSE_FAILURE,
            apiResponse.message,
            selectedSyncType
        )
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

    private suspend fun handleAPIResponseFailure(
        pendingEventList: List<Events>,
        failureType: String,
        failureMessage: String = BLANK_STRING,
        selectedSyncType: Int
    ) {
        syncManagerUseCase.syncAnalyticsEventUseCase.sendSyncApiFailureEvent(
            selectedSyncType,
            failureType,
            failureMessage,
            commonEventParams = CommonEventParams(batchLimit, retryCount, connectionQuality.name),
            pendingEventList
        )

        when (failureType) {
            EMPTY_EVENT_LIST_FAILURE -> handleEmptyEventListResponse(pendingEventList)
            NULL_RESPONSE_FAILURE -> handleNullApiResponse(pendingEventList)
            FAILED_RESPONSE_FAILURE -> handleFailedApiResponse(pendingEventList)
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

    private suspend fun handleException(
        ex: Exception,
        mPendingEventList: List<Events>,
        selectedSyncType: Int
    ): Result {

        syncManagerUseCase.syncAnalyticsEventUseCase.sendSyncFailureDueToExceptionAnalyticsEvent(
            ex, selectedSyncType, CommonEventParams(
                batchLimit, retryCount, connectionQuality.name
            ), mPendingEventList
        )

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


    private suspend fun syncImageToServerAPI(
        imageMultipartList: List<MultipartBody.Part>,
        imageStatusEventList: List<ImageEventDetailsModel>,
        onAPIResponse: suspend (ApiResponseModel<List<SyncEventResponse>>) -> Unit
    ) {

        try {
            val imagePayloadRequest: ArrayList<SyncImageUploadPayload> = arrayListOf()
            imageStatusEventList.forEach { imageEvent ->
                imageEvent.filePath?.let {
                    val file = File(it)

                    var metaDataMap = hashMapOf<String, Any>(
                        FILE_PATH to file.path,
                        FILE_NAME to (imageEvent.fileName ?: BLANK_STRING),
                        CONTENT_TYPE to getFileMimeType(file).toString(),
                        IS_ONLY_DATA to false,
                        BLOB_URL to BLANK_STRING,
                        DRIVE_TYPE to if (syncManagerUseCase.getUserDetailsSyncUseCase.getLoggedInUserType() == UPCM_USER)
                            SYNC_POST_SELECTION_DRIVE else SYNC_SELECTION_DRIVE
                    )
                    imageEvent.metadata?.getMetaDataDtoFromString()?.data?.let { it1 ->
                        metaDataMap.putAll(
                            it1
                        )
                    }
                    imagePayloadRequest.add(
                        SyncImageUploadPayload(
                            createdBy = imageEvent.createdBy,
                            fileEventClientId = imageEvent.imageStatusId ?: BLANK_STRING,
                            eventTopic = EventName.BLOB_UPLOAD_TOPIC.topicName,
                            clientId = imageEvent.id,
                            fileName = imageEvent.fileName ?: BLANK_STRING,
                            filePath = imageEvent.filePath ?: BLANK_STRING,
                            eventName = imageEvent.name,
                            mobileNo = imageEvent.mobile_number,
                            payload = imageEvent.request_payload ?: BLANK_STRING,
                            driveType = if (syncManagerUseCase.getUserDetailsSyncUseCase.getLoggedInUserType() == UPCM_USER)
                                SYNC_POST_SELECTION_DRIVE else SYNC_SELECTION_DRIVE,
                            metadata = SyncImageMetadataRequest(
                                data = metaDataMap,
                                dependsOn = emptyList()
                            ).json()
                        )
                    )
                }

            }


            val multipartData =
                imagePayloadRequest.json().toRequestBody(MULTIPART_FORM_DATA.toMediaTypeOrNull())
            CoreLogger.d(
                context = applicationContext,
                TAG,
                "syncImageToServerAPI: SyncImageAPI Request: ${imagePayloadRequest.json()}",
            )
            val response = syncManagerUseCase.syncAPIUseCase.syncImageWithEventToServer(
                imageList = imageMultipartList,
                imagePayload = multipartData
            )
            onAPIResponse(response)
        } catch (ex: Exception) {
            CoreLogger.e(
                applicationContext,
                TAG,
                "syncImageToServerAPI: Exception: ${ex.message} :: ${imageStatusEventList.json()}",
                ex,
                true
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
        postSelectionContainerName: String,
        selectionContainerName: String,
        blobConnectionUrl: String,
        onAPIResponse: suspend (ApiResponseModel<List<SyncEventResponse>>) -> Unit
    ) {

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
                    fileName = imageDetail.fileName ?: BLANK_STRING,
                    postSelectionContainerName = postSelectionContainerName,
                    selectionContainerName = selectionContainerName,
                    blobConnectionUrl = blobConnectionUrl
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
                var metaDataMap = hashMapOf<String, Any>(
                    FILE_PATH to file.path,
                    FILE_NAME to (imageDetail.fileName ?: BLANK_STRING),
                    CONTENT_TYPE to getFileMimeType(file).toString(),
                    IS_ONLY_DATA to true,
                    BLOB_URL to uploadedBlobUrl,
                    DRIVE_TYPE to if (syncManagerUseCase.getUserDetailsSyncUseCase.getLoggedInUserType() == UPCM_USER)
                        SYNC_POST_SELECTION_DRIVE else SYNC_SELECTION_DRIVE
                )
                imageDetail.metadata?.getMetaDataDtoFromString()?.data?.let { it1 ->
                    metaDataMap.putAll(
                        it1
                    )
                }
                val imageEvent = Events(
                    id = imageDetail.id,
                    name = imageDetail.name,
                    type = EventName.BLOB_UPLOAD_TOPIC.topicName,
                    createdBy = imageDetail.createdBy,
                    modified_date = System.currentTimeMillis().toDate(),
                    request_payload = imageDetail.request_payload,
                    status = imageDetail.status,
                    metadata = SyncImageMetadataRequest(
                        data = metaDataMap,
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


    private suspend fun findImageEventAndImageForMultipart(
        imageEventList: List<ImageEventDetailsModel>,
        onAPIResponse: suspend (ApiResponseModel<List<SyncEventResponse>>) -> Unit
    ) {
            if (imageEventList.isNotEmpty()) {
                CoreLogger.d(
                    applicationContext,
                    TAG,
                    "findImageEventAndImageList: ${imageEventList.json()} "
                )
                imageEventList.forEach { imageDetail ->
                    try {
                        val imageMultiPart = addImageToMultipart(imageDetail)
                        imageMultiPart?.let {
                            syncImageToServerAPI(
                                imageMultipartList = listOf(it),
                                imageStatusEventList = listOf(imageDetail)
                            ) { response ->
                                onAPIResponse(response)
                            }
                        } ?: handleFailedImageStatus(
                            imageEventDetail = imageDetail,
                            errorMessage = SyncException.IMAGE_MULTIPART_IS_NULL_EXCEPTION.message
                        )
                    } catch (e: Exception) {
                        handleFailedImageStatus(
                            imageEventDetail = imageDetail,
                            errorMessage = e.message
                                ?: SyncException.EXCEPTION_WHILE_FINDING_IMAGE.message
                        )
                    }
                }

            }
    }

    private suspend fun SyncUploadWorker.addImageToMultipart(
        imageDetail: ImageEventDetailsModel
    ): MultipartBody.Part? {
        imageDetail.fileName?.let { imageName ->
            if (imageName.isNotEmpty()) {
                val picturePath = getImagePathFromPicture()
                CoreLogger.d(
                    context = applicationContext,
                    "addImageToMultipart",
                    "ImagePath: $picturePath/$imageName"
                )
                val file =
                    File("$picturePath/$imageName")

                if (file.exists() && file.isFile) {
                    val imageMultiPart = convertFileIntoMultipart(
                        imageFile = file,
                        imageEventDetail = imageDetail
                    )
                    return imageMultiPart
                } else {
                    handleFailedImageStatus(
                        imageEventDetail = imageDetail,
                        errorMessage = SyncException.IMAGE_FILE_IS_NOT_EXIST_EXCEPTION.message
                    )
                }
            } else {
                handleFailedImageStatus(
                    imageEventDetail = imageDetail,
                    errorMessage = SyncException.IMAGE_NAME_IS_EMPTY_OR_NULL_EXCEPTION.message
                )
            }
        } ?: handleFailedImageStatus(
            imageEventDetail = imageDetail,
            errorMessage = SyncException.IMAGE_NAME_IS_EMPTY_OR_NULL_EXCEPTION.message
        )
        return null
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
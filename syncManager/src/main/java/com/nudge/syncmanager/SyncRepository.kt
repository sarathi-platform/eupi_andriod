package com.nudge.syncmanager


import android.content.Context
import com.nudge.core.BLANK_STRING
import com.nudge.core.CONSUMER
import com.nudge.core.EventSyncStatus
import com.nudge.core.LAST_SYNC_TIME
import com.nudge.core.SOMETHING_WENT_WRONG
import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.dao.ImageStatusDao
import com.nudge.core.database.dao.RequestStatusDao
import com.nudge.core.database.entities.EventStatusEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.database.entities.RequestStatusEntity
import com.nudge.core.datamodel.ImageEventDetailsModel
import com.nudge.core.datamodel.RequestIdCountModel
import com.nudge.core.json
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.model.request.EventConsumerRequest
import com.nudge.core.model.request.EventRequest
import com.nudge.core.model.request.toEventRequest
import com.nudge.core.model.response.SyncEventResponse
import com.nudge.core.model.response.SyncImageStatusResponse
import com.nudge.core.preference.CorePrefRepo
import com.nudge.core.toDate
import com.nudge.core.utils.CoreLogger
import com.nudge.syncmanager.network.SyncApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class SyncApiRepository @Inject constructor(
    val apiService: SyncApiService,
    private val eventDao: EventsDao,
    private val eventStatusDao: EventStatusDao,
    private val prefRepo: CorePrefRepo,
    private val imageStatusDao: ImageStatusDao,
    private val requestStatusDao: RequestStatusDao
) {
    suspend fun syncProducerEventToServer(events: List<Events>): ApiResponseModel<List<SyncEventResponse>> {
        val eventRequest: List<EventRequest> = events.map {
            it.toEventRequest()
        }
        return apiService.syncEvent(eventRequest)
    }

    suspend fun fetchAllImageEventDetails(eventIds: List<String>): List<ImageEventDetailsModel> {
        return eventDao.fetchAllImageEventsWithImageDetails(
            mobileNumber = prefRepo.getMobileNo(),
            eventIds = eventIds
        )
    }

    fun loggedInUserType() = prefRepo.getUserType()


    suspend fun fetchConsumerEventStatus(eventConsumerRequest: EventConsumerRequest)
            : ApiResponseModel<List<SyncEventResponse>> {
        return apiService.syncConsumerStatusApi(eventConsumerRequest)
    }

    suspend fun getPendingEventFromDb(
        batchLimit: Int,
        retryCount: Int,
        syncType: Int
    ): List<Events> {
        return eventDao.getAllPendingEventList(
            listOf(
                EventSyncStatus.OPEN.eventSyncStatus,
                EventSyncStatus.PRODUCER_IN_PROGRESS.eventSyncStatus,
                EventSyncStatus.PRODUCER_FAILED.eventSyncStatus
            ),
            batchLimit = batchLimit,
            retryCount = retryCount,
            mobileNumber = prefRepo.getMobileNo(),
            syncType = syncType
        )
    }

    suspend fun syncImageToServer(
        image: MultipartBody.Part,
        imagePayload: RequestBody
    ): ApiResponseModel<List<SyncImageStatusResponse>> {
        return apiService.syncImage(imageFile = image, imagePayload = imagePayload)
    }


    suspend fun syncImageWithEventToServer(
        imageList: List<MultipartBody.Part>,
        imagePayload: RequestBody
    ): ApiResponseModel<List<SyncEventResponse>> {
        return apiService.syncImageWithEvent(imageFileList = imageList, imagePayload = imagePayload)
    }

    suspend fun getPendingEventCount(syncType: Int): Int {
        return eventDao.getSyncPendingEventCount(
            listOf(
                EventSyncStatus.OPEN.eventSyncStatus,
                EventSyncStatus.PRODUCER_IN_PROGRESS.eventSyncStatus,
                EventSyncStatus.PRODUCER_FAILED.eventSyncStatus
            ),
            mobileNumber = prefRepo.getMobileNo(),
            syncType = syncType
        )
    }

    suspend fun updateSuccessEventStatus(context: Context, eventList: List<SyncEventResponse>) {
        try {
            prefRepo.savePref(LAST_SYNC_TIME, System.currentTimeMillis())
            eventDao.updateSuccessEventStatus(eventList)
            eventList.forEach {
                eventStatusDao.insert(
                    EventStatusEntity(
                        clientId = it.clientId,
                        errorMessage = BLANK_STRING,
                        status = it.status,
                        mobileNumber = it.mobileNumber,
                        createdBy = prefRepo.getUserId(),
                        eventStatusId = 0,
                        requestId = it.requestId
                    )
                )

            }
        } catch (ex: Exception) {
            CoreLogger.d(
                context = context,
                "SyncApiRepository",
                "updateSuccessEventStatus: Exception: ${ex.message}"
            )
        }

    }

    suspend fun updateFailedEventStatus(context: Context, eventList: List<SyncEventResponse>) {
        try {
            CoreLogger.d(
                context,
                "updateFailedEventStatus",
                "Failed Event Details1: ${eventList.json()}"
            )
            eventDao.updateFailedEventStatus(eventList)
            eventList.forEach {
                CoreLogger.d(
                    context,
                    "updateFailedEventStatus",
                    "Failed Event Details: ${it.json()}"
                )
                eventStatusDao.insert(
                    EventStatusEntity(
                        clientId = it.clientId,
                        errorMessage = it.errorMessage.ifEmpty { SOMETHING_WENT_WRONG },
                        status = EventSyncStatus.PRODUCER_FAILED.eventSyncStatus,
                        mobileNumber = prefRepo.getMobileNo(),
                        createdBy = prefRepo.getUserId(),
                        eventStatusId = 0,
                        requestId = it.requestId
                    )
                )
            }
        } catch (e: Exception) {
            CoreLogger.d(
                context = context,
                "SyncApiRepository",
                "updateFailedEventStatus: Exception: ${e.message}"
            )
        }
    }

    suspend fun updateEventConsumerStatus(context: Context, eventList: List<SyncEventResponse>) {
        try {
            CoreLogger.d(
                context = context,
                "SyncApiRepository",
                "updateEventConsumerStatus: ${eventList.json()}"
            )
            prefRepo.savePref(LAST_SYNC_TIME, System.currentTimeMillis())
            eventDao.updateConsumerStatus(eventList)
            eventList.forEach {
                eventStatusDao.insert(
                    EventStatusEntity(
                        clientId = it.clientId,
                        errorMessage = it.errorMessage,
                        status = it.status,
                        mobileNumber = prefRepo.getMobileNo(),
                        createdBy = prefRepo.getUserId(),
                        eventStatusId = 0
                    )
                )
            }
            imageStatusDao.updateImageConsumerStatus(
                eventList = eventList,
                mobileNumber = prefRepo.getMobileNo()
            )
            findRequestEvents(eventList, CONSUMER)

        } catch (e: Exception) {
            CoreLogger.d(
                context = context,
                "SyncApiRepository",
                "updateFailedEventStatus: Exception: ${e.message}"
            )
        }
    }

    fun getLoggedInMobileNumber(): String = prefRepo.getMobileNo()


    suspend fun updateImageDetailsEventStatus(
        eventId: String,
        status: String,
        requestId: String,
        errorMessage: String? = BLANK_STRING
    ) {
        imageStatusDao.updateImageEventStatus(
            status = status,
            eventId = eventId,
            errorMessage = errorMessage ?: SOMETHING_WENT_WRONG,
            modifiedDate = System.currentTimeMillis().toDate(),
            mobileNumber = getLoggedInMobileNumber()
        )

        eventDao.updateEventStatus(
            retryCount = 1,
            clientId = eventId,
            errorMessage = errorMessage ?: SOMETHING_WENT_WRONG,
            modifiedDate = System.currentTimeMillis().toDate(),
            newStatus = status,
            requestId = requestId
        )

        eventStatusDao.insert(
            EventStatusEntity(
                clientId = eventId,
                errorMessage = errorMessage ?: SOMETHING_WENT_WRONG,
                status = status,
                mobileNumber = prefRepo.getMobileNo(),
                createdBy = prefRepo.getUserId(),
                eventStatusId = 0
            )
        )
    }
    suspend fun findEventAndUpdateRetryCount(eventId: String) {
        eventDao.findEventAndUpdateRetryCount(eventId = eventId)
    }

    suspend fun findEventCountForRequestId(requestId: String): Int {
        return eventDao.fetchEventCountDetailForRequestId(requestId, prefRepo.getMobileNo())
    }

    suspend fun addOrUpdateRequestStatus(requestId: String, eventCount: Int, status: String) {
        requestStatusDao.addOrUpdateRequestId(
            requestStatusEntity = RequestStatusEntity(
                requestId = requestId,
                eventCount = eventCount,
                mobileNumber = prefRepo.getMobileNo(),
                status = status,
                eventStatusId = 0,
                createdBy = prefRepo.getUserId(),
                modifiedDate = System.currentTimeMillis().toDate()
            )
        )
    }

    suspend fun fetchEventStatusCount(requestId: String): List<RequestIdCountModel> {
        return eventDao.fetchEventStatusCount(requestId, prefRepo.getMobileNo())
    }

    fun fetchAllRequestEventForConsumerStatus(): List<RequestStatusEntity> {
        return requestStatusDao.getAllRequestEventForConsumerStatus(
            prefRepo.getMobileNo(),
            listOf(
                EventSyncStatus.PRODUCER_SUCCESS.eventSyncStatus,
                EventSyncStatus.CONSUMER_FAILED.eventSyncStatus
            )
        )
    }

    suspend fun findRequestEvents(eventList: List<SyncEventResponse>, tag: String) {
        val requestIdList = eventList.distinctBy { it.requestId }.map { it.requestId }
        if (requestIdList.isNotEmpty()) {
            requestIdList.forEach { requestId ->
                val reqEventList =
                    requestId?.let { it1 ->
                        fetchEventStatusCount(it1)
                    }
                val eventCount =
                    requestId?.let { it1 ->
                        findEventCountForRequestId(it1)
                    }

                reqEventList?.let { statusCountList ->
                    var status = BLANK_STRING
                    if (statusCountList.any { it.status == EventSyncStatus.CONSUMER_SUCCESS.eventSyncStatus }) {
                        status = EventSyncStatus.CONSUMER_SUCCESS.eventSyncStatus
                    }

                    if (statusCountList.any { it.status == EventSyncStatus.CONSUMER_FAILED.eventSyncStatus }) {
                        status = EventSyncStatus.CONSUMER_FAILED.eventSyncStatus
                    }

                    if (statusCountList.any { it.status == EventSyncStatus.PRODUCER_SUCCESS.eventSyncStatus }) {
                        status = EventSyncStatus.PRODUCER_SUCCESS.eventSyncStatus
                    }
                    CoreLogger.d(
                        CoreAppDetails.getApplicationContext().applicationContext,
                        tag,
                        "findRequestEvents: $requestId :: $status :: $eventCount"
                    )

                    addOrUpdateRequestStatus(
                        requestId = requestId,
                        eventCount = eventCount ?: 0,
                        status = status
                    )
                }

            }

        }


    }
}





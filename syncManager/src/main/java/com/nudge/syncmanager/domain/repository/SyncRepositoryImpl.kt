package com.nudge.syncmanager.domain.repository

import com.nudge.core.BLANK_STRING
import com.nudge.core.CONSUMER
import com.nudge.core.CRP_USER_TYPE
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
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.model.response.SyncEventResponse
import com.nudge.core.preference.CorePrefRepo
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_KEY_EMAIL
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_KEY_NAME
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_KEY_TYPE_NAME
import com.nudge.core.toDate
import com.nudge.core.utils.CoreLogger
import com.nudge.syncmanager.imageupload.BlobImageUploader
import com.nudge.syncmanager.network.SyncApiService
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    val apiService: SyncApiService,
    val eventDao: EventsDao,
    val eventStatusDao: EventStatusDao,
    val corePrefRepo: CorePrefRepo,
    val imageStatusDao: ImageStatusDao,
    val requestStatusDao: RequestStatusDao,
    val imageUploader: BlobImageUploader
) : SyncRepository {
    val pendingEventStatusList = listOf(
        EventSyncStatus.OPEN.eventSyncStatus,
        EventSyncStatus.PRODUCER_IN_PROGRESS.eventSyncStatus,
        EventSyncStatus.PRODUCER_FAILED.eventSyncStatus
    )
    override fun getUserMobileNumber(): String {
        return corePrefRepo.getMobileNo()
    }

    override fun getUserID(): String {
        return corePrefRepo.getUserId()
    }

    override fun getUserEmail(): String {
        return corePrefRepo.getPref(PREF_KEY_EMAIL, BLANK_STRING) ?: BLANK_STRING
    }

    override fun getUserName(): String {
        return corePrefRepo.getPref(PREF_KEY_NAME, BLANK_STRING) ?: BLANK_STRING
    }

    override fun getLoggedInUserType(): String {
        return corePrefRepo.getPref(PREF_KEY_TYPE_NAME, CRP_USER_TYPE) ?: CRP_USER_TYPE
    }

    override suspend fun fetchAllImageEventDetails(eventIds: List<String>): List<ImageEventDetailsModel> {
        return eventDao.fetchAllImageEventsWithImageDetails(
            mobileNumber = corePrefRepo.getMobileNo(),
            eventIds = eventIds
        )
    }

    override suspend fun getPendingEventFromDb(
        batchLimit: Int,
        retryCount: Int,
        syncType: Int
    ): List<Events> {
        return eventDao.getAllPendingEventList(
            pendingEventStatusList,
            batchLimit = batchLimit,
            retryCount = retryCount,
            mobileNumber = corePrefRepo.getMobileNo(),
            syncType = syncType
        )
    }

    override suspend fun getPendingEventCount(syncType: Int): Int {
        return eventDao.getSyncPendingEventCount(
            pendingEventStatusList,
            mobileNumber = corePrefRepo.getMobileNo(),
            syncType = syncType
        )
    }

    override suspend fun updateSuccessEventStatus(
        eventList: List<SyncEventResponse>
    ) {
        try {
            corePrefRepo.savePref(LAST_SYNC_TIME, System.currentTimeMillis())
            eventDao.updateSuccessEventStatus(eventList)
            eventList.forEach {
                eventStatusDao.insert(
                    EventStatusEntity(
                        clientId = it.clientId,
                        errorMessage = BLANK_STRING,
                        status = it.status ?: BLANK_STRING,
                        mobileNumber = it.mobileNumber,
                        createdBy = corePrefRepo.getUserId(),
                        eventStatusId = 0,
                        requestId = it.requestId
                    )
                )

            }
        } catch (ex: Exception) {
            CoreLogger.d(
                context = CoreAppDetails.getApplicationContext().applicationContext,
                "SyncRepositoryImpl",
                "updateSuccessEventStatus: Exception: ${ex.message}"
            )
        }
    }

    override suspend fun updateFailedEventStatus(
        eventList: List<SyncEventResponse>
    ) {
        try {
            CoreLogger.d(
                CoreAppDetails.getApplicationContext().applicationContext,
                "updateFailedEventStatus",
                "Failed Event Details1: ${eventList.json()}"
            )
            eventDao.updateFailedEventStatus(eventList)
            eventList.forEach {
                CoreLogger.d(
                    CoreAppDetails.getApplicationContext().applicationContext,
                    "updateFailedEventStatus",
                    "Failed Event Details: ${it.json()}"
                )
                eventStatusDao.insert(
                    EventStatusEntity(
                        clientId = it.clientId,
                        errorMessage = it.errorMessage.ifEmpty { SOMETHING_WENT_WRONG },
                        status = EventSyncStatus.PRODUCER_FAILED.eventSyncStatus,
                        mobileNumber = corePrefRepo.getMobileNo(),
                        createdBy = corePrefRepo.getUserId(),
                        eventStatusId = 0,
                        requestId = it.requestId
                    )
                )
            }
        } catch (e: Exception) {
            CoreLogger.d(
                context = CoreAppDetails.getApplicationContext().applicationContext,
                "SyncRepositoryImpl",
                "updateFailedEventStatus: Exception: ${e.message}"
            )
        }
    }

    override suspend fun updateEventConsumerStatus(
        eventList: List<SyncEventResponse>
    ) {
        try {
            CoreLogger.d(
                context = CoreAppDetails.getApplicationContext().applicationContext,
                "SyncRepositoryImpl",
                "updateEventConsumerStatus: ${eventList.json()}"
            )
            corePrefRepo.savePref(LAST_SYNC_TIME, System.currentTimeMillis())
            eventDao.updateConsumerStatus(eventList)
            eventList.forEach {
                eventStatusDao.insert(
                    EventStatusEntity(
                        clientId = it.clientId,
                        errorMessage = it.errorMessage,
                        status = it.status ?: BLANK_STRING,
                        mobileNumber = corePrefRepo.getMobileNo(),
                        createdBy = corePrefRepo.getUserId(),
                        eventStatusId = 0
                    )
                )
            }
            imageStatusDao.updateImageConsumerStatus(
                eventList = eventList,
                mobileNumber = corePrefRepo.getMobileNo()
            )
            findRequestEvents(eventList, CONSUMER)

        } catch (e: Exception) {
            CoreLogger.d(
                context = CoreAppDetails.getApplicationContext().applicationContext,
                "SyncRepositoryImpl",
                "updateFailedEventStatus: Exception: ${e.message}"
            )
        }
    }

    override suspend fun updateImageDetailsEventStatus(
        eventId: String,
        status: String,
        requestId: String,
        errorMessage: String?
    ) {

        var retryCount = 0
        if (status == EventSyncStatus.PRODUCER_FAILED.eventSyncStatus
            || status == EventSyncStatus.IMAGE_NOT_EXIST.eventSyncStatus
        ) {
            retryCount = eventDao.fetchRetryCountForEvent(eventId) + 1
        }
        imageStatusDao.updateImageEventStatus(
            status = status,
            eventId = eventId,
            errorMessage = errorMessage ?: SOMETHING_WENT_WRONG,
            modifiedDate = System.currentTimeMillis().toDate(),
            mobileNumber = corePrefRepo.getMobileNo(),
            retryCount = retryCount
        )

        eventDao.updateEventStatus(
            retryCount = retryCount,
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
                mobileNumber = corePrefRepo.getMobileNo(),
                createdBy = corePrefRepo.getUserId(),
                eventStatusId = 0
            )
        )

    }

    override suspend fun findEventAndUpdateRetryCount(eventId: String) {
        eventDao.findEventAndUpdateRetryCount(eventId = eventId)
    }

    override suspend fun findEventCountForRequestId(requestId: String): Int {
        return eventDao.fetchEventCountDetailForRequestId(requestId, corePrefRepo.getMobileNo())
    }

    override suspend fun addOrUpdateRequestStatus(
        requestId: String,
        eventCount: Int,
        status: String
    ) {
        requestStatusDao.addOrUpdateRequestId(
            requestStatusEntity = RequestStatusEntity(
                requestId = requestId,
                eventCount = eventCount,
                mobileNumber = corePrefRepo.getMobileNo(),
                status = status,
                eventStatusId = 0,
                createdBy = corePrefRepo.getUserId(),
                modifiedDate = System.currentTimeMillis().toDate()
            )
        )
    }

    override suspend fun fetchEventStatusCount(requestId: String): List<RequestIdCountModel> {
        return eventDao.fetchEventStatusCount(requestId, corePrefRepo.getMobileNo())
    }

    override suspend fun fetchAllRequestEventForConsumerStatus(): List<RequestStatusEntity> {
        return requestStatusDao.getAllRequestEventForConsumerStatus(
            corePrefRepo.getMobileNo(),
            listOf(
                EventSyncStatus.PRODUCER_SUCCESS.eventSyncStatus,
                EventSyncStatus.CONSUMER_FAILED.eventSyncStatus
            )
        )
    }

    override suspend fun findRequestEvents(eventList: List<SyncEventResponse>, tag: String) {
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

    override suspend fun resetRetryCountForProducerFailed() {
        eventDao.resetRetryCountForProducerFailed(
            EventSyncStatus.PRODUCER_FAILED.eventSyncStatus,
            mobileNo = corePrefRepo.getMobileNo()
        )
    }

    override suspend fun getEventListForConsumer(): List<String> {
        return eventDao.getAllEventsForConsumerStatus(
            corePrefRepo.getMobileNo(),
            EventSyncStatus.CONSUMER_SUCCESS.eventSyncStatus
        )
    }


}
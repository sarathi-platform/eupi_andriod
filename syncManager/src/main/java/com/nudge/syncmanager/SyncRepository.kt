package com.nudge.syncmanager


import android.content.Context
import com.nudge.core.BLANK_STRING
import com.nudge.core.EventSyncStatus
import com.nudge.core.SOMETHING_WENT_WRONG
import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.EventStatusEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.request.EventConsumerRequest
import com.nudge.core.model.request.EventRequest
import com.nudge.core.model.request.toEventRequest
import com.nudge.core.model.response.SyncEventResponse
import com.nudge.core.preference.CorePrefRepo
import com.nudge.core.utils.CoreLogger
import com.nudge.syncmanager.network.SyncApiService
import javax.inject.Inject

class SyncApiRepository @Inject constructor(
    val apiService: SyncApiService,
    private val eventDao: EventsDao,
    private val eventStatusDao: EventStatusDao,
    private val prefRepo: CorePrefRepo
) {
    suspend fun syncProducerEventToServer(events: List<Events>): ApiResponseModel<List<SyncEventResponse>> {
        val eventRequest: List<EventRequest> = events.map {
            it.toEventRequest()
        }
        return apiService.syncEvent(eventRequest)
    }

    suspend fun fetchConsumerEventStatus(eventConsumerRequest: EventConsumerRequest): ApiResponseModel<List<SyncEventResponse>> {
        return apiService.syncConsumerStatusApi(eventConsumerRequest)
    }

    fun getPendingEventFromDb(batchLimit: Int, retryCount: Int, syncType: Int): List<Events> {
        return eventDao.getAllPendingEventList(
            listOf(
                EventSyncStatus.OPEN.eventSyncStatus,
                EventSyncStatus.PRODUCER_IN_PROGRESS.eventSyncStatus
            ),
            batchLimit = batchLimit,
            retryCount = retryCount,
            mobileNumber = prefRepo.getMobileNumber(),
            syncType = syncType
        )
    }

    fun getPendingEventCount(syncType: Int): Int {
        return eventDao.getSyncPendingEventCount(
            listOf(
                EventSyncStatus.OPEN.eventSyncStatus,
                EventSyncStatus.PRODUCER_IN_PROGRESS.eventSyncStatus
            ),
            mobileNumber = prefRepo.getMobileNumber(),
            syncType = syncType
        )
    }

    fun updateSuccessEventStatus(context: Context, eventList: List<SyncEventResponse>) {
        try {
            eventDao.updateSuccessEventStatus(eventList)
            eventList.forEach {
                eventStatusDao.insert(
                    EventStatusEntity(
                        clientId = it.clientId,
                        errorMessage = BLANK_STRING,
                        status = it.status,
                        mobileNumber = it.mobileNumber,
                        createdBy = prefRepo.getUserId(),
                        eventStatusId = 0
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

    fun updateFailedEventStatus(context: Context, eventList: List<SyncEventResponse>) {
        try {
            eventDao.updateFailedEventStatus(eventList)
            eventList.forEach {
                eventStatusDao.insert(
                    EventStatusEntity(
                        clientId = it.clientId,
                        errorMessage = it.errorMessage.ifEmpty { SOMETHING_WENT_WRONG },
                        status = EventSyncStatus.PRODUCER_FAILED.eventSyncStatus,
                        mobileNumber = prefRepo.getMobileNumber(),
                        createdBy = prefRepo.getUserId(),
                        eventStatusId = 0
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

    fun updateEventConsumerStatus(context: Context, eventList: List<SyncEventResponse>) {
        try {
            eventDao.updateConsumerStatus(eventList)
            eventList.forEach {
                eventStatusDao.insert(
                    EventStatusEntity(
                        clientId = it.clientId,
                        errorMessage = it.errorMessage,
                        status = it.status,
                        mobileNumber = prefRepo.getMobileNumber(),
                        createdBy = prefRepo.getUserId(),
                        eventStatusId = 0
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

    fun getLoggedInMobileNumber():String = prefRepo.getMobileNumber()
}





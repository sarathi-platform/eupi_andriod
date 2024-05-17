package com.nudge.syncmanager


import com.nudge.core.EventSyncStatus
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.Events
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.request.EventRequest
import com.nudge.core.model.request.toEventRequest
import com.nudge.core.model.response.SyncEventResponse
import com.nudge.syncmanager.network.SyncApiService
import javax.inject.Inject

class SyncApiRepository @Inject constructor(
    val apiService: SyncApiService,
    private val eventDao: EventsDao
) {
    suspend fun syncProducerEventToServer(events: List<Events>): ApiResponseModel<List<SyncEventResponse>> {
        val eventRequest: List<EventRequest> = events.map {
            it.toEventRequest()
        }
        return apiService.syncEvent(eventRequest)
    }

    fun getPendingEventFromDb(batchLimit:Int,retryCount:Int): List<Events> {
        return eventDao.getAllPendingEvent(
            listOf(
                EventSyncStatus.OPEN.eventSyncStatus,
                EventSyncStatus.PRODUCER_IN_PROGRESS.eventSyncStatus
            ),
            batchLimit = batchLimit,
            retryCount=retryCount
        )
    }

    suspend fun getPendingEventCount(): Int {
        return eventDao.getTotalPendingEventCount(
            listOf(
                EventSyncStatus.OPEN.eventSyncStatus,
                EventSyncStatus.PRODUCER_IN_PROGRESS.eventSyncStatus
            )
        )

    }

    fun updateSuccessEventStatus(eventList:List<SyncEventResponse>){
        eventDao.updateSuccessEventStatus(eventList)
    }

    fun updateFailedEventStatus(eventList:List<SyncEventResponse>){
        eventDao.updateFailedEventStatus(eventList)
    }
}





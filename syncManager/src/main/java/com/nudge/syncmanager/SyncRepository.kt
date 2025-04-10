package com.nudge.syncmanager


import com.nudge.core.EventSyncStatus
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.Events
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.request.EventRequest
import com.nudge.core.model.request.toEventRequest
import com.nudge.core.model.response.SyncEventResponse
import javax.inject.Inject

class SyncApiRepository @Inject constructor(
    val apiService: SyncApiService,
    private val eventDao: EventsDao
) {
    suspend fun syncEventToServer(events: List<Events>): ApiResponseModel<List<SyncEventResponse>> {
        val eventRequest: List<EventRequest> = events.map {
            it.toEventRequest()
        }
        return apiService.syncEvent(eventRequest)
    }

    suspend fun getPendingEventFromDb(): List<Events> {
        return eventDao.getAllPendingEvent(listOf(EventSyncStatus.RETRY, EventSyncStatus.OPEN))

    }

    suspend fun getPendingEventCount(): Int {
        return eventDao.getTotalPendingEventCount(
            listOf(
                EventSyncStatus.RETRY,
                EventSyncStatus.OPEN
            )
        )

    }
}





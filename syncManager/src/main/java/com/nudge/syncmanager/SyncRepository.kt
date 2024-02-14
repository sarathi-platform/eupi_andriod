package com.nudge.syncmanager


import com.nudge.core.EventSyncStatus
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.Events
import com.nudge.core.model.ApiResponseModel
import com.nudge.syncmanager.model.ConfigResponseModel
import javax.inject.Inject

class SyncApiRepository @Inject constructor(
    val apiService: SyncApiService,
    val eventDao: EventsDao
) {
    suspend fun fetchLanguageFromAPI(): ApiResponseModel<ConfigResponseModel> {
        return apiService.configDetails()
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

    suspend fun syncEventInNetwork(events: List<Events>) {
//        val eventRequest: List<EventRequest> = events.map {
//            EventRequest.EventRequestMapper.fromEvent(it)
//        }

        //    Log.d("Sync Request", Gson().toJson(eventRequest))

    }

}





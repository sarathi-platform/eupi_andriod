package com.nudge.syncmanager


import android.util.Log
import com.nudge.core.BLANK_STRING
import com.nudge.core.EventSyncStatus
import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.EventStatusEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.request.EventRequest
import com.nudge.core.model.request.toEventRequest
import com.nudge.core.model.response.SyncEventResponse
import com.nudge.core.preference.CorePrefRepo
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

    fun getPendingEventFromDb(batchLimit:Int,retryCount:Int): List<Events> {
        return eventDao.getAllPendingEvent(
            listOf(
                EventSyncStatus.OPEN.eventSyncStatus,
                EventSyncStatus.PRODUCER_IN_PROGRESS.eventSyncStatus
            ),
            batchLimit = batchLimit,
            retryCount=retryCount,
            mobileNumber = prefRepo.getMobileNumber()
        )
    }

    suspend fun getPendingEventCount(): Int {
        Log.d("TAG", "getPendingEventCount: ${prefRepo.getMobileNumber()}")
        return eventDao.getTotalPendingEventCount(
            listOf(
                EventSyncStatus.OPEN.eventSyncStatus,
                EventSyncStatus.PRODUCER_IN_PROGRESS.eventSyncStatus
            ),
            mobileNumber = prefRepo.getMobileNumber()
        )

    }

    fun updateSuccessEventStatus(eventList: List<SyncEventResponse>) {
        eventDao.updateSuccessEventStatus(eventList)
        eventList.forEach {
            eventStatusDao.insert(
                EventStatusEntity(
                    clientId = it.clientId,
                    name = it.eventName,
                    errorMessage = BLANK_STRING,
                    type = it.type,
                    status = EventSyncStatus.OPEN.eventSyncStatus,
                    mobileNumber = it.mobileNumber,
                    createdBy = prefRepo.getUserId(),
                    eventStatusId = 0
                )
            )
        }
    }

    fun updateFailedEventStatus(eventList:List<SyncEventResponse>){
        eventDao.updateFailedEventStatus(eventList)
        eventList.forEach {
            eventStatusDao.insert(
                EventStatusEntity(
                    clientId = it.clientId,
                    name = it.eventName,
                    errorMessage = BLANK_STRING,
                    type = it.type,
                    status = EventSyncStatus.OPEN.eventSyncStatus,
                    mobileNumber = it.mobileNumber,
                    createdBy = prefRepo.getUserId(),
                    eventStatusId = 0
                )
            )
        }
    }
}





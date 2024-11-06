package com.nudge.syncmanager.domain.repository

import com.nudge.core.BLANK_STRING
import com.nudge.core.datamodel.RequestIdCountModel
import com.nudge.core.model.response.SyncEventResponse

interface SyncAddUpdateEventRepository {
    suspend fun updateSuccessEventStatus(eventList: List<SyncEventResponse>)
    suspend fun updateFailedEventStatus(eventList: List<SyncEventResponse>)
    suspend fun updateEventConsumerStatus(eventList: List<SyncEventResponse>)
    suspend fun updateImageDetailsEventStatus(
        eventId: String,
        status: String,
        requestId: String,
        errorMessage: String? = BLANK_STRING
    )

    suspend fun addOrUpdateRequestStatus(requestId: String, eventCount: Int, status: String)
    suspend fun resetRetryCountForProducerFailed()
    suspend fun findRequestEvents(eventList: List<SyncEventResponse>, tag: String)

    suspend fun findEventCountForRequestId(requestId: String): Int
    suspend fun fetchEventStatusCount(requestId: String): List<RequestIdCountModel>
    suspend fun findEventAndUpdateRetryCount(eventId: String)


}
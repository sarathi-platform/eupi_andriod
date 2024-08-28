package com.nudge.syncmanager.domain.usecase

import com.nudge.core.BLANK_STRING
import com.nudge.core.model.response.SyncEventResponse
import com.nudge.syncmanager.domain.repository.SyncRepository

class AddUpdateEventUseCase(
    private val repository: SyncRepository
) {
    suspend fun updateSuccessEventStatus(eventList: List<SyncEventResponse>) {
        repository.updateSuccessEventStatus(eventList)
    }

    suspend fun updateFailedEventStatus(eventList: List<SyncEventResponse>) =
        repository.updateFailedEventStatus(eventList)

    suspend fun updateImageDetailsEventStatus(
        eventId: String,
        status: String,
        requestId: String,
        errorMessage: String? = BLANK_STRING
    ) {
        repository.updateImageDetailsEventStatus(
            eventId = eventId,
            status = status,
            requestId = requestId,
            errorMessage = errorMessage
        )
    }

    suspend fun findEventAndUpdateRetryCount(eventId: String) {
        repository.findEventAndUpdateRetryCount(eventId)
    }

    suspend fun addOrUpdateRequestStatus(requestId: String, eventCount: Int, status: String) {
        repository.addOrUpdateRequestStatus(
            requestId = requestId,
            eventCount = eventCount,
            status = status
        )
    }

}
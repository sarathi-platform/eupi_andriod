package com.nudge.syncmanager.domain.usecase

import com.nudge.core.database.entities.Events
import com.nudge.core.model.response.SyncEventResponse
import com.nudge.syncmanager.domain.repository.SyncRepository

class FetchEventsFromDBUseCase(
    private val repository: SyncRepository
) {
    suspend fun fetchAllImageEventDetails(eventIds: List<String>) =
        repository.fetchAllImageEventDetails(eventIds)

    suspend fun getPendingEventFromDb(
        batchLimit: Int,
        retryCount: Int,
        syncType: Int
    ): List<Events> {
        return repository.getPendingEventFromDb(
            batchLimit = batchLimit,
            retryCount = retryCount,
            syncType = syncType
        )
    }

    suspend fun getPendingEventCount(syncType: Int) = repository.getPendingEventCount(syncType)

    suspend fun findRequestEvents(eventList: List<SyncEventResponse>, tag: String) =
        repository.findRequestEvents(eventList = eventList, tag)

}
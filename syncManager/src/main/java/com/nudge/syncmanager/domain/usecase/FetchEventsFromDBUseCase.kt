package com.nudge.syncmanager.domain.usecase

import com.nudge.core.database.entities.Events
import com.nudge.core.model.response.SyncEventResponse
import com.nudge.syncmanager.domain.repository.SyncAddUpdateEventRepository
import com.nudge.syncmanager.domain.repository.SyncRepository
import javax.inject.Inject

class FetchEventsFromDBUseCase @Inject constructor(
    private val repository: SyncRepository,
    private val syncAddUpdateEventRepository: SyncAddUpdateEventRepository
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
        syncAddUpdateEventRepository.findRequestEvents(eventList = eventList, tag)

}
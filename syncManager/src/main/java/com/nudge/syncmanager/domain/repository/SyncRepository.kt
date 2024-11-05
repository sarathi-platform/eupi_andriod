package com.nudge.syncmanager.domain.repository

import com.nudge.core.database.entities.Events
import com.nudge.core.database.entities.RequestStatusEntity
import com.nudge.core.datamodel.ImageEventDetailsModel

interface SyncRepository {

    suspend fun fetchAllImageEventDetails(eventIds: List<String>): List<ImageEventDetailsModel>
    suspend fun getPendingEventFromDb(
        batchLimit: Int,
        retryCount: Int,
        syncType: Int
    ): List<Events>

    suspend fun getPendingEventCount(syncType: Int): Int
    suspend fun fetchAllRequestEventForConsumerStatus(): List<RequestStatusEntity>
    suspend fun getEventListForConsumer(): List<String>

}
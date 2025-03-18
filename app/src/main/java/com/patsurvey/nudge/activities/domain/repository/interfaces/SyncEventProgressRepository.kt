package com.patsurvey.nudge.activities.domain.repository.interfaces

import com.nudge.core.database.entities.Events

interface SyncEventProgressRepository {

    suspend fun getAllEventsForUser(): List<Events>

    suspend fun sendAnalyticsEventForSyncProgress(
        dataEventParamMap: Map<String, Int>,
        imageEventParamMap: Map<String, Int>
    )

    suspend fun deleteSyncedEventForUser(): Int

    suspend fun sendAnalyticsEventForDeletedEventCount(deletedEventCount: Int)

    suspend fun getThresholdDate(): Long

    suspend fun isDeleteEventAllowed(): Boolean

}
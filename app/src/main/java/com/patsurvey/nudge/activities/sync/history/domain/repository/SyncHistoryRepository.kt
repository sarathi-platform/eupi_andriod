package com.patsurvey.nudge.activities.sync.history.domain.repository

import com.nudge.core.database.entities.EventStatusEntity
import com.nudge.core.database.entities.Events

interface SyncHistoryRepository {

    fun getUserMobileNumber(): String
    fun getUserID(): String

    suspend fun getAllEventsBetweenDates(
        startDate: String,
        endDate: String
    ): List<EventStatusEntity>

    suspend fun getAllEventsForUser(): List<Events>
}
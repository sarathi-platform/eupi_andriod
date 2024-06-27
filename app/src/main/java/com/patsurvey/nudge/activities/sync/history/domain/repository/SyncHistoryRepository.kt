package com.patsurvey.nudge.activities.sync.history.domain.repository

import com.nudge.core.database.entities.EventStatusEntity

interface SyncHistoryRepository {

    fun getUserMobileNumber(): String
    fun getUserID(): String

    suspend fun getAllEventsBetweenDates(
        startDate: String,
        endDate: String
    ): List<EventStatusEntity>

    suspend fun getAllEventStatusForUser(): List<EventStatusEntity>
}
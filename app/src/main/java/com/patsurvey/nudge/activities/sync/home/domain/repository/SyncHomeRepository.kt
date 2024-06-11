package com.patsurvey.nudge.activities.sync.home.domain.repository

import com.nudge.core.database.entities.Events

interface SyncHomeRepository {
    fun getTotalEvents():List<Events>

    fun getUserMobileNumber(): String
    fun getUserID(): String
    fun getUserEmail(): String
    fun getUserName(): String
    fun getLoggedInUserType(): String
}
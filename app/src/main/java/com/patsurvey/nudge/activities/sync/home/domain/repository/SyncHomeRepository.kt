package com.patsurvey.nudge.activities.sync.home.domain.repository

import androidx.lifecycle.LiveData
import com.nudge.core.database.entities.Events
import kotlinx.coroutines.flow.Flow

interface SyncHomeRepository {
    fun getTotalEvents():LiveData<List<Events>>

    fun getUserMobileNumber(): String
    fun getUserID(): String
    fun getUserEmail(): String
    fun getUserName(): String
    fun getLoggedInUserType(): String
}
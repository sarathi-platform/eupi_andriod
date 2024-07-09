package com.patsurvey.nudge.activities.sync.home.domain.repository

import androidx.lifecycle.LiveData
import com.nudge.core.database.entities.Events
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.response.LastSyncResponseModel

interface SyncHomeRepository {
    fun getTotalEvents():LiveData<List<Events>>
    fun getAllFailedEventListFromDB(): List<Events>
    fun getUserMobileNumber(): String
    fun getUserID(): String
    fun getUserEmail(): String
    fun getUserName(): String
    fun getLoggedInUserType(): String
    fun saveLastSyncDateTime(dateTime: Long)
    suspend fun getLastSyncDateTime(): ApiResponseModel<LastSyncResponseModel>
}
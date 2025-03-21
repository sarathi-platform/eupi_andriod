package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.model.uiModel.ActivityFormUIModel
import com.sarathi.dataloadingmangement.model.uiModel.ActivityUiModel
import kotlinx.coroutines.flow.Flow

interface IActivityRepository {
    fun getActivity(missionId: Int): Flow<List<ActivityUiModel>>
    suspend fun isActivityCompleted(missionId: Int, activityId: Int): Boolean
    suspend fun isAllActivityCompleted(missionId: Int): Boolean
    suspend fun updateMissionStatus(missionId: Int, status: String)
    suspend fun getActiveForm(
        formType: String
    ): List<ActivityFormUIModel>

    suspend fun getTypeForActivity(missionId: Int, activityId: Int): String?
}
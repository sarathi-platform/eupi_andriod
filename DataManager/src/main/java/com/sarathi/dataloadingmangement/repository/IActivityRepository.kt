package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.model.uiModel.ActivityUiModel

interface IActivityRepository {
    suspend fun getActivity(missionId: Int): List<ActivityUiModel>
    suspend fun isAllActivityCompleted(missionId: Int, activityId: Int): Boolean
    suspend fun updateMissionStatus(missionId: Int, status: String)

}
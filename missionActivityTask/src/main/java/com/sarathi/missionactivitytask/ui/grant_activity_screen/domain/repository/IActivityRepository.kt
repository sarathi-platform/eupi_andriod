package com.sarathi.missionactivitytask.ui.grant_activity_screen.domain.repository

import com.sarathi.dataloadingmangement.model.uiModel.ActivityUiModel

interface IActivityRepository {
    suspend fun getActivity(missionId: Int): List<ActivityUiModel>
    suspend fun isAllActivityCompleted(): Boolean
    suspend fun updateMissionStatus(missionId: Int, status: String)

}
package com.sarathi.missionactivitytask.ui.grantTask.domain.repository

import com.sarathi.dataloadingmangement.model.uiModel.UiConfigModel

interface IUiConfigRepository {

    suspend fun getActivityUiConfig(missionId: Int, activityId: Int): List<UiConfigModel>

}
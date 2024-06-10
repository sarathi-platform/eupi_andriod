package com.sarathi.missionactivitytask.ui.grantTask.domain.repository

import com.sarathi.dataloadingmangement.model.uiModel.ActivityConfigUiModel

interface IActivityConfigRepository {

    suspend fun getActivityConfig(activityId: Int): ActivityConfigUiModel

}
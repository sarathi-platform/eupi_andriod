package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.ActivityConfigEntity
import com.sarathi.dataloadingmangement.model.uiModel.UiConfigModel

interface IUiConfigRepository {

    suspend fun getActivityUiConfig(missionId: Int, activityId: Int): List<UiConfigModel>
    suspend fun getActivityConfig(activityId: Int): ActivityConfigEntity?

    suspend fun getActivityConfig(activityId: Int, missionId: Int): ActivityConfigEntity?

}
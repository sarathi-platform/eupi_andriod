package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.model.uiModel.UiConfigModel

interface IUiConfigRepository {

    suspend fun getActivityUiConfig(missionId: Int, activityId: Int): List<UiConfigModel>

}
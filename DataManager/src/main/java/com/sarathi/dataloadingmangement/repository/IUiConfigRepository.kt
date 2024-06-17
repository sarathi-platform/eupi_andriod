package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.UiConfigEntity

interface IUiConfigRepository {

    suspend fun getActivityUiConfig(missionId: Int, activityId: Int): List<UiConfigEntity>

}
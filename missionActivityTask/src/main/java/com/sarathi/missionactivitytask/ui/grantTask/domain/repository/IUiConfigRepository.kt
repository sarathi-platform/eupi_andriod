package com.sarathi.missionactivitytask.ui.grantTask.domain.repository

import com.sarathi.dataloadingmangement.data.entities.UiConfigEntity

interface IUiConfigRepository {

    suspend fun getActivityUiConfig(missionId: Int, activityId: Int): List<UiConfigEntity>

}
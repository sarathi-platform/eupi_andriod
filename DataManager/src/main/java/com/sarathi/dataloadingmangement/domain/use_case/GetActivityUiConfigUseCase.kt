package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.data.entities.ActivityConfigEntity
import com.sarathi.dataloadingmangement.model.uiModel.UiConfigModel
import com.sarathi.dataloadingmangement.repository.GetActivityUiConfigRepositoryImpl
import javax.inject.Inject


class GetActivityUiConfigUseCase @Inject constructor(private val configRepositoryImpl: GetActivityUiConfigRepositoryImpl) {

    suspend fun getActivityUiConfig(missionId: Int, activityId: Int): List<UiConfigModel> =
        configRepositoryImpl.getActivityUiConfig(missionId, activityId)

    suspend fun getActivityConfig(activityId: Int): ActivityConfigEntity? {
        return configRepositoryImpl.getActivityConfig(activityId = activityId)
    }

    suspend fun getActivityConfig(activityId: Int, missionId: Int): ActivityConfigEntity? {
        return configRepositoryImpl.getActivityConfig(activityId = activityId, missionId)
    }

}
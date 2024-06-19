package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.model.uiModel.UiConfigModel
import com.sarathi.dataloadingmangement.repository.GetActivityUiConfigRepositoryImpl
import javax.inject.Inject


class GetActivityUiConfigUseCase @Inject constructor(private val configRepositoryImpl: GetActivityUiConfigRepositoryImpl) {

    suspend fun getActivityUiConfig(missionId: Int, activityId: Int): List<UiConfigModel> =
        configRepositoryImpl.getActivityUiConfig(missionId, activityId)

}
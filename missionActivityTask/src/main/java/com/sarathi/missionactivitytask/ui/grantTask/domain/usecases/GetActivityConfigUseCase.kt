package com.sarathi.missionactivitytask.ui.grantTask.domain.usecases

import com.sarathi.dataloadingmangement.model.uiModel.ActivityConfigUiModel
import com.sarathi.missionactivitytask.ui.grantTask.domain.repository.GetActivityConfigRepositoryImpl
import javax.inject.Inject


class GetActivityConfigUseCase @Inject constructor(private val configRepositoryImpl: GetActivityConfigRepositoryImpl) {

    suspend fun getActivityUiConfig(activityId: Int): ActivityConfigUiModel =
        configRepositoryImpl.getActivityConfig(activityId)

}
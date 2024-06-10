package com.sarathi.missionactivitytask.ui.grantTask.domain.usecases

import com.sarathi.dataloadingmangement.data.entities.UiConfigEntity
import com.sarathi.missionactivitytask.ui.grantTask.domain.repository.GetActivityUiConfigRepositoryImpl
import javax.inject.Inject


class GetActivityUiConfigUseCase @Inject constructor(private val configRepositoryImpl: GetActivityUiConfigRepositoryImpl) {

    suspend fun getActivityUiConfig(missionId: Int, activityId: Int): List<UiConfigEntity> =
        configRepositoryImpl.getActivityUiConfig(missionId, activityId)

}
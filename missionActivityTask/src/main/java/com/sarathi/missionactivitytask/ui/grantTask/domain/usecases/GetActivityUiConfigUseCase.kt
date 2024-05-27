package com.sarathi.missionactivitytask.ui.grantTask.domain.usecases

import com.sarathi.dataloadingmangement.data.entities.UiConfigEntity
import com.sarathi.missionactivitytask.ui.grantTask.domain.repository.GetActivityConfigRepositoryImpl
import javax.inject.Inject


class GetActivityUiConfigUseCase @Inject constructor(private val configRepositoryImpl: GetActivityConfigRepositoryImpl) {

    suspend fun getActivityUiConfig(missionId: Int, activityId: Int): List<UiConfigEntity> =
        configRepositoryImpl.getActivityUiConfig(missionId, activityId)

}
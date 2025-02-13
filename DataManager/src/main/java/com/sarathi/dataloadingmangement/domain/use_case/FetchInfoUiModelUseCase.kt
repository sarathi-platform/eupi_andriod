package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.model.uiModel.ActivityInfoUIModel
import com.sarathi.dataloadingmangement.model.uiModel.MissionInfoUIModel
import com.sarathi.dataloadingmangement.repository.FetchInfoUiModelRepository
import javax.inject.Inject

class FetchInfoUiModelUseCase @Inject constructor(
    private val repository: FetchInfoUiModelRepository
) {

    suspend fun fetchMissionInfo(missionId: Int): MissionInfoUIModel {
        return repository.fetchMissionInfo(missionId) ?: MissionInfoUIModel.getDefaultValue()
    }

    suspend fun fetchActivityInfo(missionId: Int, activityId: Int): ActivityInfoUIModel {
        return repository.fetchActivityInfo(missionId, activityId)
            ?: ActivityInfoUIModel.getDefaultValue()
    }

}
package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.model.uiModel.ActivityInfoUIModel
import com.sarathi.dataloadingmangement.model.uiModel.MissionInfoUIModel

interface FetchInfoUiModelRepository {

    suspend fun fetchMissionInfo(missionId: Int): MissionInfoUIModel?
    suspend fun fetchActivityInfo(missionId: Int, activityId: Int): ActivityInfoUIModel?

}
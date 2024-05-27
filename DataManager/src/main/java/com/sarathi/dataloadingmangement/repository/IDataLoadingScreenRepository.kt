package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.model.ApiResponseModel
import com.sarathi.dataloadingmangement.model.dataModel.MissionActivityModel
import com.sarathi.dataloadingmangement.model.dataModel.MissionTaskModel
import com.sarathi.dataloadingmangement.model.response.MissionResponseModel


interface IDataLoadingScreenRepository {
    suspend fun fetchMissionDataFromServer(
        languageCode: String,
        missionName: String
    ): ApiResponseModel<List<MissionResponseModel>>

    suspend fun saveMissionToDB(missions: List<MissionResponseModel>)

    suspend fun saveMissionsActivityToDB(
        activities: List<MissionActivityModel>,
        missionId: Int,
    )

    fun saveMissionsActivityTaskToDB(
        missionId: Int,
        activityId: Int,
        activityName: String,
        activities: List<MissionTaskModel>
    )
}
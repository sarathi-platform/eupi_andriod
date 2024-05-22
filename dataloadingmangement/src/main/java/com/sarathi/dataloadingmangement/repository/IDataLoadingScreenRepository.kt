package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.model.ApiResponseModel
import com.sarathi.dataloadingmangement.response.MissionActivityModel
import com.sarathi.dataloadingmangement.response.MissionResponseModel
import com.sarathi.dataloadingmangement.response.MissionTaskModel


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
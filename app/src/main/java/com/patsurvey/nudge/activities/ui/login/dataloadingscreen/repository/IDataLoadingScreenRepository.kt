package com.patsurvey.nudge.activities.ui.login.dataloadingscreen.repository

import com.patsurvey.nudge.model.response.ApiResponseModel
import com.sarathi.missionactivitytask.models.response.MissionActivityModel
import com.sarathi.missionactivitytask.models.response.MissionResponseModel
import com.sarathi.missionactivitytask.models.response.MissionTaskModel

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
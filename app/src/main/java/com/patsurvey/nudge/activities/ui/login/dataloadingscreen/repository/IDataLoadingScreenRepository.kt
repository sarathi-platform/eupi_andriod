package com.patsurvey.nudge.activities.ui.login.dataloadingscreen.repository

import com.patsurvey.nudge.model.response.ApiResponseModel
import com.sarathi.contentmodule.model.ContentResponse
import com.sarathi.contentmodule.request.ContentRequest
import com.sarathi.missionactivitytask.data.entities.Content
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

    suspend fun fetchContentsFromServer(contentMangerRequest: ContentRequest): ApiResponseModel<List<ContentResponse>>

    suspend fun saveContentToDB(contents: List<Content>)
    suspend fun deleteContentFromDB()
    suspend fun getContentData(): List<Content>

}
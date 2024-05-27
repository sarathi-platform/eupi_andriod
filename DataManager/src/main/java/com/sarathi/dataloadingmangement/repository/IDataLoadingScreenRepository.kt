package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.model.ApiResponseModel
import com.sarathi.dataloadingmangement.model.mat.response.ActivityResponse
import com.sarathi.dataloadingmangement.model.mat.response.ContentResponse
import com.sarathi.dataloadingmangement.model.mat.response.MissionResponse
import com.sarathi.dataloadingmangement.model.mat.response.TaskResponse
import com.sarathi.dataloadingmangement.network.request.ContentRequest


interface IDataLoadingScreenRepository {
    suspend fun fetchMissionDataFromServer(
        languageCode: String,
        missionName: String
    ): ApiResponseModel<List<MissionResponse>>

    suspend fun saveMissionToDB(missions: List<MissionResponse>)

    suspend fun saveMissionsActivityToDB(
        activities: List<ActivityResponse>,
        missionId: Int,
    )

    fun saveMissionsActivityTaskToDB(
        missionId: Int,
        activityId: Int,
        subject: String,
        activities: List<TaskResponse>
    )

    suspend fun fetchContentsFromServer(contentMangerRequest: ContentRequest): ApiResponseModel<List<ContentResponse>>

    suspend fun saveContentToDB(contents: List<Content>)
    suspend fun deleteContentFromDB()
    suspend fun getContentData(): List<Content>

}
package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.model.MissionActivityModel
import com.sarathi.dataloadingmangement.model.MissionTaskModel
import com.sarathi.dataloadingmangement.network.request.ContentRequest
import com.sarathi.dataloadingmangement.network.response.ApiResponseModel
import com.sarathi.dataloadingmangement.network.response.ContentResponse
import com.sarathi.dataloadingmangement.network.response.MissionResponseModel


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
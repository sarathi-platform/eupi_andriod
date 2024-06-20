package com.sarathi.dataloadingmangement.repository

import com.nudge.core.model.ApiResponseModel
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.model.mat.response.ActivityResponse
import com.sarathi.dataloadingmangement.model.mat.response.ContentResponse
import com.sarathi.dataloadingmangement.model.mat.response.MissionResponse
import com.sarathi.dataloadingmangement.model.mat.response.ProgrameResponse
import com.sarathi.dataloadingmangement.model.mat.response.TaskResponse
import com.sarathi.dataloadingmangement.network.request.ContentRequest
import com.sarathi.dataloadingmangement.network.response.UserDetailsResponse


interface IDataLoadingScreenRepository {
    suspend fun fetchMissionDataFromServer(
    ): ApiResponseModel<List<ProgrameResponse>>

    suspend fun saveMissionToDB(missions: List<MissionResponse>, programmeId: Int)

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
    suspend fun saveProgrammeToDb(programme: ProgrameResponse)

    suspend fun getUserDetailsFromNetwork(languageId: String): ApiResponseModel<UserDetailsResponse>

    fun saveUserDetails(userDetails: UserDetailsResponse)

}
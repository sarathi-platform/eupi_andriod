package com.sarathi.dataloadingmangement.repository

import com.nudge.core.model.ApiResponseModel
import com.sarathi.dataloadingmangement.model.mat.response.ActivityResponse
import com.sarathi.dataloadingmangement.model.mat.response.MissionResponse
import com.sarathi.dataloadingmangement.model.mat.response.ProgrameResponse
import com.sarathi.dataloadingmangement.model.mat.response.TaskResponse
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel

interface IMissionRepository {
    suspend fun fetchActivityDataFromServer(
        programId: Int,
        missionId: Int
    ): ApiResponseModel<List<ActivityResponse>>

    suspend fun fetchMissionListFromServer(
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

    suspend fun saveProgrammeToDb(programme: ProgrameResponse)


    suspend fun getAllMission(): List<MissionUiModel>
    suspend fun saveActivityConfig(
        missionActivityModel: ActivityResponse,
        missionId: Int,
    )

    suspend fun isMissionLoaded(missionId: Int, programId: Int): Int
    suspend fun setMissionLoaded(missionId: Int, programId: Int)
    suspend fun getActivityTypesForMission(missionId: Int): List<String>
}
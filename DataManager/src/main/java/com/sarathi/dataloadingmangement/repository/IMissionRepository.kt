package com.sarathi.dataloadingmangement.repository

import com.nudge.core.model.ApiResponseModel
import com.sarathi.dataloadingmangement.model.mat.response.ActivityResponse
import com.sarathi.dataloadingmangement.model.mat.response.MissionResponse
import com.sarathi.dataloadingmangement.model.mat.response.ProgrameResponse
import com.sarathi.dataloadingmangement.model.mat.response.TaskResponse

interface IMissionRepository {
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

    suspend fun saveProgrammeToDb(programme: ProgrameResponse)

}
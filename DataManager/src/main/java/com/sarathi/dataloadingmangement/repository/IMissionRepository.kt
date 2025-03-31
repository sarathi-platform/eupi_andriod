package com.sarathi.dataloadingmangement.repository

import com.nudge.core.model.ApiResponseModel
import com.sarathi.dataloadingmangement.model.mat.response.ActivityResponse
import com.sarathi.dataloadingmangement.model.mat.response.MissionResponse
import com.sarathi.dataloadingmangement.model.mat.response.ProgrameResponse
import com.sarathi.dataloadingmangement.model.uiModel.MissionInfoUIModel
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel
import kotlinx.coroutines.flow.Flow

interface IMissionRepository {
    suspend fun fetchMissionListFromServer(
    ): ApiResponseModel<List<ProgrameResponse>>
    suspend fun saveProgrammeToDb(programme: ProgrameResponse)
    suspend fun saveMissionToDB(missions: List<MissionResponse>, programmeId: Int)

    suspend fun saveMissionsActivityToDB(
        activities: List<ActivityResponse>,
        missionId: Int
    )
    fun getAllMission(): Flow<List<MissionUiModel>>
    suspend fun isMissionLoaded(missionId: Int, programId: Int): Int
    suspend fun setMissionLoaded(missionId: Int, programId: Int)
    suspend fun fetchMissionInfo(missionId: Int): MissionInfoUIModel?

}
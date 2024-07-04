package com.nrlm.baselinesurvey.data.domain.repository

import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.database.entity.MissionEntity

interface UpdateBaselineStatusOnInitRepository {

    fun getUserId(): String

    suspend fun getBaselineMission(): MissionEntity

    suspend fun getActivitiesForMission(
        userId: String,
        missionInt: Int
    ): List<MissionActivityEntity>

    suspend fun updateBaselineMissionStatusForGrant(missionId: Int, status: String, userId: String)

    suspend fun updateBaselineActivityStatusForGrant(
        missionId: Int,
        activityId: Int,
        status: String,
        userId: String
    )

}
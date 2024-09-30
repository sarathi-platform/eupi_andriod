package com.nrlm.baselinesurvey.data.domain.repository

import com.nrlm.baselinesurvey.database.dao.MissionActivityDao
import com.nrlm.baselinesurvey.database.dao.MissionEntityDao
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.database.entity.MissionEntity
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.ActivityDao
import com.sarathi.dataloadingmangement.data.dao.MissionDao
import javax.inject.Inject

class UpdateBaselineStatusOnInitRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val missionEntityDao: MissionEntityDao,
    private val missionActivityDao: MissionActivityDao,
    private val grantMissionDao: MissionDao,
    private val grantActivityDao: ActivityDao
) : UpdateBaselineStatusOnInitRepository {

    private val BASELINE_MISSION_ID: Int
        get() = 1

    override fun getUserId(): String {
        return coreSharedPrefs.getUniqueUserIdentifier()
    }

    override suspend fun getBaselineMission(): MissionEntity {
        return missionEntityDao.getMission(getUserId(), BASELINE_MISSION_ID)
    }

    override suspend fun getActivitiesForMission(
        userId: String,
        missionInt: Int
    ): List<MissionActivityEntity> {
        return missionActivityDao.getActivities(userId, missionInt)
    }

    override suspend fun updateBaselineMissionStatusForGrant(
        missionId: Int,
        status: String,
        userId: String
    ) {
        grantMissionDao.updateMissionStatus(userId, status, missionId)
    }

    override suspend fun updateBaselineActivityStatusForGrant(
        missionId: Int,
        activityId: Int,
        status: String,
        userId: String
    ) {
        grantActivityDao.updateActivityStatus(
            userId = userId,
            missionId = missionId,
            activityId = activityId,
            status = status
        )
    }
}






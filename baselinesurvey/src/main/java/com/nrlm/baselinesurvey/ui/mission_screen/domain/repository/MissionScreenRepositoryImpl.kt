package com.nrlm.baselinesurvey.ui.mission_screen.domain.repository

import com.nrlm.baselinesurvey.database.dao.MissionActivityDao
import com.nrlm.baselinesurvey.database.dao.MissionEntityDao
import com.nrlm.baselinesurvey.database.entity.MissionEntity
import javax.inject.Inject

class MissionScreenRepositoryImpl @Inject constructor(
    private val missionEntityDao: MissionEntityDao,
    private val missionActivityDao: MissionActivityDao
) : MissionScreenRepository {
    override suspend fun getMissionsFromDB(): List<MissionEntity> {
        return missionEntityDao.getMissions()
    }

    override fun getLanguageId(): String {
        return "en"
    }

    override suspend fun getMissionsStatusFromDB(missions: List<MissionEntity>?): List<MissionEntity>? {
        missions?.forEach { missions ->
            var isStatusUpdate = false
            var pendingInc = 0
            var activityComplete = 0
            if (missionActivityDao.isActivityExist(missions.missionId)) {
                missionActivityDao.getActivities(missions.missionId).forEach { activity ->
                    isStatusUpdate = activity.activityStatus == 2
                    if (activity.activityStatus == 2) {
                        isStatusUpdate = true
                        ++activityComplete
                    }
                    if (!isStatusUpdate) {
                        ++pendingInc
                    }
                }
            }
            missionEntityDao.updateMissionStatus(
                missions.missionId,
                if (isStatusUpdate) 2 else 0,
                activityComplete,
                pendingInc
            )
        }
        return missions ?: listOf()
    }
}
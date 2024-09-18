package com.patsurvey.nudge.activities.backup.domain.repository

import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
import com.nrlm.baselinesurvey.database.dao.MissionActivityDao
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.ActivityDao
import com.sarathi.dataloadingmangement.data.entities.ActivityEntity
import javax.inject.Inject

class ReopenActivityEventHelperRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val prefBSRepo: PrefBSRepo,
    private val activityDao: ActivityDao,
    private val missionActivityDao: MissionActivityDao
) : ReopenActivityEventHelperRepository {
    override suspend fun getActivitiesForMission(
        missionId: Int,
        activityIds: List<Int>
    ): List<ActivityEntity> {
        return activityDao.getActivityEntityList(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            missionId = missionId,
            activityIds = activityIds
        )
    }

    override suspend fun getActivitiesForBaselineMission(
        missionId: Int,
        activityIds: List<Int>
    ): List<MissionActivityEntity> {
        return missionActivityDao.getActivitiesFormIds(
            prefBSRepo.getUniqueUserIdentifier(),
            missionId,
            activityIds
        )
    }


}
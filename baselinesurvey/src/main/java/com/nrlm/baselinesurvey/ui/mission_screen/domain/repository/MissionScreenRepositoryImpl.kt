package com.nrlm.baselinesurvey.ui.mission_screen.domain.repository

import androidx.lifecycle.LiveData
import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
import com.nrlm.baselinesurvey.database.dao.ActivityTaskDao
import com.nrlm.baselinesurvey.database.dao.MissionActivityDao
import com.nrlm.baselinesurvey.database.dao.MissionEntityDao
import com.nrlm.baselinesurvey.database.entity.MissionEntity
import javax.inject.Inject

class MissionScreenRepositoryImpl @Inject constructor(
    private val missionEntityDao: MissionEntityDao,
    private val missionActivityDao: MissionActivityDao,
    private val taskDao: ActivityTaskDao,
    private val prefBSRepo: PrefBSRepo
) : MissionScreenRepository {
    override suspend fun getMissionsFromDB(): List<MissionEntity> {
        return missionEntityDao.getMissions(getBaseLineUserId())
    }

    /*override fun getLanguageId(): String {
        return "en"
    }*/

    override fun getTotalTaskCountForMission(missionId: Int): Int {
        return taskDao.getTaskCountForMission(userId = getBaseLineUserId(), missionId)
    }

    override fun getPendingTaskCountLiveForMission(missionId: Int): LiveData<Int> {
        return taskDao.getPendingTaskCountLiveForMission(userId = getBaseLineUserId(), missionId)
    }

    override fun getBaseLineUserId(): String {
        return prefBSRepo.getUniqueUserIdentifier()
    }

    override fun getPendingActivityCountForMissionLive(missionId: Int): LiveData<Int> {
        return missionActivityDao.getPendingTaskCountLiveForMission(
            userId = getBaseLineUserId(),
            missionId
        )
    }

    override fun getTotalActivityCountForMission(missionId: Int): Int {
        return missionActivityDao.getTotalActivityCountForMission(
            userId = getBaseLineUserId(),
            missionId
        )
    }

}
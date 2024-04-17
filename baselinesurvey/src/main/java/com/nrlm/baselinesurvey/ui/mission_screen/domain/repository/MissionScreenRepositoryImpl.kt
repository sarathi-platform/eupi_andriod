package com.nrlm.baselinesurvey.ui.mission_screen.domain.repository

import androidx.lifecycle.LiveData
import com.nrlm.baselinesurvey.database.dao.ActivityTaskDao
import com.nrlm.baselinesurvey.database.dao.MissionActivityDao
import com.nrlm.baselinesurvey.database.dao.MissionEntityDao
import com.nrlm.baselinesurvey.database.entity.MissionEntity
import javax.inject.Inject

class MissionScreenRepositoryImpl @Inject constructor(
    private val missionEntityDao: MissionEntityDao,
    private val missionActivityDao: MissionActivityDao,
    private val taskDao: ActivityTaskDao
) : MissionScreenRepository {
    override suspend fun getMissionsFromDB(): List<MissionEntity> {
        return missionEntityDao.getMissions()
    }

    /*override fun getLanguageId(): String {
        return "en"
    }*/

    override fun getTotalTaskCountForMission(missionId: Int): Int {
        return taskDao.getTaskCountForMission(missionId)
    }

    override fun getPendingTaskCountLiveForMission(missionId: Int): LiveData<Int> {
        return taskDao.getPendingTaskCountLiveForMission(missionId)
    }

    override fun getPendingActivityCountForMissionLive(missionId: Int): LiveData<Int> {
        return missionActivityDao.getPendingTaskCountLiveForMission(missionId)
    }

    override fun getTotalActivityCountForMission(missionId: Int): Int {
        return missionActivityDao.getTotalActivityCountForMission(missionId)
    }

}
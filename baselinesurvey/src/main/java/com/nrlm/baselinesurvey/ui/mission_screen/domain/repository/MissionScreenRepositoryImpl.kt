package com.nrlm.baselinesurvey.ui.mission_screen.domain.repository

import androidx.lifecycle.LiveData
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.ActivityTaskDao
import com.nrlm.baselinesurvey.database.dao.MissionActivityDao
import com.nrlm.baselinesurvey.database.dao.MissionEntityDao
import com.nrlm.baselinesurvey.database.entity.MissionEntity
import javax.inject.Inject

class MissionScreenRepositoryImpl @Inject constructor(
    private val missionEntityDao: MissionEntityDao,
    private val missionActivityDao: MissionActivityDao,
    private val taskDao: ActivityTaskDao,
    private val prefRepo: PrefRepo
) : MissionScreenRepository {
    override suspend fun getMissionsFromDB(): List<MissionEntity> {
        return missionEntityDao.getMissions(getUserId())
    }

    /*override fun getLanguageId(): String {
        return "en"
    }*/

    override fun getTotalTaskCountForMission(missionId: Int): Int {
        return taskDao.getTaskCountForMission(getUserId(), missionId)
    }

    override fun getPendingTaskCountLiveForMission(missionId: Int): LiveData<Int> {
        return taskDao.getPendingTaskCountLiveForMission(getUserId(), missionId)
    }

    override fun getUserId(): String {
        return prefRepo.getMobileNumber() ?: BLANK_STRING
    }

}
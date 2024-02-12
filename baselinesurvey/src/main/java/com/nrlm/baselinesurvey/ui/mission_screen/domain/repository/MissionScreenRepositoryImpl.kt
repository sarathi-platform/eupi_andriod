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
}
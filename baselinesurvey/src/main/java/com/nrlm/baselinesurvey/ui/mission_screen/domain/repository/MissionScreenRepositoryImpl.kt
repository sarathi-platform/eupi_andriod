package com.nrlm.baselinesurvey.ui.mission_screen.domain.repository

import com.nrlm.baselinesurvey.database.dao.MissionEntityDao
import com.nrlm.baselinesurvey.database.entity.MissionEntity
import javax.inject.Inject

class MissionScreenRepositoryImpl @Inject constructor(
    private val missionEntityDao: MissionEntityDao,
) : MissionScreenRepository {
    override suspend fun getMissions(): List<MissionEntity>? {
        return missionEntityDao.getAllMission()
    }

    override fun getLanguageId(): String {
        return "en"
    }
}
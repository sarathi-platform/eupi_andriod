package com.sarathi.missionactivitytask.domain.repository

import com.sarathi.dataloadingmangement.data.dao.MissionDao
import com.sarathi.dataloadingmangement.data.entities.Mission
import javax.inject.Inject

class GetMissionRepositoryImpl @Inject constructor(val missionDao: MissionDao) : BaseRepository(),
    IMissionRepository {

    override suspend fun getAllActiveMission(): List<Mission> = missionDao.getMissions("99")

}

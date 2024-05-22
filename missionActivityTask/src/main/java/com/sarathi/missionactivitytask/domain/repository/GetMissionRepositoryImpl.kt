package com.sarathi.missionactivitytask.domain.repository

import com.sarathi.dataloadingmangement.data.dao.MissionDao
import com.sarathi.dataloadingmangement.data.entities.MissionEntity
import javax.inject.Inject

class GetMissionRepositoryImpl @Inject constructor(val missionDao: MissionDao) : BaseRepository(),
    IMissionRepository {

    override suspend fun getAllActiveMission(): List<MissionEntity> = missionDao.getMissions("99")

}

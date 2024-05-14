package com.sarathi.missionactivitytask.domain.repository

import com.sarathi.missionactivitytask.data.dao.MissionDao
import com.sarathi.missionactivitytask.data.entities.MissionEntity
import javax.inject.Inject

class GetMissionRepositoryImpl @Inject constructor(val missionDao: MissionDao) : BaseRepository(),
    IMissionRepository {

    override suspend fun getAllActiveMission(): List<MissionEntity> = missionDao.getMissions("")

}

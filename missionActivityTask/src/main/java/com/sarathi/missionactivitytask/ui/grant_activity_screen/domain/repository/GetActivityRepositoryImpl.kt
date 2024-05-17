package com.sarathi.missionactivitytask.ui.grant_activity_screen.domain.repository

import com.sarathi.missionactivitytask.data.dao.MissionActivityDao
import com.sarathi.missionactivitytask.data.entities.MissionActivityEntity
import com.sarathi.missionactivitytask.domain.repository.BaseRepository
import javax.inject.Inject


class GetActivityRepositoryImpl @Inject constructor(val activityDao: MissionActivityDao) :
    BaseRepository(),
    IActivityRepository {
    override suspend fun getActivity(): List<MissionActivityEntity> {
        return activityDao.getActivities("99", 1)
    }

}

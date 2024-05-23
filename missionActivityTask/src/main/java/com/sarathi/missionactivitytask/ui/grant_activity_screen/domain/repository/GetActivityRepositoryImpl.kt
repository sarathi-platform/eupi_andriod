package com.sarathi.missionactivitytask.ui.grant_activity_screen.domain.repository

import com.sarathi.dataloadingmangement.data.dao.ActivityDao
import com.sarathi.dataloadingmangement.data.entities.Activity
import com.sarathi.missionactivitytask.domain.repository.BaseRepository
import javax.inject.Inject


class GetActivityRepositoryImpl @Inject constructor(val activityDao: ActivityDao) :
    BaseRepository(),
    IActivityRepository {
    override suspend fun getActivity(): List<Activity> {
        return activityDao.getActivities("99", 1)
    }

}

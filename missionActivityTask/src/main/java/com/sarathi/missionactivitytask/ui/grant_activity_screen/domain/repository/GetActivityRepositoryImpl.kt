package com.sarathi.missionactivitytask.ui.grant_activity_screen.domain.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.ActivityDao
import com.sarathi.dataloadingmangement.model.uiModel.ActivityUiModel
import com.sarathi.missionactivitytask.domain.repository.BaseRepository
import javax.inject.Inject


class GetActivityRepositoryImpl @Inject constructor(
    val activityDao: ActivityDao,
    val coreSharedPrefs: CoreSharedPrefs
) :
    BaseRepository(),
    IActivityRepository {
    override suspend fun getActivity(): List<ActivityUiModel> {
        return activityDao.getActivities(coreSharedPrefs.getUniqueUserIdentifier(), "en")
    }

}

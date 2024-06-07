package com.sarathi.missionactivitytask.ui.grant_activity_screen.domain.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.ActivityDao
import com.sarathi.dataloadingmangement.data.dao.MissionDao
import com.sarathi.dataloadingmangement.model.SurveyStatusEnum
import com.sarathi.dataloadingmangement.model.uiModel.ActivityUiModel
import com.sarathi.missionactivitytask.domain.repository.BaseRepository
import javax.inject.Inject


class GetActivityRepositoryImpl @Inject constructor(
    val activityDao: ActivityDao,
    val missionDao: MissionDao,
    val coreSharedPrefs: CoreSharedPrefs
) :
    BaseRepository(),
    IActivityRepository {
    override suspend fun getActivity(missionId: Int): List<ActivityUiModel> {
        return activityDao.getActivities(
            coreSharedPrefs.getUniqueUserIdentifier(),
            coreSharedPrefs.getAppLanguage(),
            missionId
        )
    }

    override suspend fun isAllActivityCompleted(): Boolean {
        return activityDao.countActivityByStatus(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            statuses = listOf(SurveyStatusEnum.NOT_STARTED.name, SurveyStatusEnum.INPROGRESS.name)
        ) == 0
    }

    override suspend fun updateMissionStatus(missionId: Int, status: String) {
        missionDao.updateMissionStatus(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            missionId = missionId,
            status = status
        )
    }

}

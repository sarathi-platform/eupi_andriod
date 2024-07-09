package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.ActivityDao
import com.sarathi.dataloadingmangement.data.dao.MissionDao
import com.sarathi.dataloadingmangement.model.uiModel.ActivityFormUIModel
import com.sarathi.dataloadingmangement.model.uiModel.ActivityUiModel
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import javax.inject.Inject


class GetActivityRepositoryImpl @Inject constructor(
    val activityDao: ActivityDao,
    val missionDao: MissionDao,
    val coreSharedPrefs: CoreSharedPrefs
) :
    IActivityRepository {
    override suspend fun getActivity(missionId: Int): List<ActivityUiModel> {
        return activityDao.getActivities(
            coreSharedPrefs.getUniqueUserIdentifier(),
            coreSharedPrefs.getAppLanguage(),
            missionId
        )
    }

    override suspend fun isAllActivityCompleted(missionId: Int, activityId: Int): Boolean {
        return activityDao.countActivityByStatus(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            missionId = missionId,
            activityId = activityId,
            statuses = listOf(SurveyStatusEnum.NOT_STARTED.name, SurveyStatusEnum.INPROGRESS.name)
        ) == 0
    }

    override suspend fun isAllActivityCompleted(missionId: Int): Boolean {
        return activityDao.countActivityByStatus(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            missionId = missionId,
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

    override suspend fun getActiveForm(
        formType: String
    ): List<ActivityFormUIModel> {
        return activityDao.getActiveForm(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            languageCode = coreSharedPrefs.getSelectedLanguageCode(),
            formType = formType
        )

    }

}

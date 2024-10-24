package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.SurveyConfigEntityDao
import com.sarathi.dataloadingmangement.data.entities.SurveyConfigEntity
import javax.inject.Inject

class GetSurveyConfigFromDbRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val surveyConfigEntityDao: SurveyConfigEntityDao,
) : GetSurveyConfigFromDbRepository {

    override suspend fun getSurveyConfig(
        missionId: Int,
        activityId: Int,
        surveyId: Int,
    ): List<SurveyConfigEntity> {
        return surveyConfigEntityDao.getSurveyConfigForSurvey(
            missionId = missionId,
            activityId = activityId,
            surveyId = surveyId,
            uniqueUserIdentifier = coreSharedPrefs.getUniqueUserIdentifier()
        )
    }

    override suspend fun getSurveyConfig(
        missionId: Int,
        activityId: Int,
        surveyId: Int,
        formId: Int
    ): List<SurveyConfigEntity> {
        return surveyConfigEntityDao.getSurveyConfigForFormId(
            missionId = missionId,
            activityId = activityId,
            surveyId = surveyId,
            formId = formId,
            uniqueUserIdentifier = coreSharedPrefs.getUniqueUserIdentifier()
        )
    }

}
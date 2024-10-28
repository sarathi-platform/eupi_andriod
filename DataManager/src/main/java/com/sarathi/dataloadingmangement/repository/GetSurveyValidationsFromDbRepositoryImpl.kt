package com.sarathi.dataloadingmangement.repository

import com.nudge.core.model.response.SurveyValidations
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.SurveyEntityDao
import javax.inject.Inject

class GetSurveyValidationsFromDbRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val surveyEntityDao: SurveyEntityDao
) : GetSurveyValidationsFromDbRepository {
    override fun getSurveyValidationsForSurvey(surveyId: Int): List<SurveyValidations>? {
        return surveyEntityDao.getSurveyDetailForLanguage(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            surveyId = surveyId
        )?.validations
    }


}
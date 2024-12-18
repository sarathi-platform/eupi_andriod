package com.sarathi.dataloadingmangement.repository

import com.nudge.core.model.response.SurveyValidations

interface GetSurveyValidationsFromDbRepository {

    fun getSurveyValidationsForSurvey(surveyId: Int): List<SurveyValidations>?

}
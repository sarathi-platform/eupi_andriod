package com.nrlm.baselinesurvey.ui.start_screen.domain.repository

import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity

interface StartScreenRepository {

    suspend fun getSurveyeeDetails(didiId: Int): SurveyeeEntity

    suspend fun saveImageLocalPathForSurveyee(surveyeeEntity: SurveyeeEntity, finalPathWithCoordinates: String)

}
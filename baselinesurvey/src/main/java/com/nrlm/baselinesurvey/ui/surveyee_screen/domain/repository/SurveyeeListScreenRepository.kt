package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository

import com.nrlm.baselinesurvey.database.entity.DidiEntity

interface SurveyeeListScreenRepository {

    suspend fun getSurveyeeList(): List<DidiEntity>

}
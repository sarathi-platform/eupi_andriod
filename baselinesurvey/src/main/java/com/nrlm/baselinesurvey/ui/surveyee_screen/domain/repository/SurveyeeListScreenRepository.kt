package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository

import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import kotlinx.coroutines.flow.Flow

interface SurveyeeListScreenRepository {

    fun getSurveyeeList(): Flow<List<SurveyeeEntity>>

}
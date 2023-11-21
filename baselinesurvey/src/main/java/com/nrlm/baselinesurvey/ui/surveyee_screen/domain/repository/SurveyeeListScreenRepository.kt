package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository

import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import kotlinx.coroutines.flow.Flow

interface SurveyeeListScreenRepository {

    suspend fun getSurveyeeList(): List<SurveyeeEntity>

    suspend fun getSurveyeeListFromNetwork(): Boolean

    suspend fun moveSurveyeesToThisWeek(didiIdList: Set<Int>, moveDidisToNextWeek: Boolean)

    suspend fun moveSurveyeeToThisWeek(didiId: Int, moveDidisToNextWeek: Boolean)

}
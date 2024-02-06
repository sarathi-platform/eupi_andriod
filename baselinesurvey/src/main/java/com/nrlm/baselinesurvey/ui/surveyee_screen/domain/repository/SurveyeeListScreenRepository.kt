package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository

import com.nrlm.baselinesurvey.database.entity.MissionEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity

interface SurveyeeListScreenRepository {

    suspend fun getSurveyeeList(missionId: Int, activityId: Int): List<SurveyeeEntity>

    suspend fun getSurveyeeListFromNetwork(): Boolean

    suspend fun moveSurveyeesToThisWeek(didiIdList: Set<Int>, moveDidisToNextWeek: Boolean)

    suspend fun moveSurveyeeToThisWeek(didiId: Int, moveDidisToNextWeek: Boolean)
    suspend fun getMission(missionId: Int): MissionEntity

}
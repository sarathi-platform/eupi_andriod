package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.SurveyeeListScreenRepository

class MoveSurveyeeToThisWeekUseCase (
    private val repository: SurveyeeListScreenRepository
) {

    suspend fun moveSurveyeesToThisWeek(didiIdList: Set<Int>, moveDidisToNextWeek: Boolean) {
        repository.moveSurveyeesToThisWeek(didiIdList, moveDidisToNextWeek)
    }

    suspend fun moveSurveyeeToThisWeek(didiId: Int, moveDidisToNextWeek: Boolean) {
        repository.moveSurveyeeToThisWeek(didiId, moveDidisToNextWeek)
    }

}
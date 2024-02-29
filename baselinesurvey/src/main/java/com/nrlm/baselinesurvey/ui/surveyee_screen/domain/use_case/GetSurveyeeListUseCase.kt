package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.SurveyeeListScreenRepository

class GetSurveyeeListUseCase(
    private val repository: SurveyeeListScreenRepository
) {
    suspend operator fun invoke(missionId: Int, activityName: String): List<SurveyeeEntity> {
        return repository.getSurveyeeList(missionId, activityName)
    }

    suspend fun getSurveyeeListFromNetwork(): Boolean {
        return repository.getSurveyeeListFromNetwork()
    }

}

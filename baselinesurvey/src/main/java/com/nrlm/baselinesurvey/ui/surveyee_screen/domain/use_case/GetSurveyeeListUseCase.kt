package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.SurveyeeListScreenRepository
import kotlinx.coroutines.flow.Flow

class GetSurveyeeListUseCase(
    private val repository: SurveyeeListScreenRepository
) {
    suspend operator fun invoke(): List<SurveyeeEntity> {
        return repository.getSurveyeeList()
    }

    suspend fun getSurveyeeListFromNetwork(): Boolean {
        return repository.getSurveyeeListFromNetwork()
    }
}

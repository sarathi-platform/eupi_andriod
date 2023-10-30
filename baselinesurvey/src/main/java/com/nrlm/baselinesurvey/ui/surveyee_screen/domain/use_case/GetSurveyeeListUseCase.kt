package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import com.nrlm.baselinesurvey.database.entity.DidiEntity
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.SurveyeeListScreenRepository

class GetSurveyeeListUseCase(
    private val repository: SurveyeeListScreenRepository
) {
    suspend operator fun invoke(): List<DidiEntity> {
        return repository.getSurveyeeList()
    }
}

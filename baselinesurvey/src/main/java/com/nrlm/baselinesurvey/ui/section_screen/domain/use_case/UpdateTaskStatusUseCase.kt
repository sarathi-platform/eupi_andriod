package com.nrlm.baselinesurvey.ui.section_screen.domain.use_case

import com.nrlm.baselinesurvey.ui.section_screen.domain.repository.SectionListScreenRepository
import com.nrlm.baselinesurvey.utils.states.SurveyState

class UpdateTaskStatusUseCase(private val repository: SectionListScreenRepository) {

    suspend operator fun invoke(didiId: Int, surveyState: SurveyState) {
        repository.updateSubjectStatus(didiId = didiId, surveyState = surveyState)
    }

}
package com.nrlm.baselinesurvey.ui.common_components.common_domain.common_use_case

import com.nrlm.baselinesurvey.ui.common_components.common_domain.commo_repository.SurveyStateRepository
import com.nrlm.baselinesurvey.utils.states.SurveyState

class UpdateSurveyStateUserCase (private val repository: SurveyStateRepository) {

    suspend fun invoke(didiId: Int, surveyState: SurveyState) {
        repository.updateSurveyState(didiId, surveyState)
    }

}
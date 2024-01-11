package com.nrlm.baselinesurvey.ui.common_components.common_events

import com.nrlm.baselinesurvey.ui.start_screen.presentation.StartSurveyScreenEvents
import com.nrlm.baselinesurvey.utils.states.SurveyState

sealed class SurveyStateEvents {

    data class UpdateDidiSurveyStatus(val didiId: Int, val didiSurveyState: SurveyState): SurveyStateEvents()
}
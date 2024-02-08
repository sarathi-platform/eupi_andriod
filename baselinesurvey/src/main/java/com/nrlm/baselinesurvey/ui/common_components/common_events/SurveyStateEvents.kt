package com.nrlm.baselinesurvey.ui.common_components.common_events

import com.nrlm.baselinesurvey.database.entity.DidiIntoEntity
import com.nrlm.baselinesurvey.utils.states.SurveyState

sealed class SurveyStateEvents {

    data class UpdateDidiSurveyStatus(
        val didiId: Int,
        val didiInfo: DidiIntoEntity,
        val didiSurveyState: SurveyState
    ) : SurveyStateEvents()
}
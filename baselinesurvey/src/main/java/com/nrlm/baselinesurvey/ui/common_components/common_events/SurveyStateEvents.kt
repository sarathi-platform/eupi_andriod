package com.nrlm.baselinesurvey.ui.common_components.common_events

import com.nrlm.baselinesurvey.database.entity.DidiInfoEntity
import com.nrlm.baselinesurvey.utils.states.SurveyState

sealed class SurveyStateEvents {

    data class UpdateDidiSurveyStatus(
        val didiId: Int,
        val didiInfo: DidiInfoEntity,
        val didiSurveyState: SurveyState
    ) : SurveyStateEvents()
}
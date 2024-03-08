package com.nrlm.baselinesurvey.ui.section_screen.presentation

import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nrlm.baselinesurvey.utils.states.SurveyState

sealed class SectionScreenEvent {

    data class UpdateSubjectStatus(val didiId: Int, val surveyState: SurveyState) :
        SectionScreenEvent()

    data class UpdateTaskStatus(val didiId: Int, val surveyState: SectionStatus) :
        SectionScreenEvent()

}
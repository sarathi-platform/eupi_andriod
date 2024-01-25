package com.nrlm.baselinesurvey.ui.surveyee_screen.presentation

import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.utils.states.FilterListState

sealed class SurveyeeListScreenActions {
    data class CheckBoxClicked(val isChecked: Boolean, val surveyeeEntity: SurveyeeEntity) :
        SurveyeeListScreenActions()

    data class IsFilterApplied(val isFilterAppliedState: FilterListState) :
        SurveyeeListScreenActions()
}

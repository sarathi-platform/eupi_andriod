package com.nrlm.baselinesurvey.ui.surveyee_screen.presentation

sealed class SurveyeeListEvents {
    data class OnCheckedStatus(val id:Int,val isChecked: Boolean) : SurveyeeListEvents()
    data class CancelAllSelection(val isFilterApplied: Boolean): SurveyeeListEvents()
}

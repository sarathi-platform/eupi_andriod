package com.nrlm.baselinesurvey.ui.surveyee_screen.presentation

sealed class SurveyeeListEvents {
    data class OnCheckedStatus(val id:Int,val isChecked: Boolean) : SurveyeeListEvents()
    data class CancelAllSelection(val isFilterApplied: Boolean): SurveyeeListEvents()
    data class MoveDidisThisWeek(val didiIdList: Set<Int>, val moveDidisToNextWeek: Boolean)
    data class MoveDidiToThisWeek(val didiId: Int, val moveDidisToNextWeek: Boolean)
}

package com.nrlm.baselinesurvey.ui.surveyee_screen.presentation

import com.nrlm.baselinesurvey.utils.states.SectionStatus

sealed class SurveyeeListEvents {
    data class OnCheckedStatus(val id: Int, val isChecked: Boolean) : SurveyeeListEvents()
    data class CancelAllSelection(val isFilterApplied: Boolean) : SurveyeeListEvents()
    data class MoveDidisThisWeek(val didiIdList: Set<Int>, val moveDidisToNextWeek: Boolean)
    data class MoveDidiToThisWeek(val didiId: Int, val moveDidisToNextWeek: Boolean)
    data class UpdateActivityStatus(
        val missionId: Int,
        val activityId: Int,
        val status: SectionStatus
    )

    data class UpdateActivityAllTask(val activityId: Int, val isAllTask: Boolean)

}

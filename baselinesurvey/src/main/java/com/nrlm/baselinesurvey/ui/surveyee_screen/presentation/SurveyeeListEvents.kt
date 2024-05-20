package com.nrlm.baselinesurvey.ui.surveyee_screen.presentation

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nrlm.baselinesurvey.utils.states.SurveyState

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

    data class UpdateSurveyeeStatusForUi(
        val surveyeeId: Int,
        val key: String = BLANK_STRING,
        val isFilterApplied: Boolean,
        val state: SurveyState
    ) : SurveyeeListEvents()

    data class UpdateActivityAllTask(val activityId: Int, val isAllTask: Boolean)

}

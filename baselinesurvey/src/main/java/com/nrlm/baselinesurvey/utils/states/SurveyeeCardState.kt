package com.nrlm.baselinesurvey.utils.states

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nudge.core.utils.SubjectStatus

data class SurveyeeCardState(
//    val surveyee: Surveyee
    val surveyeeDetails: SurveyeeEntity,
    val imagePath: String = BLANK_STRING,
    val subtitle: String = BLANK_STRING,
    val address: String = BLANK_STRING,
    val surveyState: SurveyState,
    val activityName: String,
    val isCohortName: Boolean = false,
    var isChecked: MutableState<Boolean> = mutableStateOf(false),
    val isActive: Int = SubjectStatus.SUBJECT_ACTIVE.ordinal
)
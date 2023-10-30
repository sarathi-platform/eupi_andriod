package com.nrlm.baselinesurvey.utils

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.database.entity.DidiEntity

data class SurveyeeCardState(
//    val surveyee: Surveyee
    val didiDetails: DidiEntity,
    val imagePath: String = BLANK_STRING,
    val subtitle: String = BLANK_STRING,
    val address: String = BLANK_STRING,
    val surveyState: SurveyState,

)
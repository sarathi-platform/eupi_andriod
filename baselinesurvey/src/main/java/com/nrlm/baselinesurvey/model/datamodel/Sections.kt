package com.nrlm.baselinesurvey.model.datamodel

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.NO_SECTION

data class Sections(
    val sectionId: Int = 0,
    val sectionName: String = NO_SECTION,
    val sectionOrder: Int = 1,
    val sectionDetails: String = BLANK_STRING,
//    val sectionIcon: String = BLANK_STRING,
    val sectionIcon: Int = 0,
    val questionList: List<QuestionEntity?>? = null
)
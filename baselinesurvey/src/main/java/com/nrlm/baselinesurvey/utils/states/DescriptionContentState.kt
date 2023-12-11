package com.nrlm.baselinesurvey.utils.states

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.utils.DescriptionContentType

data class DescriptionContentState(
    val descriptionContentType: List<DescriptionContentType> = emptyList(),
    val textTypeDescriptionContent: String = BLANK_STRING,
    val imageTypeDescriptionContent: String = BLANK_STRING,
    val videoTypeDescriptionContent: String = BLANK_STRING
)

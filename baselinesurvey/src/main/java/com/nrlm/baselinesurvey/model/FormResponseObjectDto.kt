package com.nrlm.baselinesurvey.model

import com.nrlm.baselinesurvey.BLANK_STRING

data class FormResponseObjectDto(
    var referenceId: String = BLANK_STRING,
    var questionId: Int = -1,
    var questionTag: String = BLANK_STRING,
    var memberDetailsMap: Map<Int, String> = emptyMap(),
    var selectedValueId: Map<Int, List<Int>> = emptyMap()
)

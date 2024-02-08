package com.nrlm.baselinesurvey.model

import com.nrlm.baselinesurvey.BLANK_STRING

data class FormResponseObjectDto(
    var referenceId: String = BLANK_STRING,
    var memberDetailsMap: Map<Int, String> = emptyMap()
)

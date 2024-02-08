package com.nrlm.baselinesurvey.model

import com.nrlm.baselinesurvey.BLANK_STRING

data class HouseholdMemberDto(
    var referenceId: String = BLANK_STRING,
    var memberDetailsMap: Map<Int, String> = emptyMap()
)

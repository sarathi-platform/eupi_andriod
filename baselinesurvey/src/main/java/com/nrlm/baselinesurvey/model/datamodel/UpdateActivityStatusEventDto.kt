package com.nrlm.baselinesurvey.model.datamodel

data class UpdateActivityStatusEventDto(
    val missionId: Int,
    val activityId: Int,
    val actualStartDate: String,
    val completedDate: String,
    val referenceType: String,
    val status: String
)

package com.nrlm.baselinesurvey.model.datamodel

data class UpdateTaskStatusEventDto(
    val missionId: Int,
    val activityId: Int,
    val taskId: Int,
    val subjectId: Int,
    val subjectType: String,
    val referenceType: String,
    val status: String
)

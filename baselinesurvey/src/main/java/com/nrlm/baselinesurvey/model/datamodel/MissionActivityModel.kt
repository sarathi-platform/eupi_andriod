package com.nrlm.baselinesurvey.model.datamodel

data class MissionActivityModel(
    val activityId: Int,
    val activityName: String,
    val activityType: String,
    val activityTypeId: Int,
    val doer: String,
    val endDate: String,
    val reviewer: String,
    val startDate: String,
    val subject: String,
    val tasks: List<MissionTaskModel>
)
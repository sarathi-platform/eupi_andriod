package com.nrlm.baselinesurvey.model.datamodel

import com.google.gson.annotations.SerializedName

data class MissionActivityModel(
    @SerializedName("id")
    val activityId: Int,
    @SerializedName("name")
    val activityName: String,
    val activityType: String,
    val activityTypeId: Int,
    val doer: String,
    val endDate: String,
    val reviewer: String,
    val startDate: String,
    val subject: String,
    val tasks: List<MissionTaskModel>,
    val language: String?,
    @SerializedName("activityStatus")
    val status: String?
)
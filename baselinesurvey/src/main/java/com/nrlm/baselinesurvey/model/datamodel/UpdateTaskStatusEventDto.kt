package com.nrlm.baselinesurvey.model.datamodel

import com.google.gson.annotations.SerializedName

data class UpdateTaskStatusEventDto(
    @SerializedName("missionId")
    val missionId: Int,

    @SerializedName("activityId")
    val activityId: Int,

    @SerializedName("taskId")
    val taskId: Int,

    @SerializedName("subjectId")
    val subjectId: Int,

    @SerializedName("subjectType")
    val subjectType: String,

    @SerializedName("referenceType")
    val referenceType: String,

    @SerializedName("actualStartDate")
    val actualStartDate: String,

    @SerializedName("actualCompletedDate")
    val actualCompletedDate: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("localTaskId")
    val localTaskId: String,
)

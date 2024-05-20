package com.nrlm.baselinesurvey.model.datamodel

import com.google.gson.annotations.SerializedName

data class UpdateActivityStatusEventDto(
    @SerializedName("missionId")
    val missionId: Int,
    @SerializedName("activityId")
    val activityId: Int,
    @SerializedName("actualStartDate")
    val actualStartDate: String,
    @SerializedName("completedDate")
    val completedDate: String,
    @SerializedName("referenceType")
    val referenceType: String,
    @SerializedName("status")
    val status: String
)

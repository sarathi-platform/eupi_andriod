package com.sarathi.dataloadingmangement.model.events

import com.google.gson.annotations.SerializedName

data class UpdateMissionStatusEventDto(
    @SerializedName("missionId")
    val missionId: Int,
    @SerializedName("actualStartDate")
    val actualStartDate: String,
    @SerializedName("completedDate")
    val completedDate: String,
    @SerializedName("referenceType")
    val referenceType: String,
    @SerializedName("status")
    val status: String
)
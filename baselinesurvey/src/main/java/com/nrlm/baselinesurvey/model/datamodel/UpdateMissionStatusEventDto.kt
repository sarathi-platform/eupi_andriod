package com.nrlm.baselinesurvey.model.datamodel

import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.utils.states.SectionStatus

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
    val status: SectionStatus
)
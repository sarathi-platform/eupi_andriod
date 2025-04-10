package com.nrlm.baselinesurvey.model.response

import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.model.datamodel.MissionActivityModel

data class MissionResponseModel(
    val activities: List<MissionActivityModel>,
    val endDate: String,
    @SerializedName("id")
    val missionId: Int,
    @SerializedName("name")
    val missionName: String,
    val startDate: String,
    val language: String?,
    val missionStatus: String?
)
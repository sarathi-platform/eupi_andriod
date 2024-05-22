package com.sarathi.dataloadingmangement.response

import com.google.gson.annotations.SerializedName

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
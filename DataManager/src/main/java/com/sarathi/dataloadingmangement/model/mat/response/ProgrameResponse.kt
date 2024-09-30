package com.sarathi.dataloadingmangement.model.mat.response


import com.google.gson.annotations.SerializedName

data class ProgrameResponse(
    @SerializedName("actualStartDate")
    val actualStartDate: String?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("missions")
    val missions: List<MissionResponse>,
    @SerializedName("name")
    val name: String,
    @SerializedName("startDate")
    val startDate: String?
)
package com.sarathi.dataloadingmangement.model.mat.response


import com.google.gson.annotations.SerializedName

data class MissionResponse(
    @SerializedName("activities")
    val activities: List<ActivityResponse>,
    @SerializedName("actualEndDate")
    val actualEndDate: String?,
    @SerializedName("actualStartDate")
    val actualStartDate: String?,
    @SerializedName("conditions")
    val conditions: List<Any>,
    @SerializedName("endOffset")
    val endOffset: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("languages")
    val languages: List<Language>,
    @SerializedName("missionConfig")
    val missionConfig: MissionConfig?,
    @SerializedName("missionStatus")
    val missionStatus: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("startOffset")
    val startOffset: Int,
    @SerializedName("validations")
    val validations: List<Any>
)
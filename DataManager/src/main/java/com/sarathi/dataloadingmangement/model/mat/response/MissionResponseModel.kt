package com.sarathi.dataloadingmangement.model.mat.response

import com.google.gson.annotations.SerializedName

data class MissionResponseModel(
    @SerializedName("activities")
    val activities: List<ActivityResponse>,
    val endDate: String,
    @SerializedName("id")
    val missionId: Int,
    @SerializedName("startOffset")
    val startOffset: Int,
    @SerializedName("endOffset")
    val endOffSet: Int,
    @SerializedName("languages")
    val languages: List<Language>,
    @SerializedName("missionStatus")
    val missionStatus: String?,
    @SerializedName("name")
    val name: String?
)


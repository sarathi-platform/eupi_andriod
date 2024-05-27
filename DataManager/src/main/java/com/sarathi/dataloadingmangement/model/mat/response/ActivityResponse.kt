package com.sarathi.dataloadingmangement.model.mat.response


import com.google.gson.annotations.SerializedName

data class ActivityResponse(
    @SerializedName("activityConfig")
    val activityConfig: ActivityConfig,
    @SerializedName("activityStatus")
    val activityStatus: String,
    @SerializedName("actualEndDate")
    val actualEndDate: String,
    @SerializedName("actualStartDate")
    val actualStartDate: String,
    @SerializedName("endOffset")
    val endOffset: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("startOffset")
    val startOffset: Int,
    @SerializedName("tasks")
    val taskResponses: List<TaskResponse>
)
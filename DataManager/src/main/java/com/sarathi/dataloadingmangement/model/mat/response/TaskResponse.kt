package com.sarathi.dataloadingmangement.model.mat.response


import com.google.gson.annotations.SerializedName

data class TaskResponse(
    @SerializedName("actualEndDate")
    val actualEndDate: String?,
    @SerializedName("actualStartDate")
    val actualStartDate: String?,
    @SerializedName("endOffset")
    val endOffset: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("startOffset")
    val startOffset: Int,
    @SerializedName("subjectId")
    val subjectId: Int,
    @SerializedName("taskData")
    val taskData: List<TaskData>?,
    @SerializedName("taskStatus")
    val taskStatus: String
)
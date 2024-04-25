package com.nrlm.baselinesurvey.model.datamodel

import com.google.gson.annotations.SerializedName

data class MissionTaskModel(
    @SerializedName("id")
    val id: Int?,
    val taskDate: String,
    val taskId: Int,
    @SerializedName("name")
    val taskName: String,
    val language: String?,
    @SerializedName("taskStatus")
    val status: String?,
    val subjectId: Int?,
    val completedDate: String?,
    @SerializedName("localTaskId")
    val localTaskId: String?

)
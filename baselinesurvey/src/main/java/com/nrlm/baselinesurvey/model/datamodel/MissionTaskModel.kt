package com.nrlm.baselinesurvey.model.datamodel

import com.google.gson.annotations.SerializedName

data class MissionTaskModel(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("taskDate")
    val taskDate: String,
    @SerializedName("taskId")
    val taskId: Int,
    @SerializedName("name")
    val taskName: String,
    @SerializedName("language")
    val language: String?,
    @SerializedName("taskStatus")
    val taskStatus: String?,
    @SerializedName("subjectId")
    val subjectId: Int?,
    @SerializedName("completedDate")
    val completedDate: String?,
    @SerializedName("localTaskId")
    val localTaskId: String?,
    @SerializedName("status")
    val status: Int?,

)
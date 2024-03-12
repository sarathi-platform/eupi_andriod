package com.nrlm.baselinesurvey.model.datamodel

import com.google.gson.annotations.SerializedName

data class MissionTaskModel(
    @SerializedName("id")
    val didiId: Int?,
    val taskDate: String,
    val taskId: Int,
    @SerializedName("name")
    val taskName: String,
    val language: String,
    val status: String,
    val subjectId: Int?
)
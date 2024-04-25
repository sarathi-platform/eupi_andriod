package com.nrlm.baselinesurvey.model.datamodel

import com.google.gson.annotations.SerializedName


data class SectionStatusUpdateEventDto(
    @SerializedName("surveyId")
    val surveyId: Int,

    @SerializedName("sectionId")
    val sectionId: Int,

    @SerializedName("didiId")
    val didiId: Int,

    @SerializedName("sectionStatus")
    val sectionStatus: String,
    @Transient
    @SerializedName("localTaskId")
    val localTaskId: String,
)

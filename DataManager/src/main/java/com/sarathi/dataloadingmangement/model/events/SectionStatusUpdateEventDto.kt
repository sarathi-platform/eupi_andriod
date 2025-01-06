package com.sarathi.dataloadingmangement.model.events

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
    @SerializedName("localTaskId")
    val localTaskId: String,

    @SerializedName("subjectType")
    val subjectType: String
)

package com.sarathi.dataloadingmangement.model.events

import com.google.gson.annotations.SerializedName

data class SaveFormAnswerEventDto(
    @SerializedName("generatedDate")
    val generatedDate: String,
    @SerializedName("formType")
    val formType: String,
    @SerializedName("subjectId")
    val subjectId: Int,
    @SerializedName("subjectType")
    val subjectType: String,
    @SerializedName("surveyId")
    val surveyId: Int,
    @SerializedName("taskId")
    val taskId: Int,
    @SerializedName("activityId")
    val activityId: Int,
    @SerializedName("formGenerated")
    val formGenerated: Boolean,
    @SerializedName("localReferenceId")
    val localReferenceId: String,

    )

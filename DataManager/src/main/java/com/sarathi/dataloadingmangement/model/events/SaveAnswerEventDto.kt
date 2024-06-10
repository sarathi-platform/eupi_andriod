package com.sarathi.dataloadingmangement.model.events


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SaveAnswerEventDto(
    @SerializedName("dateCreated")
    @Expose
    val dateCreated: Long,
    @SerializedName("languageId")
    @Expose
    val languageId: String,
    @SerializedName("localReferenceId")
    @Expose
    val referenceId: String,
    @SerializedName("sectionId")
    @Expose
    val sectionId: Int,
    @SerializedName("question")
    @Expose
    val question: SaveAnswerEventQuestionItemDto,
    @SerializedName("subjectId")
    @Expose
    val subjectId: Int,
    @SerializedName("subjectType")
    @Expose
    val subjectType: String,
    @SerializedName("surveyId")
    @Expose
    val surveyId: Int,
    @SerializedName("grantId")
    @Expose
    val grantId: Int,
    @SerializedName("grantType")
    @Expose
    val grantType: String,
    @SerializedName("localTaskId")
    @Expose
    val localTaskId: String,

    )
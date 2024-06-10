package com.sarathi.dataloadingmangement.model.events


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SaveAnswerMoneyJorunalEventDto(
    @SerializedName("dateCreated")
    @Expose
    val dateCreated: Long,
    @SerializedName("languageId")
    @Expose
    val languageId: String,
    @SerializedName("referenceId")
    @Expose
    val referenceId: String,
    @SerializedName("sectionId")
    @Expose
    val sectionId: Int,
    @SerializedName("questions")
    @Expose
    val question: List<SaveAnswerEventQuestionItemDto>,
    @SerializedName("subjectId")
    @Expose
    val subjectId: Int,
    @SerializedName("subjectType")
    @Expose
    val subjectType: String,
    @SerializedName("surveyId")
    @Expose
    val surveyId: Int,
    @SerializedName("localTaskId")
    @Expose
    val localTaskId: String,

    )
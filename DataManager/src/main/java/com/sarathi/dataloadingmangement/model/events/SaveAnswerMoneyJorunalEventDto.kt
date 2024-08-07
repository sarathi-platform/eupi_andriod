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
    @SerializedName("transactionId")
    @Expose
    val transactionId: String,
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
    @SerializedName("grantId")
    @Expose
    val grantId: Int,
    @SerializedName("taskId")
    @Expose
    val taskId: Int,
    @SerializedName("grantType")
    @Expose
    val grantType: String,
    @SerializedName("localTaskId")
    @Expose
    val localTaskId: String,

    @SerializedName("tagId")
    @Expose
    val tagId: List<Int>,

    )
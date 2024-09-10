package com.sarathi.dataloadingmangement.model.events


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

interface BaseSaveAnswerEventDto {

    val dateCreated: Long

    val languageId: String

    val referenceId: String

    val sectionId: Int

    val question: SaveAnswerEventQuestionItemDto

    val subjectId: Int

    val subjectType: String

    val surveyId: Int

    val grantId: Int

    val taskId: Int

    val grantType: String

    val localTaskId: String

    val activityId: Int
}

data class SaveAnswerEventDto(
    @SerializedName("dateCreated")
    @Expose
    override val dateCreated: Long,
    @SerializedName("languageId")
    @Expose
    override val languageId: String,
    @SerializedName("localReferenceId")
    @Expose
    override val referenceId: String,
    @SerializedName("sectionId")
    @Expose
    override val sectionId: Int,
    @SerializedName("question")
    @Expose
    override val question: SaveAnswerEventQuestionItemDto,
    @SerializedName("subjectId")
    @Expose
    override val subjectId: Int,
    @SerializedName("subjectType")
    @Expose
    override val subjectType: String,
    @SerializedName("surveyId")
    @Expose
    override val surveyId: Int,
    @SerializedName("grantId")
    @Expose
    override val grantId: Int,
    @SerializedName("taskId")
    @Expose
    override val taskId: Int,
    @SerializedName("grantType")
    @Expose
    override val grantType: String,
    @SerializedName("localTaskId")
    @Expose
    override val localTaskId: String,
    @SerializedName("activityId")
    @Expose
    override val activityId: Int,
) : BaseSaveAnswerEventDto

data class TrainingTypeActivitySaveAnswerEventDto(
    @SerializedName("dateCreated")
    @Expose
    override val dateCreated: Long,
    @SerializedName("languageId")
    @Expose
    override val languageId: String,
    @SerializedName("localReferenceId")
    @Expose
    override val referenceId: String,
    @SerializedName("sectionId")
    @Expose
    override val sectionId: Int,
    @SerializedName("question")
    @Expose
    override val question: SaveAnswerEventQuestionItemDto,
    @SerializedName("subjectId")
    @Expose
    override val subjectId: Int,
    @SerializedName("subjectType")
    @Expose
    override val subjectType: String,
    @SerializedName("surveyId")
    @Expose
    override val surveyId: Int,
    @SerializedName("grantId")
    @Expose
    override val grantId: Int,
    @SerializedName("taskId")
    @Expose
    override val taskId: Int,
    @SerializedName("grantType")
    @Expose
    override val grantType: String,
    @SerializedName("localTaskId")
    @Expose
    override val localTaskId: String,
    @SerializedName("activityId")
    @Expose
    override val activityId: Int,
    @SerializedName("activityReferenceId")
    @Expose
    val activityReferenceId: Int?,
    @SerializedName("activityReferenceType")
    @Expose
    val activityReferenceType: String?
) : BaseSaveAnswerEventDto

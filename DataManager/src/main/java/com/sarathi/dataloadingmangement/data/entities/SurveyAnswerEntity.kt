package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.DEFAULT_ID
import com.sarathi.dataloadingmangement.ANSWER_TABLE
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.data.converters.QuestionsOptionsConverter
import com.sarathi.dataloadingmangement.model.survey.response.QuestionAnswerResponseModel
import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel


@Entity(tableName = ANSWER_TABLE)
data class SurveyAnswerEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int,
    var userId: String? = BLANK_STRING,
    @SerializedName("questionId")
    @Expose
    @ColumnInfo(name = "questionId")
    var questionId: Int,

    @SerializedName("subjectId")
    @Expose
    @ColumnInfo(name = "subjectId")
    var subjectId: Int,

    @SerializedName("surveyId")
    @Expose
    @ColumnInfo(name = "surveyId")
    var surveyId: Int,

    @SerializedName("sectionId")
    @Expose
    @ColumnInfo(name = "sectionId")
    var sectionId: Int,

    @SerializedName("referenceId")
    @Expose
    @ColumnInfo(name = "referenceId")
    var referenceId: String,

    @SerializedName("questionType")
    @Expose
    @ColumnInfo(name = "questionType")
    var questionType: String,

    @SerializedName("taskId")
    @Expose
    @ColumnInfo(name = "taskId")
    var taskId: Int,
    @SerializedName("grantId")
    @Expose
    @ColumnInfo(name = "grantId")
    var grantId: Int,

    @SerializedName("grantType")
    @Expose
    @ColumnInfo(name = "grantType")
    var grantType: String,
    @SerializedName("answerValue")
    @Expose
    @ColumnInfo(name = "answerValue")
    var answerValue: String,

    @SerializedName("optionItems")
    @Expose
    @ColumnInfo("optionItems")
    @TypeConverters(QuestionsOptionsConverter::class)
    var optionItems: List<OptionsUiModel>,

    @SerializedName("questionSummary")
    @Expose
    val questionSummary: String? = BLANK_STRING,

    @SerializedName("needsToPost")
    @Expose
    @ColumnInfo(name = "needsToPost")
    var needsToPost: Boolean = true,
) {
    companion object {

        fun getSurveyAnswerEntity(
            question: QuestionUiModel,
            userId: String,
            referenceId: String,
            taskId: Int,
            subjectId: Int,
            grantId: Int,
            grantType: String
        ): SurveyAnswerEntity {
            return SurveyAnswerEntity(
                id = 0,
                userId = userId,
                questionId = question.questionId,
                surveyId = question.surveyId,
                sectionId = question.sectionId,
                referenceId = referenceId,
                questionType = question.type,
                taskId = taskId,
                answerValue = "",
                questionSummary = question.questionSummary,
                optionItems = question.options ?: listOf(),
                subjectId = subjectId,
                grantId = grantId,
                grantType = grantType
            )
        }

        fun getSurveyAnswerEntityFromQuestionAnswerResponse(
            questionAnswerResponse: QuestionAnswerResponseModel,
            userId: String,
            questionSummary: String,
            optionsUiModel: List<OptionsUiModel>

        ): SurveyAnswerEntity {
            return SurveyAnswerEntity(
                id = 0,
                userId = userId,
                questionId = questionAnswerResponse.question?.questionId ?: DEFAULT_ID,
                surveyId = questionAnswerResponse.surveyId,
                sectionId = questionAnswerResponse.sectionId.toInt(),
                referenceId = questionAnswerResponse.localReferenceId,
                questionType = questionAnswerResponse.question?.questionType ?: BLANK_STRING,
                taskId = questionAnswerResponse.taskId,
                answerValue = "",
                questionSummary = questionSummary,
                optionItems = optionsUiModel,
                subjectId = questionAnswerResponse.subjectId,
                grantId = questionAnswerResponse.grantId ?: DEFAULT_ID,
                grantType = questionAnswerResponse.grantType ?: BLANK_STRING
            )
        }
    }
}

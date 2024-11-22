package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING
import com.nudge.core.DEFAULT_ID
import com.sarathi.dataloadingmangement.QUESTION_TABLE
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
import com.sarathi.dataloadingmangement.model.survey.response.ContentListConverter
import com.sarathi.dataloadingmangement.model.survey.response.QuestionList


@Entity(tableName = QUESTION_TABLE)
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int,
    var userId: String? = BLANK_STRING,
    @SerializedName("questionId")
    @Expose
    @ColumnInfo(name = "questionId")
    var questionId: Int? = 0,

    @SerializedName("sectionId")
    @Expose
    @ColumnInfo(name = "sectionId")
    var sectionId: Int = 0,

    @SerializedName("surveyId")
    @Expose
    @ColumnInfo(name = "surveyId")
    val surveyId: Int,

    @SerializedName("questionImageUrl")
    @Expose
    @ColumnInfo(name = "questionImageUrl")
    var questionImageUrl: String? = BLANK_STRING,

    @SerializedName("type")
    @Expose
    @ColumnInfo(name = "type")
    var type: String? = BLANK_STRING,

    @SerializedName("gotoQuestionId")
    @Expose
    @ColumnInfo(name = "gotoQuestionId")
    var gotoQuestionId: Int? = 0,

    @SerializedName("order")
    @Expose
    @ColumnInfo(name = "order")
    var order: Int? = 0,

    @SerializedName("languageId")
    @Expose
    @ColumnInfo(name = "languageId")
    var languageId: String? = BLANK_STRING,
    @SerializedName("originalValue")
    @Expose
    @ColumnInfo(name = "originalValue")
    var originalValue: String? = BLANK_STRING,

    @SerializedName("isConditional")
    @Expose
    @ColumnInfo(name = "isConditional")
    var isConditional: Boolean = false,

    @SerializedName("isMandatory")
    @Expose
    @ColumnInfo(name = "isMandatory")
    var isMandatory: Boolean = false,

    @SerializedName("tag")
    @Expose
    @ColumnInfo(name = "formId")
    var formId: Int = 0,
    @TypeConverters(ContentListConverter::class)
    val contentEntities: List<ContentList>? = listOf(),

    @ColumnInfo(name = "parentQuestionId")
    val parentQuestionId: Int? = 0
) {
    companion object {
        fun getQuestionEntity(
            userId: String,
            sectionId: Int,
            surveyId: Int,
            isCondition: Boolean,
            parentId: Int,
            question: QuestionList
        ): QuestionEntity {
            return QuestionEntity(
                id = 0,
                userId = userId,
                questionId = question.questionId,
                sectionId = sectionId,
                surveyId = surveyId,
                gotoQuestionId = question.gotoQuestionId,
                order = question.order,
                type = question.type,
                isConditional = isCondition,
                contentEntities = question.contentList ?: listOf(),
                parentQuestionId = parentId,
                isMandatory = question.isMandatory,
                formId = question.formId ?: DEFAULT_ID,
                originalValue = question.originalValue
            )
        }

    }
}

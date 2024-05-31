package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.ANSWER_TABLE
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.data.converters.QuestionsOptionsConverter


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
    var referenceId: Int,

    @SerializedName("questionType")
    @Expose
    @ColumnInfo(name = "questionType")
    var questionType: String,

    @SerializedName("taskId")
    @Expose
    @ColumnInfo(name = "taskId")
    var taskId: Int,

    @SerializedName("answerValue")
    @Expose
    @ColumnInfo(name = "answerValue")
    var answerValue: String,

    @SerializedName("optionItems")
    @Expose
    @ColumnInfo("optionItems")
    @TypeConverters(QuestionsOptionsConverter::class)
    var optionItems: List<OptionItemEntity>,

    @SerializedName("questionSummary")
    @Expose
    val questionSummary: String? = BLANK_STRING,

    @SerializedName("needsToPost")
    @Expose
    @ColumnInfo(name = "needsToPost")
    var needsToPost: Boolean = true,
)

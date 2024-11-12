package com.sarathi.dataloadingmangement.model.uiModel

import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.data.converters.QuestionsOptionsConverter
import com.sarathi.dataloadingmangement.data.converters.TagConverter

data class SurveyAnswerFormSummaryUiModel(
    @SerializedName("subjectId")
    var subjectId: Int,
    @SerializedName("questionId")
    var questionId: Int,

    @SerializedName("surveyId")
    var surveyId: Int,

    @SerializedName("sectionId")

    var sectionId: Int,

    @SerializedName("referenceId")
    var referenceId: String,
    @SerializedName("questionType")
    var questionType: String,

    @SerializedName("taskId")
    var taskId: Int,
    @SerializedName("tagId")
    @TypeConverters(TagConverter::class)
    var tagId: List<Int> = emptyList(),
    @SerializedName("optionItems")
    @TypeConverters(QuestionsOptionsConverter::class)
    var optionItems: List<OptionsUiModel>,

    @SerializedName("questionSummary")
    @Expose
    val questionSummary: String? = BLANK_STRING,

    var isFormGenerated: Boolean,

    @SerializedName("formId")
    @Expose
    var formId: Int = 0

    )

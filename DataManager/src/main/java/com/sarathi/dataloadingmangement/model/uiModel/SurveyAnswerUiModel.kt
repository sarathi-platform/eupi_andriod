package com.sarathi.dataloadingmangement.model.uiModel

import androidx.room.TypeConverters
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.data.converters.QuestionsOptionsConverter
import com.sarathi.dataloadingmangement.data.converters.TagConverter


data class SurveyAnswerUiModel(

    var questionId: Int,


    var subjectId: Int,


    var surveyId: Int,


    var sectionId: Int,


    var referenceId: String,

    var questionType: String,


    var taskId: Int,

    @TypeConverters(TagConverter::class)
    var tagId: List<Int>,

    var grantId: Int,

    var grantType: String,


    @TypeConverters(QuestionsOptionsConverter::class)
    var optionItems: List<OptionsUiModel>,


    val questionSummary: String? = BLANK_STRING,


    )


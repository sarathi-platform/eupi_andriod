package com.sarathi.dataloadingmangement.model.uiModel

import androidx.room.TypeConverters
import com.nudge.core.BLANK_STRING
import com.sarathi.dataloadingmangement.data.converters.IntConverter
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
import com.sarathi.dataloadingmangement.model.survey.response.ContentListConverter

class QuestionUiEntity(


    var questionId: Int? = 0,


    var sectionId: Int = 0,


    val surveyId: Int,


    var questionImageUrl: String? = BLANK_STRING,


    var type: String? = BLANK_STRING,

    var paraphrase: String? = BLANK_STRING,

    var originalValue: String? = BLANK_STRING,

    var description: String? = BLANK_STRING,

    var languageCode: String? = BLANK_STRING,

    var gotoQuestionId: Int? = 0,
    var formId: Int? = 0,


    var order: Int? = 0,


    var isConditional: Boolean = false,


    var isMandatory: Boolean = false,

    @TypeConverters(IntConverter::class)
    var tag: List<Int> = listOf(),
    @TypeConverters(ContentListConverter::class)
    val contentEntities: List<ContentList> = listOf(),

    val parentQuestionId: Int? = 0
)

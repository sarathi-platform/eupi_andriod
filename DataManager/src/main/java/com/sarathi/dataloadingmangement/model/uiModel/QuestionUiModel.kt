package com.sarathi.dataloadingmangement.model.uiModel

import com.nudge.core.BLANK_STRING

class QuestionUiModel(
    var questionId: Int,
    var isMandatory: Boolean,
    var surveyId: Int,
    var sectionId: Int,
    var type: String,
    var languageId: String,
    var questionDisplay: String,
    var questionSummary: String?,
    var options: List<OptionsUiModel>?,
    var display: String,
    var summary: String? = "",
    val tagId: List<Int>,
    val surveyName: String,
    val formId: Int,
    var subjectId: Int = 0,
    var subjectType: String = BLANK_STRING,
    var isConditional: Boolean = false
)


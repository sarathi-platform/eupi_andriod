package com.sarathi.dataloadingmangement.model.uiModel

import com.nudge.core.BLANK_STRING
import com.sarathi.dataloadingmangement.model.survey.response.ContentList

data class QuestionUiModel(
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
    var order: Int,
    var subjectType: String = BLANK_STRING,
    var isConditional: Boolean = false,
    var showQuestion: Boolean = false,
    var sectionName: String,
    var formDescriptionInEnglish: String?,
    val contentEntities: List<ContentList> = listOf(),
) {


}


package com.sarathi.dataloadingmangement.model.uiModel

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
    val formId: Int
    )


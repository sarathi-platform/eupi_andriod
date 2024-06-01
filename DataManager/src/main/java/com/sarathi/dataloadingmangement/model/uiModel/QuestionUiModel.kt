package com.sarathi.dataloadingmangement.model.uiModel


class QuestionUiModel(
    var questionId: Int,
    var surveyId: Int,
    var sectionId: Int,
    var type: String,
    var languageId: String,
    var questionDisplay: String,
    var questionSummary: String?,
    var optionId: Int,
    var optionType: String,
    var optionValue: String? = "",
    var display: String,
    var summary: String? = "",
)


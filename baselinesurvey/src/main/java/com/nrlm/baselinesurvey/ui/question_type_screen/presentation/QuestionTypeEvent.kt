package com.nrlm.baselinesurvey.ui.question_type_screen.presentation

sealed class QuestionTypeEvent {
    data class FormTypeQuestionAnswered(
        val surveyId: Int,
        val sectionId: Int,
        val didiId: Int,
        val questionId: Int,
        val optionItemId: Int,
        val selectedValue: String,
    ) : QuestionTypeEvent()

}
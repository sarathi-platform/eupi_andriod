package com.nrlm.baselinesurvey.ui.question_type_screen.presentation

import com.nrlm.baselinesurvey.database.entity.FormQuestionResponseEntity

sealed class QuestionTypeEvent {
    data class SaveFormQuestionResponseEvent(
       val formQuestionResponseEntity: FormQuestionResponseEntity
    ) : QuestionTypeEvent()

}
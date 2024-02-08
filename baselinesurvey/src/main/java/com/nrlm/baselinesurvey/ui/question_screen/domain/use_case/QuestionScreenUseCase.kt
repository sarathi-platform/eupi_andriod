package com.nrlm.baselinesurvey.ui.question_screen.domain.use_case

import com.nrlm.baselinesurvey.ui.question_type_screen.domain.use_case.GetFormQuestionResponseUseCase

data class QuestionScreenUseCase(
    val getSectionUseCase: GetSectionUseCase,
    val getSectionsListUseCase: GetSectionsListUseCase,
    val updateSectionProgressUseCase: UpdateSectionProgressUseCase,
    val saveSectionAnswerUseCase: SaveSectionAnswerUseCase,
    val getSectionAnswersUseCase: GetSectionAnswersUseCase,
    val getFormQuestionResponseUseCase: GetFormQuestionResponseUseCase
)

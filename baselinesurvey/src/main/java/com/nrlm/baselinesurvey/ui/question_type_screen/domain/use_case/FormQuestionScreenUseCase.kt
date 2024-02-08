package com.nrlm.baselinesurvey.ui.question_type_screen.domain.use_case

data class FormQuestionScreenUseCase(
    val getFormQuestionResponseUseCase: GetFormQuestionResponseUseCase,
    val saveFormQuestionResponseUseCase: SaveFormQuestionResponseUseCase,
    val updateFormQuestionResponseUseCase: UpdateFormQuestionResponseUseCase
    )

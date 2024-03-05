package com.nrlm.baselinesurvey.ui.question_type_screen.domain.use_case

import com.nrlm.baselinesurvey.ui.question_screen.domain.use_case.DeleteFormQuestionOptionResponseUseCase
import com.nrlm.baselinesurvey.ui.question_screen.domain.use_case.DeleteFormQuestionResponseUseCase

data class FormQuestionScreenUseCase(
    val getFormQuestionResponseUseCase: GetFormQuestionResponseUseCase,
    val saveFormQuestionResponseUseCase: SaveFormQuestionResponseUseCase,
    val updateFormQuestionResponseUseCase: UpdateFormQuestionResponseUseCase,
    val deleteFormQuestionOptionResponseUseCase: DeleteFormQuestionOptionResponseUseCase,
    val deleteFormQuestionResponseUseCase: DeleteFormQuestionResponseUseCase
)

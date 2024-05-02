package com.nrlm.baselinesurvey.ui.question_type_screen.domain.use_case

import com.nrlm.baselinesurvey.ui.common_components.common_domain.common_use_case.EventsWriterUserCase
import com.nrlm.baselinesurvey.ui.question_screen.domain.use_case.DeleteFormQuestionOptionResponseUseCase
import com.nrlm.baselinesurvey.ui.question_screen.domain.use_case.DeleteFormQuestionResponseUseCase
import com.nrlm.baselinesurvey.ui.question_screen.domain.use_case.UpdateSectionProgressUseCase

data class FormQuestionScreenUseCase(
    val getFormQuestionResponseUseCase: GetFormQuestionResponseUseCase,
    val saveFormQuestionResponseUseCase: SaveFormQuestionResponseUseCase,
    val updateFormQuestionResponseUseCase: UpdateFormQuestionResponseUseCase,
    val deleteFormQuestionOptionResponseUseCase: DeleteFormQuestionOptionResponseUseCase,
    val deleteFormQuestionResponseUseCase: DeleteFormQuestionResponseUseCase,
    val updateSectionProgressUseCase: UpdateSectionProgressUseCase,
    val eventsWriterUserCase: EventsWriterUserCase
)

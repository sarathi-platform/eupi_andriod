package com.nrlm.baselinesurvey.ui.form_response_summary_screen.domain.use_case

import com.nrlm.baselinesurvey.ui.common_components.common_domain.common_use_case.EventsWriterUserCase
import com.nrlm.baselinesurvey.ui.question_screen.domain.use_case.DeleteFormQuestionResponseUseCase
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.use_case.GetFormQuestionResponseUseCase

data class FormResponseSummaryScreenUseCase(
    val getFormQuestionResponseUseCase: GetFormQuestionResponseUseCase,
    val deleteFormQuestionResponseUseCase: DeleteFormQuestionResponseUseCase,
    val eventsWriterUseCase: EventsWriterUserCase
)
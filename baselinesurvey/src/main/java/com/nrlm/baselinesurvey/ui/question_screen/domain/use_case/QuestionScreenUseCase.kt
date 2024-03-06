package com.nrlm.baselinesurvey.ui.question_screen.domain.use_case

import com.nrlm.baselinesurvey.ui.common_components.common_domain.common_use_case.EventsWriterUserCase
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.use_case.GetFormQuestionResponseUseCase
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.use_case.SaveFormQuestionResponseUseCase
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.use_case.UpdateFormQuestionResponseUseCase
import com.nrlm.baselinesurvey.ui.start_screen.domain.use_case.GetSurveyeeDetailsUserCase

data class QuestionScreenUseCase(
    val getSectionUseCase: GetSectionUseCase,
    val getSectionsListUseCase: GetSectionsListUseCase,
    val updateSectionProgressUseCase: UpdateSectionProgressUseCase,
    val saveSectionAnswerUseCase: SaveSectionAnswerUseCase,
    val getSectionAnswersUseCase: GetSectionAnswersUseCase,
    val getFormQuestionResponseUseCase: GetFormQuestionResponseUseCase,
    val saveFormQuestionResponseUseCase: SaveFormQuestionResponseUseCase,
    val updateFormQuestionResponseUseCase: UpdateFormQuestionResponseUseCase,
    val deleteFormQuestionResponseUseCase: DeleteFormQuestionResponseUseCase,
    val getSurveyeeDetailsUserCase: GetSurveyeeDetailsUserCase,
    val eventsWriterUseCase: EventsWriterUserCase
)

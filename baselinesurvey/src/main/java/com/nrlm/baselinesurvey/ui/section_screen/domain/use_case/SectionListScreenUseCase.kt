package com.nrlm.baselinesurvey.ui.section_screen.domain.use_case

import com.nrlm.baselinesurvey.ui.common_components.common_domain.common_use_case.EventsWriterUserCase

data class SectionListScreenUseCase(
    val getSectionListUseCase: GetSectionListUseCase,
    val getSectionProgressForDidiUseCase: GetSectionProgressForDidiUseCase,
    val getSurvyeDetails: GetSurvyeDetails,
    val updateSubjectStatusUseCase: UpdateSubjectStatusUseCase,
    val updateTaskStatusUseCase: UpdateTaskStatusUseCase,
    val eventsWriterUseCase: EventsWriterUserCase
)

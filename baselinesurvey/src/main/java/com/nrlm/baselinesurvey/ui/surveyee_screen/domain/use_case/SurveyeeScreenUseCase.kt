package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import com.nrlm.baselinesurvey.ui.common_components.common_domain.common_use_case.EventsWriterUserCase
import com.nrlm.baselinesurvey.ui.section_screen.domain.use_case.UpdateSubjectStatusUseCase
import com.nrlm.baselinesurvey.ui.section_screen.domain.use_case.UpdateTaskStatusUseCase

data class SurveyeeScreenUseCase(
    val getSurveyeeListUseCase: GetSurveyeeListUseCase,
    val moveSurveyeeToThisWeek: MoveSurveyeeToThisWeekUseCase,
    val getActivityStateFromDBUseCase: GetActivityStateFromDBUseCase,
    val updateActivityStatusUseCase: UpdateActivityStatusUseCase,
    val eventsWriterUseCase: EventsWriterUserCase,
    val updateSubjectStatusUseCase: UpdateSubjectStatusUseCase,
    val updateTaskStatusUseCase: UpdateTaskStatusUseCase
)

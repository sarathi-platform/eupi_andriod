package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import com.nrlm.baselinesurvey.ui.common_components.common_domain.common_use_case.EventsWriterUserCase

data class SurveyeeScreenUseCase(
    val getSurveyeeListUseCase: GetSurveyeeListUseCase,
    val moveSurveyeeToThisWeek: MoveSurveyeeToThisWeekUseCase,
    val getActivityStateFromDBUseCase: GetActivityStateFromDBUseCase,
    val updateActivityStatusUseCase: UpdateActivityStatusUseCase,
    val eventsWriterUseCase: EventsWriterUserCase
)

package com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.usecase

import com.nrlm.baselinesurvey.ui.common_components.common_domain.common_use_case.EventsWriterUserCase

data class MissionSummaryScreenUseCase(
    val getMissionActivitiesFromDBUseCase: GetMissionActivitiesFromDBUseCase,
    val updateMisisonState: UpdateMisisonState,
    val updateMissionStatusUseCase: UpdateMissionStatusUseCase,
    val getPendingTaskCountLiveUseCase: GetPendingTaskCountLiveUseCase,
    val eventsWriterUserCase: EventsWriterUserCase
)

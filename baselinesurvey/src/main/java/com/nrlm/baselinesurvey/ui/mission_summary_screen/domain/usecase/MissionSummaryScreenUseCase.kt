package com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.usecase

data class MissionSummaryScreenUseCase(
    val getMissionActivitiesFromDBUseCase: GetMissionActivitiesFromDBUseCase,
    val getActivityStateFromDBUseCase: GetActivityStateFromDBUseCase
)

package com.patsurvey.nudge.activities.sync.home.domain.use_case

data class SyncHomeUseCase(
    val getUserDetailsSyncUseCase: GetUserDetailsSyncUseCase,
    val getSyncEventsUseCase: GetSyncEventsUseCase
)
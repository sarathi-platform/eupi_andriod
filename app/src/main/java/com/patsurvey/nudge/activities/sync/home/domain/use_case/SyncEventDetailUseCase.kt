package com.patsurvey.nudge.activities.sync.home.domain.use_case

data class SyncEventDetailUseCase(
    val getUserDetailsSyncUseCase: GetUserDetailsSyncUseCase,
    val getSyncEventsUseCase: GetSyncEventsUseCase
)
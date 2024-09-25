package com.nudge.syncmanager.domain.usecase

data class SyncManagerUseCase(
    val addUpdateEventUseCase: AddUpdateEventUseCase,
    val getUserDetailsSyncUseCase: GetUserDetailsSyncRepoUseCase,
    val fetchEventsFromDBUseCase: FetchEventsFromDBUseCase,
    val syncAPIUseCase: SyncAPIUseCase,
    val syncAnalyticsEventUseCase: SyncAnalyticsEventUseCase
)

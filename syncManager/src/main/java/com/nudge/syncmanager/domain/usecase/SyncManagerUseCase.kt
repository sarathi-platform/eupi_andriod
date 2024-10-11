package com.nudge.syncmanager.domain.usecase

import com.nudge.core.usecase.FetchAppConfigFromCacheOrDbUsecase

data class SyncManagerUseCase(
    val addUpdateEventUseCase: AddUpdateEventUseCase,
    val getUserDetailsSyncUseCase: GetUserDetailsSyncRepoUseCase,
    val fetchEventsFromDBUseCase: FetchEventsFromDBUseCase,
    val syncAPIUseCase: SyncAPIUseCase,
    val syncAnalyticsEventUseCase: SyncAnalyticsEventUseCase,
    val syncBlobUploadUseCase: BlobUploadUseCase,
    val fetchAppConfigFromCacheOrDbUsecase: FetchAppConfigFromCacheOrDbUsecase
)

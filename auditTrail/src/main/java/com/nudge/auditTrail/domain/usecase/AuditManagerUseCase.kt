package com.nudge.auditTrail.domain.usecase

import com.nudge.core.usecase.FetchAppConfigFromCacheOrDbUsecase

data class AuditManagerUseCase(
//    val addUpdateEventUseCase: AddUpdateEventUseCase,
//    val getUserDetailsSyncUseCase: GetUserDetailsSyncRepoUseCase,
//    val fetchEventsFromDBUseCase: FetchEventsFromDBUseCase,
    val auditAPIUseCase: AuditTrailUseCase,
    val fetchAppConfigFromCacheOrDbUsecase: FetchAppConfigFromCacheOrDbUsecase
)

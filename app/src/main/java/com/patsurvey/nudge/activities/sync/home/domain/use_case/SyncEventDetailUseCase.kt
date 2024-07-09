package com.patsurvey.nudge.activities.sync.home.domain.use_case

import com.nrlm.baselinesurvey.ui.common_components.common_domain.common_use_case.EventsWriterUserCase

data class SyncEventDetailUseCase(
    val getUserDetailsSyncUseCase: GetUserDetailsSyncUseCase,
    val getSyncEventsUseCase: GetSyncEventsUseCase,
    val eventsWriterUseCase: EventsWriterUserCase,
    val fetchLastSyncDateForNetwork: FetchLastSyncDateForNetwork
)
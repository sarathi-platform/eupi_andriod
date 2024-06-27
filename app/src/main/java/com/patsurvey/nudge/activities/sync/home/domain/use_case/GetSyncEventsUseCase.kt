package com.patsurvey.nudge.activities.sync.home.domain.use_case

import com.patsurvey.nudge.activities.sync.home.domain.repository.SyncHomeRepository
import javax.inject.Inject

class GetSyncEventsUseCase @Inject constructor(
    val repository: SyncHomeRepository
) {
    fun getTotalEvents() = repository.getTotalEvents()
}
package com.patsurvey.nudge.activities.domain.useCase

import com.nudge.core.enums.SyncAlertType
import com.nudge.core.utils.SyncType
import com.nudge.syncmanager.domain.usecase.FetchEventsFromDBUseCase
import com.patsurvey.nudge.activities.domain.repository.interfaces.CheckEventLimitThresholdRepository
import javax.inject.Inject

class CheckEventLimitThresholdUseCase @Inject constructor(
    private val checkEventLimitThresholdRepository: CheckEventLimitThresholdRepository,
    private val fetchEventsFromDBUseCase: FetchEventsFromDBUseCase
) {

    suspend fun invoke(): SyncAlertType {

        val totalPendingEventCount =
            fetchEventsFromDBUseCase.getPendingEventCount(syncType = SyncType.SYNC_ALL.ordinal)

        return checkEventLimitThresholdRepository.checkEventLimitStatus(totalPendingEventCount)

    }

}
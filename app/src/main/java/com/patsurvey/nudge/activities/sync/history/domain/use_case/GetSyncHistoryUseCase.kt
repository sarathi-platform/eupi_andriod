package com.patsurvey.nudge.activities.sync.history.domain.use_case

import com.patsurvey.nudge.activities.sync.history.domain.repository.SyncHistoryRepository
import javax.inject.Inject

class GetSyncHistoryUseCase @Inject constructor(
    val repository: SyncHistoryRepository
){
    fun getUserMobileNumber()= repository.getUserMobileNumber()
    fun getUserID()= repository.getUserID()

    suspend fun getAllEventsBetweenDates(startDate: String, endDate: String) =
        repository.getAllEventsBetweenDates(startDate = startDate, endDate = endDate)

    suspend fun getAllEventStatusForUser() = repository.getAllEventStatusForUser()
}
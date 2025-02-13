package com.patsurvey.nudge.activities.backup.domain.use_case

import com.patsurvey.nudge.activities.backup.domain.repository.RemoteQueryExecutionRepository
import javax.inject.Inject

class RemoteQueryExecutionUseCase @Inject constructor(
    private val remoteQueryExecutionRepository: RemoteQueryExecutionRepository
) {

    suspend operator fun invoke() {

    }

}
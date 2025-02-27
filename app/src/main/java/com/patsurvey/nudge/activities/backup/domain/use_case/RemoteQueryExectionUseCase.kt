package com.patsurvey.nudge.activities.backup.domain.use_case

import com.nudge.core.FAILED
import com.nudge.core.LOGGING_TYPE_DEBUG
import com.nudge.core.OPEN
import com.patsurvey.nudge.activities.backup.domain.repository.RemoteQueryExecutionRepository
import javax.inject.Inject

class RemoteQueryExecutionUseCase @Inject constructor(
    private val remoteQueryExecutionRepository: RemoteQueryExecutionRepository
) {

    suspend operator fun invoke() {

        var remoteQueries = remoteQueryExecutionRepository.getRemoteQuery()
        if (remoteQueries.isEmpty()) {
            remoteQueryExecutionRepository.logEvent(
                LOGGING_TYPE_DEBUG,
                FAILED,
                "RemoteQueryExecutionUseCase: invoke no query found for execution",
                null
            )
            return
        }
        remoteQueries = remoteQueries.sortedBy { it?.executionOrder }
        for (query in remoteQueries) {
            query?.let { it ->
                if (remoteQueryExecutionRepository.checkIfQueryIsValid(
                        it.query,
                        remoteQueryExecutionRepository.isUserIdCheckNotRequired(it)
                    )
                ) {
                    remoteQueryExecutionRepository.executeQuery(it)
                }
            } ?: {
                remoteQueryExecutionRepository.logEvent(
                    LOGGING_TYPE_DEBUG,
                    OPEN,
                    "RemoteQueryExecutionUseCase: invoke no query found for execution",
                    null
                )
            }
        }
    }

}
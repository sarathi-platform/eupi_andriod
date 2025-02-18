package com.patsurvey.nudge.activities.backup.domain.use_case

import com.nudge.core.utils.CoreLogger
import com.patsurvey.nudge.activities.backup.domain.repository.RemoteQueryExecutionRepository
import javax.inject.Inject

class RemoteQueryExecutionUseCase @Inject constructor(
    private val remoteQueryExecutionRepository: RemoteQueryExecutionRepository
) {

    suspend operator fun invoke() {

        val remoteQuery = remoteQueryExecutionRepository.getRemoteQuery()

        remoteQuery?.let {
            if (remoteQueryExecutionRepository.checkIfQueryIsValid(
                    it.query,
                    remoteQueryExecutionRepository.isUserIdCheckNotRequired(it)
                )
            ) {
                remoteQueryExecutionRepository.executeQuery(it)
            }
        } ?: {
            CoreLogger.d(
                tag = "RemoteQueryExecutionUseCase",
                msg = "invoke no query found for execution",
            )
        }
    }

}
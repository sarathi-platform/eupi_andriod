package com.patsurvey.nudge.activities.backup.domain.use_case

import com.nudge.core.FAILED
import com.nudge.core.LOGGING_TYPE_DEBUG
import com.nudge.core.model.RemoteQueryDto
import com.nudge.core.model.request.RemoteSqlQueryApiRequest
import com.nudge.core.usecase.SaveRemoteQueryStatusToNetworkUseCase
import com.patsurvey.nudge.activities.backup.domain.repository.RemoteQueryExecutionRepository
import javax.inject.Inject

class RemoteQueryExecutionUseCase @Inject constructor(
    private val remoteQueryExecutionRepository: RemoteQueryExecutionRepository,
    private val remoteSaveRemoteQueryStatusToNetworkUseCase: SaveRemoteQueryStatusToNetworkUseCase,

    ) {

    suspend operator fun invoke() {

        var remoteQueries = remoteQueryExecutionRepository.getRemoteQuery()
        val apiRequests: ArrayList<RemoteSqlQueryApiRequest> = arrayListOf()

        if (remoteQueries.isEmpty()) {
            remoteQueryExecutionRepository.logEvent(
                LOGGING_TYPE_DEBUG,
                FAILED,
                "RemoteQueryExecutionUseCase: invoke no query found for execution",
                null
            )
            return
        }
        val remoteQueriesGroupByLevel = remoteQueries.groupBy { it.level }
        remoteQueriesGroupByLevel.entries.forEach {

            val sortedRemoteQueries = it.value.sortedBy { it.executionOrder }
            for (query in sortedRemoteQueries) {
                query?.let { it ->
                    if (remoteQueryExecutionRepository.checkIfQueryIsValid(
                            it.query,
                            remoteQueryExecutionRepository.isUserIdCheckNotRequired(it)
                        )
                    ) {
                        remoteQueryExecutionRepository.executeQuery(it)
                    }
                }
            }

        }

        val updatedRemoteQueries = remoteQueryExecutionRepository.getRemoteQuery()
        updatedRemoteQueries.groupBy { it.propertyValueId }.entries.forEach {
            val apiRequest = RemoteSqlQueryApiRequest(
                propertyValueId = it.value.first().propertyValueId,
                userId = it.value.first().userId.toInt(),
                value = it.value.map {
                    RemoteQueryDto(
                        appVersion = it.appVersion,
                        dbVersion = it.dbVersion,
                        databaseName = it.databaseName,
                        tableName = it.tableName,
                        executionOrder = it.executionOrder,
                        queryStatus = it.status,
                        query = it.query,
                        operationType = it.operationType
                    )
                }
            )
            apiRequests.add(apiRequest)
        }
        remoteSaveRemoteQueryStatusToNetworkUseCase.invoke(apiRequests)

    }

}
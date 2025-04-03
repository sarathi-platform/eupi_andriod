package com.nudge.core.data.repository

import com.nudge.core.apiService.CoreApiService
import com.nudge.core.enums.AppConfigKeysEnum
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.request.RemoteSqlQueryApiRequest
import com.nudge.core.model.response.RemoteSqlQueryApiResponseItem
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.toSafeInt
import javax.inject.Inject

class RemoteQueryNetworkRepositoryImpl @Inject constructor(
    private val coreApiService: CoreApiService,
    private val coreSharedPrefs: CoreSharedPrefs,
) : RemoteQueryNetworkRepository {
    override suspend fun fetchRemoteQueryFromNetwork(): ApiResponseModel<List<RemoteSqlQueryApiResponseItem>> {
        return coreApiService.fetchRemoteSqlQueryConfig(
            propertyName = AppConfigKeysEnum.SQL_QUERY_EXECUTOR.name,
            userId = coreSharedPrefs.getUserId().toSafeInt("0"),
            stateId = coreSharedPrefs.getStateId()
        )
    }

    override suspend fun saveRemoteQueryStatusToNetwork(apiRequest: List<RemoteSqlQueryApiRequest>): ApiResponseModel<String> {
        return coreApiService.saveRemoteSqlQueryStatus(apiRequest)
    }


}
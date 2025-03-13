package com.nudge.core.data.repository

import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.request.RemoteSqlQueryApiRequest
import com.nudge.core.model.response.RemoteSqlQueryApiResponseItem

interface RemoteQueryNetworkRepository {

    suspend fun fetchRemoteQueryFromNetwork(): ApiResponseModel<List<RemoteSqlQueryApiResponseItem>>
    suspend fun saveRemoteQueryStatusToNetwork(apiRequest: List<RemoteSqlQueryApiRequest>): ApiResponseModel<String>
}
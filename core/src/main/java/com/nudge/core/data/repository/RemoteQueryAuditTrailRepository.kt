package com.nudge.core.data.repository

import com.nudge.core.model.response.RemoteSqlQueryApiResponseItem

interface RemoteQueryAuditTrailRepository {

    suspend fun saveRemoteQueryToDb(remoteSqlQueryApiResponseItem: List<RemoteSqlQueryApiResponseItem>)
}
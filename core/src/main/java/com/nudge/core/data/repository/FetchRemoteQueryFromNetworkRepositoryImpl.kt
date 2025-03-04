package com.nudge.core.data.repository

import com.nudge.core.apiService.CoreApiService
import com.nudge.core.database.dao.RemoteQueryAuditTrailEntityDao
import com.nudge.core.preference.CoreSharedPrefs
import javax.inject.Inject

class FetchRemoteQueryFromNetworkRepositoryImpl @Inject constructor(
    private val coreApiService: CoreApiService,
    private val coreSharedPrefs: CoreSharedPrefs,
    private val remoteQueryAuditTrailEntityDao: RemoteQueryAuditTrailEntityDao
) : FetchRemoteQueryFromNetworkRepository {

}
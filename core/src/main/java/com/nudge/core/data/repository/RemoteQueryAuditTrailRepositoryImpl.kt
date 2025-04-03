package com.nudge.core.data.repository

import com.nudge.core.database.dao.RemoteQueryAuditTrailEntityDao
import com.nudge.core.database.entities.RemoteQueryAuditTrailEntity
import com.nudge.core.model.response.RemoteSqlQueryApiResponseItem
import com.nudge.core.preference.CoreSharedPrefs
import javax.inject.Inject

class RemoteQueryAuditTrailRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val remoteQueryAuditTrailEntityDao: RemoteQueryAuditTrailEntityDao
) : RemoteQueryAuditTrailRepository {
    override suspend fun saveRemoteQueryToDb(remoteSqlQueryApiResponseItem: List<RemoteSqlQueryApiResponseItem>) {
        remoteSqlQueryApiResponseItem.forEach {
            it.value.forEach { remoteQueryDto ->
                remoteQueryAuditTrailEntityDao.insertRemoteQueryAuditTrailEntity(
                    RemoteQueryAuditTrailEntity.getRemoteQueryAuditTrailEntity(
                        remoteQueryDto = remoteQueryDto,
                        userId = coreSharedPrefs.getUniqueUserIdentifier(),
                        level = it.level,
                        propertyValueId = it.propertyValueId
                    )
                )
            }
        }
    }


}
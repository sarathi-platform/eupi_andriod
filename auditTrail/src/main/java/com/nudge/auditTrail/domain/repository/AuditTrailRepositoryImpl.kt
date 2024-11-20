package com.nudge.auditTrail.domain.repository

import com.nudge.auditTrail.apiService.AuditTrailApiService
import com.nudge.auditTrail.database.dao.AuditTrailDao
import com.nudge.auditTrail.entities.AuditTrailEntity
import com.nudge.auditTrail.model.AuditRequest
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.preference.CoreSharedPrefs
import java.util.Objects
import javax.inject.Inject

class AuditTrailRepositoryImpl @Inject constructor(
    val auditTrailApiService: AuditTrailApiService,
    val coreSharedPrefs: CoreSharedPrefs,
    val auditDao :AuditTrailDao
) : AuditTrailRepository {

    override suspend fun insertEvent(auditDetail: Map<String, Any>) {
        return  auditDao.insert(AuditTrailEntity.getAuditDetailEvent(auditDetail,coreSharedPrefs.getMobileNo()))
    }



}
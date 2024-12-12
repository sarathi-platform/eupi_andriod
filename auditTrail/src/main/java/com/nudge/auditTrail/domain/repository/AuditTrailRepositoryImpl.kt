package com.nudge.auditTrail.domain.repository

import com.nudge.auditTrail.apiService.AuditTrailApiService
import com.nudge.auditTrail.database.dao.AuditTrailDao
import com.nudge.auditTrail.entities.AuditTrailEntity
import com.nudge.auditTrail.model.AuditRequest
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.preference.CoreSharedPrefs
import javax.inject.Inject

class AuditTrailRepositoryImpl @Inject constructor(
    val auditTrailApiService: AuditTrailApiService,
    val coreSharedPrefs: CoreSharedPrefs,
    val auditDao :AuditTrailDao
) : AuditTrailRepository {

    override suspend fun insertEvent(
        auditDetail: Map<String, Any>,
        actionType: String,
        actionStatusType: String,
        message:String
    ) {
        return auditDao.insert(
            AuditTrailEntity.getAuditDetailEvent(
            auditDetail,
              mobileNo =   coreSharedPrefs.getMobileNo(),
               actionType =  actionType,
               actionStatusType =  actionStatusType,
                message = message
            )
        )
    }
    override suspend fun auditTrailEventToServer(auditRequest: List<AuditRequest>): ApiResponseModel<String> {
        return auditTrailApiService.auditTrail(auditRequest)
    }
    override suspend fun getAuditTrailEventFromDb(): List<AuditTrailEntity> {
        return auditDao.getAllEvent()
    }
}
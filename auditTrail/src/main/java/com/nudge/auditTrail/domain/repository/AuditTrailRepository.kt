package com.nudge.auditTrail.domain.repository

import com.nudge.auditTrail.entities.AuditTrailEntity
import com.nudge.auditTrail.model.AuditRequest
import com.nudge.core.model.ApiResponseModel

interface AuditTrailRepository {
     suspend fun insertEvent (auditDetail : Map<String,Any>)
     suspend fun auditTrailEventToServer(auditRequest: List<AuditRequest>): ApiResponseModel<String>
     suspend fun getAuditTrailEventFromDb(): List<AuditTrailEntity>

}
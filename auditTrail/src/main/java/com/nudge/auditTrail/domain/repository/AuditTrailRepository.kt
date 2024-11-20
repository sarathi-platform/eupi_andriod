package com.nudge.auditTrail.domain.repository

import com.nudge.auditTrail.entities.AuditTrailEntity
import com.nudge.auditTrail.model.AuditRequest
import com.nudge.core.model.ApiResponseModel
import java.util.Objects

interface AuditTrailRepository {
     suspend fun insertEvent (auditDetail : Map<String,Any>)

}
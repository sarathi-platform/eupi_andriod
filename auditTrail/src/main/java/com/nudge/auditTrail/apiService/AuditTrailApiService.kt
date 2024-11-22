package com.nudge.auditTrail.apiService

import com.nudge.auditTrail.model.AuditRequest
import com.nudge.core.model.ApiResponseModel
import retrofit2.http.Body
import retrofit2.http.POST

interface AuditTrailApiService {

    @POST("audit-trail-service/audit-trail/add")
    suspend fun auditTrail(@Body auditRequest: List<AuditRequest>
    ): ApiResponseModel<String>

}
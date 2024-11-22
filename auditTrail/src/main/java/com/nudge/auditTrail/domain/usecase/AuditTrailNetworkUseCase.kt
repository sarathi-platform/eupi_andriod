package com.nudge.auditTrail.domain.usecase

import android.content.Context

import com.nudge.auditTrail.domain.repository.AuditTrailRepository
import com.nudge.auditTrail.entities.AuditTrailEntity
import com.nudge.auditTrail.model.AuditRequest
import com.nudge.auditTrail.model.toEventRequest
import com.nudge.core.model.ApiResponseModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AuditTrailNetworkUseCase@Inject constructor(
    private val repository: AuditTrailRepository,
    @ApplicationContext private val context: Context
) {
    suspend fun auditTrailEventToServer(events: List<AuditTrailEntity>): ApiResponseModel<String> {
        val eventRequest: List<AuditRequest> = events.map {
            it.toEventRequest()
        }
        return repository.auditTrailEventToServer(eventRequest)
    }
    suspend fun getAuditTrailEventFromDb(
    ): List<AuditTrailEntity> {
        return repository.getAuditTrailEventFromDb(
        )
    }
}
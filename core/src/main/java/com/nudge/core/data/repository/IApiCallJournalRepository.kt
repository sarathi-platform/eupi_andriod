package com.nudge.core.data.repository

import com.nudge.core.database.entities.api.ApiCallJournalEntity

interface IApiCallJournalRepository {
    suspend fun addApiCall(
        screenName: String,
        moduleName: String,
        dataLoadingTriggerType: String,
        requestPayload: String,
        transactionId: String,
        apiUrl: String
    )

    suspend fun updateApiCallStatus(
        screenName: String,
        moduleName: String,
        dataLoadingTriggerType: String,
        requestPayload: String,
        apiUrl: String,
        status: String,
        errorMsg: String
    )

    suspend fun getApiCallStatus(
        screenName: String,
        moduleName: String,
        apiUrl: String,
        requestPayload: String
    ): ApiCallJournalEntity?

    suspend fun getFailedApiCallJournalEntity(
        screenName: String,
        moduleName: String
    ): List<ApiCallJournalEntity>?

    suspend fun getTotalInProgressApiCallJournalEntity(
        screenName: String,
        moduleName: String,
        requestBody: String,
        transactionId: String
    ): Int?

}
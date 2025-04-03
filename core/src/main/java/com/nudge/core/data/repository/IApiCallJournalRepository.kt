package com.nudge.core.data.repository

import com.nudge.core.database.entities.api.ApiCallJournalEntity

interface IApiCallJournalRepository {
    suspend fun addApiCall(
        screenName: String,
        moduleName: String,
        dataLoadingTriggerType: String,
        requestPayload: String,
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
        apiUrl: String
    ): ApiCallJournalEntity?

}
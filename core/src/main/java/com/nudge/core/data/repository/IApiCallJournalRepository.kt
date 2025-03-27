package com.nudge.core.data.repository

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

}
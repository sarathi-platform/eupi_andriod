package com.nudge.core.data.repository

import com.nudge.core.database.dao.api.ApiCallJournalDao
import com.nudge.core.database.entities.api.ApiCallJournalEntity
import com.nudge.core.preference.CoreSharedPrefs
import javax.inject.Inject

class ApiCallJournalRepositoryImpl @Inject constructor(
    val apiCallJournalJournalDao: ApiCallJournalDao,
    val coreSharedPrefs: CoreSharedPrefs
) : IApiCallJournalRepository {
    override suspend fun addApiCall(
        screenName: String,
        moduleName: String,
        dataLoadingTriggerType: String,
        requestPayload: String,
        apiUrl: String
    ) {

        if (!isApiCallAlreadyExist(apiUrl, requestPayload))
            apiCallJournalJournalDao.insert(
                apiCallJournalEntity = ApiCallJournalEntity.getApiCallJournalEntity(
                    screenName = screenName,
                    moduleName = moduleName,
                    triggerPoint = dataLoadingTriggerType,
                    userId = coreSharedPrefs.getUniqueUserIdentifier(),
                    apiUrl = apiUrl,
                    requestBody = requestPayload
                )
            )

    }

    override suspend fun updateApiCallStatus(
        screenName: String,
        moduleName: String,
        dataLoadingTriggerType: String,
        requestPayload: String,
        apiUrl: String,
        status: String,
        errorMsg: String
    ) {
        //Todo increase retry count after failed status
        apiCallJournalJournalDao.updateApiCallStatus(
            screenName = screenName,
            moduleName = moduleName,
            triggerPoint = dataLoadingTriggerType,
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            apiUrl = apiUrl,
            requestBody = requestPayload,
            status = status, errorMsg = errorMsg
        )
    }

    override suspend fun getApiCallStatus(
        screenName: String,
        moduleName: String,
        apiUrl: String,
        requestPayload: String
    ): ApiCallJournalEntity? {
        return apiCallJournalJournalDao.getApiCallStatus(
            screenName = screenName,
            moduleName = moduleName,
            apiUrl = apiUrl,
            requestPayload = requestPayload
        )
    }


    private suspend fun isApiCallAlreadyExist(apiUrl: String, requestPayload: String): Boolean {
        return apiCallJournalJournalDao.isApiCallAlreadyExist(
            apiUrl,
            requestPayload,
            coreSharedPrefs.getUniqueUserIdentifier()
        ) > 0
    }
}
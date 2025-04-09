package com.nudge.core.data.repository

import com.nudge.core.database.dao.api.ApiCallJournalDao
import com.nudge.core.database.entities.api.ApiCallJournalEntity
import com.nudge.core.enums.ApiStatus
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
        transactionId: String,
        apiUrl: String
    ) {

        if (!isApiCallAlreadyExist(
                apiUrl,
                requestPayload,
                screenName = screenName,
                moduleName = moduleName,
                transactionId = transactionId
            )
        )
            apiCallJournalJournalDao.insert(
                apiCallJournalEntity = ApiCallJournalEntity.getApiCallJournalEntity(
                    screenName = screenName,
                    moduleName = moduleName,
                    triggerPoint = dataLoadingTriggerType,
                    userId = coreSharedPrefs.getUniqueUserIdentifier(),
                    apiUrl = apiUrl,
                    requestBody = requestPayload,
                    transactionId = transactionId
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
            apiUrl = apiUrl,
            requestBody = requestPayload,
            status = status,
            errorMsg = errorMsg,
            userId = coreSharedPrefs.getUniqueUserIdentifier()
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

    override suspend fun getFailedApiCallJournalEntity(
        screenName: String,
        moduleName: String
    ): List<ApiCallJournalEntity>? {
        return apiCallJournalJournalDao.getFailedApiCallJournalEntity(
            screenName = screenName,
            moduleName = moduleName,
            status = ApiStatus.FAILED.name,
            userId = coreSharedPrefs.getUniqueUserIdentifier()
        )
    }

    override suspend fun getTotalInProgressApiCallJournalEntity(
        screenName: String,
        moduleName: String,
        requestBody: String,
        transactionId: String
    ): Int? {
        return apiCallJournalJournalDao.getTotalInProgressApiCallJournalEntity(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            screenName = screenName,
            moduleName = moduleName,
            transactionId = transactionId
        )
    }


    private suspend fun isApiCallAlreadyExist(
        apiUrl: String,
        requestPayload: String,
        screenName: String,
        moduleName: String,
        transactionId: String,
    ): Boolean {
        return apiCallJournalJournalDao.isApiCallAlreadyExist(
            apiUrl,
            requestPayload,
            moduleName = moduleName,
            screenName = screenName,
            transactionId = transactionId,
            uniqueUserIdentifier = coreSharedPrefs.getUniqueUserIdentifier()
        ) > 0
    }
}
package com.nudge.core.data.repository.api

import com.nudge.core.database.dao.api.ApiCallJournalDao
import com.nudge.core.database.entities.api.ApiCallJournalEntity
import com.nudge.core.preference.CoreSharedPrefs
import javax.inject.Inject

class ApiJournalDatabaseRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val apiCallJournalDao: ApiCallJournalDao
) : IApiJournalDatabaseRepository {

    override fun saveOrUpdateApiJournal(apiCallJournalEntity: ApiCallJournalEntity) {
        apiCallJournalDao.insertOrUpdate(apiCallJournalEntity)
    }

    override fun deleteApiJournalEntry(): Int {
        return apiCallJournalDao.deleteApiCallJournalTable(coreSharedPrefs.getUniqueUserIdentifier())
    }

    override fun getApiCallJournalEntity(
        apiUrl: String,
        apiName: String,
        status: String,
        moduleName: String,
        screenName: String,
        requestBody: String,
        triggerPoint: String
    ): ApiCallJournalEntity {
        return ApiCallJournalEntity.getApiCallJournalEntity(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            apiName = apiName,
            apiUrl = apiUrl,
            status = status,
            moduleName = moduleName,
            screenName = screenName,
            requestBody = requestBody,
            triggerPoint = triggerPoint,
            errorMsg = "errorMsg",
            retryCount = 0,
            createdDate = System.currentTimeMillis(),
            modifiedDate = System.currentTimeMillis()
        )
    }

}
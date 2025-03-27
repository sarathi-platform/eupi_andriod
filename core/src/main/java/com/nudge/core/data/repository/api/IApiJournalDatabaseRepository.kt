package com.nudge.core.data.repository.api

import com.nudge.core.database.entities.api.ApiCallJournalEntity

interface IApiJournalDatabaseRepository {

    fun saveOrUpdateApiJournal(
        apiCallJournalEntity: ApiCallJournalEntity
    )

    fun deleteApiJournalEntry(): Int
    open fun getApiCallJournalEntity(
        apiUrl: String,
        apiName: String,
        status: String,
        moduleName: String,
        screenName: String,
        requestBody: String,
        triggerPoint: String
    ): ApiCallJournalEntity

}
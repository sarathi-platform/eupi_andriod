package com.sarathi.dataloadingmangement.repository

interface IMoneyJournalRepository {
    suspend fun saveAndUpdateMoneyJournalTransaction(
        amount: Int,
        date: String,
        particulars: String,
        referenceId: String,
        grantId: Int,
        grantType: String,
        subjectType: String,
        subjectId: Int
    )

    suspend fun deleteMoneyJournalTransaction(transactionId: String)
}
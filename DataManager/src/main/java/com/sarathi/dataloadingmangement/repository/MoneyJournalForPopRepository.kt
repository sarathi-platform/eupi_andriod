package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.livelihood.MoneyJournalEntity

interface MoneyJournalForPopRepository {

    suspend fun saveAndUpdateMoneyJournalTransaction(
        amount: Int,
        date: String,
        particulars: String,
        transactionId: String,
        referenceId: Int,
        referenceType: String,
        subjectType: String,
        subjectId: Int,
        transactionFlow: String
    ): MoneyJournalEntity

    suspend fun updateMoneyJournalTransaction(
        referenceId: Int,
        referenceType: String,
        subjectId: Int,
        subjectType: String,
        moneyJournalEntity: MoneyJournalEntity
    ): MoneyJournalEntity

    suspend fun deleteOldEntries(
        referenceId: Int,
        referenceType: String,
        subjectId: Int,
        subjectType: String,
    )
}
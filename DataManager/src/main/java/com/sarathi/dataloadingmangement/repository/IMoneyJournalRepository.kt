package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.livelihood.MoneyJournalEntity
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventScreenData

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
    suspend fun saveAndUpdateMoneyJournalTransaction(
        particular: String,
        eventData: LivelihoodEventScreenData
    )

    suspend fun deleteMoneyJournalTransaction(transactionId: String)
    suspend fun getMoneyJournalTransaction(
        transactionId: String,
        subjectId: Int
    ): MoneyJournalEntity?
}
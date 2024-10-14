package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.livelihood.MoneyJournalEntity
import com.sarathi.dataloadingmangement.model.events.incomeExpense.SaveMoneyJournalEventDto
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
        eventData: LivelihoodEventScreenData,
        createdData: Long
    )

    suspend fun deleteMoneyJournalTransaction(transactionId: String, subjectId: Int)
    suspend fun getMoneyJournalTransaction(
        transactionId: String,
        subjectId: Int
    ): MoneyJournalEntity?

    suspend fun getMoneyJournalEventDto(
        particular: String,
        eventData: LivelihoodEventScreenData,
        currentDateTime: Long,
        modifiedDateTime: Long
    ): SaveMoneyJournalEventDto

    suspend fun getMoneyJournalEventForUser(): List<MoneyJournalEntity>

}
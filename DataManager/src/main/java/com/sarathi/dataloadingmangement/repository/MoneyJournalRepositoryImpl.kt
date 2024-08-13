package com.sarathi.dataloadingmangement.repository

import com.nudge.core.BLANK_STRING
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.toDateString
import com.sarathi.dataloadingmangement.data.dao.livelihood.MoneyJournalDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.MoneyJournalEntity
import com.sarathi.dataloadingmangement.model.events.incomeExpense.SaveMoneyJournalEventDto
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventScreenData
import javax.inject.Inject

class MoneyJournalRepositoryImpl @Inject constructor(
    val moneyJournalDao: MoneyJournalDao,
    val coreSharedPrefs: CoreSharedPrefs
) : IMoneyJournalRepository {
    override suspend fun saveAndUpdateMoneyJournalTransaction(
        amount: Int,
        date: String,
        particulars: String,
        referenceId: String,
        grantId: Int,
        grantType: String,
        subjectType: String,
        subjectId: Int
    ) {
        val moneyJournalEntity = MoneyJournalEntity.getMoneyJournalEntity(
            coreSharedPrefs.getUniqueUserIdentifier(),
            amount,
            date,
            particulars,
            referenceId,
            grantId,
            "GRANT",
            subjectType,
            subjectId,
            "INFLOW"
        )
        if (moneyJournalDao.isTransactionAlreadyExist(
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                transactionId = referenceId
            ) == 0
        ) {
            moneyJournalDao.insetMoneyJournalEntry(moneyJournalEntity)
        } else {
            moneyJournalDao.updateMoneyJournal(
                userId = moneyJournalEntity.userId,
                transactionId = moneyJournalEntity.transactionId,
                amount = moneyJournalEntity.transactionAmount,
                date = moneyJournalEntity.transactionDate,
                particulars = moneyJournalEntity.transactionDetails
            )
        }


    }

    override suspend fun saveAndUpdateMoneyJournalTransaction(
        particular: String,
        eventData: LivelihoodEventScreenData
    ) {
        val moneyJournalEntity = MoneyJournalEntity.getMoneyJournalEntity(
            coreSharedPrefs.getUniqueUserIdentifier(),
            eventData.amount,
            eventData.date.toDateString(),
            particular,
            eventData.transactionId,
            eventData.livelihoodId,
            "LivelihoodEvent",
            "didi",
            eventData.subjectId,
            eventData.selectedEvent.moneyJournalEntryFlowType?.name ?: BLANK_STRING,
            dateFormat = "dd/MM/yyyy",

            )
        if (moneyJournalDao.isTransactionAlreadyExist(
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                transactionId = eventData.transactionId
            ) > 0
        ) {
            deleteMoneyJournalTransaction(eventData.transactionId)
        }
        moneyJournalDao.insetMoneyJournalEntry(moneyJournalEntity)

    }

    override suspend fun deleteMoneyJournalTransaction(transactionId: String) {
        moneyJournalDao.softDeleteTransaction(
            transactionId = transactionId,
            userId = coreSharedPrefs.getUniqueUserIdentifier()
        )
    }

    override suspend fun getMoneyJournalTransaction(
        transactionId: String,
        subjectId: Int
    ): MoneyJournalEntity? {
        return moneyJournalDao.getMoneyJournalForTransaction(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            subjectId = subjectId,
            transactionId = transactionId
        )
    }

    override suspend fun getMoneyJournalEventDto(
        particular: String,
        eventData: LivelihoodEventScreenData
    ): SaveMoneyJournalEventDto {
        return SaveMoneyJournalEventDto(
            amount = eventData.amount,
            createdDate = System.currentTimeMillis(),
            particulars = particular,
            referenceType = "LivelihoodEvent",
            transactionType = "LivelihoodEvent",
            transactionFlow = eventData.selectedEvent.assetJournalEntryFlowType?.name
                ?: BLANK_STRING,
            transactionId = eventData.transactionId,
            subjectId = eventData.subjectId,
            subjectType = "Didi",
            transactionDate = eventData.date,
            referenceId = eventData.livelihoodId,
            status = 1
        )
    }


}
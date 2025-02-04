package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.MoneyJournalDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.MoneyJournalEntity
import com.sarathi.dataloadingmangement.model.events.incomeExpense.SaveMoneyJournalEventDto

class MoneyJournalForPopEventWriterRepositoryImpl(
    moneyJournalDao: MoneyJournalDao,
    coreSharedPrefs: CoreSharedPrefs
) : MoneyJournalForPopEventWriterRepository {
    override suspend fun getMoneyJournalEventDto(
        moneyJournalEntity: MoneyJournalEntity
    ): SaveMoneyJournalEventDto {
        return with(moneyJournalEntity) {
            SaveMoneyJournalEventDto(
                amount = transactionAmount.toInt(),
                particulars = transactionDetails,
                transactionType = transactionType,
                createdDate = createdDate,
                transactionId = transactionId,
                referenceId = referenceId,
                referenceType = referenceType,
                transactionFlow = transactionFlow,
                subjectId = subjectId,
                subjectType = subjectType,
                transactionDate = transactionDate,
                status = status,
                modifiedDate = modifiedDate,
                eventId = eventId,
                eventType = eventType
            )
        }

    }


}
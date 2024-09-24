package com.sarathi.dataloadingmangement.repository

import com.nudge.core.BLANK_STRING
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.toDateString
import com.sarathi.dataloadingmangement.INFLOW
import com.sarathi.dataloadingmangement.KIND
import com.sarathi.dataloadingmangement.OUTFLOW
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
            INFLOW,
            createdDate = System.currentTimeMillis()
        )
        if (moneyJournalDao.isTransactionAlreadyExist(
                userId = coreSharedPrefs.getUniqueUserIdentifier(), transactionId = referenceId
            ) == 0
        ) {
            moneyJournalDao.insetMoneyJournalEntry(moneyJournalEntity)
            if (particulars.contains(KIND)) {
                moneyJournalEntity.transactionFlow = OUTFLOW
                moneyJournalDao.insetMoneyJournalEntry(moneyJournalEntity)
            }
        } else {

            val existingMoneyJournal = moneyJournalDao.getMoneyJournalTransaction(
                userId = coreSharedPrefs.getUniqueUserIdentifier(), transactionId = referenceId
            )
            if (existingMoneyJournal.transactionDetails.contains(KIND)) {
                if (!particulars.contains(KIND)) {
                    moneyJournalDao.softDeleteTransaction(
                        transactionId = referenceId,
                        userId = coreSharedPrefs.getUniqueUserIdentifier(),
                        transactionFlow = OUTFLOW
                    )

                }

            } else {

                if (particulars.contains(KIND)) {
                    moneyJournalEntity.transactionFlow = OUTFLOW
                    moneyJournalDao.insetMoneyJournalEntry(moneyJournalEntity)
                }
            }

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
        eventData: LivelihoodEventScreenData,
        createdData: Long
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
            createdDate = createdData

            )
        moneyJournalDao.insetMoneyJournalEntry(moneyJournalEntity)

    }

    override suspend fun deleteMoneyJournalTransaction(transactionId: String, subjectId: Int) {
        if (moneyJournalDao.isTransactionAlreadyExist(
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                transactionId = transactionId
            ) > 0
        ) {
        moneyJournalDao.softDeleteTransaction(
            transactionId = transactionId,
            subjectId = subjectId,
            userId = coreSharedPrefs.getUniqueUserIdentifier()
        )
        }
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
        eventData: LivelihoodEventScreenData,
        currentDateTime: Long,
        modifiedDateTime: Long
    ): SaveMoneyJournalEventDto {
        return SaveMoneyJournalEventDto(
            amount = eventData.amount,
            createdDate = currentDateTime,
            particulars = particular,
            referenceType = "LivelihoodEvent",
            transactionType = "LivelihoodEvent",
            transactionFlow = eventData.selectedEvent.moneyJournalEntryFlowType?.name
                ?: BLANK_STRING,
            transactionId = eventData.transactionId,
            subjectId = eventData.subjectId,
            subjectType = "Didi",
            transactionDate = eventData.date,
            referenceId = eventData.livelihoodId,
            status = 1,
            modifiedDate = modifiedDateTime
        )
    }

    override suspend fun getMoneyJournalEventForUser(): List<MoneyJournalEntity> {
        return moneyJournalDao.getMoneyJournalTransactionForUser(userId = coreSharedPrefs.getUniqueUserIdentifier())

    }


}
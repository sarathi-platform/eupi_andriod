package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.MoneyJournalDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.MoneyJournalEntity
import javax.inject.Inject

class MoneyJournalForPopRepositoryImpl @Inject constructor(
    val moneyJournalDao: MoneyJournalDao,
    val coreSharedPrefs: CoreSharedPrefs
) : MoneyJournalForPopRepository {

    override suspend fun saveAndUpdateMoneyJournalTransaction(
        amount: Int,
        date: String,
        particulars: String,
        transactionId: String,
        referenceId: Int,
        referenceType: String,
        subjectType: String,
        subjectId: Int,
        transactionFlow: String
    ): MoneyJournalEntity {
        var moneyJournalEntity = MoneyJournalEntity.getMoneyJournalEntity(
            coreSharedPrefs.getUniqueUserIdentifier(),
            amount,
            date,
            particulars,
            transactionId,
            referenceId,
            referenceType,
            subjectType,
            subjectId,
            transactionFlow,
            createdDate = System.currentTimeMillis()
        )
        val existingTransactionCount = moneyJournalDao.isTransactionAlreadyExist(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            referenceId = referenceId,
            referenceType = referenceType,
            subjectId = subjectId,
            subjectType = subjectType
        )
        if (existingTransactionCount == 0
        ) {
            moneyJournalDao.insertMoneyJournalEntry(moneyJournalEntity)
            return moneyJournalEntity
        } else {
            if (existingTransactionCount > 1) {
                deleteOldEntries(
                    referenceId = referenceId,
                    referenceType = referenceType,
                    subjectId = subjectId,
                    subjectType = subjectType
                )
            }
            return updateMoneyJournalTransaction(
                referenceId,
                referenceType,
                subjectId,
                subjectType,
                moneyJournalEntity
            )
        }
    }

    override suspend fun updateMoneyJournalTransaction(
        referenceId: Int,
        referenceType: String,
        subjectId: Int,
        subjectType: String,
        moneyJournalEntity: MoneyJournalEntity
    ): MoneyJournalEntity {
        var updatedMoneyJournalEntity = moneyJournalEntity
        val existingMoneyJournal = moneyJournalDao.getMoneyJournalTransaction(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            referenceId = referenceId,
            referenceType = referenceType,
            subjectId = subjectId,
            subjectType = subjectType
        )

        updatedMoneyJournalEntity =
            updatedMoneyJournalEntity.copy(
                transactionId = existingMoneyJournal.transactionId,
                createdDate = existingMoneyJournal.createdDate
            )

        deleteOldEntries(referenceId, referenceType, subjectId, subjectType)
        moneyJournalDao.insertMoneyJournalEntry(updatedMoneyJournalEntity)

        return updatedMoneyJournalEntity
    }

    override suspend fun deleteOldEntries(
        referenceId: Int,
        referenceType: String,
        subjectId: Int,
        subjectType: String
    ) {
        moneyJournalDao.softDeleteTransaction(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            referenceId = referenceId,
            referenceType = referenceType,
            subjectId = subjectId,
            subjectType = subjectType
        )
    }

}
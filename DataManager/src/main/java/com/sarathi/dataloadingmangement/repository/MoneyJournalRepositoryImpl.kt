package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.INFLOW
import com.sarathi.dataloadingmangement.KIND
import com.sarathi.dataloadingmangement.OUTFLOW
import com.sarathi.dataloadingmangement.data.dao.livelihood.MoneyJournalDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.MoneyJournalEntity
import javax.inject.Inject

class MoneyJournalRepositoryImpl @Inject constructor(
    val moneyJournalDao: MoneyJournalDao, val coreSharedPrefs: CoreSharedPrefs
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
            INFLOW
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


    override suspend fun deleteMoneyJournalTransaction(transactionId: String) {
        moneyJournalDao.softDeleteTransaction(
            transactionId = transactionId, userId = coreSharedPrefs.getUniqueUserIdentifier()
        )
    }


}
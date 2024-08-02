package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.MoneyJournalDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.MoneyJournalEntity
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
            grantType,
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

    override suspend fun deleteMoneyJournalTransaction(transactionId: String) {
        moneyJournalDao.softDeleteTransaction(
            transactionId = transactionId,
            userId = coreSharedPrefs.getUniqueUserIdentifier()
        )
    }


}
package com.sarathi.dataloadingmangement.data.dao.livelihood

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sarathi.dataloadingmangement.data.entities.livelihood.MoneyJournalEntity


@Dao
interface MoneyJournalDao {

    @Insert
    suspend fun insetMoneyJournalEntry(moneyJournalEntity: MoneyJournalEntity)

    @Insert
    suspend fun insertMoneyJournalEntry(moneyJournalEntity: List<MoneyJournalEntity>)

    @Query("Select count(*) from money_journal_table where userId=:userId and transactionId=:transactionId and status=1")
    suspend fun isTransactionAlreadyExist(userId: String, transactionId: String): Int

    @Query("Select * from money_journal_table where userId=:userId and transactionId=:transactionId and status=1")
    suspend fun getMoneyJournalTransaction(
        userId: String,
        transactionId: String
    ): MoneyJournalEntity

    @Query("update money_journal_table set transactionAmount=:amount, transactionDate=:date, transactionDetails=:particulars, modifiedDate=:modifiedDate where userId=:userId and transactionId=:transactionId ")
    suspend fun updateMoneyJournal(
        transactionId: String,
        userId: String,
        date: Long,
        amount: Double,
        particulars: String,
        modifiedDate: Long = System.currentTimeMillis()
    )


    @Query("update money_journal_table set status=0 where transactionId=:transactionId and userId=:userId")
    suspend fun softDeleteTransaction(transactionId: String, userId: String)

    @Query("update money_journal_table set status=0 where transactionId=:transactionId and userId=:userId and transactionFlow=:transactionFlow")
    suspend fun softDeleteTransaction(
        transactionId: String,
        transactionFlow: String,
        userId: String
    )

    @Query("Delete from money_journal_table where userId=:userId")
    suspend fun deleteMoneyJournal(userId: String)
}
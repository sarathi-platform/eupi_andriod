package com.sarathi.dataloadingmangement.data.dao.livelihood

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sarathi.dataloadingmangement.data.entities.livelihood.AssetJournalEntity

@Dao
interface AssetJournalDao {

    @Insert
    suspend fun insetMoneyJournalEntry(moneyJournalEntity: AssetJournalEntity)

    @Insert
    suspend fun insertMoneyJournalEntry(moneyJournalEntity: List<AssetJournalEntity>)

    @Query("Select count(*) from asset_journal_table where userId=:userId and transactionId=:transactionId and status=1")
    suspend fun isTransactionAlreadyExist(userId: String, transactionId: String): Int


    @Query("update asset_journal_table set transactionAmount=:amount, transactionDate=:date, transactionDetails=:particulars, modifiedDate=:modifiedDate where userId=:userId and transactionId=:transactionId ")
    suspend fun updateMoneyJournal(
        transactionId: String,
        userId: String,
        date: Long,
        amount: Double,
        particulars: String,
        modifiedDate: Long = System.currentTimeMillis()
    )


    @Query("update asset_journal_table set status=0 where transactionId=:transactionId and userId=:userId")
    suspend fun softDeleteTransaction(transactionId: String, userId: String)

    @Query("Delete from asset_journal_table where userId=:userId")
    suspend fun deleteMoneyJournal(userId: String)

}
package com.sarathi.dataloadingmangement.data.dao.livelihood

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sarathi.dataloadingmangement.data.entities.livelihood.AssetJournalEntity

@Dao
interface AssetJournalDao {

    @Insert
    suspend fun insetAssetJournalEntry(assetJournal: AssetJournalEntity)

    @Insert
    suspend fun insetAssetJournalEntry(assetJournals: List<AssetJournalEntity>)

    @Query("Select count(*) from asset_journal_table where userId=:userId and transactionId=:transactionId and status=1")
    suspend fun isTransactionAlreadyExist(userId: String, transactionId: String): Int

    @Query("Select * from asset_journal_table where userId=:userId and transactionId=:transactionId and subjectId=:subjectId and status=1")
    suspend fun getAssetJournalForTransaction(
        userId: String,
        transactionId: String,
        subjectId: Int
    ): AssetJournalEntity?


    @Query("update asset_journal_table set assetCount=:amount, transactionDate=:date, transactionDetails=:particulars, modifiedDate=:modifiedDate where userId=:userId and transactionId=:transactionId ")
    suspend fun updateAssetJournal(
        transactionId: String,
        userId: String,
        date: Long,
        amount: Int,
        particulars: String,
        modifiedDate: Long = System.currentTimeMillis()
    )


    @Query("update asset_journal_table set status=0 where transactionId=:transactionId and userId=:userId and subjectId=:subjectId")
    suspend fun softDeleteTransaction(transactionId: String, userId: String, subjectId: Int)

    @Query("Delete from asset_journal_table where userId=:userId")
    suspend fun deleteMoneyJournal(userId: String)

}
package com.patsurvey.nudge.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.patsurvey.nudge.database.BpcSummaryEntity
import com.patsurvey.nudge.utils.BPC_SUMMARY_TABLE

@Dao
interface BpcSummaryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(summaryList: List<BpcSummaryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(summary: BpcSummaryEntity)

    @Query("Select * from $BPC_SUMMARY_TABLE where villageId = :villageId")
    fun getBpcSummaryForVillage(villageId: Int): BpcSummaryEntity?

    @Query("Select * from $BPC_SUMMARY_TABLE")
    fun getBpcSummaryForAllVillage(): List<BpcSummaryEntity?>?
    @Query("DELETE from $BPC_SUMMARY_TABLE")
    fun deleteAllSummary()

    @Query("DELETE from $BPC_SUMMARY_TABLE where villageId = :villageId")
    fun deleteForVillage(villageId: Int)

    @Query("Select * from $BPC_SUMMARY_TABLE where villageId = :villageId")
    fun getBpcSummaryForVillageLiveData(villageId: Int): LiveData<BpcSummaryEntity>

    @Query("SELECT COUNT(*) from $BPC_SUMMARY_TABLE where villageId = :villageId")
    fun isSummaryAlreadyExistsForVillage(villageId: Int): Int

    @Transaction
    fun updateBpcSummaryData(forceRefresh: Boolean = false, villageId: Int, summary: BpcSummaryEntity) {
        if (forceRefresh)
            deleteForVillage(villageId)
        insert(summary)
    }

}
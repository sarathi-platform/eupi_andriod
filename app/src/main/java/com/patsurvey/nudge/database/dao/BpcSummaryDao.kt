package com.patsurvey.nudge.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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

}
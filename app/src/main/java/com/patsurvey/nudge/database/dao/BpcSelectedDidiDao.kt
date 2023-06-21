package com.patsurvey.nudge.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.patsurvey.nudge.database.BpcSelectedDidiEntity
import com.patsurvey.nudge.utils.BPC_SELECTED_DIDI_TABLE

@Dao
interface BpcSelectedDidiDao {

    @Insert
    fun insertDidi(selectedDidiEntity: BpcSelectedDidiEntity)

    @Insert
    fun insertAllDidi(selectedDidiEntityList: List<BpcSelectedDidiEntity>)

    @Query("Select * from $BPC_SELECTED_DIDI_TABLE where activeStatus = 1 and isAlsoSelected = 1 and villageId = :villageId ORDER BY createdDate DESC")
    fun fetchAllSelectedDidiForVillage(villageId: Int): List<BpcSelectedDidiEntity>

    @Query("Select * from $BPC_SELECTED_DIDI_TABLE where activeStatus = 1 and villageId = :villageId ORDER BY createdDate DESC")
    fun fetchAllDidisForVillage(villageId: Int): List<BpcSelectedDidiEntity>

    @Query("SELECT COUNT(*) FROM $BPC_SELECTED_DIDI_TABLE where villageId = :villageId AND patSurveyStatus< 2 AND activeStatus = 1 ORDER BY createdDate DESC")
    fun getAllPendingPATDidisCount(villageId: Int): Int
    @Query("DELETE from $BPC_SELECTED_DIDI_TABLE")
    fun deleteAllDidis()

    @Query("Update $BPC_SELECTED_DIDI_TABLE set isAlsoSelected = :selected where id = :didiId")
    fun markDidiSelected(didiId: Int, selected: Boolean)
}
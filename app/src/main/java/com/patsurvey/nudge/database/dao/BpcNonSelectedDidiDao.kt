package com.patsurvey.nudge.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.patsurvey.nudge.database.BpcNonSelectedDidiEntity
import com.patsurvey.nudge.utils.BPC_NON_SELECTED_DIDI_TABLE

@Dao
interface BpcNonSelectedDidiDao {
    @Insert
    fun insertNonSelectedDidi(selectedDidiEntity: BpcNonSelectedDidiEntity)

    @Insert
    fun insertAllNonSelectedDidi(selectedDidiEntityList: List<BpcNonSelectedDidiEntity>)

    @Query("Select * from $BPC_NON_SELECTED_DIDI_TABLE where activeStatus = 1 and isAlsoSelected = 0 and villageId = :villageId  ORDER BY createdDate DESC")
    fun fetchAllNonSelectedDidiForVillage(villageId: Int): List<BpcNonSelectedDidiEntity>

    @Query("Select * from $BPC_NON_SELECTED_DIDI_TABLE where activeStatus = 1 and villageId =:villageId ORDER BY createdDate DESC")
    fun fetchAllDidisForVillage(villageId: Int): List<BpcNonSelectedDidiEntity>

    @Query("Update $BPC_NON_SELECTED_DIDI_TABLE set isAlsoSelected = :selected where id = :didiId")
    fun markDidiSelected(didiId: Int, selected: Boolean)

    @Query("DELETE from $BPC_NON_SELECTED_DIDI_TABLE")
    fun deleteAllDidis()

    @Query("SELECT * from $BPC_NON_SELECTED_DIDI_TABLE where id = :didiId")
    fun getNonSelectedDidi(didiId: Int): BpcNonSelectedDidiEntity

}
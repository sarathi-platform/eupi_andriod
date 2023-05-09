package com.patsurvey.nudge.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.utils.VILLAGE_TABLE_NAME

@Dao
interface VillageListDao {

    @Query("SELECT * FROM $VILLAGE_TABLE_NAME")
    fun getAllVillages(): List<VillageEntity>

    @Query("Select * FROM $VILLAGE_TABLE_NAME where id = :id")
    fun getVillage(id: Int): VillageEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVillage(village: VillageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(villages: List<VillageEntity>)

    @Query("UPDATE $VILLAGE_TABLE_NAME SET steps_completed = :stepId where id = :villageId")
    fun updateLastCompleteStep(villageId: Int, stepId: List<Int>)

}
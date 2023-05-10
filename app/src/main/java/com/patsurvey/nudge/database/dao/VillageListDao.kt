package com.patsurvey.nudge.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.patsurvey.nudge.database.VillageEntity

@Dao
interface VillageListDao {

    @Query("SELECT * FROM village_table")
    fun getAllVillages(): List<VillageEntity>

    @Query("Select * FROM village_table where id = :id")
    fun getVillage(id: Int): VillageEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVillage(village: VillageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(villages: List<VillageEntity>)

}
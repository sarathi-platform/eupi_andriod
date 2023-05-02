package com.patsurvey.nudge.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.patsurvey.nudge.database.VillageList

@Dao
interface VillageListDao {

    @Query("SELECT * FROM village_list_table")
    fun getAllVillages(): LiveData<List<VillageList>>

    @Query("Select * FROM village_list_table where id = :id")
    fun getVillage(id: Int): VillageList

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVillage(village: VillageList)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(villages: List<VillageList>)

}
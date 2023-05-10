package com.patsurvey.nudge.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.utils.DIDI_TABLE

@Dao
interface DidiDao {

    @Query("SELECT * FROM $DIDI_TABLE")
    fun getAllDidis(): List<DidiEntity>

    @Query("Select * FROM $DIDI_TABLE where id = :id")
    fun getDidi(id: Int): DidiEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDidi(didi: DidiEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(didis: List<DidiEntity>)

}
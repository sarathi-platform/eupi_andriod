package com.patsurvey.nudge.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.utils.STEPS_LIST_TABLE
import com.patsurvey.nudge.utils.TOLA_TABLE

@Dao
interface TolaDao {

    @Query("SELECT * FROM $TOLA_TABLE")
    fun getAllTolas(): List<TolaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(tolas: List<TolaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tola: TolaEntity)

    @Query("DELETE from $TOLA_TABLE where id = :id")
    fun removeTola(id: Int)
}
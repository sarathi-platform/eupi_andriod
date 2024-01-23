package com.nudge.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nudge.core.database.entities.Events

@Dao
interface EventsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event: Events)

    @Query("SELECT * from Events_table")
    fun getAllEvent(): List<Events>

}
package com.nudge.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nudge.core.EventsTable
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventName

@Dao
interface EventsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event: Events)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(events: List<Events>)

    @Query("SELECT * from $EventsTable")
    fun getAllEvent(): List<Events>

    @Query("SELECT * from $EventsTable where name = :eventName")
    fun getAllEventsForEventName(eventName: String): List<Events>

}
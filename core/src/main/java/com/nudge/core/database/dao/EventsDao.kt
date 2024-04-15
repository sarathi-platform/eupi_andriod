package com.nudge.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nudge.core.EventSyncStatus
import com.nudge.core.EventsTable
import com.nudge.core.database.entities.Events

@Dao
interface EventsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event: Events)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(events: List<Events>)

    @Query("SELECT * from $EventsTable")
    fun getAllEvent(): List<Events>

    @Query("SELECT * from $EventsTable where name = :eventName ORDER BY created_date DESC")
    fun getAllEventsForEventName(eventName: String): List<Events>

    @Query("SELECT * from $EventsTable where status in (:status)  ORDER BY id DESC LIMIT 10")
    fun getAllPendingEvent(status: List<EventSyncStatus>): List<Events>

    @Query("SELECT  COUNT(*) from $EventsTable where status in (:status)")
    fun getTotalPendingEventCount(status: List<EventSyncStatus>): Int

    @Query("UPDATE $EventsTable SET status = :newStatus WHERE id = :eventId")
    fun updateEventStatus(eventId: String, newStatus: EventSyncStatus?)


    @Query("DELETE FROM events_table")
    fun deleteAllEvents()

}
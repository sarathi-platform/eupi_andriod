package com.nudge.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nudge.core.EVENT_STATUS_TABLE_NAME
import com.nudge.core.database.entities.EventStatusEntity

@Dao
interface EventStatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(eventStatusEntity: EventStatusEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(eventStatusEntities: List<EventStatusEntity>)

    @Query("SELECT * FROM $EVENT_STATUS_TABLE_NAME")
    fun getAllEventStatus():List<EventStatusEntity>

    @Query("SELECT * FROM $EVENT_STATUS_TABLE_NAME WHERE mobileNumber =:mobileNumber")
    fun getAllEventStatusBetweenDates(mobileNumber:String):List<EventStatusEntity>

    @Query("SELECT * FROM $EVENT_STATUS_TABLE_NAME WHERE mobileNumber =:mobileNumber GROUP BY createdDate")
    fun getAllEventStatusForUser(mobileNumber:String):List<EventStatusEntity>
}
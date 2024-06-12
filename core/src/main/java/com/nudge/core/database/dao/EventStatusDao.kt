package com.nudge.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nudge.core.EventsStatusTable
import com.nudge.core.database.entities.EventStatusEntity

@Dao
interface EventStatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(eventStatusEntity: EventStatusEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(eventStatusEntities: List<EventStatusEntity>)

    @Query("SELECT * FROM $EventsStatusTable")
    fun getAllEventStatus():List<EventStatusEntity>

    @Query("SELECT * FROM $EventsStatusTable WHERE mobile_number =:mobileNumber AND created_date BETWEEN :startDate AND :endDate")
    fun getAllEventStatusBetweenDates(startDate:String,endDate:String,mobileNumber:String):List<EventStatusEntity>

    @Query("SELECT * FROM $EventsStatusTable WHERE mobile_number =:mobileNumber GROUP BY created_date")
    fun getAllEventStatusForUser(mobileNumber:String):List<EventStatusEntity>
}
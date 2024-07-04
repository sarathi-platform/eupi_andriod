package com.nudge.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nudge.core.ImageStatusTable
import com.nudge.core.database.entities.ImageStatusEntity
import java.util.Date

@Dao
interface ImageStatusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(imageStatusEntity: ImageStatusEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(imageEventList: List<ImageStatusEntity>)

    @Query("SELECT * from $ImageStatusTable")
    fun getAllImageEvent(): List<ImageStatusEntity>

    @Query("SELECT * FROM $ImageStatusTable WHERE image_event_id =:eventId AND mobile_number =:mobileNumber")
    fun fetchImageStatusFromEventId(eventId: String, mobileNumber: String): ImageStatusEntity

    @Query("UPDATE $ImageStatusTable SET status=:status, error_message =:errorMessage, modified_date =:modifiedDate WHERE id=:eventId AND mobile_number =:mobileNumber")
    fun updateImageEventStatus(
        status: String,
        eventId: String,
        errorMessage: String,
        modifiedDate: Date,
        mobileNumber: String
    )

}
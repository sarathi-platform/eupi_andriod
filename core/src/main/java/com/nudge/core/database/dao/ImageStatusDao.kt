package com.nudge.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nudge.core.ImageStatusTable
import com.nudge.core.database.entities.ImageStatusEntity
import com.nudge.core.datamodel.SyncImageUploadPayload
import java.util.Date

@Dao
interface ImageStatusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(imageStatusEntity: ImageStatusEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(imageEventList: List<ImageStatusEntity>)

    @Query("SELECT * from $ImageStatusTable")
    fun getAllImageEvent(): List<ImageStatusEntity>

    @Query(
        "Select e.id,e.name,e.type,i.id as imageId,i.mobile_number,i.createdBy,i.file_name,i.file_path,e.status,i.status as imageStatus \n" +
                "from events_table as e JOIN image_status_table as i on e.id == i.image_event_id \n" +
                " where i.status in ('NOT_SYNCED','FAILED') \n" +
                " AND e.status in ('PRODUCER_OPEN')\n" +
                " AND i.mobile_number =:mobileNumber"
    )
    fun getAllValidImageStatusEvent(
        mobileNumber: String,
        eventId: String
    ): List<SyncImageUploadPayload>

    @Query("SELECT * FROM $ImageStatusTable WHERE image_event_id =:eventId AND mobile_number =:mobileNumber")
    fun fetchImageStatusFromEventId(eventId: String, mobileNumber: String): ImageStatusEntity

    @Query("UPDATE $ImageStatusTable SET status=:status, error_message =:errorMessage, modified_date =:modifiedDate WHERE id=:eventId AND mobile_number =:")
    fun updateImageEventStatus(
        status: String,
        eventId: String,
        errorMessage: String,
        modifiedDate: Date,
        mobileNumber: String
    )

}
package com.nudge.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nudge.core.IMAGE_STATUS_TABLE_NAME
import com.nudge.core.database.entities.ImageStatusEntity
import com.nudge.core.model.response.SyncEventResponse
import com.nudge.core.toDate
import java.util.Date

@Dao
interface ImageStatusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(imageStatusEntity: ImageStatusEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(imageEventList: List<ImageStatusEntity>)

    @Query("SELECT * from $IMAGE_STATUS_TABLE_NAME")
    fun getAllImageEvent(): List<ImageStatusEntity>

    @Query("SELECT * FROM $IMAGE_STATUS_TABLE_NAME WHERE imageEventId =:eventId AND mobileNumber =:mobileNumber")
    fun fetchImageStatusFromEventId(eventId: String, mobileNumber: String): ImageStatusEntity

    @Query("SELECT COUNT(*) FROM $IMAGE_STATUS_TABLE_NAME WHERE imageEventId =:eventId AND mobileNumber =:mobileNumber")
    fun fetchImageStatusCount(eventId: String, mobileNumber: String): Int

    @Query("UPDATE $ImageStatusTable SET status=:status, error_message =:errorMessage, modified_date =:modifiedDate,retry_count =:retryCount WHERE id=:eventId AND mobile_number =:mobileNumber")
    fun updateImageEventStatus(
        status: String,
        eventId: String,
        errorMessage: String,
        modifiedDate: Date,
        mobileNumber: String,
        retryCount: Int
    )

    @Transaction
    fun updateImageConsumerStatus(eventList: List<SyncEventResponse>, mobileNumber: String) {
        val modifiedDate = System.currentTimeMillis().toDate()
        eventList.forEach {
            val imageStatusCount =
                fetchImageStatusCount(eventId = it.clientId, mobileNumber = mobileNumber)
            if (imageStatusCount > 0) {
                updateImageEventStatus(
                    eventId = it.clientId,
                    status = it.status,
                    modifiedDate = modifiedDate,
                    errorMessage = it.errorMessage,
                    mobileNumber = mobileNumber,
                    retryCount = 0
                )
            }
        }
    }

}
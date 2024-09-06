package com.nudge.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nudge.core.BLANK_STRING
import com.nudge.core.RequestStatusTable
import com.nudge.core.database.entities.RequestStatusEntity
import java.util.Date

@Dao
interface RequestStatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(requestStatusEntity: RequestStatusEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(requestStatusList: List<RequestStatusEntity>)

    @Query("SELECT * from $RequestStatusTable")
    fun getAllRequestEvent(): List<RequestStatusEntity>

    @Query("UPDATE $RequestStatusTable SET eventCount =:eventCount,status=:status,modifiedDate =:modifiedDate WHERE requestId =:requestId AND mobileNumber =:mobileNumber")
    fun updateEventRequestIdStatus(
        eventCount: Int,
        requestId: String,
        status: String,
        modifiedDate: Date,
        mobileNumber: String
    )

    @Query("SELECT COUNT(*) FROM $RequestStatusTable WHERE requestId =:requestId AND mobileNumber =:mobileNumber")
    fun fetchRequestIdCount(requestId: String, mobileNumber: String): Int

    @Query("SELECT * from $RequestStatusTable WHERE mobileNumber =:mobileNumber AND status in (:status)")
    fun getAllRequestEventForConsumerStatus(
        mobileNumber: String,
        status: List<String>
    ): List<RequestStatusEntity>

    @Transaction
    fun addOrUpdateRequestId(requestStatusEntity: RequestStatusEntity) {
        requestStatusEntity.requestId?.let { requestId ->
            val requestCount = fetchRequestIdCount(requestId, requestStatusEntity.mobileNumber)
            if (requestCount > 0) {
                updateEventRequestIdStatus(
                    eventCount = requestStatusEntity.eventCount ?: 0,
                    status = requestStatusEntity.status ?: BLANK_STRING,
                    requestId = requestStatusEntity.requestId,
                    modifiedDate = requestStatusEntity.modifiedDate,
                    mobileNumber = requestStatusEntity.mobileNumber
                )
            } else insert(requestStatusEntity)
        }
    }
}
package com.nudge.core.database.dao

import android.annotation.SuppressLint
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nudge.core.BLANK_STRING
import com.nudge.core.EventSyncStatus
import com.nudge.core.EventsTable
import com.nudge.core.SOMETHING_WENT_WRONG
import com.nudge.core.database.entities.Events
import com.nudge.core.model.response.SyncEventResponse
import com.nudge.core.toDate
import java.util.Date

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

    @Query("SELECT * from $EventsTable where status in (:status) AND retry_count<= :retryCount AND mobile_number =:mobileNumber ORDER BY modified_date DESC LIMIT :batchLimit")
    fun getAllPendingEvent(status: List<String>,batchLimit:Int,retryCount: Int,mobileNumber:String): List<Events>

    @Query("SELECT  COUNT(*) from $EventsTable where status in (:status) AND mobile_number =:mobileNumber")
    fun getTotalPendingEventCount(status: List<String>,mobileNumber:String): Int

    @Query("DELETE FROM events_table")
    fun deleteAllEvents()
    @Query("UPDATE $EventsTable SET status = :newStatus, modified_date =:modifiedDate,error_message = :errorMessage, retry_count =:retryCount WHERE id = :clientId")
    fun updateEventStatus(clientId: String, newStatus: String,modifiedDate:Date,errorMessage:String,retryCount:Int)

    @Transaction
    fun updateSuccessEventStatus(successEventList:List<SyncEventResponse>){
        val modifiedDate=System.currentTimeMillis().toDate()
            successEventList.forEach {
                updateEventStatus(
                    clientId = it.clientId,
                    newStatus = EventSyncStatus.PRODUCER_SUCCESS.eventSyncStatus,
                    modifiedDate = modifiedDate,
                    errorMessage = BLANK_STRING,
                    retryCount = 0
                )
            }
    }

    @Query("SELECT retry_count from $EventsTable where id =:clientId")
    fun fetchRetryCountForEvent(clientId: String):Int

   @SuppressLint("SuspiciousIndentation")
   @Transaction
    fun updateFailedEventStatus(failedEventList:List<SyncEventResponse>){
        val modifiedDate=System.currentTimeMillis().toDate()

            failedEventList.forEach {
                val retryCount=fetchRetryCountForEvent(clientId = it.clientId).plus(1)
                updateEventStatus(
                    clientId = it.clientId,
                    newStatus = EventSyncStatus.PRODUCER_FAILED.eventSyncStatus,
                    modifiedDate = modifiedDate,
                    errorMessage = it.eventResult.message ?: SOMETHING_WENT_WRONG,
                    retryCount=retryCount
                )
            }

    }

}
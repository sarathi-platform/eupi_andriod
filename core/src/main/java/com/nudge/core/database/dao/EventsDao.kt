package com.nudge.core.database.dao

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
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
import com.nudge.core.model.response.EventConsumerResponse
import com.nudge.core.model.response.SyncEventResponse
import com.nudge.core.toDate
import com.nudge.core.utils.SyncType
import kotlinx.coroutines.flow.Flow
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

    @SuppressLint("SuspiciousIndentation")
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

    @Transaction
    fun updateConsumerStatus(eventList:List<SyncEventResponse>){
        val modifiedDate=System.currentTimeMillis().toDate()
        eventList.forEach {
            updateEventStatus(
                clientId = it.clientId,
                newStatus = it.status,
                modifiedDate = modifiedDate,
                errorMessage = it.errorMessage,
                retryCount = 0
            )
        }
    }

    @Query("SELECT * FROM $EventsTable WHERE mobile_number= :mobileNumber")
    fun getTotalSyncEvent(mobileNumber:String):LiveData<List<Events>>

    @Query("SELECT * FROM $EventsTable WHERE status= :status AND mobile_number= :mobileNumber")
    fun getSuccessEventCount(status:String,mobileNumber:String):List<Events>


    @Query("SELECT * from $EventsTable where status in (:status) AND retry_count<= :retryCount AND mobile_number =:mobileNumber AND type NOT LIKE '%image%' ORDER BY modified_date DESC LIMIT :batchLimit")
    fun getAllPendingDataEvent(
        status: List<String>,
        batchLimit: Int,
        retryCount: Int,
        mobileNumber: String
    ): List<Events>

    @Query("SELECT * from $EventsTable where status in (:status) AND retry_count<= :retryCount AND mobile_number =:mobileNumber AND type LIKE '%image%' ORDER BY modified_date DESC LIMIT :batchLimit")
    fun getAllPendingImageEvent(
        status: List<String>,
        batchLimit: Int,
        retryCount: Int,
        mobileNumber: String
    ): List<Events>

    @Transaction
    fun getAllPendingEventList(status: List<String>,batchLimit:Int,retryCount: Int,mobileNumber:String,syncType:Int): List<Events>{
        return when(syncType){
            SyncType.SYNC_ALL.ordinal ->{
                 getAllPendingEvent(
                    status,
                    batchLimit,
                    retryCount,
                    mobileNumber
                )
            }
            SyncType.SYNC_ONLY_DATA.ordinal -> {
                getAllPendingDataEvent(
                    status,
                    batchLimit,
                    retryCount,
                    mobileNumber
                )
            }

            SyncType.SYNC_ONLY_IMAGES.ordinal -> {
                 getAllPendingImageEvent(
                    status,
                    batchLimit,
                    retryCount,
                    mobileNumber
                )
            }

            else -> {
                emptyList<Events>()
            }
        }
    }
}
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
import com.nudge.core.datamodel.ImageEventDetailsModel
import com.nudge.core.datamodel.RequestIdCountModel
import com.nudge.core.model.response.SyncEventResponse
import com.nudge.core.toDate
import com.nudge.core.utils.SyncType
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

    @Query("SELECT * from $EventsTable where status in (:status) AND retry_count<= :retryCount AND mobile_number =:mobileNumber ORDER BY created_date LIMIT :batchLimit")
    fun getAllPendingEvent(status: List<String>,batchLimit:Int,retryCount: Int,mobileNumber:String): List<Events>

    @Query("SELECT COUNT(*) from $EventsTable where status in (:status) AND mobile_number =:mobileNumber")
    fun getTotalPendingEventCount(status: List<String>,mobileNumber:String): Int

    @Query("DELETE FROM events_table")
    fun deleteAllEvents()

    @Query("UPDATE $EventsTable SET status = :newStatus, modified_date =:modifiedDate,error_message = :errorMessage, retry_count =:retryCount,requestId =:requestId WHERE id = :clientId")
    fun updateEventStatus(
        clientId: String,
        newStatus: String,
        modifiedDate: Date,
        errorMessage: String,
        retryCount: Int,
        requestId: String
    )

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
                    retryCount = 0,
                    requestId = it.requestId
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
                    retryCount = retryCount,
                    requestId = it.requestId
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
                retryCount = 0,
                requestId = it.requestId
            )
        }
    }

    @Query("SELECT * FROM $EventsTable WHERE mobile_number= :mobileNumber")
    fun getTotalSyncEvent(mobileNumber:String):LiveData<List<Events>>

    @Query("SELECT COUNT(*) FROM $EventsTable WHERE mobile_number= :mobileNumber")
    fun getTotalSyncEventCount(mobileNumber: String): Int

    @Query("SELECT * FROM $EventsTable WHERE status= :status AND mobile_number= :mobileNumber")
    fun getSuccessEventCount(status:String,mobileNumber:String):List<Events>


    @Query("SELECT * from $EventsTable where status in (:status) AND retry_count<= :retryCount AND mobile_number =:mobileNumber AND type NOT LIKE '%image%' AND name !='FORM_C_TOPIC' AND name !='FORM_D_TOPIC' ORDER BY created_date LIMIT :batchLimit")
    fun getAllPendingDataEvent(
        status: List<String>,
        batchLimit: Int,
        retryCount: Int,
        mobileNumber: String
    ): List<Events>

    @Query("SELECT * from $EventsTable where status in (:status) AND retry_count<= :retryCount AND mobile_number =:mobileNumber AND (type LIKE '%image%' OR name ='FORM_C_TOPIC' OR name ='FORM_D_TOPIC') ORDER BY created_date LIMIT :batchLimit")
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


    @Query("SELECT  COUNT(*) from $EventsTable where status in (:status) AND mobile_number =:mobileNumber AND type NOT LIKE '%image%' AND name !='FORM_C_TOPIC' AND name !='FORM_D_TOPIC'")
    fun getTotalPendingDataEventCount(status: List<String>, mobileNumber: String): Int

    @Query("SELECT  COUNT(*) from $EventsTable where status in (:status) AND mobile_number =:mobileNumber AND (type LIKE '%image%' OR name ='FORM_C_TOPIC' OR name ='FORM_D_TOPIC')")
    fun getTotalPendingImageEventCount(status: List<String>, mobileNumber: String): Int

    @Transaction
    fun getSyncPendingEventCount(status: List<String>, mobileNumber: String, syncType: Int): Int {
        return when (syncType) {
            SyncType.SYNC_ALL.ordinal -> {
                getTotalPendingEventCount(
                    status,
                    mobileNumber
                )
            }

            SyncType.SYNC_ONLY_DATA.ordinal -> {
                getTotalPendingDataEventCount(
                    status,
                    mobileNumber
                )
            }

            SyncType.SYNC_ONLY_IMAGES.ordinal -> {
                getTotalPendingImageEventCount(
                    status,
                    mobileNumber
                )
            }

            else -> 0
        }
    }

    @Query("SELECT * FROM $EventsTable where id =:eventId")
    fun getEventDetail(eventId: String): Events

    @Query("UPDATE $EventsTable SET retry_count =:retryCount WHERE id =:eventId")
    fun updateEventRetryCount(retryCount: Int, eventId: String)

    @Transaction
    fun findEventAndUpdateRetryCount(eventId: String) {
        val eventDetail = getEventDetail(eventId)
        eventDetail?.let {
            updateEventRetryCount(it.retry_count++, eventId)
        }
    }

    @Query("SELECT * FROM $EventsTable WHERE status IN (:status) and mobile_number =:mobileNumber")
    fun fetchAllFailedEventList(mobileNumber: String, status: List<String>): List<Events>

    @Query("Select events_table.*,image_status_table.id as imageStatusId, image_status_table.fileName as fileName,image_status_table.filePath as filePath from events_table LEFT JOIN image_status_table on events_table.id == image_status_table.imageEventId where  events_table.mobile_number == image_status_table.mobileNumber AND events_table.mobile_number =:mobileNumber AND events_table.id in (:eventIds) ORDER BY events_table.created_date")
    fun fetchAllImageEventsWithImageDetails(
        mobileNumber: String,
        eventIds: List<String>
    ): List<ImageEventDetailsModel>

    @Query("SELECT COUNT(*) from $EventsTable where requestId =:requestId and mobile_number=:mobileNumber")
    fun fetchEventCountDetailForRequestId(requestId: String, mobileNumber: String): Int

    @Query("SELECT status,requestId AS requestId,COUNT(*) AS count from $EventsTable WHERE requestId =:requestId AND mobile_number =:mobileNumber  group by requestId, status")
    fun fetchEventStatusCount(requestId: String, mobileNumber: String): List<RequestIdCountModel>

    @Query("SELECT * FROM $EventsTable WHERE mobile_number =:mobileNumber")
    fun getAllEventsForUser(mobileNumber: String): List<Events>

    @Query("UPDATE $EventsTable SET retry_count =0 WHERE status =:eventStatus and mobile_number =:mobileNo")
    fun resetRetryCountForProducerFailed(eventStatus: String, mobileNo: String)

    @Query("SELECT id FROM $EventsTable WHERE status IN (:status) and mobile_number =:mobileNo")
    fun getAllEventsForConsumerStatus(mobileNo: String, status: List<String>): List<String>
}
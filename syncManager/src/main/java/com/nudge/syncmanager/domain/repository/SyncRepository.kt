package com.nudge.syncmanager.domain.repository

import com.nudge.core.BLANK_STRING
import com.nudge.core.database.entities.Events
import com.nudge.core.database.entities.RequestStatusEntity
import com.nudge.core.datamodel.ImageEventDetailsModel
import com.nudge.core.datamodel.RequestIdCountModel
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.request.EventConsumerRequest
import com.nudge.core.model.request.EventRequest
import com.nudge.core.model.response.SyncEventResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface SyncRepository {
    fun getUserMobileNumber(): String
    fun getUserID(): String
    fun getUserEmail(): String
    fun getUserName(): String
    fun getLoggedInUserType(): String

    suspend fun fetchAllImageEventDetails(eventIds: List<String>): List<ImageEventDetailsModel>
    suspend fun getPendingEventFromDb(
        batchLimit: Int,
        retryCount: Int,
        syncType: Int
    ): List<Events>

    suspend fun getPendingEventCount(syncType: Int): Int
    suspend fun updateSuccessEventStatus(eventList: List<SyncEventResponse>)
    suspend fun updateFailedEventStatus(eventList: List<SyncEventResponse>)
    suspend fun updateEventConsumerStatus(eventList: List<SyncEventResponse>)
    suspend fun updateImageDetailsEventStatus(
        eventId: String,
        status: String,
        requestId: String,
        errorMessage: String? = BLANK_STRING
    )

    suspend fun findEventAndUpdateRetryCount(eventId: String)
    suspend fun findEventCountForRequestId(requestId: String): Int
    suspend fun addOrUpdateRequestStatus(requestId: String, eventCount: Int, status: String)
    suspend fun fetchEventStatusCount(requestId: String): List<RequestIdCountModel>
    suspend fun fetchAllRequestEventForConsumerStatus(): List<RequestStatusEntity>
    suspend fun findRequestEvents(eventList: List<SyncEventResponse>, tag: String)

    // Sync Producer API
    suspend fun syncProducerEventToServer(eventRequest: List<EventRequest>): ApiResponseModel<List<SyncEventResponse>>

    // Sync Producer Image API
    suspend fun syncImageWithEventToServer(
        imageList: List<MultipartBody.Part>,
        imagePayload: RequestBody
    ): ApiResponseModel<List<SyncEventResponse>>

    //Sync Consumer Status API
    suspend fun fetchConsumerEventStatus(eventConsumerRequest: EventConsumerRequest)
            : ApiResponseModel<List<SyncEventResponse>>

    fun getSyncBatchSize(): Int
    fun getSyncRetryCount(): Int

}
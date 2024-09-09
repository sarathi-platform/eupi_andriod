package com.nudge.syncmanager.domain.repository

import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.request.EventConsumerRequest
import com.nudge.core.model.request.EventRequest
import com.nudge.core.model.response.SyncEventResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface SyncApiRepository {

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
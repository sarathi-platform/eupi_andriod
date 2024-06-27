package com.nudge.syncmanager.network

import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.request.EventConsumerRequest
import com.nudge.core.model.request.EventRequest
import com.nudge.core.model.response.SyncEventResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface SyncApiService {
    @POST("/sync-server/sync/events")
    suspend fun syncEvent(@Body eventRequest: List<EventRequest>): ApiResponseModel<List<SyncEventResponse>>

    @POST("sync-server/sync/events-status/by-date")
    suspend fun syncConsumerStatusApi(@Body consumerRequest: EventConsumerRequest)
            : ApiResponseModel<List<SyncEventResponse>>

}
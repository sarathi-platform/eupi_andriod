package com.nudge.syncmanager.network

import com.nudge.core.KEY_HEADER_MOBILE
import com.nudge.core.KEY_HEADER_TYPE
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.request.EventConsumerRequest
import com.nudge.core.model.request.EventRequest
import com.nudge.core.model.response.LastSyncResponseModel
import com.nudge.core.model.response.SyncEventResponse
import com.nudge.core.model.response.SyncImageStatusResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface SyncApiService {
    @POST("/sync-server/sync/events")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun syncEvent(@Body eventRequest: List<EventRequest>): ApiResponseModel<List<SyncEventResponse>>

    @POST("sync-server/sync/events-status/by-date")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun syncConsumerStatusApi(@Body consumerRequest: EventConsumerRequest)
            : ApiResponseModel<List<SyncEventResponse>>

    @Multipart
    @POST("sync-server/sync/upload/file-event")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun syncImage(
        @Part imageFile: MultipartBody.Part,
        @Part("fileEvents") imagePayload: RequestBody
    ): ApiResponseModel<List<SyncImageStatusResponse>>

    @Multipart
    @POST("sync-server/sync/upload/file-event-v1")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun syncImageWithEvent(
        @Part imageFileList: List<MultipartBody.Part>,
        @Part("fileEvents") imagePayload: RequestBody
    ): ApiResponseModel<List<SyncEventResponse>>


    @GET("/sync-server/lastSync/status")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun fetchLastSyncStatus(@Query("mobile") mobileNumber: String): ApiResponseModel<LastSyncResponseModel>
}
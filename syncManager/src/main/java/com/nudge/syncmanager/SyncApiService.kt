package com.nudge.syncmanager

import retrofit2.http.GET
import com.nudge.syncmanager.model.ConfigResponseModel
import com.nudge.core.model.ApiResponseModel

interface SyncApiService {
    @GET("/read-api/config/language/get")
    suspend fun configDetails() : ApiResponseModel<ConfigResponseModel>

}
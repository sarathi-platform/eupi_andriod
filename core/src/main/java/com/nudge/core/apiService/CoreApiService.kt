package com.nudge.core.apiService

import com.nudge.core.model.ApiResponseModel
import retrofit2.http.POST
import retrofit2.http.Query

interface CoreApiService {
    @POST("/registry-service/property")
    suspend fun fetchAppConfig(@Query("mobileNo") mobileNo: String): ApiResponseModel<HashMap<String, String>>

}
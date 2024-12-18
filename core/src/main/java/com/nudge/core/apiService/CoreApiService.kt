package com.nudge.core.apiService

import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.CasteModel
import com.nudge.core.model.request.AppConfigApiRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CoreApiService {
    @POST("/registry-service/property")
    suspend fun fetchAppConfig(@Body appConfigApiRequest: AppConfigApiRequest): ApiResponseModel<HashMap<String, String>>

    // Get CasteList
    @GET("/read-api/config/caste/get")
    suspend fun getCasteList(): ApiResponseModel<List<CasteModel>>


}
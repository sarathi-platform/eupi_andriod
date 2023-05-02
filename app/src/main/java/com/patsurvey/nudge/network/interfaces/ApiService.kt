package com.patsurvey.nudge.network.interfaces

import com.patsurvey.nudge.model.response.ApiResponseModel
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("/read-api/config/language/get")
    suspend fun configDetails() : Response<List<String>>
}
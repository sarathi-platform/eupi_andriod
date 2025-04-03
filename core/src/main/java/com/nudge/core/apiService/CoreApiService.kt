package com.nudge.core.apiService

import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.CasteModel
import com.nudge.core.model.request.AppConfigApiRequest
import com.nudge.core.model.request.RemoteSqlQueryApiRequest
import com.nudge.core.model.response.RemoteSqlQueryApiResponseItem
import com.nudge.core.model.response.TranslationModel
import com.nudge.core.model.response.language.LanguageConfigModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CoreApiService {
    @POST("/registry-service/property")
    suspend fun fetchAppConfig(@Body appConfigApiRequest: AppConfigApiRequest): ApiResponseModel<HashMap<String, String>>

    // Get CasteList
    @GET("/read-api/config/caste/get")
    suspend fun getCasteList(): ApiResponseModel<List<CasteModel>>


    @GET("/registry-service/translations/fetch")
    suspend fun fetchTranslationConfigData(@Query("stateId") stateId: Int): ApiResponseModel<List<TranslationModel>>

    @GET("/read-api/config/language/get/v3")
    suspend fun languageConfigV3(@Query("userId") userId: Int?): ApiResponseModel<LanguageConfigModel>

    @GET("/registry-service/fetchStatus")
    suspend fun fetchRemoteSqlQueryConfig(
        @Query("propertyName") propertyName: String,
        @Query("userId") userId: Int,
        @Query("stateId") stateId: Int
    ): ApiResponseModel<List<RemoteSqlQueryApiResponseItem>>

    @POST("/registry-service/saveStatus")
    suspend fun saveRemoteSqlQueryStatus(@Body apiRequest: List<RemoteSqlQueryApiRequest>): ApiResponseModel<String>


}
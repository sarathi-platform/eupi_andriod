package com.nudge.core.apiService

import com.nudge.core.constants.SUB_PATH_FETCH_TRANSLATIONS
import com.nudge.core.constants.SUB_PATH_GET_CONFIG_CASTE
import com.nudge.core.constants.SUB_PATH_GET_V3_CONFIG_LANGUAGE
import com.nudge.core.constants.SUB_PATH_REGISTRY_SERVICE_PROPERTY
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.CasteModel
import com.nudge.core.model.request.AppConfigApiRequest
import com.nudge.core.model.response.TranslationModel
import com.nudge.core.model.response.language.LanguageConfigModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CoreApiService {
    @POST(SUB_PATH_REGISTRY_SERVICE_PROPERTY)
    suspend fun fetchAppConfig(@Body appConfigApiRequest: AppConfigApiRequest): ApiResponseModel<HashMap<String, String>>

    // Get CasteList
    @GET(SUB_PATH_GET_CONFIG_CASTE)
    suspend fun getCasteList(): ApiResponseModel<List<CasteModel>>


    @GET(SUB_PATH_FETCH_TRANSLATIONS)
    suspend fun fetchTranslationConfigData(@Query("stateId") stateId: Int): ApiResponseModel<List<TranslationModel>>

    @GET(SUB_PATH_GET_V3_CONFIG_LANGUAGE)
    suspend fun languageConfigV3(@Query("userId") userId: Int?): ApiResponseModel<LanguageConfigModel>

}
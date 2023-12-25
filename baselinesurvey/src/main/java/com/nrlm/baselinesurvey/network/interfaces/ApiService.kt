package com.nrlm.baselinesurvey.network.interfaces

import com.google.gson.JsonObject
import com.nrlm.baselinesurvey.KEY_HEADER_MOBILE
import com.nrlm.baselinesurvey.KEY_HEADER_TYPE
import com.nrlm.baselinesurvey.model.request.LoginRequest
import com.nrlm.baselinesurvey.model.request.OtpRequest
import com.nrlm.baselinesurvey.model.request.SaveSurveyRequestModel
import com.nrlm.baselinesurvey.model.request.SurveyRequestBodyModel
import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.model.response.BeneficiaryApiResponse
import com.nrlm.baselinesurvey.model.response.ConfigResponseModel
import com.nrlm.baselinesurvey.model.response.OtpVerificationModel
import com.nrlm.baselinesurvey.model.response.SaveSurveyResponseModel
import com.nrlm.baselinesurvey.model.response.SurveyResponseModel
import com.nrlm.baselinesurvey.model.response.UserDetailsResponse
import com.nrlm.baselinesurvey.network.SUBPATH_AUTH_GENERATE_OTP
import com.nrlm.baselinesurvey.network.SUBPATH_AUTH_VALIDATE_OTP
import com.nrlm.baselinesurvey.network.SUBPATH_CONFIG_GET_LANGUAGE
import com.nrlm.baselinesurvey.network.SUBPATH_FETCH_SURVEY_FROM_NETWORK
import com.nrlm.baselinesurvey.network.SUBPATH_GET_DIDI_LIST
import com.nrlm.baselinesurvey.network.SUBPATH_SAVE_SURVEY_ANSWES
import com.nrlm.baselinesurvey.network.SUBPATH_USER_VIEW
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @GET(SUBPATH_CONFIG_GET_LANGUAGE)
    suspend fun fetchLanguageConfigDetailsFromNetwork() : ApiResponseModel<ConfigResponseModel?>

    @POST(SUBPATH_AUTH_GENERATE_OTP)
    suspend fun generateOtp(@Body loginRequest: LoginRequest
    ): ApiResponseModel<String>

    @POST(SUBPATH_AUTH_VALIDATE_OTP)
    suspend fun validateOtp(@Body otpRequest: OtpRequest): ApiResponseModel<OtpVerificationModel>

    @GET(SUBPATH_GET_DIDI_LIST)
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getDidisFromNetwork(@Query("userId") userId: Int): ApiResponseModel<BeneficiaryApiResponse>

    @GET(SUBPATH_USER_VIEW)
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun userAndVillageListAPI(
        @Query("languageId") languageId: String
    ): ApiResponseModel<UserDetailsResponse>

    @POST(SUBPATH_FETCH_SURVEY_FROM_NETWORK)
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getSurveyFromNetwork(@Body surveyRequestBodyModel: SurveyRequestBodyModel): ApiResponseModel<SurveyResponseModel>

    @POST(SUBPATH_SAVE_SURVEY_ANSWES)
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun saveAnswersToServer(@Body saveSurveyRequest: List<SaveSurveyRequestModel>): ApiResponseModel<SaveSurveyResponseModel>

    @POST("/read-api/custom/log")
    suspend fun addLogs(@Body logsBody: String): JsonObject?

}
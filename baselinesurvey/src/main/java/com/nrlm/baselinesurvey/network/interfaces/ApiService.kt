package com.nrlm.baselinesurvey.network.interfaces

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

    @GET("/read-api/web/upcm/view")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getDidisFromNetwork(@Query("userId") userId: Int): ApiResponseModel<BeneficiaryApiResponse>

    @GET("/read-api/user/view")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun userAndVillageListAPI(
        @Query("languageId") languageId: String
    ): ApiResponseModel<UserDetailsResponse>

    @POST("/survey-engine/survey/view")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getSurveyFromNetwork(@Body surveyRequestBodyModel: SurveyRequestBodyModel): ApiResponseModel<SurveyResponseModel>

    @POST("/baseline-service/baseline/save")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun saveAnswersToServer(@Body saveSurveyRequest: List<SaveSurveyRequestModel>): ApiResponseModel<SaveSurveyResponseModel>

}
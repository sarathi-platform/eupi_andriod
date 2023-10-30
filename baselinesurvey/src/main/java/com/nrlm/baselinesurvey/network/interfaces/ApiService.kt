package com.nrlm.baselinesurvey.network.interfaces

import com.nrlm.baselinesurvey.KEY_HEADER_MOBILE
import com.nrlm.baselinesurvey.KEY_HEADER_TYPE
import com.nrlm.baselinesurvey.model.request.LoginRequest
import com.nrlm.baselinesurvey.model.request.OtpRequest
import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.model.response.BeneficiaryApiResponse
import com.nrlm.baselinesurvey.model.response.ConfigResponseModel
import com.nrlm.baselinesurvey.model.response.OtpVerificationModel
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

    @POST("/write-api/beneficiary/view")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getDidisFromNetwork(@Query("villageId") villageId: Int = 57012): ApiResponseModel<BeneficiaryApiResponse>

    /*@GET("/read-api/user/view")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun userAndVillageListAPI(
        @Query("languageId") languageId: String
    ): ApiResponseModel<UserDetailsResponse>*/

}
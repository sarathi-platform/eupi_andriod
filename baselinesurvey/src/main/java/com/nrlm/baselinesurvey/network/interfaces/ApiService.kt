package com.nrlm.baselinesurvey.network.interfaces

import com.nrlm.baselinesurvey.KEY_HEADER_MOBILE
import com.nrlm.baselinesurvey.KEY_HEADER_TYPE
import com.nrlm.baselinesurvey.model.request.LoginRequest
import com.nrlm.baselinesurvey.model.request.OtpRequest
import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.model.response.ConfigResponseModel
import com.nrlm.baselinesurvey.model.response.OtpVerificationModel
import com.nrlm.baselinesurvey.model.response.UserDetailsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @GET("/read-api/config/language/get")
    suspend fun configDetails() : ApiResponseModel<ConfigResponseModel>

    @POST("/auth-api/user/generate-otp")
    suspend fun generateOtp(@Body loginRequest: LoginRequest
    ): ApiResponseModel<String>

    @POST("/auth-api/user/validate-otp")
    suspend fun validateOtp(@Body otpRequest: OtpRequest): ApiResponseModel<OtpVerificationModel>

    /*@GET("/read-api/user/view")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun userAndVillageListAPI(
        @Query("languageId") languageId: String
    ): ApiResponseModel<UserDetailsResponse>*/

}
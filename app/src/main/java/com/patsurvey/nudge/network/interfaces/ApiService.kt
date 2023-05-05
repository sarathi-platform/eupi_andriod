package com.patsurvey.nudge.network.interfaces


import com.patsurvey.nudge.model.request.LoginRequest
import com.patsurvey.nudge.model.request.OtpRequest
import com.patsurvey.nudge.model.request.StepsListRequest
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.ConfigResponseModel
import com.patsurvey.nudge.model.response.OtpVerificationModel
import com.patsurvey.nudge.model.response.UserDetailsResponse
import com.patsurvey.nudge.utils.KEY_HEADER_MOBILE
import com.patsurvey.nudge.utils.KEY_HEADER_TYPE
import retrofit2.http.*

interface ApiService {

    @GET("/read-api/config/language/get")
    suspend fun configDetails() : ApiResponseModel<ConfigResponseModel>

    @POST("/auth-api/user/generate-otp")
    suspend fun generateOtp(@Body loginRequest: LoginRequest
    ): ApiResponseModel<String>

    @POST("/auth-api/user/validate-otp")
    suspend fun validateOtp(@Body otpRequest: OtpRequest): ApiResponseModel<OtpVerificationModel>

    @GET("/read-api/user/view")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun userAndVillageListAPI(
        @Query("languageId") languageId: Int
    ): ApiResponseModel<UserDetailsResponse>

    @GET("/read-api/config/step/get")
    suspend fun getStepsList(): ApiResponseModel<StepsListRequest>

}
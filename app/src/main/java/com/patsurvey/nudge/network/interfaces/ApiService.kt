package com.patsurvey.nudge.network.interfaces


import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.ConfigResponseModel
import com.patsurvey.nudge.model.request.LoginRequest
import com.patsurvey.nudge.model.request.OtpRequest
import com.patsurvey.nudge.model.response.OtpVerificationModel
import com.patsurvey.nudge.utils.HEADER_TYPE_NONE
import com.patsurvey.nudge.utils.KEY_HEADER_AUTH
import com.patsurvey.nudge.utils.KEY_HEADER_MOBILE
import com.patsurvey.nudge.utils.KEY_HEADER_TYPE
import org.json.JSONObject
import retrofit2.http.*
import retrofit2.http.GET

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
    ): ApiResponseModel<JSONObject>

}
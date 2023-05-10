package com.patsurvey.nudge.network.interfaces


import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.patsurvey.nudge.model.request.AddCohortRequest
import com.patsurvey.nudge.model.request.LoginRequest
import com.patsurvey.nudge.model.request.OtpRequest
import com.patsurvey.nudge.model.request.StepsListRequest
import com.patsurvey.nudge.model.response.*
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

    //TODO Integrate Api when backend fixes the response.
    @POST("/write-api/cohort/add")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun addCohort(@Body cohortList: JsonArray): ApiResponseModel<List<TolaApiResponse>>

    @POST("/write-api/cohort/edit")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun editCohort(@Body updatedCohort: JsonArray): ApiResponseModel<String?>

    @POST("/write-api/cohort/deletet")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun deleteCohort(@Body deleteCohort: JsonArray): ApiResponseModel<String?>

    @GET("/write-api/cohort/view")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getCohortFromNetwork(@Query("villageId") villageId: Int): ApiResponseModel<List<GetCohortResponseModel>>

}
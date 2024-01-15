package com.patsurvey.nudge.network.interfaces


import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.patsurvey.nudge.activities.settings.TransactionIdRequest
import com.patsurvey.nudge.activities.settings.TransactionIdResponse
import com.patsurvey.nudge.activities.settings.TransactionIdResponseForPatStatus
import com.patsurvey.nudge.database.CasteEntity
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.model.request.*
import com.patsurvey.nudge.model.response.*
import com.patsurvey.nudge.utils.KEY_HEADER_MOBILE
import com.patsurvey.nudge.utils.KEY_HEADER_TYPE
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
        @Query("languageId") languageId: String
    ): ApiResponseModel<UserDetailsResponse>

    @GET("/read-api/config/step/get")
    suspend fun getStepsList(@Query("villageId") villageId: Int): ApiResponseModel<StepsListRequest>

    //TODO Integrate Api when backend fixes the response.
    @POST("/write-api/cohort/add")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun addCohort(@Body cohortList: JsonArray): ApiResponseModel<List<TolaApiResponse>>

    @POST("/write-api/cohort/edit")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun editCohort(@Body updatedCohort: JsonArray): ApiResponseModel<List<TolaApiResponse>>

    @POST("/write-api/cohort/delete")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun deleteCohort(@Body deleteCohort: JsonArray): ApiResponseModel<List<TolaApiResponse?>>

    @GET("/write-api/cohort/view")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getCohortFromNetwork(@Query("villageId") villageId: Int): ApiResponseModel<List<TolaEntity>>

    //Get Didi List
    @POST("/write-api/beneficiary/view")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getDidisFromNetwork(@Query("villageId") villageId: Int): ApiResponseModel<BeneficiaryApiResponse>

    @POST("write-api/beneficiary/view")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getDidisWithRankingFromNetwork(@Query("villageId") villageId: Int,
                                                @Query("type") type:String,
                                               @Body stepResultTypeRequest: StepResultTypeRequest): DidiWealthRankingResponse

    @POST("/write-api/beneficiary/add")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun addDidis(@Body didiList: JsonArray): ApiResponseModel<List<DidiApiResponse>>

    // Get CasteList
    @GET("/read-api/config/caste/get")
    suspend fun getCasteList(@Query("languageId") languageId: Int): ApiResponseModel<List<CasteEntity>>

    // Add WorkFlow
    @POST("/write-api/workflow/add")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun addWorkFlow(@Body addWorkFlowRequest: List<AddWorkFlowRequest>):ApiResponseModel<List<WorkFlowResponse>>

    // Edit WorkFlow
    @POST("/write-api/workflow/edit")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun editWorkFlow(@Body addWorkFlowRequest: List<EditWorkFlowRequest>):ApiResponseModel<List<WorkFlowResponse>>

    // Get Questions List
    @POST("/pat-api/pat/view")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun fetchQuestionListFromServer(@Body getQuestionListRequest: GetQuestionListRequest):ApiResponseModel<QuestionListResponse>

    // Edit Didi and updating Wealth Ranking
    @POST("/write-api/beneficiary/edit")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun updateDidiRanking(@Body didiWealthRankingRequest: List<EditDidiWealthRankingRequest>): ApiResponseModel<List<DidiEntity>>

    @POST("/write-api/beneficiary/delete")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun deleteDidi(@Body didiId: JsonArray): ApiResponseModel<List<DidiEntity>>

    @POST("/pat-api/pat/save")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun savePATSurveyToServer(@Body patSummarySaveRequest: List<PATSummarySaveRequest>): ApiResponseModel<List<TransactionResponseModel>>

    @POST("/pat-api/pat/summary")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun fetchPATSurveyToServer(@Body villageIdList: List<Int>): ApiResponseModel<List<PATSummaryResponseItem>>

    @POST("/read-api/callback/status")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getPendingStatus(@Body transactionIdRequest: TransactionIdRequest): ApiResponseModel<List<TransactionIdResponse>>

    @POST("/read-api/callback/status")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getPendingStatusForPat(@Body transactionIdRequest: TransactionIdRequest): ApiResponseModel<List<TransactionIdResponseForPatStatus>>

    @POST("/auth-api/user/logout")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun performLogout() : ApiResponseModel<String>

    @POST("/write-api/beneficiary/edit")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun updateDidiScore(@Body didiWealthRankingRequest: List<EditDidiWealthRankingRequest>): ApiResponseModel<List<DidiEntity>>


    @POST("/write-api/beneficiary/edit")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun updateDidis(@Body didiWealthRankingRequest: List<EditDidiRequest>): ApiResponseModel<List<DidiEntity>>

    @GET("/read-api/bpc/view-summary")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getBpcSummary(@Query("villageId") villageId: Int): ApiResponseModel<BpcSummaryResponse>
    @GET("/write-api/bpc/beneficiary-list")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getDidiForBpcFromNetwork(@Query("villageId") villageId: Int): ApiResponseModel<List<DidiEntity>>


    @POST("/write-api/bpc/update-beneficiary-selection")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun sendSelectedDidiList(@Body bpcBeneficiaryApiResponse: BpcUpdateSelectedDidiRequest): ApiResponseModel<String?>

    @POST("/write-api/bpc/save-summary")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun saveMatchSummary(@Body saveMatchSummaryRequest: ArrayList<SaveMatchSummaryRequest>): ApiResponseModel<ArrayList<SaveMatchSummaryResponse>>

    //https://uat.eupi-sarthi.in/write-api/beneficiary/upload-image
    @Multipart
    @POST("/write-api/beneficiary/upload-image")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun uploadDidiImage(@Part image:MultipartBody.Part,
                                 @Part ("id") didiId:RequestBody,
                                 @Part ("userType") userType:RequestBody,
                                 @Part ("location") location: RequestBody): ApiResponseModel<Object>

    @Multipart
    @POST("/write-api/beneficiary/upload-bulkImages")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun uploadDidiBulkImage(@Part  image:ArrayList<MultipartBody.Part>,
                                @Part ("ids") didiId:ArrayList<RequestBody>,
                                @Part ("userTypes") userType:ArrayList<RequestBody>,
                                @Part ("locations") location: ArrayList<RequestBody>): ApiResponseModel<Object>

    @Multipart
    @POST("/write-api/form/uploadDocument")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun uploadDocument(@Part imageList:List<MultipartBody.Part>,
                                 @Part ("villageId") villageId:RequestBody,
                                 @Part ("flowType") userType:RequestBody): ApiResponseModel<Object>

    @Multipart
    @POST("/write-api/form/uploadDocument")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun uploadDataDump(@Part dataDump: MultipartBody.Part,
                                 @Part ("villageId") villageId:RequestBody,
                                 @Part ("flowType") userType:RequestBody): ApiResponseModel<Object>


    @POST("/read-api/custom/log")
    suspend fun addLogs(@Body logsBody: String): JsonObject?
}
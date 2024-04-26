package com.nrlm.baselinesurvey.network.interfaces

import com.nrlm.baselinesurvey.KEY_HEADER_MOBILE
import com.nrlm.baselinesurvey.KEY_HEADER_TYPE
import com.nrlm.baselinesurvey.model.datamodel.CasteModel
import com.nrlm.baselinesurvey.model.request.ContentMangerRequest
import com.nrlm.baselinesurvey.model.request.FetchSavedSurveyAnswersRequest
import com.nrlm.baselinesurvey.model.request.GetSurveyAnswerRequest
import com.nrlm.baselinesurvey.model.request.LoginRequest
import com.nrlm.baselinesurvey.model.request.MissionRequest
import com.nrlm.baselinesurvey.model.request.OtpRequest
import com.nrlm.baselinesurvey.model.request.SaveSurveyRequestModel
import com.nrlm.baselinesurvey.model.request.SectionStatusRequest
import com.nrlm.baselinesurvey.model.request.SurveyRequestBodyModel
import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.model.response.BeneficiaryApiResponse
import com.nrlm.baselinesurvey.model.response.ConfigResponseModel
import com.nrlm.baselinesurvey.model.response.ContentResponse
import com.nrlm.baselinesurvey.model.response.MissionResponseModel
import com.nrlm.baselinesurvey.model.response.OtpVerificationModel
import com.nrlm.baselinesurvey.model.response.QuestionAnswerResponseModel
import com.nrlm.baselinesurvey.model.response.SavedSurveyAnswersResponse
import com.nrlm.baselinesurvey.model.response.SectionStatusResponseModel
import com.nrlm.baselinesurvey.model.response.SurveyResponseModel
import com.nrlm.baselinesurvey.model.response.TransactionResponse
import com.nrlm.baselinesurvey.model.response.UserDetailsResponse
import com.nrlm.baselinesurvey.network.GET_SECTION_STATUS
import com.nrlm.baselinesurvey.network.SUBPATH_AUTH_GENERATE_OTP
import com.nrlm.baselinesurvey.network.SUBPATH_AUTH_VALIDATE_OTP
import com.nrlm.baselinesurvey.network.SUBPATH_CONFIG_GET_LANGUAGE
import com.nrlm.baselinesurvey.network.SUBPATH_CONTENT_MANAGER
import com.nrlm.baselinesurvey.network.SUBPATH_FETCH_SURVEY_FROM_NETWORK
import com.nrlm.baselinesurvey.network.SUBPATH_GET_CASTE_LIST
import com.nrlm.baselinesurvey.network.SUBPATH_GET_DIDI_LIST
import com.nrlm.baselinesurvey.network.SUBPATH_GET_MISSION
import com.nrlm.baselinesurvey.network.SUBPATH_GET_SAVED_SURVEY
import com.nrlm.baselinesurvey.network.SUBPATH_LOGOUT
import com.nrlm.baselinesurvey.network.SUBPATH_SAVE_SURVEY_ANSWES
import com.nrlm.baselinesurvey.network.SUBPATH_SURVEY_ANSWERS
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
//    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getDidisFromNetwork(@Query("userId") userId: Int): ApiResponseModel<BeneficiaryApiResponse>

    @GET(SUBPATH_USER_VIEW)
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun userAndVillageListAPI(
        @Query("languageId") languageId: String
    ): ApiResponseModel<UserDetailsResponse>

    @POST(SUBPATH_FETCH_SURVEY_FROM_NETWORK)
//    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
//    suspend fun getSurveyFromNetwork(@Body surveyRequestBodyModel: SurveyRequestBodyModel): ApiResponseModel<List<SurveyResponseModel>>
        suspend fun getSurveyFromNetwork(@Body surveyRequestBodyModel: SurveyRequestBodyModel): ApiResponseModel<SurveyResponseModel>


    @POST(SUBPATH_SAVE_SURVEY_ANSWES)
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun saveAnswersToServer(@Body saveSurveyRequest: List<SaveSurveyRequestModel>): ApiResponseModel<List<TransactionResponse>>

    @POST(SUBPATH_GET_SAVED_SURVEY)
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun fetchSavedSurveyAnswersFromServer(@Body savedSurveyAnswersRequest: FetchSavedSurveyAnswersRequest): ApiResponseModel<List<SavedSurveyAnswersResponse>>

    @POST(SUBPATH_GET_MISSION)
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getBaseLineMission(@Body missionRequest: MissionRequest): ApiResponseModel<List<MissionResponseModel>>

    @GET(SUBPATH_GET_CASTE_LIST)
    suspend fun getCasteList(@Query("languageId") languageId: Int): ApiResponseModel<List<CasteModel>>

    @POST(GET_SECTION_STATUS)
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getSectionStatus(@Body sectionStatusRequest: SectionStatusRequest): ApiResponseModel<List<SectionStatusResponseModel>>

    @POST(SUBPATH_LOGOUT)
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun performLogout(): ApiResponseModel<String>

    @POST(SUBPATH_CONTENT_MANAGER)
    suspend fun getAllContent(@Body contentMangerRequest: ContentMangerRequest): ApiResponseModel<List<ContentResponse>>

    @POST(SUBPATH_SURVEY_ANSWERS)
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getSurveyAnswers(@Body surveyAnswerRequest: GetSurveyAnswerRequest): ApiResponseModel<List<QuestionAnswerResponseModel>>
}
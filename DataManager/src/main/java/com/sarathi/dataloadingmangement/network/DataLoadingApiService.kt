package com.sarathi.dataloadingmangement.network

import com.nudge.core.model.ApiResponseModel
import com.sarathi.dataloadingmangement.KEY_HEADER_MOBILE
import com.sarathi.dataloadingmangement.KEY_HEADER_TYPE
import com.sarathi.dataloadingmangement.domain.MissionRequest
import com.sarathi.dataloadingmangement.model.mat.response.ProgrameResponse
import com.sarathi.dataloadingmangement.model.request.SmallGroupApiRequest
import com.sarathi.dataloadingmangement.model.response.BeneficiaryApiResponse
import com.sarathi.dataloadingmangement.model.response.MoneyJournalApiResponse
import com.sarathi.dataloadingmangement.model.response.SmallGroupMappingResponseModel
import com.sarathi.dataloadingmangement.model.survey.request.GetSurveyAnswerRequest
import com.sarathi.dataloadingmangement.model.survey.request.SurveyRequest
import com.sarathi.dataloadingmangement.model.survey.response.QuestionAnswerResponseModel
import com.sarathi.dataloadingmangement.model.survey.response.SurveyResponseModel
import com.sarathi.dataloadingmangement.network.request.AttendanceHistoryRequest
import com.sarathi.dataloadingmangement.network.request.ContentRequest
import com.sarathi.dataloadingmangement.network.response.AttendanceHistoryResponse
import com.sarathi.dataloadingmangement.network.response.ConfigResponseModel
import com.sarathi.dataloadingmangement.network.response.ContentResponse
import com.sarathi.dataloadingmangement.network.response.FormDetailResponseModel
import com.sarathi.dataloadingmangement.network.response.UserDetailsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DataLoadingApiService {
    @POST(SUB_PATH_GET_MISSION)
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getMissions(@Body missionRequest: MissionRequest): ApiResponseModel<List<ProgrameResponse>>

    @POST(SUB_PATH_CONTENT_MANAGER)
    suspend fun fetchContentData(@Body contentMangerRequest: List<ContentRequest>): ApiResponseModel<List<ContentResponse>>

    // @POST("http://10.0.2.2:3001/home") DateType
    @POST(SUBPATH_FETCH_SURVEY_FROM_NETWORK)
    //  @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getSurveyFromNetwork(@Body request: SurveyRequest): ApiResponseModel<SurveyResponseModel>

    @GET(SUBPATH_CONFIG_GET_LANGUAGE)
    suspend fun fetchLanguageConfigDetailsFromNetwork(): ApiResponseModel<ConfigResponseModel?>

    @GET(SUBPATH_USER_VIEW)
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun userAndVillageListAPI(
        @Query("languageId") languageId: String
    ): ApiResponseModel<UserDetailsResponse>

    @POST(SUBPATH_SURVEY_ANSWERS)
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getSurveyAnswers(@Body surveyAnswerRequest: GetSurveyAnswerRequest): ApiResponseModel<List<QuestionAnswerResponseModel>>

    @GET(SUBPATH_GET_FORM_DETAILS)
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getFormDetail(
        @Query("activityId") activityId: Int,
        @Query("surveyId") surveyId: Int,
        @Query("formType") formType: String
    ): ApiResponseModel<List<FormDetailResponseModel>>

    @POST(SUBPATH_GET_SMALL_GROUP_MAPPING)
    suspend fun getSmallGroupBeneficiaryMapping(@Body smallGroupApiRequest: SmallGroupApiRequest): ApiResponseModel<List<SmallGroupMappingResponseModel>>

    @GET(SUBPATH_GET_DIDI_LIST)
    suspend fun getDidisFromNetwork(@Query("userId") userId: Int): ApiResponseModel<BeneficiaryApiResponse>

    @POST(SUBPATH_GET_ATTENDANCE_HISTORY_FROM_NETWORK)
    suspend fun getAttendanceHistoryFromNetwork(@Body attendanceHistoryRequest: AttendanceHistoryRequest): ApiResponseModel<List<AttendanceHistoryResponse>>

    @GET(SUBPATH_GET_MONEY_JOURNAL_DETAILS)
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getMoneyJournalDetails(@Path("doerId") doerId: Int): ApiResponseModel<List<MoneyJournalApiResponse>>

}
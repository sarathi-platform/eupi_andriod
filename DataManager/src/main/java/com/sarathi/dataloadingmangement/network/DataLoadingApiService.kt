package com.sarathi.dataloadingmangement.network

import com.nudge.core.model.ApiResponseModel
import com.sarathi.dataloadingmangement.KEY_HEADER_MOBILE
import com.sarathi.dataloadingmangement.KEY_HEADER_TYPE
import com.sarathi.dataloadingmangement.domain.MissionRequest
import com.sarathi.dataloadingmangement.model.mat.response.ContentResponse
import com.sarathi.dataloadingmangement.model.mat.response.ProgrameResponse
import com.sarathi.dataloadingmangement.model.survey.request.SurveyRequest
import com.sarathi.dataloadingmangement.model.survey.response.SurveyResponseModel
import com.sarathi.dataloadingmangement.network.request.ContentRequest
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface DataLoadingApiService {
    @POST(SUB_PATH_GET_MISSION)
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getMissions(@Body missionRequest: MissionRequest): ApiResponseModel<List<ProgrameResponse>>

    @POST(SUB_PATH_CONTENT_MANAGER)
    suspend fun fetchContentData(@Body contentMangerRequest: ContentRequest): ApiResponseModel<List<ContentResponse>>

    // @POST("http://10.0.2.2:3001/home") DateType
    @POST(SUBPATH_FETCH_SURVEY_FROM_NETWORK)
    //  @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getSurveyFromNetwork(@Body request: SurveyRequest): ApiResponseModel<SurveyResponseModel>


}
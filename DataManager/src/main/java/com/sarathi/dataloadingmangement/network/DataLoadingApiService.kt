package com.sarathi.dataloadingmangement.network

import com.sarathi.dataloadingmangement.network.request.ContentRequest
import com.sarathi.dataloadingmangement.network.request.MissionRequest
import com.sarathi.dataloadingmangement.network.response.ApiResponseModel
import com.sarathi.dataloadingmangement.network.response.ContentResponse
import com.sarathi.dataloadingmangement.network.response.MissionResponseModel
import com.sarathi.dataloadingmangement.util.KEY_HEADER_MOBILE
import com.sarathi.dataloadingmangement.util.KEY_HEADER_TYPE
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface DataLoadingApiService {
    @POST(SUB_PATH_GET_MISSION)
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun fetchMissionData(@Body missionRequest: MissionRequest): ApiResponseModel<List<MissionResponseModel>>

    @POST(SUB_PATH_CONTENT_MANAGER)
    suspend fun fetchContentData(@Body contentMangerRequest: ContentRequest): ApiResponseModel<List<ContentResponse>>
}
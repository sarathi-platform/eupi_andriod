package com.sarathi.dataloadingmangement.network

import com.sarathi.dataloadingmangement.domain.MissionRequest
import com.sarathi.dataloadingmangement.model.ApiResponseModel
import com.sarathi.dataloadingmangement.model.ContentResponse
import com.sarathi.dataloadingmangement.network.request.ContentRequest
import com.sarathi.dataloadingmangement.response.MissionResponseModel
import com.sarathi.dataloadingmangement.util.KEY_HEADER_MOBILE
import com.sarathi.dataloadingmangement.util.KEY_HEADER_TYPE
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface DataLoadingApiService {
    @POST("/mission-service/mission/view")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getBaseLineMission(@Body missionRequest: MissionRequest): ApiResponseModel<List<MissionResponseModel>>

    @POST("/content-manager/content/getContent")
    suspend fun getContent(@Body contentMangerRequest: ContentRequest): ApiResponseModel<List<ContentResponse>>
}
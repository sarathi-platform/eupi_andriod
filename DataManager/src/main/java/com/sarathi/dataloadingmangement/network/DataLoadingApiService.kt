package com.sarathi.dataloadingmangement.network

import com.sarathi.dataloadingmangement.model.ApiResponseModel
import com.sarathi.dataloadingmangement.model.request.MissionRequest
import com.sarathi.dataloadingmangement.model.request.SmallGroupApiRequest
import com.sarathi.dataloadingmangement.model.response.BeneficiaryApiResponse
import com.sarathi.dataloadingmangement.model.response.MissionResponseModel
import com.sarathi.dataloadingmangement.model.response.SmallGroupMappingResponseModel
import com.sarathi.dataloadingmangement.util.KEY_HEADER_MOBILE
import com.sarathi.dataloadingmangement.util.KEY_HEADER_TYPE
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface DataLoadingApiService {
    @POST("/mission-service/mission/view")
    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getBaseLineMission(@Body missionRequest: MissionRequest): ApiResponseModel<List<MissionResponseModel>>

    @POST(SUBPATH_GET_SMALL_GROUP_MAPPING)
//    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getSmallGroupBeneficiaryMapping(@Body smallGroupApiRequest: SmallGroupApiRequest): ApiResponseModel<List<SmallGroupMappingResponseModel>>

    @GET(SUBPATH_GET_DIDI_LIST)
//    @Headers("$KEY_HEADER_TYPE:$KEY_HEADER_MOBILE")
    suspend fun getDidisFromNetwork(@Query("userId") userId: Int): ApiResponseModel<BeneficiaryApiResponse>
}
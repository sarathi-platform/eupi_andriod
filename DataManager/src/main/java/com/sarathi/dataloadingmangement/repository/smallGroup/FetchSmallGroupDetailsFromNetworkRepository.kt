package com.sarathi.dataloadingmangement.repository.smallGroup

import com.nudge.core.database.entities.ApiStatusEntity
import com.nudge.core.model.ApiResponseStatusModel
import com.sarathi.dataloadingmangement.model.response.SmallGroupMappingResponseModel

interface FetchSmallGroupDetailsFromNetworkRepository {

    suspend fun fetchSmallGroupDetails(onResult: (ApiResponseStatusModel) -> Unit)

    suspend fun saveSmallGroupMapping(smallGroupMapping: List<SmallGroupMappingResponseModel>)
    fun updateApiStatus(
        apiEndPoint: String,
        status: Int,
        errorMessage: String,
        errorCode: Int
    )

    fun insertApiStatus(apiEndPoint: String)
    suspend fun isFetchSmallGroupDetailsAPIStatus(): ApiStatusEntity?

}
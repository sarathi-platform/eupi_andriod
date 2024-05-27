package com.sarathi.dataloadingmangement.repository.smallGroup

import com.sarathi.dataloadingmangement.model.response.SmallGroupMappingResponseModel

interface FetchSmallGroupDetailsFromNetworkRepository {

    suspend fun fetchSmallGroupDetails(userId: Int)

    suspend fun saveSmallGroupMapping(smallGroupMapping: List<SmallGroupMappingResponseModel>)

}
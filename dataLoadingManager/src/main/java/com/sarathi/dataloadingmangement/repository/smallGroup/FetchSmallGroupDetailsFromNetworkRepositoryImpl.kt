package com.sarathi.dataloadingmangement.repository.smallGroup

import android.util.Log
import com.nudge.core.SUCCESS_CODE
import com.sarathi.dataloadingmangement.data.dao.smallGroup.SmallGroupDidiMappingDao
import com.sarathi.dataloadingmangement.data.entities.smallGroup.SmallGroupDidiMappingEntity
import com.sarathi.dataloadingmangement.model.request.SmallGroupApiRequest
import com.sarathi.dataloadingmangement.model.response.SmallGroupMappingResponseModel
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import javax.inject.Inject

class FetchSmallGroupDetailsFromNetworkRepositoryImpl @Inject constructor(
//    private val corePrefRepo: CorePrefRepo,
    private val dataLoadingApiService: DataLoadingApiService,
    private val smallGroupDidiMappingDao: SmallGroupDidiMappingDao
) : FetchSmallGroupDetailsFromNetworkRepository {

    private val TAG = FetchSmallGroupDetailsFromNetworkRepositoryImpl::class.java.simpleName

    override suspend fun fetchSmallGroupDetails(userId: Int) {
//        val userId = corePrefRepo.getUserId()

        try {
            val smallGroupApiRequest = SmallGroupApiRequest(userList = listOf(userId))
            val response =
                dataLoadingApiService.getSmallGroupBeneficiaryMapping(smallGroupApiRequest)

            if (response.status.equals(SUCCESS_CODE)) {

                response.data?.let { sgMapping ->
                    saveSmallGroupMapping(sgMapping)
                } ?: throw NullPointerException("Data is null")

            }
        } catch (ex: Exception) {
            Log.e(TAG, "fetchSmallGroupDetails -> exception = ${ex.message}", ex)
        }


    }

    override suspend fun saveSmallGroupMapping(smallGroupMapping: List<SmallGroupMappingResponseModel>) {

        val uniqueUserId = /*corePrefRepo.getUniqueUserIdentifier()*/
            "Ultra Poor change maker (UPCM)_6789543210"

        smallGroupMapping.forEach {

            smallGroupDidiMappingDao.insertAllSmallGroupDidiMapping(
                SmallGroupDidiMappingEntity.getSmallGroupDidiMappingEntityListForSmallGroup(
                    it,
                    uniqueUserId
                )
            )

        }

    }

}
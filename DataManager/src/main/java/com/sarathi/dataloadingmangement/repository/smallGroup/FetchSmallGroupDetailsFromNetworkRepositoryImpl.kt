package com.sarathi.dataloadingmangement.repository.smallGroup

import android.util.Log
import com.nudge.core.SUCCESS_CODE
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.utils.CoreLogger
import com.sarathi.dataloadingmangement.data.dao.smallGroup.SmallGroupDidiMappingDao
import com.sarathi.dataloadingmangement.data.entities.smallGroup.SmallGroupDidiMappingEntity
import com.sarathi.dataloadingmangement.model.request.SmallGroupApiRequest
import com.sarathi.dataloadingmangement.model.response.SmallGroupMappingResponseModel
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import javax.inject.Inject

class FetchSmallGroupDetailsFromNetworkRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val dataLoadingApiService: DataLoadingApiService,
    private val smallGroupDidiMappingDao: SmallGroupDidiMappingDao
) : FetchSmallGroupDetailsFromNetworkRepository {

    private val TAG = FetchSmallGroupDetailsFromNetworkRepositoryImpl::class.java.simpleName

    override suspend fun fetchSmallGroupDetails() {
        try {
            val userId = coreSharedPrefs.getUserName().toInt()
            val smallGroupApiRequest = SmallGroupApiRequest(userList = listOf(userId))
            val response =
                dataLoadingApiService.getSmallGroupBeneficiaryMapping(smallGroupApiRequest)

            if (response.status.equals(SUCCESS_CODE)) {

                response.data?.let { sgMapping ->
                    saveSmallGroupMapping(sgMapping)
                } ?: throw NullPointerException("Data is null")

            }
        } catch (ex: Exception) {
            CoreLogger.e(
                CoreAppDetails.getApplicationContext(),
                TAG,
                "fetchSmallGroupDetails -> exception = ${ex.message}",
                ex
            )
        }


    }

    override suspend fun saveSmallGroupMapping(smallGroupMapping: List<SmallGroupMappingResponseModel>) {

        try {
            val uniqueUserId = coreSharedPrefs.getUniqueUserIdentifier()

            val date = System.currentTimeMillis()

            smallGroupMapping.forEach {

                smallGroupDidiMappingDao.insertAllSmallGroupDidiMapping(
                    SmallGroupDidiMappingEntity.getSmallGroupDidiMappingEntityListForSmallGroup(
                        it,
                        uniqueUserId,
                        date
                    )
                )

            }
        } catch (ex: Exception) {
            Log.e(TAG, "fetchSmallGroupDetails -> exception = ${ex.message}", ex)
        }

    }

}
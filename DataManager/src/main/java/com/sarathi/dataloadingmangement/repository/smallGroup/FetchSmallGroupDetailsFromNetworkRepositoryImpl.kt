package com.sarathi.dataloadingmangement.repository.smallGroup

import android.util.Log
import com.nudge.core.BLANK_STRING
import com.nudge.core.DEFAULT_ERROR_CODE
import com.nudge.core.DEFAULT_SUCCESS_CODE
import com.nudge.core.SUCCESS_CODE
import com.nudge.core.database.dao.ApiStatusDao
import com.nudge.core.database.entities.ApiStatusEntity
import com.nudge.core.enums.ApiStatus
import com.nudge.core.model.ApiResponseStatusModel
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.toDate
import com.nudge.core.utils.CoreLogger
import com.nudge.core.utils.SUBPATH_GET_DIDI_LIST
import com.nudge.core.utils.SUBPATH_GET_SMALL_GROUP_MAPPING
import com.sarathi.dataloadingmangement.data.dao.smallGroup.SmallGroupDidiMappingDao
import com.sarathi.dataloadingmangement.data.entities.smallGroup.SmallGroupDidiMappingEntity
import com.sarathi.dataloadingmangement.model.request.SmallGroupApiRequest
import com.sarathi.dataloadingmangement.model.response.SmallGroupMappingResponseModel
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import javax.inject.Inject

class FetchSmallGroupDetailsFromNetworkRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val dataLoadingApiService: DataLoadingApiService,
    private val smallGroupDidiMappingDao: SmallGroupDidiMappingDao,
    private val apiStatusDao: ApiStatusDao
) : FetchSmallGroupDetailsFromNetworkRepository {

    private val TAG = FetchSmallGroupDetailsFromNetworkRepositoryImpl::class.java.simpleName

    override suspend fun fetchSmallGroupDetails(onResult: (ApiResponseStatusModel) -> Unit) {
        try {
            val userId = coreSharedPrefs.getUserName().toInt()
            val smallGroupApiRequest = SmallGroupApiRequest(userList = listOf(userId))
            val response =
                dataLoadingApiService.getSmallGroupBeneficiaryMapping(smallGroupApiRequest)

            insertApiStatus(apiEndPoint = SUBPATH_GET_SMALL_GROUP_MAPPING)
            if (response.status.equals(SUCCESS_CODE)) {

                response.data?.let { sgMapping ->
                    onResult(
                        ApiResponseStatusModel(
                            apiEndPoint = SUBPATH_GET_SMALL_GROUP_MAPPING,
                            status = ApiStatus.SUCCESS.ordinal,
                            errorMessage = BLANK_STRING,
                            errorCode = DEFAULT_SUCCESS_CODE
                        )
                    )
                    saveSmallGroupMapping(sgMapping)
                } ?: throw NullPointerException("Data is null")

            } else {
                onResult(
                    ApiResponseStatusModel(
                        apiEndPoint = SUBPATH_GET_SMALL_GROUP_MAPPING,
                        status = ApiStatus.FAILED.ordinal,
                        errorMessage = response.message,
                        errorCode = DEFAULT_ERROR_CODE
                    )
                )
            }
        } catch (ex: Exception) {
            onResult(
                ApiResponseStatusModel(
                    apiEndPoint = SUBPATH_GET_SMALL_GROUP_MAPPING,
                    status = ApiStatus.FAILED.ordinal,
                    errorMessage = ex.message ?: BLANK_STRING,
                    errorCode = DEFAULT_ERROR_CODE
                )
            )
            CoreLogger.e(
                CoreAppDetails.getContext()!!,
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
    override fun updateApiStatus(
        apiEndPoint: String,
        status: Int,
        errorMessage: String,
        errorCode: Int
    ) {
        apiStatusDao.updateApiStatus(apiEndPoint, status = status, errorMessage, errorCode)
    }

    override fun insertApiStatus(apiEndPoint: String) {
        val apiStatusEntity = ApiStatusEntity(
            apiEndpoint = apiEndPoint,
            status = ApiStatus.INPROGRESS.ordinal,
            modifiedDate = System.currentTimeMillis().toDate(),
            createdDate = System.currentTimeMillis().toDate(),
            errorCode = 0,
            errorMessage = BLANK_STRING
        )
        apiStatusDao.insert(apiStatusEntity)
    }

    override suspend fun isFetchSmallGroupDetailsAPIStatus(): ApiStatusEntity? {
        return apiStatusDao.getAPIStatus(apiEndpoint = SUBPATH_GET_DIDI_LIST)
    }
}
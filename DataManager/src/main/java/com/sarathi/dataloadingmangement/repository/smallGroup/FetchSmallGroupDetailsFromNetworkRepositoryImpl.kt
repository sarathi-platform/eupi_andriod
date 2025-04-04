package com.sarathi.dataloadingmangement.repository.smallGroup

import android.util.Log
import com.nudge.core.BLANK_STRING
import com.nudge.core.DEFAULT_ERROR_CODE
import com.nudge.core.DEFAULT_SUCCESS_CODE
import com.nudge.core.SUCCESS_CODE
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.ApiCallJournalRepositoryImpl
import com.nudge.core.database.dao.ApiStatusDao
import com.nudge.core.database.entities.ApiStatusEntity
import com.nudge.core.enums.ApiStatus
import com.nudge.core.json
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.toDate
import com.nudge.core.utils.CoreLogger
import com.sarathi.dataloadingmangement.data.dao.smallGroup.SmallGroupDidiMappingDao
import com.sarathi.dataloadingmangement.data.entities.smallGroup.SmallGroupDidiMappingEntity
import com.sarathi.dataloadingmangement.model.request.SmallGroupApiRequest
import com.sarathi.dataloadingmangement.model.response.SmallGroupMappingResponseModel
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import com.sarathi.dataloadingmangement.network.SUBPATH_GET_DIDI_LIST
import com.sarathi.dataloadingmangement.network.SUBPATH_GET_SMALL_GROUP_MAPPING
import javax.inject.Inject

class FetchSmallGroupDetailsFromNetworkRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val dataLoadingApiService: DataLoadingApiService,
    private val smallGroupDidiMappingDao: SmallGroupDidiMappingDao,
    private val apiStatusDao: ApiStatusDao,
    private val apiCallJournalRepository: ApiCallJournalRepositoryImpl
) : FetchSmallGroupDetailsFromNetworkRepository {

    private val TAG = FetchSmallGroupDetailsFromNetworkRepositoryImpl::class.java.simpleName

    override suspend fun fetchSmallGroupDetails(
        screenName: String,
        triggerType: DataLoadingTriggerType,
        moduleName: String,
        customData: Map<String, Any>
    ): Boolean {
        try {
            val userId = coreSharedPrefs.getUserName().toInt()
            val smallGroupApiRequest = SmallGroupApiRequest(userList = listOf(userId))
            val response =
                dataLoadingApiService.getSmallGroupBeneficiaryMapping(smallGroupApiRequest)

            insertApiStatus(apiEndPoint = SUBPATH_GET_SMALL_GROUP_MAPPING)
            if (response.status.equals(SUCCESS_CODE)) {

                response.data?.let { sgMapping ->
                    updateApiStatus(
                        apiEndPoint = SUBPATH_GET_SMALL_GROUP_MAPPING,
                        status = ApiStatus.SUCCESS.ordinal,
                        errorMessage = BLANK_STRING,
                        errorCode = DEFAULT_SUCCESS_CODE
                    )
                    saveSmallGroupMapping(sgMapping)
                } ?: throw NullPointerException("Data is null")
                updateApiCallStatus(
                    screenName = screenName,
                    moduleName = moduleName,
                    triggerType = triggerType,
                    status = ApiStatus.SUCCESS.name,
                    customData = customData,
                    errorMsg = BLANK_STRING
                )
                return true
            } else {
                updateApiStatus(
                    apiEndPoint = SUBPATH_GET_SMALL_GROUP_MAPPING,
                    status = ApiStatus.FAILED.ordinal,
                    errorMessage = response.message,
                    errorCode = DEFAULT_ERROR_CODE
                )
                updateApiCallStatus(
                    screenName = screenName,
                    moduleName = moduleName,
                    triggerType = triggerType,
                    status = ApiStatus.FAILED.name,
                    customData = customData,
                    errorMsg = response.message
                )
                return false
            }
        } catch (ex: Exception) {
            updateApiStatus(
                apiEndPoint = SUBPATH_GET_SMALL_GROUP_MAPPING,
                status = ApiStatus.FAILED.ordinal,
                errorMessage = ex.message ?: BLANK_STRING,
                errorCode = DEFAULT_ERROR_CODE
            )
            updateApiCallStatus(
                screenName = screenName,
                moduleName = moduleName,
                triggerType = triggerType,
                status = ApiStatus.FAILED.name,
                customData = customData,
                errorMsg = ex.stackTraceToString()
            )
            CoreLogger.e(
                CoreAppDetails.getApplicationContext(),
                TAG,
                "fetchSmallGroupDetails -> exception = ${ex.message}",
                ex
            )
            throw ex
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

    suspend fun updateApiCallStatus(
        screenName: String,
        triggerType: DataLoadingTriggerType,
        moduleName: String,
        customData: Map<String, Any>,
        status: String,
        errorMsg: String
    ) {

        apiCallJournalRepository.updateApiCallStatus(
            screenName = screenName,
            moduleName = moduleName,
            dataLoadingTriggerType = triggerType.name,
            requestPayload = customData.json(),
            apiUrl = getApiEndpoint(), status = status, errorMsg = errorMsg
        )
    }

    fun getApiEndpoint(): String {
        return SUBPATH_GET_SMALL_GROUP_MAPPING
    }
}
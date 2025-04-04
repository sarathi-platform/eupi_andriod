package com.sarathi.dataloadingmangement.repository.smallGroup

import android.util.Log
import com.nudge.core.BLANK_STRING
import com.nudge.core.DEFAULT_ERROR_CODE
import com.nudge.core.DEFAULT_SUCCESS_CODE
import com.nudge.core.SUCCESS
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.ApiCallJournalRepositoryImpl
import com.nudge.core.database.dao.ApiStatusDao
import com.nudge.core.database.entities.ApiStatusEntity
import com.nudge.core.enums.ApiStatus
import com.nudge.core.json
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.dataloadingmangement.download_manager.DownloaderManager
import com.sarathi.dataloadingmangement.model.response.BeneficiaryApiResponse
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import com.sarathi.dataloadingmangement.network.SUBPATH_GET_DIDI_LIST
import javax.inject.Inject

class FetchDidiDetailsFromNetworkRepositoryImpl @Inject constructor(
    private val corePrefRepo: CoreSharedPrefs,
    private val dataLoadingApiService: DataLoadingApiService,
    private val subjectEntityDao: SubjectEntityDao,
    private val apiStatusDao: ApiStatusDao,
    private val downloaderManager: DownloaderManager,
    private val apiCallJournalRepository: ApiCallJournalRepositoryImpl
) : FetchDidiDetailsFromNetworkRepository {

    private val TAG = FetchDidiDetailsFromNetworkRepositoryImpl::class.java.simpleName

    override suspend fun fetchDidiDetailsFromNetwork(
        screenName: String,
        triggerType: DataLoadingTriggerType,
        moduleName: String,
        customData: Map<String, Any>,
    ): Boolean {
        try {
            val userId = corePrefRepo.getUserName().toInt()
            val response = dataLoadingApiService.getDidisFromNetwork(userId = userId)
            apiStatusDao.insert(ApiStatusEntity.getApiStatusEntity(SUBPATH_GET_DIDI_LIST))
            if (response.status.equals(SUCCESS)) {
                response.data?.let {
                    updateApiStatus(
                        apiEndPoint = SUBPATH_GET_DIDI_LIST,
                        status = ApiStatus.SUCCESS.ordinal,
                        errorMessage = BLANK_STRING,
                        errorCode = DEFAULT_SUCCESS_CODE
                    )
                    saveDidiDetailsToDb(it)

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
                    apiEndPoint = SUBPATH_GET_DIDI_LIST,
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
                apiEndPoint = SUBPATH_GET_DIDI_LIST,
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
                errorMsg = ex.message ?: BLANK_STRING
            )
            Log.e(TAG, "fetchDidiDetailsFromNetwork -> exception: ${ex.message}", ex)
            throw ex
        }
    }

    override suspend fun saveDidiDetailsToDb(beneficiaryApiResponse: BeneficiaryApiResponse) {
        val uniqueUserId = corePrefRepo.getUniqueUserIdentifier()

        subjectEntityDao.deleteSubjectsForUsers(uniqueUserId)

        val subjectList = ArrayList<SubjectEntity>()
        beneficiaryApiResponse.didiList.forEach {
            val fileLocalPath =
                if (it.crpImageName.isNullOrEmpty()) BLANK_STRING else downloaderManager.getFilePath(
                    it.crpImageName ?: BLANK_STRING
                ).path

            subjectList.add(
                SubjectEntity.getSubjectEntityFromResponse(
                    it,
                    uniqueUserId,
                    crpImageLocalPath = fileLocalPath
                )
            )
            if (!it.crpImageName.isNullOrEmpty()) {
                downloaderManager.downloadItem(it.crpImageName ?: BLANK_STRING)
            }
        }

        subjectEntityDao.addAllSubjects(subjectList)

    }

    override suspend fun isFetchDidiDetailsAPIStatus(): ApiStatusEntity? {
        return apiStatusDao.getAPIStatus(apiEndpoint = SUBPATH_GET_DIDI_LIST)
    }

    override fun updateApiStatus(
        apiEndPoint: String,
        status: Int,
        errorMessage: String,
        errorCode: Int
    ) {
        apiStatusDao.updateApiStatus(apiEndPoint, status = status, errorMessage, errorCode)
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
        return SUBPATH_GET_DIDI_LIST
    }


}
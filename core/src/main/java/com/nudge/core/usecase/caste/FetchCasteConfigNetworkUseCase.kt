package com.nudge.core.usecase.caste

import com.google.android.gms.common.api.ApiException
import com.nudge.core.BLANK_STRING
import com.nudge.core.DEFAULT_ERROR_CODE
import com.nudge.core.DEFAULT_SUCCESS_CODE
import com.nudge.core.SUBPATH_GET_CASTE_LIST
import com.nudge.core.SUCCESS
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.constants.SUB_PATH_GET_CONFIG_CASTE
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.data.repository.IApiCallJournalRepository
import com.nudge.core.data.repository.caste.CasteConfigRepositoryImpl
import com.nudge.core.database.dao.ApiStatusDao
import com.nudge.core.database.entities.ApiStatusEntity
import com.nudge.core.database.entities.CasteEntity
import com.nudge.core.enums.ApiStatus
import com.nudge.core.json
import com.nudge.core.model.CasteModel
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.toDate
import com.nudge.core.utils.CoreLogger
import javax.inject.Inject

class FetchCasteConfigNetworkUseCase @Inject constructor(
    private val casteConfigRepositoryImpl: CasteConfigRepositoryImpl,
    private val apiStatusDao: ApiStatusDao,
    private val coreSharedPrefs: CoreSharedPrefs,
    apiCallJournalRepository: IApiCallJournalRepository,
) : BaseApiCallNetworkUseCase(apiCallJournalRepository) {

    override suspend fun invoke(
        screenName: String,
        triggerType: DataLoadingTriggerType,
        moduleName: String,
        customData: Map<String, Any>
    ): Boolean {
        if (!super.invoke(
                screenName = screenName,
                triggerType = triggerType,
                moduleName = moduleName,
                customData = mapOf(),
            )
        ) {
            return false
        }
        return invoke(screenName = screenName, triggerType = triggerType, moduleName = moduleName)
    }


    suspend operator fun invoke(
        screenName: String = BLANK_STRING,
        triggerType: DataLoadingTriggerType = DataLoadingTriggerType.FRESH_LOGIN,
        moduleName: String = BLANK_STRING,
    ): Boolean {
        try {
            if (!isNeedToCallApi(SUBPATH_GET_CASTE_LIST)) {
                return false
            }
            val casteList = arrayListOf<CasteModel>()
            val casteEntityList = arrayListOf<CasteEntity>()
            insertApiStatus(SUBPATH_GET_CASTE_LIST)
            val casteApiResponse =
                casteConfigRepositoryImpl.getCasteConfigFromNetwork()
            if (casteApiResponse.status.equals(SUCCESS, true)) {
                if (casteApiResponse.data != null) {
                    casteConfigRepositoryImpl.deleteCasteTable()
                    updateApiStatus(
                        SUBPATH_GET_CASTE_LIST,
                        status = ApiStatus.SUCCESS.ordinal,
                        BLANK_STRING,
                        DEFAULT_SUCCESS_CODE
                    )
                    casteApiResponse.data?.forEach { casteModel ->
                        casteEntityList.add(CasteEntity.getCasteEntity(casteModel))
                    }
                    casteConfigRepositoryImpl.insertAll(
                        casteEntityList
                    )
                    casteApiResponse.data?.let { remoteCasteList ->
                        casteList.addAll(casteApiResponse.data)
                    }
                }
                coreSharedPrefs.savePref("caste_list", casteList.json())
                if (screenName.isNotBlank() && moduleName.isNotBlank()) {
                    updateApiCallStatus(
                        screenName = screenName,
                        moduleName = moduleName,
                        triggerType = triggerType,
                        status = ApiStatus.SUCCESS.name,
                        customData = mapOf(),
                        errorMsg = BLANK_STRING
                    )
                }
                return true
            } else {
                coreSharedPrefs.savePref("caste_list", casteList.json())
                updateApiStatus(
                    SUBPATH_GET_CASTE_LIST,
                    status = ApiStatus.FAILED.ordinal,
                    casteApiResponse.message,
                    DEFAULT_ERROR_CODE
                )
                if (screenName.isNotBlank() && moduleName.isNotBlank()) {
                    updateApiCallStatus(
                        screenName = screenName,
                        moduleName = moduleName,
                        triggerType = triggerType,
                        status = ApiStatus.FAILED.name,
                        customData = mapOf(),
                        errorMsg = casteApiResponse.message
                    )
                }
                return false
            }
        } catch (apiException: ApiException) {
            updateApiStatus(
                SUBPATH_GET_CASTE_LIST,
                status = ApiStatus.FAILED.ordinal,
                apiException.message ?: BLANK_STRING,
                apiException.getStatusCode()
            )
            if (screenName.isNotBlank() && moduleName.isNotBlank()) {
                updateApiCallStatus(
                    screenName = screenName,
                    moduleName = moduleName,
                    triggerType = triggerType,
                    status = ApiStatus.FAILED.name,
                    customData = mapOf(),
                    errorMsg = apiException.stackTraceToString()
                )
            }
            throw apiException
        } catch (ex: Exception) {
            updateApiStatus(
                SUBPATH_GET_CASTE_LIST,
                status = ApiStatus.FAILED.ordinal,
                ex.message ?: BLANK_STRING,
                DEFAULT_ERROR_CODE
            )
            CoreLogger.e(
                tag = "FetchCasteConfigNetworkUseCase",
                msg = "invoke: ApiException -> ${ex.message}",
                ex = ex,
            )
            if (screenName.isNotBlank() && moduleName.isNotBlank()) {
                updateApiCallStatus(
                    screenName = screenName,
                    moduleName = moduleName,
                    triggerType = triggerType,
                    status = ApiStatus.FAILED.name,
                    customData = mapOf(),
                    errorMsg = ex.stackTraceToString()
                )
            }
            throw ex
        }
    }

    private fun isNeedToCallApi(apiEndPoint: String): Boolean {
        return if (apiStatusDao.getFailedAPICount() > 0) {
            val apiStatusEntity = apiStatusDao.getAPIStatus(apiEndPoint)
            apiStatusEntity?.status != ApiStatus.SUCCESS.ordinal
        } else {
            true
        }
    }

    fun insertApiStatus(apiEndPoint: String) {
        val apiStatusEntity = ApiStatusEntity(
            apiEndpoint = apiEndPoint,
            status = ApiStatus.INPROGRESS.ordinal,
            modifiedDate = System.currentTimeMillis().toDate(),
            createdDate = System.currentTimeMillis().toDate(),
            errorCode = 0,
            errorMessage = ""
        )
        apiStatusDao.insert(apiStatusEntity)
    }

    fun updateApiStatus(
        apiEndPoint: String,
        status: Int,
        errorMessage: String,
        errorCode: Int
    ) {
        apiStatusDao.updateApiStatus(apiEndPoint, status = status, errorMessage, errorCode)
    }

    suspend fun getCasteIdValue(casteId: Int): String? {
        return casteConfigRepositoryImpl.getCasteIdValue(casteId = casteId) ?: BLANK_STRING
    }

    override fun getApiEndpoint(): String {
        return SUB_PATH_GET_CONFIG_CASTE
    }

}
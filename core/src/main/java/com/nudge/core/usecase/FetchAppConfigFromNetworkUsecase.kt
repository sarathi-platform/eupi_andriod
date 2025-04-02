package com.nudge.core.usecase

import com.google.android.gms.common.api.ApiException
import com.nudge.core.BLANK_STRING
import com.nudge.core.SUCCESS
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.constants.SUB_PATH_REGISTRY_SERVICE_PROPERTY
import com.nudge.core.data.repository.AppConfigDatabaseRepository
import com.nudge.core.data.repository.AppConfigNetworkRepository
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.data.repository.IApiCallJournalRepository
import com.nudge.core.enums.ApiStatus
import com.nudge.core.enums.AppConfigKeysEnum
import javax.inject.Inject

class FetchAppConfigFromNetworkUseCase @Inject constructor(
    val apiConfigNetworkRepository: AppConfigNetworkRepository,
    val apiConfigDatabaseRepository: AppConfigDatabaseRepository,
    apiCallJournalRepository: IApiCallJournalRepository
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
                customData = customData,
            )
        ) {
            return false
        }
        val propertiesName = customData["propertiesName"] as List<String>
        return invoke(
            propertiesName = propertiesName,
            screenName = screenName,
            triggerType = triggerType,
            moduleName = moduleName,
            customData = customData
        )
    }

    suspend operator fun invoke(
        screenName: String = BLANK_STRING,
        triggerType: DataLoadingTriggerType = DataLoadingTriggerType.FRESH_LOGIN,
        moduleName: String = BLANK_STRING,
        customData: Map<String, Any> = mapOf(),
        propertiesName: List<String> = AppConfigKeysEnum.values().map { it.name },
        onApiSuccess: () -> Unit = {}
    ): Boolean {
        try {
            val apiResponse = apiConfigNetworkRepository.getAppConfigFromNetwork(propertiesName)
            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let {
                    apiConfigDatabaseRepository.saveAppConfig(apiResponse.data)
                    onApiSuccess()
                }
                if (screenName.isNotBlank() && moduleName.isNotBlank()) {
                    updateApiCallStatus(
                        screenName = screenName,
                        moduleName = moduleName,
                        triggerType = triggerType,
                        status = ApiStatus.SUCCESS.name,
                        customData = customData,
                        errorMsg = BLANK_STRING
                    )
                }
                return true
            } else {
                if (screenName.isNotBlank() && moduleName.isNotBlank()) {
                    updateApiCallStatus(
                        screenName = screenName,
                        moduleName = moduleName,
                        triggerType = triggerType,
                        status = ApiStatus.FAILED.name,
                        customData = customData,
                        errorMsg = apiResponse.message
                    )
                }
                return false
            }

        } catch (apiException: ApiException) {
            if (screenName.isNotBlank() && moduleName.isNotBlank()) {
                updateApiCallStatus(
                    screenName = screenName,
                    moduleName = moduleName,
                    triggerType = triggerType,
                    status = ApiStatus.FAILED.name,
                    customData = customData,
                    errorMsg = apiException.stackTraceToString()
                )
            }
            throw apiException
        } catch (ex: Exception) {
            if (screenName.isNotBlank() && moduleName.isNotBlank()) {
                updateApiCallStatus(
                    screenName = screenName,
                    moduleName = moduleName,
                    triggerType = triggerType,
                    status = ApiStatus.FAILED.name,
                    customData = customData,
                    errorMsg = ex.stackTraceToString()
                )
            }
            throw ex
        }
    }

    override fun getApiEndpoint(): String {
        return SUB_PATH_REGISTRY_SERVICE_PROPERTY
    }
}
package com.nudge.core.usecase

import com.google.android.gms.common.api.ApiException
import com.nudge.core.SUCCESS
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.constants.SUB_PATH_REGISTRY_SERVICE_PROPERTY
import com.nudge.core.data.repository.AppConfigDatabaseRepository
import com.nudge.core.data.repository.AppConfigNetworkRepository
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.enums.AppConfigKeysEnum
import javax.inject.Inject

class FetchAppConfigFromNetworkUseCase @Inject constructor(
    val apiConfigNetworkRepository: AppConfigNetworkRepository,
    val apiConfigDatabaseRepository: AppConfigDatabaseRepository
) : BaseApiCallNetworkUseCase() {

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
        return invoke(propertiesName = propertiesName)
    }

    suspend operator fun invoke(
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
                return true
            } else {
                return false
            }

        } catch (apiException: ApiException) {
            throw apiException
        } catch (ex: Exception) {
            throw ex
        }
    }

    override fun getApiEndpoint(): String {
        return SUB_PATH_REGISTRY_SERVICE_PROPERTY
    }
}
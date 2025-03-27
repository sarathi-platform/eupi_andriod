package com.nudge.core.usecase

import com.google.android.gms.common.api.ApiException
import com.nudge.core.BLANK_STRING
import com.nudge.core.SUCCESS
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.constants.SUB_PATH_REGISTRY_SERVICE_PROPERTY
import com.nudge.core.data.repository.AppConfigDatabaseRepository
import com.nudge.core.data.repository.AppConfigNetworkRepository
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.data.repository.api.IApiJournalDatabaseRepository
import com.nudge.core.enums.AppConfigKeysEnum
import javax.inject.Inject

class FetchAppConfigFromNetworkUseCase @Inject constructor(
    val apiConfigNetworkRepository: AppConfigNetworkRepository,
    val apiConfigDatabaseRepository: AppConfigDatabaseRepository,
    private val apiJournalDatabaseRepository: IApiJournalDatabaseRepository,
) : BaseApiCallNetworkUseCase() {

    override suspend fun invoke(
        screenName: String,
        triggerType: DataLoadingTriggerType,
        customData: Map<String, Any>
    ): Boolean {
        if (!super.invoke(screenName, triggerType, customData)) {
            return false
        }
        val propertiesName = customData["propertiesName"] as List<String>
        return invoke(propertiesName = propertiesName)
    }

    suspend operator fun invoke(
        screenName: String = BLANK_STRING,
        triggerType: DataLoadingTriggerType = DataLoadingTriggerType.FRESH_LOGIN,
        propertiesName: List<String> = AppConfigKeysEnum.values().map { it.name },
        onApiSuccess: () -> Unit = {}
    ): Boolean {
        try {
            val apiResponse = apiConfigNetworkRepository.getAppConfigFromNetwork(propertiesName)
            val journalEntity = apiJournalDatabaseRepository.getApiCallJournalEntity(
                apiUrl = getApiEndpoint(),
                apiName = getApiEndpoint(),
                status = apiResponse.status,
                moduleName = "MAT",
                screenName = screenName,
                requestBody = propertiesName.toString(),
                triggerPoint = triggerType.name
            )
            apiJournalDatabaseRepository.saveOrUpdateApiJournal(
                journalEntity
            )
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
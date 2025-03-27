package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.data.repository.IApiCallJournalRepository
import com.nudge.core.json
import javax.inject.Inject

abstract class BaseApiCallNetworkUseCase {
    abstract fun getApiEndpoint(): String

    @Inject
    lateinit var apiCallJournalRepository: IApiCallJournalRepository

    open suspend fun invoke(
        screenName: String,
        triggerType: DataLoadingTriggerType,
        moduleName: String,
        customData: Map<String, Any>
    ): Boolean {
        apiCallJournalRepository.addApiCall(
            screenName = screenName,
            moduleName = moduleName,
            dataLoadingTriggerType = triggerType.name,
            requestPayload = customData.json(),
            apiUrl = getApiEndpoint()
        )




        return true
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
}
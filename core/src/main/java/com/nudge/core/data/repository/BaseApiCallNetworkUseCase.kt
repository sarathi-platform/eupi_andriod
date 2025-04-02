package com.nudge.core.data.repository

import com.nudge.core.BLANK_STRING
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.json
import javax.inject.Inject

open class BaseApiCallNetworkUseCase @Inject constructor(val apiCallJournalRepository: IApiCallJournalRepository) {
    open fun getApiEndpoint(): String {
        return BLANK_STRING
    }

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
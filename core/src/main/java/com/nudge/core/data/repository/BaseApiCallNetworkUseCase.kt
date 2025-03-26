package com.nudge.core.data.repository

import com.nudge.core.constants.DataLoadingTriggerType

abstract class BaseApiCallNetworkUseCase {
    abstract fun getApiEndpoint(): String

    init {

        //get the appApiConfig
    }

    open suspend fun invoke(
        screenName: String,
        triggerType: DataLoadingTriggerType,
        customData: Map<String, Any>
    ): Boolean {

        return true
    }
}
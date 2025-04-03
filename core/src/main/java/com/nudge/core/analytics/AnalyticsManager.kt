package com.nudge.core.analytics

import com.nudge.core.BLANK_STRING
import com.nudge.core.analytics.mixpanel.AnalyticsEventsParam
import com.nudge.core.json
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.utils.CoreLogger


class AnalyticsManager(private var analyticsProvider: IAnalyticsProvider) {

    private val TAG = AnalyticsManager::class.java.simpleName

    fun trackEvent(eventName: String, properties: Map<String, Any>? = null) {
        val updatedProperties = properties?.toMutableMap() ?: mutableMapOf()
        updatedProperties[AnalyticsEventsParam.BUILD_ENVIRONMENT_NAME.name] =
            CoreAppDetails.getApplicationDetails()?.buildEnvironment ?: BLANK_STRING
        CoreLogger.d(
            tag = TAG,
            msg = "Event -> $eventName, properties: ${updatedProperties?.json()}"
        )
        analyticsProvider.trackEvent(eventName, updatedProperties)
    }

    fun logError(error: String, properties: Map<String, Any>? = null) {
        CoreLogger.e(tag = TAG, msg = "error -> $error, properties: ${properties?.json()}")
        analyticsProvider.logError(error, properties)
    }

    fun setUserDetail(
        distinctId: String,
        name: String,
        userType: String,
        buildEnvironment: String
    ) {
        CoreLogger.d(
            tag = TAG,
            msg = "setUserDetail: -> distinctId: $distinctId, name: $name, userType: $userType, buildEnvironment: $buildEnvironment"
        )
        analyticsProvider.setUserDetail(distinctId, name, userType)
    }
}
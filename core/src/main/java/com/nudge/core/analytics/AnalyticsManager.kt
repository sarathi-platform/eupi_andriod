package com.nudge.core.analytics

import com.nudge.core.json
import com.nudge.core.utils.CoreLogger


class AnalyticsManager(private var analyticsProvider: IAnalyticsProvider) {

    private val TAG = AnalyticsManager::class.java.simpleName

    fun trackEvent(eventName: String, properties: Map<String, Any>? = null) {
        CoreLogger.d(tag = TAG, msg = "Event -> $eventName, properties: ${properties?.json()}")
        analyticsProvider.trackEvent(eventName, properties)
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
        analyticsProvider.setUserDetail(distinctId, name, userType, buildEnvironment)
    }
}
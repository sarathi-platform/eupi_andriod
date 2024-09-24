package com.nudge.core.analytics


class AnalyticsManager(private var analyticsProvider: IAnalyticsProvider) {



    fun trackEvent(eventName: String, properties: Map<String, Any>? = null) {
        analyticsProvider.trackEvent(eventName, properties)
    }

    fun logError(error: String, properties: Map<String, Any>? = null) {
        analyticsProvider.logError(error, properties)
    }
    fun setUserDetail(distinctId: String, name: String, userType: String) {
        analyticsProvider.setUserDetail(distinctId, name, userType)
    }
}
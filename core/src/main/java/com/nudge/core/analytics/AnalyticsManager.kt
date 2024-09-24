package com.nudge.core.analytics


class AnalyticsManager(private var analyticsProvider: IAnalyticsProvider) {

    fun setAnalyticsProvider(provider: IAnalyticsProvider) {
        analyticsProvider = provider
    }

    fun trackEvent(eventName: String, properties: Map<String, Any>? = null) {
        analyticsProvider.trackEvent(eventName, properties)
    }

    fun logError(error: String, properties: Map<String, Any>? = null) {
        analyticsProvider.logError(error, properties)
    }
}
package com.nudge.core.analytics

interface IAnalyticsProvider {
    fun trackEvent(eventName: String, properties: Map<String, Any>? = null)
    fun logError(error: String, properties: Map<String, Any>? = null)
}
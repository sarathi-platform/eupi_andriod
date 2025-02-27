package com.nudge.core.analytics


interface IAnalyticsProvider {
    fun trackEvent(eventName: String, properties: Map<String, Any>? = null)
    fun logError(error: String, properties: Map<String, Any>? = null)
    fun setUserDetail(distinctId: String, name: String, userType: String, buildEnvironment: String)
}
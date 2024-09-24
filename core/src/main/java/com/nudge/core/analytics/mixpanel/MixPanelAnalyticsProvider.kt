package com.nudge.core.analytics.mixpanel

import com.nudge.core.analytics.IAnalyticsProvider

class MixPanelAnalyticsProvider : IAnalyticsProvider {
    override fun trackEvent(eventName: String, properties: Map<String, Any>?) {
        TODO("Not yet implemented")
    }

    override fun logError(error: String, properties: Map<String, Any>?) {
        TODO("Not yet implemented")
    }
}
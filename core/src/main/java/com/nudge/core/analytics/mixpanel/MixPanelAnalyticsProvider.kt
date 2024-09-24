package com.nudge.core.analytics.mixpanel

import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.nudge.core.analytics.IAnalyticsProvider
import org.json.JSONObject

class MixPanelAnalyticsProvider(val mixPanel: MixpanelAPI) : IAnalyticsProvider {
    override fun trackEvent(eventName: String, properties: Map<String, Any>?) {
        mixPanel.track(eventName, properties?.let { JSONObject(it) })
    }

    override fun logError(error: String, properties: Map<String, Any>?) {
        val errorProps = JSONObject(properties)
        mixPanel.track("error_$error", errorProps)
    }
}
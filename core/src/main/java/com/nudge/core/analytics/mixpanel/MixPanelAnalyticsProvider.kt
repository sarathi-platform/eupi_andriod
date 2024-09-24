package com.nudge.core.analytics.mixpanel

import android.content.Context
import com.mixpanel.android.mpmetrics.MixpanelAPI


import com.nudge.core.analytics.IAnalyticsProvider
import org.json.JSONObject

class MixPanelAnalyticsProvider(context: Context) : IAnalyticsProvider {
    var mixpanelAPI: MixpanelAPI

    init {
        mixpanelAPI = MixpanelAPI.getInstance(context, "618e2ccf90d6fcc20c66280888c938cd", true);

    }

    override fun trackEvent(eventName: String, properties: Map<String, Any>?) {
        mixpanelAPI.track(eventName, properties?.let { JSONObject(it) })
    }

    override fun logError(error: String, properties: Map<String, Any>?) {
        val errorProps = JSONObject(properties)
        mixpanelAPI.track("error_$error", errorProps)
    }

    override fun setUserDetail(distinctId: String, name: String, userType: String) {
        mixpanelAPI.identify(distinctId)
        mixpanelAPI?.getPeople()?.set("name", name);
        mixpanelAPI?.getPeople()?.set("userType", userType);

    }
}
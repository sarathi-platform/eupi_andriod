package com.nudge.core.analytics.mixpanel


import android.content.Context
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.nudge.core.BLANK_STRING
import com.nudge.core.analytics.IAnalyticsProvider
import com.nudge.core.preference.CoreSharedPrefs
import org.json.JSONObject
import javax.inject.Inject

class MixPanelAnalyticsProvider @Inject constructor(
    val context: Context,
    val sharedPrefs: CoreSharedPrefs
) : IAnalyticsProvider {
    var mixpanelAPI: MixpanelAPI? = null

    init {
        intiMixpanelApi()
    }

    private fun intiMixpanelApi(
    ) {
        val token = sharedPrefs.getMixPanelToken()
        if (!token.equals(BLANK_STRING, true))
            mixpanelAPI = MixpanelAPI.getInstance(context, token, true)
    }

    override fun trackEvent(eventName: String, properties: Map<String, Any>?) {
        if (mixpanelAPI == null) {
            intiMixpanelApi()
        }
        mixpanelAPI?.let {
            it.track(eventName, properties?.let { JSONObject(it) })
        }
    }

    override fun logError(error: String, properties: Map<String, Any>?) {
        mixpanelAPI?.let {
            val errorProps = properties?.let { JSONObject(it) }
            it.track("error_$error", errorProps)
        }
    }

    override fun setUserDetail(
        distinctId: String,
        name: String,
        userType: String,
        buildEnvironment: String
    ) {
        mixpanelAPI?.let {
            it.identify(distinctId)
            it.people?.set("name", name)
            it.people?.set("userType", userType)
            it.people?.set("build_environment", buildEnvironment)
        }
    }
}
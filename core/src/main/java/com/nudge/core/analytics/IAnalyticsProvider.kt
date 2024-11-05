package com.nudge.core.analytics

import com.nudge.core.datamodel.FederationDetailModel


interface IAnalyticsProvider {
    fun trackEvent(eventName: String, properties: Map<String, Any>? = null)
    fun logError(error: String, properties: Map<String, Any>? = null)
    fun setUserDetail(distinctId: String, name: String, userType: String, federationDetailModel: FederationDetailModel? = null, buildEnvironment: String)
}
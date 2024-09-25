package com.nudge.core.analytics

import com.nudge.core.analytics.mixpanel.AnalyticsEvents
import com.nudge.core.analytics.mixpanel.AnalyticsEventsParam
import com.nudge.core.utils.SyncType


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

    fun sendSyncSuccessEvent(selectedSyncType: Int) {
        analyticsProvider.trackEvent(
            AnalyticsEvents.SYNC_SUCCESS.eventName,
            mapOf(
                AnalyticsEventsParam.SYNC_TYPE.eventParam to SyncType.getSyncTypeFromInt(
                    selectedSyncType
                )
            )
        )
    }
}
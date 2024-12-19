package com.nudge.core.usecase

import com.nudge.core.analytics.AnalyticsManager
import javax.inject.Inject

class AnalyticsEventUseCase @Inject constructor(
    private val analyticsManager: AnalyticsManager
) {
   fun sentAnalyticsEvent(eventName:String , param :Map<String,Any>  = mapOf()) {
        analyticsManager.trackEvent(
            eventName,param
        )
    }

}
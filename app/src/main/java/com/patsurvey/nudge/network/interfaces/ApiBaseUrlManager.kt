package com.patsurvey.nudge.network.interfaces

import com.patsurvey.nudge.BuildConfig

object ApiBaseUrlManager {
    var baseUrl: String = BuildConfig.BASE_URL

    fun updateBaseUrl(newBaseUrl: String) {
        baseUrl = newBaseUrl
    }
}
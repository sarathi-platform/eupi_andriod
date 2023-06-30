package com.patsurvey.nudge.analytics

import android.os.Build
import com.patsurvey.nudge.BuildConfig

enum class EventValues(val eventValue: String) {

    SDK_INT_VALUE("${Build.VERSION.SDK_INT}"),
    BUILD_VERSION_NAME(BuildConfig.VERSION_NAME),
    TIME_STAMP("${System.currentTimeMillis()}"),
    DEVICE(Build.DEVICE),
    MANUFACTURER(Build.MANUFACTURER),
    MODEL(Build.MODEL),
    BRAND(Build.BRAND)
}
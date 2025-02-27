package com.nudge.core.model

import com.google.gson.annotations.SerializedName
import com.nudge.core.APP_UPDATE_IMMEDIATE
import com.nudge.core.BLANK_STRING

data class AppUpdateConfigModel(
    @SerializedName("isAppNeedUpdate")
    val isAppNeedUpdate: Boolean? = false,
    @SerializedName("minimumVersionCode")
    val minimumVersionCode: Int? = 0,
    @SerializedName("updateType")
    val updateType: String? = APP_UPDATE_IMMEDIATE,
    @SerializedName("isInAppUpdate")
    val isInAppUpdate: Boolean? = false,
    @SerializedName("redirectLink")
    val redirectLink: String? = BLANK_STRING,
    @SerializedName("latestVersionCode")
    val latestVersionCode: Int? = 0
)


package com.patsurvey.nudge.utils

import com.nudge.core.enums.NetworkSpeed

data class NetworkInfo(
    var isOnline: Boolean,
    var connectionSpeed: Int,
    var speedType: NetworkSpeed
) {
}
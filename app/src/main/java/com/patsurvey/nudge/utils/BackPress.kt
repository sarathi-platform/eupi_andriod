package com.patsurvey.nudge.utils

sealed class BackPress {
    object Idle : BackPress()
    object InitialTouch : BackPress()
}

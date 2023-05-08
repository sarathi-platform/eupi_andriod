package com.patsurvey.nudge.customviews


sealed class CustomSnackBarViewPosition {

    object Top: CustomSnackBarViewPosition()

    object Bottom: CustomSnackBarViewPosition()

    object Float: CustomSnackBarViewPosition()
}
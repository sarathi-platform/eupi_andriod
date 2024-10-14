package com.nudge.core.model

import androidx.annotation.DrawableRes
import com.nudge.core.R

data class EventLimitAlertUiModel(
    val alertTitle: String,
    val alertMessage: String,
    @DrawableRes val alertIcon: Int
) {

    companion object {

        fun getDefaultEventLimitAlertUiModel(
            alertTitle: String = "Warning",
            alertMessage: String = "Sync pending data now to stay up-to-date!",
            alertIcon: Int = R.drawable.baseline_warning_amber_24
        ): EventLimitAlertUiModel {
            return EventLimitAlertUiModel(
                alertTitle,
                alertMessage,
                alertIcon
            )
        }

        fun getHardEventLimitAlertUiModel(
            alertTitle: String = "Alert",
            alertMessage: String = "Sync now to prevent data loss!",
            alertIcon: Int = R.drawable.baseline_warning_24
        ): EventLimitAlertUiModel {
            return getDefaultEventLimitAlertUiModel(alertTitle, alertMessage, alertIcon)
        }

    }

}

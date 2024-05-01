package com.nudge.core.ui.events

sealed class ToastMessageEvent {
    data class ShowToastMessage(
        val message: String
    ) : ToastMessageEvent()
}
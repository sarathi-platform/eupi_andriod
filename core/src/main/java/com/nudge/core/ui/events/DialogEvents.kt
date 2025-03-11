package com.nudge.core.ui.events

sealed class DialogEvents {

    data class ShowDialogEvent(val showDialog: Boolean = false) : DialogEvents()
    data class ShowAttendanceDialogEvent(
        val showDialog: Boolean = false,
        val isFromBackButton: Boolean
    ) : DialogEvents()

    data class ShowAlertDialogEvent(val showDialog: Boolean) : DialogEvents()

}
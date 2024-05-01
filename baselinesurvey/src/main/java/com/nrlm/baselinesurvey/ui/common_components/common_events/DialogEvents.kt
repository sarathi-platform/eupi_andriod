package com.nrlm.baselinesurvey.ui.common_components.common_events

sealed class DialogEvents {

    data class ShowDialogEvent(val showDialog: Boolean = false) : DialogEvents()

}
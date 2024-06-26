package com.nudge.core.utils.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf

data class DialogState(val isDialogVisible: Boolean = false) {

    val showDialogState =
        mutableStateOf(isDialogVisible)

    fun getDialogStateVisibility() = showDialogState.value

    fun show() {
        updateDialogVisibilityState(true)
    }

    fun hide() {
        updateDialogVisibilityState(false)
    }

    private fun updateDialogVisibilityState(showDialog: Boolean) {
        this.showDialogState.value = showDialog
    }

}

@Composable
fun rememberDialogState(initialValue: Boolean = false): DialogState {
    return DialogState(initialValue)
}

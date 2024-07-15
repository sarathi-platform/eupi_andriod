package com.sarathi.missionactivitytask.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.nudge.core.utils.CoreLogger
import com.sarathi.missionactivitytask.utils.LoaderState
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
import kotlinx.coroutines.CoroutineExceptionHandler

abstract class BaseViewModel : ViewModel() {
    val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState
    abstract fun <T> onEvent(event: T)

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, e ->
        CoreLogger.e(
            tag = "MAT",
            msg = e?.localizedMessage ?: com.nudge.core.BLANK_STRING,
            stackTrace = true,
            ex = e
        )
        onCatchError(e)
    }

    open fun onCatchError(e: Throwable) {
        onEvent(LoaderEvent.UpdateLoaderState(false))

    }

    fun loaderState(): LoaderState = loaderState.value

    open fun refreshData() {
    }

}
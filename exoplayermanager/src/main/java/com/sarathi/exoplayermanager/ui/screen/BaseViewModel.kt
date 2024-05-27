package com.sarathi.exoplayermanager.ui.screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.sarathi.exoplayermanager.utils.LoaderState
import com.sarathi.exoplayermanager.utils.event.LoaderEvent
import kotlinx.coroutines.CoroutineExceptionHandler

abstract class BaseViewModel : ViewModel() {
    val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState
    abstract fun <T> onEvent(event: T)

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, e ->
        onCatchError(e)
    }

    open fun onCatchError(e: Throwable) {
        onEvent(LoaderEvent.UpdateLoaderState(false))

    }

    fun loaderState(): LoaderState = loaderState.value

}
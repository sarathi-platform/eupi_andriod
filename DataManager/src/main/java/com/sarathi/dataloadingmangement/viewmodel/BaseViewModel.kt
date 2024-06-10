package com.sarathi.dataloadingmangement.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarathi.dataloadingmangement.util.LoaderState
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class BaseViewModel : ViewModel() {
    val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState

    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
    val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
    abstract fun <T> onEvent(event: T)

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, e ->
        onCatchError(e)
    }

    open fun onCatchError(e: Throwable) {
        onEvent(LoaderEvent.UpdateLoaderState(false))

    }

    fun loaderState(): LoaderState = loaderState.value
    fun ViewModel.ioViewModelScope(
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) {
        viewModelScope.launch(context = ioDispatcher, start = start) {
            block()
        }
    }

    fun ViewModel.launchVieModelScope(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) {

        viewModelScope.launch(context = context, start = start) {
            block()
        }
    }

}
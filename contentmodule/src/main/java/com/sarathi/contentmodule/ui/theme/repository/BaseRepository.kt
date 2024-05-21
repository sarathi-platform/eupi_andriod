package com.sarathi.contentmodule.ui.theme.repository

import com.sarathi.contentmodule.utils.event.LoaderEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job

abstract class BaseRepository {
    var repoJob: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, e ->
        onCatchError(e)
    }

    open fun onCatchError(e: Throwable) {
        onEvent(LoaderEvent.UpdateLoaderState(false))

    }

    abstract fun <T> onEvent(event: T)

}
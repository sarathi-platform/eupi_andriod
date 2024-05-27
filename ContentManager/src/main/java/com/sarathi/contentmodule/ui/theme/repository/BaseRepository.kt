package com.sarathi.contentmodule.ui.theme.repository

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job

abstract class BaseRepository {
    var repoJob: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, e ->
        onCatchError(e)
    }

    open fun onCatchError(e: Throwable) {
    }


}
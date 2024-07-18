package com.sarathi.contentmodule.ui.repository

import com.nudge.core.model.CoreAppDetails
import com.nudge.core.utils.CoreLogger
import com.sarathi.dataloadingmangement.BLANK_STRING
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job

abstract class BaseRepository {
    var repoJob: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, e ->
        onCatchError(e)
    }

    open fun onCatchError(e: Throwable) {
        CoreAppDetails.getContext()?.applicationContext?.let {
            CoreLogger.d(
                context = it,
                tag = "BaseRepository->",
                msg = e.message ?: BLANK_STRING
            )
        }
    }


}
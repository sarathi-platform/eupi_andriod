package com.patsurvey.nudge.base

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job

open class BaseViewModel : ViewModel(){
    var job: Job? = null

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onError(message = "Exception handled: ${throwable.localizedMessage}")
    }
    open fun onError(tag: String = "BaseViewModel", message: String) {
        Log.e(tag, message)
    }
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}
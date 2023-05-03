package com.patsurvey.nudge.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job

open class BaseViewModel : ViewModel(){
    var job: Job? = null

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    open fun onError(message: String) {
//        usersLoadError.value = message
//        loading.value = false
    }
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}
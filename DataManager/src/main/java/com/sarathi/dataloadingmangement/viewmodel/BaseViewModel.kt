package com.sarathi.dataloadingmangement.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nudge.core.BLANK_STRING
import com.nudge.core.analytics.mixpanel.AnalyticsEvents
import com.nudge.core.analytics.mixpanel.AnalyticsEventsParam
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.usecase.AnalyticsEventUseCase
import com.nudge.core.utils.CoreLogger
import com.sarathi.dataloadingmangement.util.LoaderState
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class BaseViewModel : ViewModel() {
    val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState

    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
    val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    @Inject
    lateinit var analyticsEventUseCase: AnalyticsEventUseCase
    abstract fun <T> onEvent(event: T)

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, e ->
        CoreLogger.e(
            tag = "SurveyModule",
            msg = e?.localizedMessage ?: BLANK_STRING,
            stackTrace = true,
            ex = e
        )
        val eventParams = mapOf(
            AnalyticsEventsParam.EXCEPTION.eventParam to (e?.stackTraceToString()
                ?: BLANK_STRING)
        )
        analyticsEventUseCase.sendAnalyticsEvent(
            AnalyticsEvents.CATCHED_EXCEPTION.eventName,
            eventParams
        )
        onCatchError(e)
    }

    open fun onCatchError(e: Throwable) {
        CoreAppDetails.getContext()?.applicationContext?.let {
            CoreLogger.d(
                context = it,
                tag = "BaseViewModel->",
                msg = e.message ?: com.sarathi.dataloadingmangement.BLANK_STRING
            )
        }
        onEvent(LoaderEvent.UpdateLoaderState(false))

    }

    open fun refreshData() {}

    fun loaderState(): LoaderState = loaderState.value
    fun ViewModel.ioViewModelScope(
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) {
        viewModelScope.launch(context = ioDispatcher, start = start) {
            block()
        }
    }

    fun ViewModel.launchViewModelScope(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) {

        viewModelScope.launch(context = context, start = start) {
            block()
        }
    }


}
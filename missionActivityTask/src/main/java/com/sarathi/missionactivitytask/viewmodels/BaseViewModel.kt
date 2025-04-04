package com.sarathi.missionactivitytask.viewmodels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nudge.core.analytics.mixpanel.AnalyticsEvents
import com.nudge.core.analytics.mixpanel.AnalyticsEventsParam
import com.nudge.core.database.entities.api.ApiCallJournalEntity
import com.nudge.core.enums.ApiStatus
import com.nudge.core.helper.TranslationEnum
import com.nudge.core.helper.TranslationHelper
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.ui.commonUi.CustomProgressState
import com.nudge.core.ui.commonUi.DEFAULT_PROGRESS_VALUE
import com.nudge.core.usecase.AnalyticsEventUseCase
import com.nudge.core.utils.CoreLogger
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.missionactivitytask.utils.LoaderState
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
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

    @Inject
    lateinit var analyticsEventUseCase: AnalyticsEventUseCase
    val isDidiImageDialogVisible = mutableStateOf(Triple(false, BLANK_STRING, Uri.EMPTY))

    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
    val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    @Inject
    lateinit var translationHelper: TranslationHelper

    var TOTAL_API_CALL = -1
    var allApiStatus = mutableStateOf(ApiStatus.IDEL)
    var failedApiCount = mutableStateOf(0f)
    val progressState = CustomProgressState(DEFAULT_PROGRESS_VALUE, com.nudge.core.BLANK_STRING)
    var completedApiCount = mutableStateOf(0f)


    abstract fun <T> onEvent(event: T)

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, e ->
        CoreLogger.e(
            tag = "MAT",
            msg = e?.localizedMessage ?: com.nudge.core.BLANK_STRING,
            stackTrace = true,
            ex = e
        )
        val eventParams = mapOf(
            AnalyticsEventsParam.EXCEPTION.eventParam to (e?.stackTraceToString() ?: BLANK_STRING)
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
                msg = e.message ?: BLANK_STRING
            )
        }
        onEvent(LoaderEvent.UpdateLoaderState(false))

    }

    fun loaderState(): LoaderState = loaderState.value

    open fun refreshData() {
    }

    fun ViewModel.ioViewModelScope(
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) {
        viewModelScope.launch(context = ioDispatcher + exceptionHandler, start = start) {
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
    fun setTranslationConfig() {
        ioViewModelScope {
            translationHelper.initTranslationHelper(getScreenName())
        }
    }

    open fun getScreenName(): TranslationEnum {
        return TranslationEnum.NoScreen
    }

    fun getString(context: Context, resId: Int): String {
        return translationHelper?.getString(resId) ?: context.getString(resId)
    }

    fun stringResource(context: Context, resId: Int): String {
        return translationHelper?.stringResource(resId) ?: context.getString(resId)
    }

    fun stringResource(context: Context, resId: Int, vararg formatArgs: Any): String {
        return translationHelper?.getString(
            resId = resId,
            formatArgs = formatArgs
        ) ?: context.getString(resId, formatArgs)
    }

    open fun updateMissionFilter() {}

    open fun updateProgress(apiStatusData: ApiCallJournalEntity?) {
        // Increment counters based on API status
        if (apiStatusData?.status == ApiStatus.SUCCESS.name) {
            completedApiCount.value = completedApiCount.value.inc()
        } else {
            failedApiCount.value = failedApiCount.value.inc()
        }
        // Update progress bar and progress text
        val progress = completedApiCount.value.toFloat() / TOTAL_API_CALL.toFloat()
        progressState.updateProgress(progress)
        progressState.updateProgressText("${completedApiCount.value}/$TOTAL_API_CALL")

        // Handle completion and failure scenarios
        when {
            completedApiCount.value.toInt() == TOTAL_API_CALL -> {
                allApiStatus.value = ApiStatus.SUCCESS
            }

            failedApiCount.value > 1 -> {
                allApiStatus.value = ApiStatus.FAILED
            }

            else -> {
                allApiStatus.value = ApiStatus.INPROGRESS
            }
        }
    }

}
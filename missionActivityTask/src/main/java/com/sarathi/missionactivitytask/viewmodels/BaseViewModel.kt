package com.sarathi.missionactivitytask.viewmodels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nudge.core.analytics.mixpanel.AnalyticsEvents
import com.nudge.core.analytics.mixpanel.AnalyticsEventsParam
import com.nudge.core.constants.DataLoadingTriggerType
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
import com.sarathi.dataloadingmangement.domain.use_case.FetchAllDataUseCase
import com.sarathi.dataloadingmangement.model.ApiStatusStateModel
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

    @Inject
    lateinit var fetchAllDataUseCase: FetchAllDataUseCase

    val isDidiImageDialogVisible = mutableStateOf(Triple(false, BLANK_STRING, Uri.EMPTY))

    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
    val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    @Inject
    lateinit var translationHelper: TranslationHelper
    var apiStatusStateModel = mutableStateOf(ApiStatusStateModel(ApiStatus.IDEAL, 0f, -1, 0f))

    //    var allApiStatus = mutableStateOf(ApiStatus.IDEAL)
//    var failedApiCount = mutableStateOf(0f)
//    var totalApiCall = mutableStateOf(-1)
//    var completedApiCount = mutableStateOf(0f)
    val progressState = CustomProgressState(DEFAULT_PROGRESS_VALUE, com.nudge.core.BLANK_STRING)



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
        val state = apiStatusStateModel.value

        // Increment counters based on API status
        if (apiStatusData?.status == ApiStatus.SUCCESS.name) {
            apiStatusStateModel.value = state.copy(
                completedApiCount = state.completedApiCount.inc()
            )
        } else {
            apiStatusStateModel.value = state.copy(
                failedApiCount = state.failedApiCount.inc()
            )
        }
        // Update progress
        val progress =
            apiStatusStateModel.value.completedApiCount / apiStatusStateModel.value.totalApiCall.toFloat()
        progressState.updateProgress(progress)
        progressState.updateProgressText("${apiStatusStateModel.value.completedApiCount.toInt()}/${apiStatusStateModel.value.totalApiCall}")

        // Handle completion and failure scenarios
        apiStatusStateModel.value = when {
            apiStatusStateModel.value.completedApiCount.toInt() == apiStatusStateModel.value.totalApiCall -> apiStatusStateModel.value.copy(
                apiStatus = ApiStatus.SUCCESS
            )

            apiStatusStateModel.value.failedApiCount >= 1 -> apiStatusStateModel.value.copy(
                apiStatus = ApiStatus.FAILED
            )

            else -> apiStatusStateModel.value.copy(apiStatus = ApiStatus.INPROGRESS)
        }
    }

    fun loadAllData(
        screenName: String,
        moduleName: String,
        customData: Map<String, Any>,
        onComplete: (isSuccess: Boolean, successMsg: String) -> Unit,
        dataLoadingTriggerType: DataLoadingTriggerType = DataLoadingTriggerType.FRESH_LOGIN
    ) {
        apiStatusStateModel.value = ApiStatusStateModel(ApiStatus.IDEAL, 0f, -1, 0f)
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            apiStatusStateModel.value.apiStatus = ApiStatus.INPROGRESS
            fetchAllDataUseCase.invoke(
                customData = customData,
                screenName = screenName,
                dataLoadingTriggerType = dataLoadingTriggerType,
                isRefresh = false,
                onComplete = { isSucess, message ->
                    onComplete(isSucess, message)
                },
                totalNumberOfApi = { screenName, moduleName, requestBody, transactionId ->
                    apiStatusStateModel.value.totalApiCall =
                        fetchAllDataUseCase.getApiInProgressCount(
                        screenName = screenName,
                        moduleName = moduleName,
                        customData = requestBody,
                        triggerPoint = dataLoadingTriggerType.name,
                    )
                },
                apiPerStatus = { apiName, requestPayload ->
                    val apiStatusData = fetchAllDataUseCase.getApiStatus(
                        screenName = screenName,
                        moduleName = moduleName,
                        apiUrl = apiName,
                        requestPayload = requestPayload
                    )
                    apiStatusData?.let { updateProgress(apiStatusData = it) }
                },
                moduleName = moduleName
            )
        }
    }

}
package com.sarathi.dataloadingmangement.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nudge.core.BLANK_STRING
import com.nudge.core.analytics.mixpanel.AnalyticsEvents
import com.nudge.core.analytics.mixpanel.AnalyticsEventsParam
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.database.entities.api.ApiCallJournalEntity
import com.nudge.core.enums.ApiStatus
import com.nudge.core.helper.TranslationEnum
import com.nudge.core.helper.TranslationHelper
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.model.response.LanguageModel
import com.nudge.core.ui.commonUi.CustomProgressState
import com.nudge.core.ui.commonUi.DEFAULT_PROGRESS_VALUE
import com.nudge.core.usecase.AnalyticsEventUseCase
import com.nudge.core.utils.CoreLogger
import com.sarathi.dataloadingmangement.domain.use_case.FetchAllDataUseCase
import com.sarathi.dataloadingmangement.model.ApiStatusStateModel
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
    @Inject
    lateinit var translationHelper: TranslationHelper
    val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState

    @Inject
    lateinit var fetchAllDataUseCase: FetchAllDataUseCase
    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
    val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
    val isSettingClicked = mutableStateOf(false)
    val isAnyOptionValueChanged = mutableStateOf(false)

    @Inject
    lateinit var analyticsEventUseCase: AnalyticsEventUseCase
    private val _translationMap =
        mutableStateMapOf<String, List<LanguageModel>?>()
    val translationMap: SnapshotStateMap<String, List<LanguageModel>?> get() = _translationMap

    val progressState = CustomProgressState(DEFAULT_PROGRESS_VALUE, com.nudge.core.BLANK_STRING)
    var apiStatusStateModel = mutableStateOf(ApiStatusStateModel(ApiStatus.IDEAL, 0f, -1, 0f))

    abstract fun <T> onEvent(event: T)


    fun setTranslationConfig() {
        ioViewModelScope {
            translationHelper.initTranslationHelper(getScreenName())
        }
    }

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


    open fun getScreenName(): TranslationEnum {
        return TranslationEnum.NoScreen
    }

    fun getString(resId: Int): String {
        return translationHelper.getString(resId)
    }

    fun stringResource(resId: Int): String {
        return translationHelper.stringResource(resId)
    }

    fun stringResource(resId: Int, vararg formatArgs: Any): String {
        return translationHelper.getString(
            resId = resId,
            formatArgs = formatArgs
        )
    }

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


    suspend fun loadAllData(
        screenName: String,
        moduleName: String,
        customData: Map<String, Any>,
        onComplete: (isSuccess: Boolean, successMsg: String) -> Unit = { _, _ -> },
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
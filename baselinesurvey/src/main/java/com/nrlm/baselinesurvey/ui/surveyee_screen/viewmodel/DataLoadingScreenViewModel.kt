package com.nrlm.baselinesurvey.ui.surveyee_screen.viewmodel

import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nrlm.baselinesurvey.DEFAULT_SUCCESS_CODE
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.model.datamodel.ErrorModel
import com.nrlm.baselinesurvey.model.request.SurveyRequestBodyModel
import com.nrlm.baselinesurvey.ui.common_components.common_events.ApiStatusEvent
import com.nrlm.baselinesurvey.ui.common_components.common_events.DialogEvents
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.FetchDataUseCase
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.showCustomToast
import com.nrlm.baselinesurvey.utils.states.DialogState
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nudge.core.toTimeDateString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DataLoadingScreenViewModel @Inject constructor(
    val fetchDataUseCase: FetchDataUseCase,
) : BaseViewModel() {

    private val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState

    private val _showUserChangedDialog = mutableStateOf<DialogState>(DialogState())
    val showUserChangedDialog: State<DialogState> get() = _showUserChangedDialog
    private var baseCurrentApiCount = 0 // only count api survey count
    private var surveyApiCount = 0 // count of all apis
    private var TOTAL_API_CALL = 0
    private var SURVEY_API_CALL = 0
    var errorNavigate = mutableStateOf(false)


    override fun <T> onEvent(event: T) {
        when (event) {
            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }

            is DialogEvents.ShowDialogEvent -> {
                _showUserChangedDialog.value = _showUserChangedDialog.value
                    .copy(
                        isDialogVisible = event.showDialog
                    )
            }

            is ApiStatusEvent.showApiStatus -> {
                if (event.errorCode == DEFAULT_SUCCESS_CODE) {
                    showCustomToast(
                        BaselineCore.getAppContext(), BaselineCore.getAppContext().getString(
                            R.string.fetched_successfully
                        )
                    )
                } else {
                    showCustomToast(
                        BaselineCore.getAppContext(),
                        event.message
                    )
                }
            }
        }
    }

    fun compareWithPreviousUser(isDataLoadingAllowed: (Boolean) -> Unit) {
        val previousMobileNumber = fetchDataUseCase.loggedInUseCase.getPreviousMobileNumber()
        val mobileNumber = fetchDataUseCase.loggedInUseCase.getMobileNumber()
        if (TextUtils.isEmpty(previousMobileNumber) || previousMobileNumber == mobileNumber
        ) {
            isDataLoadingAllowed(true)
        } else {
            isDataLoadingAllowed(false)

        }
    }

    private fun fetchMissionData(fetchDataUseCase: FetchDataUseCase, callBack: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            fetchDataUseCase.fetchMissionDataFromNetworkUseCase.invoke()
            baseCurrentApiCount++
            updateLoaderEvent(callBack)
        }
    }

    private fun fetchSurveyeeData(fetchDataUseCase: FetchDataUseCase, callBack: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            fetchDataUseCase.fetchSurveyeeListFromNetworkUseCase.invoke()
            baseCurrentApiCount++
            updateLoaderEvent(callBack)
        }
    }

    private fun fetchContentData(fetchDataUseCase: FetchDataUseCase, callBack: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            fetchDataUseCase.fetchContentnDataFromNetworkUseCase.invoke()
            baseCurrentApiCount++
            updateLoaderEvent(callBack)
        }
    }

    private fun fetchCasteData(fetchDataUseCase: FetchDataUseCase, callBack: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            fetchDataUseCase.fetchCastesFromNetworkUseCase.invoke(true)
            baseCurrentApiCount++
            updateLoaderEvent(callBack)
        }
    }

    private suspend fun fetchUserDetailData(
        fetchDataUseCase: FetchDataUseCase, callBack: () -> Unit
    ): Boolean {
        var isUserDataSuccess = false
        isUserDataSuccess = fetchDataUseCase.fetchUserDetailFromNetworkUseCase.invoke()
        baseCurrentApiCount++
        updateLoaderEvent(callBack)
        return isUserDataSuccess
    }

    private fun fetchSectionStatusData(fetchDataUseCase: FetchDataUseCase, callBack: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            fetchDataUseCase.fetchSectionStatusFromNetworkUseCase.invoke()
            baseCurrentApiCount++
            updateLoaderEvent(callBack)
        }
    }

    private fun fetchSurveyAnswerData(fetchDataUseCase: FetchDataUseCase, callBack: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            fetchDataUseCase.fetchSurveyAnswerFromNetworkUseCase.invoke()
            baseCurrentApiCount++
            updateLoaderEvent(callBack)
        }
    }

    private suspend fun updateLoaderEvent(callBack: () -> Unit) {
        if (baseCurrentApiCount == TOTAL_API_CALL) {
            withContext(Dispatchers.Main) {
                Log.d(
                    "invoke",
                    "Network Transaction end " + System.currentTimeMillis().toTimeDateString()
                )
                onEvent(LoaderEvent.UpdateLoaderState(false))
                callBack()
            }
        } else if (surveyApiCount == SURVEY_API_CALL) {
            surveyApiCount = 0
            fetchSectionStatusData(fetchDataUseCase) { callBack() }
            fetchSurveyAnswerData(fetchDataUseCase) { callBack() }
            Log.d(
                "invoke",
                "Network Transaction end with   fetchSectionStatusData and fetchSurveyAnswerData " + System.currentTimeMillis()
                    .toTimeDateString()
            )
        }
    }

    fun fetchAllData(callBack: () -> Unit) {
        baseCurrentApiCount = 0
        surveyApiCount = 0

        Log.d(
            "invoke",
            "Network Transaction Start " + System.currentTimeMillis().toTimeDateString()
        )
        try {
            viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
                val languagesSize =
                    (fetchDataUseCase.fetchSurveyFromNetworkUseCase.getLanguages().size * 2)
                SURVEY_API_CALL = languagesSize
                //TODO need to be 7 make it dynamic
                TOTAL_API_CALL = 7 + languagesSize
                if (fetchUserDetailData(fetchDataUseCase) {}) {
                    fetchCasteData(fetchDataUseCase) {}
                    fetchMissionData(fetchDataUseCase) { callBack() }
                    fetchSurveyeeData(fetchDataUseCase) { callBack() }
                    fetchContentData(fetchDataUseCase) { callBack() }
                    fetchSurveyForAllLanguages { callBack() }
                } else {
                    withContext(Dispatchers.Main) {
                        onEvent(LoaderEvent.UpdateLoaderState(false))
                        callBack()
                    }
                }
            }
        } catch (ex: Exception) {
            BaselineLogger.e("DataLoadingScreenViewModel", "fetchAllData", ex)
            onEvent(LoaderEvent.UpdateLoaderState(false))
            callBack()
        }
    }

    private suspend fun fetchSurveyForAllLanguages(callBack: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val stateId = getStateId()
            if (stateId != -1) {
                fetchDataUseCase.fetchSurveyFromNetworkUseCase.getLanguages()
                    .forEach { languageEntity ->
                        callBaselineSurveyApi(
                            fetchDataUseCase,
                            languageId = languageEntity.id,
                            referenceId = stateId
                        ) { callBack() }
                        callHamletSurveyApi(
                            fetchDataUseCase,
                            languageId = languageEntity.id,
                            referenceId = stateId
                        ) { callBack() }
                    }
            }
        }

    }

    private fun callHamletSurveyApi(
        fetchDataUseCase: FetchDataUseCase,
        languageId: Int,
        referenceId: Int, callBack: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val hamletSurveyRequestBodyModel = SurveyRequestBodyModel(
                languageId = languageId,
                surveyName = "HAMLET",
                referenceId = referenceId,
                referenceType = "STATE"
            )
            fetchDataUseCase.fetchSurveyFromNetworkUseCase.invoke(
                hamletSurveyRequestBodyModel
            )
            surveyApiCount++
            baseCurrentApiCount++
            updateLoaderEvent(callBack)
        }
    }

    private fun callBaselineSurveyApi(
        fetchDataUseCase: FetchDataUseCase,
        languageId: Int,
        referenceId: Int, callBack: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {

            val baselineSurveyRequestBodyModel = SurveyRequestBodyModel(
                languageId = languageId,
                surveyName = "BASELINE",
                referenceId = referenceId,
                referenceType = "STATE"
            )
            fetchDataUseCase.fetchSurveyFromNetworkUseCase.invoke(
                baselineSurveyRequestBodyModel
            )
            baseCurrentApiCount++
            surveyApiCount++
            updateLoaderEvent(callBack)
        }
    }

    private fun getStateId(): Int {
        return fetchDataUseCase.fetchSurveyFromNetworkUseCase.getStateId()
    }

    fun isUserLoggedIn(): Boolean {
        return fetchDataUseCase.loggedInUseCase.invoke()
    }

    fun isAllDataFetched(): Boolean {
        return fetchDataUseCase.loggedInUseCase.isDataSynced()
    }

    fun setAllDataFetched() {
        fetchDataUseCase.loggedInUseCase.setDataSynced()
    }

    fun logout() {
        fetchDataUseCase.loggedInUseCase.performLogout()
    }

    fun clearLocalDB(isDataLoadingAllowed: () -> Unit) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            fetchDataUseCase.loggedInUseCase.performLogout(true)
            isDataLoadingAllowed()
        }
    }

    override fun onServerError(error: ErrorModel?) {
        baseCurrentApiCount = 0
        surveyApiCount = 0
        onEvent(LoaderEvent.UpdateLoaderState(false))
        errorNavigate.value = true
    }
}
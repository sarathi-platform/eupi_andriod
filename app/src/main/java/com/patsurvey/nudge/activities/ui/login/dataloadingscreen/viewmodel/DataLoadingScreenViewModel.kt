package com.patsurvey.nudge.activities.ui.login.dataloadingscreen.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nudge.core.toTimeDateString
import com.patsurvey.nudge.activities.ui.login.dataloadingscreen.domain.FetchDataUseCase
import com.patsurvey.nudge.activities.ui.login.dataloadingscreen.util.LoaderState
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DataLoadingScreenViewModel @Inject constructor(val fetchDataUseCase: FetchDataUseCase) :
    BaseViewModel() {
    private val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState
    override fun onServerError(error: ErrorModel?) {
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
    }

    fun fetchAllData(callBack: () -> Unit) {
        Log.d(
            "invoke",
            "Network Transaction Start " + System.currentTimeMillis().toTimeDateString()
        )
        try {
            viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
                fetchMissionData(fetchDataUseCase) { callBack() }
            }
        } catch (ex: Exception) {
            loaderView(false)
            callBack()
        }
    }

    private fun fetchMissionData(fetchDataUseCase: FetchDataUseCase, callBack: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            fetchDataUseCase.fetchMissionDataFromNetworkUseCase.invoke()
            updateLoaderEvent(callBack)
        }
    }

    private suspend fun updateLoaderEvent(callBack: () -> Unit) {
        withContext(Dispatchers.Main) {
            Log.d(
                "invoke",
                "Network Transaction end " + System.currentTimeMillis().toTimeDateString()
            )
            loaderView(false)
            callBack()
        }
    }

    fun loaderView(isLoaderVisible: Boolean) {
        _loaderState.value = _loaderState.value.copy(
            isLoaderVisible = isLoaderVisible
        )
    }
}
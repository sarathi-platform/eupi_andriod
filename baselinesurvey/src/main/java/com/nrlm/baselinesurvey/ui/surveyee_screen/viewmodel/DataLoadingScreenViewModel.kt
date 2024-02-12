package com.nrlm.baselinesurvey.ui.surveyee_screen.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case.FetchDataUseCase
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.states.LoaderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DataLoadingScreenViewModel @Inject constructor(
    private val fetchDataUseCase: FetchDataUseCase
): BaseViewModel() {

    private val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState

    override fun <T> onEvent(event: T) {
        when (event) {
            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }
    }

    fun fetchAllData(callBack: () -> Unit) {
        try {
//            BaselineApplication.appScopeLaunch(Dispatchers.IO) {
            CoroutineScope(Dispatchers.IO).launch {
                val fetchUserDetailFromNetworkUseCaseSuccess = fetchDataUseCase.fetchUserDetailFromNetworkUseCase.invoke()
                if (fetchUserDetailFromNetworkUseCaseSuccess) {
                    fetchDataUseCase.fetchSurveyeeListFromNetworkUseCase.invoke()
                    fetchDataUseCase.fetchSurveyFromNetworkUseCase.invoke()
                } else {
                    withContext(Dispatchers.Main) {
                        onEvent(LoaderEvent.UpdateLoaderState(false))
                        callBack()
                    }
                }
                withContext(Dispatchers.Main) {
                    onEvent(LoaderEvent.UpdateLoaderState(false))
                    callBack()
                }

            }
        } catch (ex: Exception) {
            BaselineLogger.e("DataLoadingScreenViewModel", "fetchAllData", ex)
            onEvent(LoaderEvent.UpdateLoaderState(false))
            callBack()
        }
    }
}
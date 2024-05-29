package com.nrlm.baselinesurvey.ui.sync_event.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.NetworkSpeed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
@HiltViewModel
class SyncEventViewModel @Inject constructor(
    val prefRepo: PrefRepo
) : BaseViewModel()  {
    private val _syncEventList = MutableStateFlow(listOf<Events>())
    val syncEventList: StateFlow<List<Events>> get() = _syncEventList
    private val _loaderState = mutableStateOf<LoaderState>(LoaderState(false))
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
    fun getAllEvents() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
             _syncEventList.value= BaselineCore.getEventObserver()?.getEvent()!!

            }catch (ex:Exception){
                BaselineLogger.d("SyncEventViewModel"," syncAllEvent: ${ex.printStackTrace()} ")
            }
        }
    }

   fun syncAllPending(networkSpeed: NetworkSpeed) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                BaselineCore.getEventObserver()
                    ?.syncPendingEvent(BaselineCore.getAppContext(), networkSpeed)
            }catch (ex:Exception){
                BaselineLogger.d("SettingBSViewModel"," syncAllPending: ${ex.printStackTrace()} ")
            }
        }
    }
}
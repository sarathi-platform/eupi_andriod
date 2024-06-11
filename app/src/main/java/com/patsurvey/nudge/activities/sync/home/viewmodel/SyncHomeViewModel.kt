package com.patsurvey.nudge.activities.sync.home.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.NetworkSpeed
import com.nudge.syncmanager.utils.WORKER_RESULT
import com.patsurvey.nudge.activities.sync.home.domain.use_case.SyncHomeUseCase
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.ZERO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncHomeViewModel @Inject constructor(
    val syncHomeUseCase: SyncHomeUseCase,
    val prefRepo: PrefBSRepo
) : BaseViewModel()  {
    val _eventList = mutableStateOf<List<Events>>(emptyList())
    val eventList: State<List<Events>> get() = _eventList
    val evList: MutableStateFlow<List<Events>> = MutableStateFlow(emptyList())

    val eventListLiveData: LiveData<List<Events>>
        get() = eventListMutableLiveData
    private val eventListMutableLiveData = MutableLiveData<List<Events>>()

    val totalDataEventCount = mutableStateOf(ZERO)
    val successDataEventCount = mutableStateOf(ZERO)
    val totalImageEventCount = mutableStateOf(ZERO)
    val successImageEventCount = mutableStateOf(ZERO)
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
    fun getAllEvents(current: Locale) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
//                val list =syncHomeUseCase.getSyncEventsUseCase.getTotalEvents()
//                evList.emit(list)
//                _eventList.value=list
//                eventListMutableLiveData.postValue(list)
//                totalDataEventCount.value =eventList.value.filter { !it.name.toLowerCase(current).contains(IMAGE_STRING) }.size
//                successDataEventCount.value =eventList.value.filter { !it.name.toLowerCase(current).contains(IMAGE_STRING) && it.status==EventSyncStatus.CONSUMER_SUCCESS.eventSyncStatus}.size
//                totalImageEventCount.value =eventList.value.filter { it.name.toLowerCase(current).contains(IMAGE_STRING) }.size
//                successImageEventCount.value =eventList.value.filter { it.name.toLowerCase(current).contains(IMAGE_STRING) && it.status==EventSyncStatus.CONSUMER_SUCCESS.eventSyncStatus}.size

            }catch (ex:Exception){
                BaselineLogger.d("SyncEventViewModel"," syncAllEvent: ${ex.printStackTrace()} ")
            }
        }
    }

   fun syncAllPending(networkSpeed: NetworkSpeed) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                NudgeCore.getEventObserver()
                    ?.syncPendingEvent(BaselineCore.getAppContext(), networkSpeed)?.collect{
                        Log.d("TAG", "syncAllPendingEvents: ${it.state.name} :: ${it.state.isFinished} :: ${it.outputData.getString(
                            WORKER_RESULT)} ")
                    }
            }catch (ex:Exception){
                BaselineLogger.d("SettingBSViewModel"," syncAllPending: ${ex.printStackTrace()} ")
            }
        }
    }
}
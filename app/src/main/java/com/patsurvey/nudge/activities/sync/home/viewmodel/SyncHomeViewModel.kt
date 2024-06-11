package com.patsurvey.nudge.activities.sync.home.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nudge.core.EventSyncStatus
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.NetworkSpeed
import com.nudge.syncmanager.utils.WORKER_RESULT
import com.patsurvey.nudge.activities.sync.home.domain.use_case.SyncHomeUseCase
import com.patsurvey.nudge.utils.IMAGE_STRING
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.ZERO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SyncHomeViewModel @Inject constructor(
    val syncHomeUseCase: SyncHomeUseCase,
    val prefRepo: PrefBSRepo
) : BaseViewModel()  {
    val _eventList = mutableStateOf<List<Events>>(emptyList())
    val eventList: State<List<Events>> get() = _eventList

    val totalDataEventCount = mutableIntStateOf(ZERO)
    val successDataEventCount = mutableIntStateOf(ZERO)
    val totalImageEventCount = mutableIntStateOf(ZERO)
    val successImageEventCount = mutableIntStateOf(ZERO)
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
                _eventList.value=syncHomeUseCase.getSyncEventsUseCase.getTotalEvents()
                totalDataEventCount.intValue =eventList.value.filter { !it.name.toLowerCase(current).contains(IMAGE_STRING) }.size
                successDataEventCount.intValue =eventList.value.filter { !it.name.toLowerCase(current).contains(IMAGE_STRING) && it.status==EventSyncStatus.PRODUCER_SUCCESS.eventSyncStatus}.size
                totalImageEventCount.intValue =eventList.value.filter { it.name.toLowerCase(current).contains(IMAGE_STRING) }.size
                successImageEventCount.intValue =eventList.value.filter { it.name.toLowerCase(current).contains(IMAGE_STRING) && it.status==EventSyncStatus.PRODUCER_SUCCESS.eventSyncStatus}.size

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
                        if(it!=null){
                            Log.d("TAG", "syncAllPendingEvents: ${it.state.name} :: ${it.state.isFinished} :: ${it.outputData.getString(
                                WORKER_RESULT)} ")
                        }
                    }
            }catch (ex:Exception){
                BaselineLogger.d("SettingBSViewModel"," syncAllPending: ${ex.printStackTrace()} ")
            }
        }
    }
}
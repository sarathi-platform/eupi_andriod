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
import com.nudge.core.utils.CoreLogger
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
   fun syncAllPending(networkSpeed: NetworkSpeed) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                NudgeCore.getEventObserver()
                    ?.syncPendingEvent(NudgeCore.getAppContext(), networkSpeed)?.collect{
                        Log.d("TAG", "syncAllPendingEvents: ${it.state.name} :: ${it.state.isFinished} :: ${it.outputData.getString(
                            WORKER_RESULT)} ")
                    }
            }catch (ex:Exception){
                CoreLogger.d(NudgeCore.getAppContext(),"SettingBSViewModel"," syncAllPending: ${ex.printStackTrace()} ")
            }
        }
    }
}
package com.patsurvey.nudge.activities.sync.home.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nudge.core.enums.NetworkSpeed
import com.nudge.core.json
import com.nudge.core.utils.CoreLogger
import com.nudge.core.utils.SyncType
import com.patsurvey.nudge.MyApplication
import com.patsurvey.nudge.activities.sync.home.domain.use_case.SyncHomeUseCase
import com.patsurvey.nudge.utils.NudgeCore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SyncHomeViewModel @Inject constructor(
    val syncHomeUseCase: SyncHomeUseCase,
    val prefRepo: PrefBSRepo
) : BaseViewModel()  {

    var syncWorkerInfo :WorkInfo?=null
    val workManager = WorkManager.getInstance(MyApplication.applicationContext())
    var uploadWorkedReqId:UUID?=null
    private val _loaderState = mutableStateOf(LoaderState(false))
    override fun <T> onEvent(event: T) {
        when (event) {
            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }
    }
   @SuppressLint("SuspiciousIndentation")
   fun syncAllPending(networkSpeed: NetworkSpeed,syncType: SyncType) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                syncWorkerInfo?.let {
                    if(it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED ){
                        uploadWorkedReqId?.let { workerId->
                            workManager.cancelWorkById(workerId)
                        }
                    }
                }
             uploadWorkedReqId=NudgeCore.getEventObserver()
                    ?.syncPendingEvent(NudgeCore.getAppContext(), networkSpeed,syncType.ordinal)
                Log.d("TAG", "syncAllPendingDetails: ${uploadWorkedReqId?.json()}")
            }catch (ex:Exception){
                CoreLogger.d(NudgeCore.getAppContext(),"SettingBSViewModel"," syncAllPending: ${ex.printStackTrace()} ")
            }
        }
    }
}
package com.patsurvey.nudge.activities.sync.home.viewmodel

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nudge.core.enums.NetworkSpeed
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.utils.CoreLogger
import com.nudge.core.utils.SyncType
import com.nudge.syncmanager.utils.SYNC_WORKER_TAG
import com.patsurvey.nudge.MyApplication
import com.patsurvey.nudge.activities.sync.home.domain.use_case.SyncHomeUseCase
import com.patsurvey.nudge.utils.NudgeCore
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
    val selectedSyncType = mutableIntStateOf(SyncType.SYNC_ALL.ordinal)
    var syncWorkerInfoState: WorkInfo.State? = null
    val imageEventProgress = mutableFloatStateOf(0f)
    val dataEventProgress = mutableFloatStateOf(0f)
    val workManager = WorkManager.getInstance(MyApplication.applicationContext())
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
   fun syncAllPending(networkSpeed: NetworkSpeed) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                cancelSyncUploadWorker()
                NudgeCore.getEventObserver()
                    ?.syncPendingEvent(
                        NudgeCore.getAppContext(),
                        networkSpeed,
                        selectedSyncType.intValue
                    )
            }catch (ex:Exception){
                CoreLogger.d(
                    CoreAppDetails.getApplicationContext(),
                    "SyncHomeViewModel",
                    " syncAllPending: ${ex.printStackTrace()} "
                )
            }
        }
    }

    private fun cancelSyncUploadWorker() {
        syncWorkerInfoState?.let {
            if (it == WorkInfo.State.RUNNING || it == WorkInfo.State.ENQUEUED) {
                CoreLogger.d(
                    CoreAppDetails.getApplicationContext(),
                    "SyncHomeViewModel",
                    "CancelSyncUploadWorker :: Worker Status: $it"
                )
                workManager.cancelAllWorkByTag(SYNC_WORKER_TAG)
                CoreLogger.d(
                    CoreAppDetails.getApplicationContext(),
                    "SyncHomeViewModel",
                    "CancelSyncUploadWorker :: Worker Cancelled with TAG : $SYNC_WORKER_TAG"
                )
            }
        }
    }

    fun calculateBarProgress(totalEventCount: Int, successEventCount: Int): Float {
        var progState = successEventCount.toFloat() / totalEventCount
        if (totalEventCount == 0 && successEventCount == 0) {
            progState = 0f
        }
        return progState
    }
}
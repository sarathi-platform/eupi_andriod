package com.patsurvey.nudge.activities.sync.home.viewmodel

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.asLiveData
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nudge.core.BASELINE
import com.nudge.core.FAILED_EVENT_STRING
import com.nudge.core.LAST_SYNC_TIME
import com.nudge.core.SARATHI
import com.nudge.core.SARATHI_DIRECTORY_NAME
import com.nudge.core.SELECTION
import com.nudge.core.UPCM_USER
import com.nudge.core.ZIP_EXTENSION
import com.nudge.core.compression.ZipFileCompression
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventType
import com.nudge.core.enums.NetworkSpeed
import com.nudge.core.getFirstName
import com.nudge.core.json
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.utils.CoreLogger
import com.nudge.core.utils.SyncType
import com.nudge.syncmanager.utils.SYNC_WORKER_TAG
import com.patsurvey.nudge.MyApplication
import com.patsurvey.nudge.activities.sync.home.domain.use_case.SyncEventDetailUseCase
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.utils.ConnectionMonitorV2
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.openShareSheet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncHomeViewModel @Inject constructor(
    val syncEventDetailUseCase: SyncEventDetailUseCase,
    val prefRepo: PrefRepo,
    val connectionMonitor: ConnectionMonitorV2,
) : BaseViewModel()  {
    val isOnline = connectionMonitor.isConnected.asLiveData()
    val selectedSyncType = mutableIntStateOf(SyncType.SYNC_ALL.ordinal)
    var syncWorkerInfoState: WorkInfo.State? = null
    val imageEventProgress = mutableFloatStateOf(0f)
    val dataEventProgress = mutableFloatStateOf(0f)
    val workManager = WorkManager.getInstance(MyApplication.applicationContext())
    val lastSyncTime = mutableLongStateOf(0L)
    private val _failedEventList = mutableStateOf<List<Events>>(emptyList())
    val failedEventList: State<List<Events>> get() = _failedEventList

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
                prefRepo.savePref(LAST_SYNC_TIME, System.currentTimeMillis())
                lastSyncTime.longValue = prefRepo.getPref(LAST_SYNC_TIME, 0L)
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

    fun findFailedEventAndWriteIntoFile() {
        val fileAndDbZipList = ArrayList<Uri>()
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _failedEventList.value =
                syncEventDetailUseCase.getSyncEventsUseCase.getAllFailedEventListFromDB()
            val moduleName =
                if (syncEventDetailUseCase.getUserDetailsSyncUseCase.getLoggedInUserType() == UPCM_USER) BASELINE else SELECTION
            val eventFileName =
                getFirstName(syncEventDetailUseCase.getUserDetailsSyncUseCase.getUserName()) +
                        "_${syncEventDetailUseCase.getUserDetailsSyncUseCase.getUserMobileNumber()}_" +
                        "${SARATHI}_${moduleName}_failed_events_${System.currentTimeMillis()}"
            if (failedEventList.value.isNotEmpty()) {
                failedEventList.value.forEach {
                    syncEventDetailUseCase.eventsWriterUseCase.writeFailedEventIntoEventFile(
                        events = it,
                        eventType = EventType.STATEFUL,
                        fileNameWithoutExtension = eventFileName
                    )
                }
                val compression = ZipFileCompression()

                val eventFileUrisList = compression.getFileUrisFromMediaStore(
                    contentResolver = CoreAppDetails.getApplicationContext().contentResolver,
                    extVolumeUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                    filePathToZipped = Environment.DIRECTORY_DOCUMENTS + SARATHI_DIRECTORY_NAME + "/" + syncEventDetailUseCase.getUserDetailsSyncUseCase.getUserMobileNumber()
                )

                if (!eventFileUrisList.isNullOrEmpty()) {
                    val eventFiles = eventFileUrisList.filter {
                        it.first.contains(FAILED_EVENT_STRING) && !it.first.contains(
                            ZIP_EXTENSION
                        )
                    }
                    eventFiles.forEach { it.second?.let { it1 -> fileAndDbZipList.add(it1) } }
                    NudgeLogger.d("SyncHomeViewModel", "Failed Event File: ${eventFiles.json()}")
                    openShareSheet(fileAndDbZipList, "Share Failed Event File", "*/*")
                }
            }
        }
    }

    fun findFailedEventList() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _failedEventList.value =
                syncEventDetailUseCase.getSyncEventsUseCase.getAllFailedEventListFromDB()
        }
    }

    fun fetchLastSyncDateTimeFromServer(isOnline: Boolean) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            if (isOnline)
                syncEventDetailUseCase.fetchLastSyncDateForNetwork.invoke()

            lastSyncTime.longValue = prefRepo.getPref(LAST_SYNC_TIME, 0L)
        }
    }
}
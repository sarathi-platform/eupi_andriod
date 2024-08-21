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
import com.nudge.core.BLANK_STRING
import com.nudge.core.FAILED_EVENT_STRING
import com.nudge.core.LAST_SYNC_TIME
import com.nudge.core.SARATHI
import com.nudge.core.SARATHI_DIRECTORY_NAME
import com.nudge.core.SELECTION
import com.nudge.core.SYNC_MANAGER_DATABASE
import com.nudge.core.UPCM_USER
import com.nudge.core.ZIP_EXTENSION
import com.nudge.core.compression.ZipFileCompression
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventType
import com.nudge.core.enums.NetworkSpeed
import com.nudge.core.exportDbFile
import com.nudge.core.getFirstName
import com.nudge.core.json
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.openShareSheet
import com.nudge.core.utils.CoreLogger
import com.nudge.core.utils.SyncType
import com.nudge.syncmanager.utils.SYNC_WORKER_TAG
import com.patsurvey.nudge.MyApplication
import com.patsurvey.nudge.activities.sync.home.domain.use_case.SyncEventDetailUseCase
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.utils.ConnectionMonitorV2
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.NudgeLogger
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
    val totalImageEventCount = mutableIntStateOf(0)
    val isDataPBVisible = mutableStateOf(false)
    val isImagePBVisible = mutableStateOf(false)
    val isDataStatusVisible = mutableStateOf(false)
    val isImageStatusVisible = mutableStateOf(false)
    val isSyncStarted = mutableStateOf(false)
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
                isSyncStarted.value = true
                when (selectedSyncType.intValue) {
                    SyncType.SYNC_ONLY_DATA.ordinal -> {
                        isDataPBVisible.value = true
                        isDataStatusVisible.value = true
                    }

                    SyncType.SYNC_ONLY_IMAGES.ordinal -> {
                        isImagePBVisible.value = true
                        isImageStatusVisible.value = true
                    }
                    SyncType.SYNC_ALL.ordinal -> {
                        isDataPBVisible.value = true
                        isDataStatusVisible.value = true
                        isImageStatusVisible.value = true
                        isImagePBVisible.value = true
                    }
                }
                NudgeCore.getEventObserver()
                    ?.syncPendingEvent(
                        NudgeCore.getAppContext(),
                        networkSpeed,
                        selectedSyncType.intValue
                    )
                prefRepo.savePref(LAST_SYNC_TIME, System.currentTimeMillis())
                lastSyncTime.longValue = prefRepo.getPref(LAST_SYNC_TIME, 0L)
            }catch (ex:Exception){
                isSyncStarted.value = false
                isDataPBVisible.value = false
                isImagePBVisible.value = false
                CoreLogger.d(
                    CoreAppDetails.getApplicationContext(),
                    "SyncHomeViewModel",
                    " syncAllPending: ${ex.printStackTrace()} "
                )
            }
        }
    }

    private fun cancelSyncUploadWorker() {
        isSyncStarted.value = false
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
        val filePairUriList = ArrayList<Pair<String, Uri>>()
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _failedEventList.value =
                syncEventDetailUseCase.getSyncEventsUseCase.getAllFailedEventListFromDB()
            val moduleName =
                if (syncEventDetailUseCase.getUserDetailsSyncUseCase.getLoggedInUserType() == UPCM_USER) BASELINE else SELECTION
            val eventFileName =
                getFirstName(syncEventDetailUseCase.getUserDetailsSyncUseCase.getUserName()) +
                        "_${syncEventDetailUseCase.getUserDetailsSyncUseCase.getUserMobileNumber()}_" +
                        "${SARATHI}_${moduleName}_Failed_Events_${System.currentTimeMillis()}"
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
                if (eventFileUrisList.isNotEmpty()) {
                    eventFileUrisList.forEach { item ->
                        filePairUriList.add(item as Pair<String, Uri>)
                    }
                }

                val dbUri = exportDbFile(
                    appContext = CoreAppDetails.getApplicationContext().applicationContext,
                    applicationID = CoreAppDetails.getApplicationDetails()?.applicationID
                        ?: BLANK_STRING,
                    databaseName = SYNC_MANAGER_DATABASE
                )
                if (dbUri != Uri.EMPTY) {
                    dbUri?.let {
                        NudgeLogger.d(
                            "SyncHomeViewModel",
                            "Sync Database File Uri: ${it.path}---------------"
                        )
                        filePairUriList.add(Pair(SYNC_MANAGER_DATABASE, it))
                    }
                }

                val zipFileName = generateZipFileName()
                val zipLogDbFileUri = compression.compressData(
                    context = CoreAppDetails.getApplicationContext().applicationContext,
                    zipFileName = zipFileName,
                    filePathToZipped = BLANK_STRING,
                    extraUris = filePairUriList,
                    folderName = syncEventDetailUseCase.getUserDetailsSyncUseCase.getUserMobileNumber()
                )
                zipLogDbFileUri?.let {
                    if (it != Uri.EMPTY) {
                        fileAndDbZipList.add(it)
                    }
                }
                if (eventFileUrisList.isNotEmpty()) {
                    val eventFiles = eventFileUrisList.filter {
                        (it.first.contains(FAILED_EVENT_STRING) && !it.first.contains(
                            ZIP_EXTENSION
                        )) || it.first.contains(SYNC_MANAGER_DATABASE)
                    }

                    eventFiles.forEach { it.second?.let { it1 -> fileAndDbZipList.add(it1) } }
                    NudgeLogger.d("SyncHomeViewModel", "Failed Event File: ${eventFiles.json()}")
                    openShareSheet(
                        fileUriList = fileAndDbZipList,
                        title = "Share Failed Event File",
                        type = "*/*",
                        context = CoreAppDetails.getApplicationContext().applicationContext
                    )
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

    fun checkSyncProgressBarStatus(isWorkerRunning: Boolean) {
        when (selectedSyncType.intValue) {
            SyncType.SYNC_ONLY_DATA.ordinal -> {
                isDataStatusVisible.value
                    (isSyncStarted.value || dataEventProgress.floatValue > 0) && dataEventProgress.floatValue < 1

                isDataPBVisible.value =
                    (isSyncStarted.value || dataEventProgress.floatValue > 0) && dataEventProgress.floatValue < 1 && isWorkerRunning
            }

            SyncType.SYNC_ONLY_IMAGES.ordinal -> {
                isImageStatusVisible.value =
                    (isSyncStarted.value || imageEventProgress.floatValue > 0) && imageEventProgress.floatValue < 1

                isImagePBVisible.value =
                    (isSyncStarted.value || imageEventProgress.floatValue > 0) && imageEventProgress.floatValue < 1 && isWorkerRunning
            }

            SyncType.SYNC_ALL.ordinal -> {
                isDataStatusVisible.value =
                    (isSyncStarted.value || dataEventProgress.floatValue > 0) && dataEventProgress.floatValue < 1
                isImageStatusVisible.value =
                    (isSyncStarted.value || imageEventProgress.floatValue > 0) && imageEventProgress.floatValue < 1

                isDataPBVisible.value =
                    (isSyncStarted.value || dataEventProgress.floatValue > 0) && dataEventProgress.floatValue < 1 && isWorkerRunning
                isImagePBVisible.value =
                    (isSyncStarted.value || imageEventProgress.floatValue > 0) && imageEventProgress.floatValue < 1 && isWorkerRunning
            }
        }
    }

    private fun generateZipFileName(): String {
        return "${getFirstName(syncEventDetailUseCase.getUserDetailsSyncUseCase.getUserName())}_${syncEventDetailUseCase.getUserDetailsSyncUseCase.getUserMobileNumber()}_${SARATHI}_Failed_Events_${System.currentTimeMillis()}"
    }
}
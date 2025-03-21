package com.patsurvey.nudge.activities.backup.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toFile
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.NUDGE_BASELINE_DATABASE
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.domain.EventWriterHelperImpl
import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.openShareSheet
import com.nrlm.baselinesurvey.utils.showCustomToast
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nudge.core.CoreDispatchers
import com.nudge.core.NUDGE_DATABASE
import com.nudge.core.SYNC_MANAGER_DATABASE
import com.nudge.core.ZIP_MIME_TYPE
import com.nudge.core.analytics.mixpanel.AnalyticsEvents
import com.nudge.core.compression.ZipFileCompression
import com.nudge.core.database.entities.Events
import com.nudge.core.exportDatabase
import com.nudge.core.getFirstName
import com.nudge.core.importDbFile
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.model.SettingOptionModel
import com.nudge.core.moduleNameAccToLoggedInUser
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.ui.events.ToastMessageEvent
import com.nudge.core.uriFromFile
import com.nudge.core.usecase.FetchAppConfigFromNetworkUseCase
import com.nudge.core.utils.SyncType
import com.nudge.syncmanager.domain.usecase.SyncManagerUseCase
import com.patsurvey.nudge.BuildConfig
import com.patsurvey.nudge.SettingRepository
import com.patsurvey.nudge.activities.backup.domain.use_case.ExportImportUseCase
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.UPCM_USER
import com.sarathi.dataloadingmangement.NUDGE_GRANT_DATABASE
import com.sarathi.dataloadingmangement.domain.use_case.RegenerateGrantEventUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.system.exitProcess

@HiltViewModel
class ExportImportViewModel @Inject constructor(
    private val exportImportUseCase: ExportImportUseCase,
    private val eventWriterHelperImpl: EventWriterHelperImpl,
    val prefBSRepo: PrefBSRepo,
    private val settingRepository: SettingRepository,
    private val coreSharedPrefs: CoreSharedPrefs,
    private val regenerateGrantEventUsecase: RegenerateGrantEventUsecase,
    private val fetchAppConfigFromNetworkUseCase: FetchAppConfigFromNetworkUseCase,
    val syncManagerUseCase: SyncManagerUseCase
) : BaseViewModel() {
    var mAppContext: Context

    val _optionList = mutableStateOf<List<SettingOptionModel>>(emptyList())
    val optionList: State<List<SettingOptionModel>> get() = _optionList
    val showRestartAppDialog = mutableStateOf(false)
    val showConfirmationDialog = mutableStateOf(false)
    val selectedTag = mutableStateOf(BLANK_STRING)
    private val _loaderState = mutableStateOf<LoaderState>(LoaderState(false))
    val applicationId = mutableStateOf(BLANK_STRING)
    val loaderState: State<LoaderState> get() = _loaderState
    val loggedInUserType = mutableStateOf(BLANK_STRING)
    private val _eventList = mutableStateOf<List<Events>>(emptyList())
    val eventList: State<List<Events>> get() = _eventList
    val isPendingEvent = mutableStateOf(false)

    init {
        mAppContext = NudgeCore.getAppContext()
        applicationId.value =
            CoreAppDetails.getApplicationDetails()?.applicationID ?: BuildConfig.APPLICATION_ID
        _optionList.value = exportImportUseCase.getExportOptionListUseCase.fetchExportOptionList()
        loggedInUserType.value =
            exportImportUseCase.getUserDetailsExportUseCase.getLoggedInUserType()
    }

    override fun <T> onEvent(event: T) {
        when (event) {
            is ToastMessageEvent.ShowToastMessage -> {
                showCustomToast(mAppContext, event.message)
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }
    }

    fun clearLocalDatabase(onPageChange: () -> Unit) {
        CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {
            try {
                val result = exportImportUseCase.clearLocalDBExportUseCase.invoke()
                if (result) {
                    exportImportUseCase.clearLocalDBExportUseCase.setAllDataSyncStatus()
                    withContext(CoreDispatchers.mainDispatcher) {
                        onPageChange()
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                BaselineLogger.e("ExportImportViewModel", "clearLocalDatabase : ${ex.message}", ex)
            }
        }
    }

    fun exportLocalDatabase(isNeedToShare:Boolean,onExportSuccess: (Uri) -> Unit) {
        BaselineLogger.d("ExportImportViewModel","exportLocalDatabase -----")
         try {
             onEvent(LoaderEvent.UpdateLoaderState(true))
             exportDatabase(
                 appContext = mAppContext,
                 applicationID = applicationId.value,
                 mobileNo = exportImportUseCase.getUserDetailsExportUseCase.getUserMobileNumber(),
                 databaseName = if (loggedInUserType.value == UPCM_USER) listOf(
                     NUDGE_BASELINE_DATABASE, NUDGE_GRANT_DATABASE,
                     SYNC_MANAGER_DATABASE
                 ) else listOf(NUDGE_DATABASE, SYNC_MANAGER_DATABASE),
                 userName = getFirstName(exportImportUseCase.getUserDetailsExportUseCase.getUserName()),
                 moduleName = moduleNameAccToLoggedInUser(loggedInUserType.value)
             ) {
                 BaselineLogger.d("ExportImportViewModel", "exportLocalDatabase : ${it.path}")
                 onExportLocalDbSuccess(isNeedToShare, it, onExportSuccess)
             }


         } catch (e: Exception) {
             onEvent(LoaderEvent.UpdateLoaderState(false))
             BaselineLogger.e("ExportImportViewModel", "exportLocalDatabase :${e.message}", e)
         }

    }

    private fun onExportLocalDbSuccess(
        isNeedToShare: Boolean,
        it: Uri,
        onExportSuccess: (Uri) -> Unit
    ) {
        onEvent(LoaderEvent.UpdateLoaderState(false))
        if (isNeedToShare) {
            openShareSheet(convertURIAccToOS(it), "", type = ZIP_MIME_TYPE)
        } else {
            onExportSuccess(it)
        }
    }
    fun loadServerDataAnalytic(){
        analyticsEventUseCase.sendAnalyticsEvent(AnalyticsEvents.LOAD_SERVER_DATA.eventName)
    }
    fun appConfigDataAnalytic(){
        analyticsEventUseCase.sendAnalyticsEvent(AnalyticsEvents.APP_CONFIG_LOG_FILE.eventName)

    }
    fun exportDataAnalytic(){
        analyticsEventUseCase.sendAnalyticsEvent(AnalyticsEvents.IMPORT_DATA.eventName)
    }

    fun compressEventData(title: String) {
        BaselineLogger.d("ExportImportViewModel", "compressEventData ----")

        CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {
            try {
                onEvent(LoaderEvent.UpdateLoaderState(true))
                val compression = ZipFileCompression()
                val fileUri = compression.compressBackupFiles(
                    mAppContext,
                    listOf(),
                    exportImportUseCase.getUserDetailsExportUseCase.getUserMobileNumber(),
                    exportImportUseCase.getUserDetailsExportUseCase.getUserName(),
                    moduleName = moduleNameAccToLoggedInUser(loggedInUserType.value)
                )
                if (fileUri != null) {
                    BaselineLogger.d(
                        "ExportImportViewModel",
                        "compressEventData ${fileUri.path}----"
                    )
                    openShareSheet(convertURIAccToOS(fileUri), title, type = ZIP_MIME_TYPE)
                }
                CoreSharedPrefs.getInstance(mAppContext).setFileExported(true)
                onEvent(LoaderEvent.UpdateLoaderState(false))
            } catch (exception: Exception) {
                BaselineLogger.e("Compression", exception.message ?: "", exception)
                exception.printStackTrace()
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private fun convertURIAccToOS(uri: Uri): ArrayList<Uri> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return arrayListOf(uri)
        return arrayListOf(uriFromFile(mAppContext, uri.toFile(), applicationId.value))
    }

    fun restartApp(context: Context, cls: Class<*>) {
        BaselineLogger.d("ExportImportViewModel", "Restarting Application")
        context.startActivity(
            Intent(mAppContext, cls)
        )
        exitProcess(0)
    }

    fun importSelectedDB(uri: Uri, onImportSuccess: () -> Unit) {
        BaselineLogger.d("ExportImportViewModel", "importSelectedDB ----")
        try {

            importDbFile(
                appContext = mAppContext,
                importedDbUri = uri,
                deleteDBName = if (loggedInUserType.value == UPCM_USER) NUDGE_BASELINE_DATABASE else NUDGE_DATABASE,
                applicationID = applicationId.value
            ) {
                BaselineLogger.d("ExportImportViewModel", "importSelectedDB Success ----")
                onImportSuccess()
            }
        } catch (exception: Exception) {
            BaselineLogger.e(
                "ExportImportViewModel",
                "importSelectedDB : ${exception.message}",
                exception
            )
            onEvent(LoaderEvent.UpdateLoaderState(false))
        }
    }

    fun regenerateEvents(title: String) {
        onEvent(LoaderEvent.UpdateLoaderState(true))

        CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {
            try {
                if (loggedInUserType.value == UPCM_USER) {

                    eventWriterHelperImpl.regenerateAllEvent(appContext = mAppContext)
                    regenerateGrantEventUsecase.invoke()
                } else {
                    settingRepository.regenerateAllEvent(coreSharedPrefs = coreSharedPrefs)
                }
                compressEventData(title)
                withContext(CoreDispatchers.mainDispatcher) {
                    onEvent(LoaderEvent.UpdateLoaderState(false))
                }
                analyticsEventUseCase.sendAnalyticsEvent(AnalyticsEvents.REGENERATE_ALL_EVENT.eventName)
            } catch (exception: Exception) {
                BaselineLogger.e("RegenerateEvent", exception.message ?: "")
                exception.printStackTrace()
                withContext(CoreDispatchers.mainDispatcher) {
                    onEvent(LoaderEvent.UpdateLoaderState(false))
                }
            }

        }
    }


    fun getMobileNumber() = exportImportUseCase.getUserDetailsExportUseCase.getUserMobileNumber()
    fun fetchAppConfig(onApiSuccess: () -> Unit) {
        onEvent(LoaderEvent.UpdateLoaderState(true))
        CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {
            fetchAppConfigFromNetworkUseCase.invoke {
                onEvent(LoaderEvent.UpdateLoaderState(false))
                onApiSuccess()
            }
        }
    }

    fun getAllEventForUser() {
        CoroutineScope(CoreDispatchers.ioDispatcher).launch {
            isPendingEvent.value =
                syncManagerUseCase.fetchEventsFromDBUseCase.getPendingEventCount(SyncType.SYNC_ALL.ordinal) > 0
        }
    }

    fun isEventPending(): Boolean {
        return isPendingEvent.value
    }
}
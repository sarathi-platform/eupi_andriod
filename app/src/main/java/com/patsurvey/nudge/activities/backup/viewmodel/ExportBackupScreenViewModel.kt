package com.patsurvey.nudge.activities.backup.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toFile
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.NUDGE_BASELINE_DATABASE
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BSLogWriter
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.openShareSheet
import com.nrlm.baselinesurvey.utils.showCustomToast
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nudge.core.CoreDispatchers
import com.nudge.core.NUDGE_DATABASE
import com.nudge.core.ZIP_MIME_TYPE
import com.nudge.core.compression.ZipFileCompression
import com.nudge.core.exportAllOldImages
import com.nudge.core.exportDatabase
import com.nudge.core.exportLogFile
import com.nudge.core.exportOldData
import com.nudge.core.getFirstName
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.model.SettingOptionModel
import com.nudge.core.moduleNameAccToLoggedInUser
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.ui.events.ToastMessageEvent
import com.nudge.core.uriFromFile
import com.patsurvey.nudge.BuildConfig
import com.patsurvey.nudge.activities.backup.domain.use_case.ExportImportUseCase
import com.patsurvey.nudge.activities.settings.domain.use_case.SettingBSUserCase
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.UPCM_USER
import com.sarathi.dataloadingmangement.NUDGE_GRANT_DATABASE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExportBackupScreenViewModel @Inject constructor(
    private val exportImportUseCase: ExportImportUseCase,
    private val settingBSUserCase: SettingBSUserCase,
    val prefBSRepo: PrefBSRepo,
    val prefRepo: PrefRepo,
) : BaseViewModel() {
    var mAppContext: Context
    val _exportOptionList = mutableStateOf<List<SettingOptionModel>>(emptyList())
    val exportOptionList: State<List<SettingOptionModel>> get() = _exportOptionList
    private val _loaderState = mutableStateOf<LoaderState>(LoaderState(false))
    val applicationId = mutableStateOf(BLANK_STRING)
    val loggedInUserType = mutableStateOf(BLANK_STRING)
    var userType: String = BLANK_STRING

    init {
        mAppContext = NudgeCore.getAppContext()
        userType = settingBSUserCase.getSettingOptionListUseCase.getUserType().toString()

        applicationId.value =
            CoreAppDetails.getApplicationDetails()?.applicationID ?: BuildConfig.APPLICATION_ID
        _exportOptionList.value =
            exportImportUseCase.getExportOptionListUseCase.fetchExportDataOptionList()

        loggedInUserType.value =
            exportImportUseCase.getUserDetailsExportUseCase.getLoggedInUserType()
    }
    fun exportOnlyLogFile(context: Context) {
        BaselineLogger.d("ExportBackupViewModel", "exportOnlyLogFile: ----")
        try {
            CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {
                onEvent(LoaderEvent.UpdateLoaderState(true))
                val logFile = BSLogWriter.buildLogFile(appContext = mAppContext) {
                    onEvent(LoaderEvent.UpdateLoaderState(false))
                    onEvent(ToastMessageEvent.ShowToastMessage(context.getString(R.string.no_logs_available)))
                }
                if (logFile != null) {
                    exportLogFile(
                        logFile,
                        appContext = mAppContext,
                        applicationID = applicationId.value,
                        userName = getFirstName(exportImportUseCase.getUserDetailsExportUseCase.getUserName()),
                        mobileNo = exportImportUseCase.getUserDetailsExportUseCase.getUserMobileNumber(),
                        moduleName = moduleNameAccToLoggedInUser(loggedInUserType.value)
                    ) {
                        onEvent(LoaderEvent.UpdateLoaderState(false))
                        openShareSheet(convertURIAccToOS(it), "", type = ZIP_MIME_TYPE)
                    }


                }
            }
        } catch (e: Exception) {
            onEvent(LoaderEvent.UpdateLoaderState(false))
            BaselineLogger.e("ExportBackupViewModel", "exportOnlyLogFile :${e.message}", e)
        }
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


    fun exportLocalDatabase(isNeedToShare: Boolean, onExportSuccess: (Uri) -> Unit) {
        BaselineLogger.d("ExportBackupViewModel", "exportLocalDatabase -----")
        try {
            onEvent(LoaderEvent.UpdateLoaderState(true))
            if (loggedInUserType.value == UPCM_USER) {
                exportDatabase(
                    appContext = mAppContext,
                    applicationID = applicationId.value,
                    mobileNo = exportImportUseCase.getUserDetailsExportUseCase.getUserMobileNumber(),
                    databaseName = listOf(NUDGE_BASELINE_DATABASE, NUDGE_GRANT_DATABASE),
                    userName = getFirstName(exportImportUseCase.getUserDetailsExportUseCase.getUserName()),
                    moduleName = moduleNameAccToLoggedInUser(loggedInUserType.value)
                ) {
                    BaselineLogger.d("ExportBackupViewModel", "exportLocalDatabase : ${it.path}")
                    onExportLocalDbSuccess(isNeedToShare, it, onExportSuccess)
                }
            } else {
                exportOldData(
                    appContext = mAppContext,
                    applicationID = applicationId.value,
                    mobileNo = exportImportUseCase.getUserDetailsExportUseCase.getUserMobileNumber(),
                    databaseName = NUDGE_DATABASE,
                    userName = getFirstName(exportImportUseCase.getUserDetailsExportUseCase.getUserName()),
                    moduleName = moduleNameAccToLoggedInUser(loggedInUser = loggedInUserType.value)
                ) {
                    BaselineLogger.d("ExportBackupViewModel", "exportLocalDatabase : ${it.path}")
                    onExportLocalDbSuccess(isNeedToShare, it, onExportSuccess)
                }
            }

        } catch (e: Exception) {
            onEvent(LoaderEvent.UpdateLoaderState(false))
            BaselineLogger.e("ExportBackupViewModel", "exportLocalDatabase :${e.message}", e)
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


    fun exportLocalImages() {
        BaselineLogger.d("ExportBackupViewModel", "exportLocalImages ----")
        try {
            CoroutineScope(CoreDispatchers.ioDispatcher).launch {
                onEvent(LoaderEvent.UpdateLoaderState(true))
                val imageZipUri = exportAllOldImages(
                    appContext = mAppContext,
                    applicationID = applicationId.value,
                    mobileNo = exportImportUseCase.getUserDetailsExportUseCase.getUserMobileNumber(),
                    moduleName = moduleNameAccToLoggedInUser(loggedInUser = loggedInUserType.value),
                    userName = getFirstName(exportImportUseCase.getUserDetailsExportUseCase.getUserName())
                )
                onEvent(LoaderEvent.UpdateLoaderState(false))
                if (imageZipUri != null) {
                    BaselineLogger.d(
                        "ExportBackupViewModel",
                        "exportLocalImages: ${imageZipUri.path} ----"
                    )
                    openShareSheet(
                        convertURIAccToOS(imageZipUri),
                        "Share All Images",
                        type = ZIP_MIME_TYPE
                    )
                }
            }
        } catch (e: Exception) {
            onEvent(LoaderEvent.UpdateLoaderState(false))
            BaselineLogger.e("ExportBackupViewModel", "exportLocalImages :${e.message}", e)
        }
    }

    fun compressEventData(title: String) {
        BaselineLogger.d("ExportBackupViewModel", "compressEventData ----")

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
                        "ExportBackupViewModel",
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

    fun getMobileNumber() = settingBSUserCase.getUserDetailsUseCase.getUserMobileNumber()

}
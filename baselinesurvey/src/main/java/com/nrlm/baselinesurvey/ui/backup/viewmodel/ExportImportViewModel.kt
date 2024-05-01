package com.nrlm.baselinesurvey.ui.backup.viewmodel

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.BuildConfig
import com.nrlm.baselinesurvey.NUDGE_BASELINE_DATABASE
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.domain.EventWriterHelperImpl
import com.nrlm.baselinesurvey.ui.backup.domain.use_case.ExportImportUseCase
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.LogWriter
import com.nrlm.baselinesurvey.utils.showCustomToast
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nudge.core.ZIP_MIME_TYPE
import com.nudge.core.compression.ZipFileCompression
import com.nudge.core.exportAllOldImages
import com.nudge.core.exportLogFile
import com.nudge.core.exportOldData
import com.nudge.core.getFirstName
import com.nudge.core.importDbFile
import com.nudge.core.model.SettingOptionModel
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.ui.events.ToastMessageEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.system.exitProcess

@HiltViewModel
class ExportImportViewModel @Inject constructor(
    private val exportImportUseCase: ExportImportUseCase,
    private val eventWriterHelperImpl: EventWriterHelperImpl
): BaseViewModel() {
    val _optionList = mutableStateOf<List<SettingOptionModel>>(emptyList())
    val optionList: State<List<SettingOptionModel>> get() = _optionList

    val showLoadConfirmationDialog = mutableStateOf(false)
    val showRestartAppDialog = mutableStateOf(false)
    private val userUniqueKey= mutableStateOf(BLANK_STRING)
    private val _loaderState = mutableStateOf<LoaderState>(LoaderState(false))

    val loaderState: State<LoaderState> get() = _loaderState

    init {
        _optionList.value=exportImportUseCase.getExportOptionListUseCase.fetchExportOptionList()
    }
    override fun <T> onEvent(event: T) {
        when(event){
            is ToastMessageEvent.ShowToastMessage ->{
              showCustomToast(BaselineCore.getAppContext(),event.message)
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }
    }

    fun clearLocalDatabase(onPageChange:()->Unit){
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val result=exportImportUseCase.clearLocalDBExportUseCase.invoke()
                if(result){
                    exportImportUseCase.clearLocalDBExportUseCase.setAllDataSyncStatus()
                    withContext(Dispatchers.Main){
                        onPageChange()
                    }
                }
            }catch (ex:Exception){
                ex.printStackTrace()
                BaselineLogger.d("ExportImportViewModel","clearLocalDatabase : ${ex.message}")
            }
        }
    }

    fun exportLocalDatabase(isNeedToShare:Boolean,onExportSuccess: (Uri) -> Unit) {
        BaselineLogger.d("ExportImportViewModel","exportLocalDatabase -----")
         try {
             onEvent(LoaderEvent.UpdateLoaderState(true))
             exportOldData(
                 appContext = BaselineCore.getAppContext(),
                 applicationID = BuildConfig.APPLICATION_ID,
                 mobileNo = exportImportUseCase.getUserDetailsExportUseCase.getUserMobileNumber(),
                 databaseName = NUDGE_BASELINE_DATABASE,
                 userName = getFirstName(exportImportUseCase.getUserDetailsExportUseCase.getUserName())
             ) {
                 BaselineLogger.d("ExportImportViewModel","exportLocalDatabase : ${it.path}")
                 onEvent(LoaderEvent.UpdateLoaderState(false))
                 if(isNeedToShare){
                     openShareSheet(arrayListOf(it) ,"")
                 } else onExportSuccess(it)
             }
         }catch (e:Exception){
             onEvent(LoaderEvent.UpdateLoaderState(false))
            BaselineLogger.e("ExportImportViewModel","exportLocalDatabase :${e.message}")
         }

    }

    private fun openShareSheet(fileUriList: ArrayList<Uri>?, title: String) {
        if(fileUriList?.isNotEmpty() == true){
            val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
            shareIntent.setType(ZIP_MIME_TYPE)
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUriList)
            shareIntent.putExtra(Intent.EXTRA_TITLE, title)
            val chooserIntent = Intent.createChooser(shareIntent, title)
            val resInfoList: List<ResolveInfo> =
                BaselineCore.getAppContext().packageManager
                    .queryIntentActivities(chooserIntent, PackageManager.MATCH_DEFAULT_ONLY)

            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                BaselineCore.getAppContext().grantUriPermission(
                    packageName,
                    fileUriList[0],
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            BaselineCore.startExternalApp(chooserIntent)
        }

    }

    fun exportLocalImages(){
        BaselineLogger.d("ExportImportViewModel","exportLocalImages ----")
        try {
        CoroutineScope(Dispatchers.IO).launch {
            onEvent(LoaderEvent.UpdateLoaderState(true))
            val imageZipUri= exportAllOldImages(
                appContext = BaselineCore.getAppContext(),
                applicationID = BuildConfig.APPLICATION_ID,
                mobileNo = exportImportUseCase.getUserDetailsExportUseCase.getUserMobileNumber(),
                timeInMillSec = System.currentTimeMillis().toString(),
                userName = getFirstName(exportImportUseCase.getUserDetailsExportUseCase.getUserName())
            )
            onEvent(LoaderEvent.UpdateLoaderState(false))
            if(imageZipUri != null){
                BaselineLogger.d("ExportImportViewModel","exportLocalImages: ${imageZipUri.path} ----")
                openShareSheet(arrayListOf(imageZipUri),"Share All Images")
            }
        }
        }catch (e:Exception){
            onEvent(LoaderEvent.UpdateLoaderState(false))
            BaselineLogger.e("ExportImportViewModel","exportLocalImages :${e.message}")
        }
    }
fun exportOnlyLogFile(context: Context){
    BaselineLogger.d("ExportImportViewModel","exportOnlyLogFile: ----")
   try {
    CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
        onEvent(LoaderEvent.UpdateLoaderState(true))
       val logFile= LogWriter.buildLogFile(appContext = BaselineCore.getAppContext()){
           onEvent(LoaderEvent.UpdateLoaderState(false))
               onEvent(ToastMessageEvent.ShowToastMessage(context.getString(R.string.no_logs_available)))
       }
        if (logFile != null) {
            exportLogFile(
                logFile,
                appContext = BaselineCore.getAppContext(),
                applicationID = BuildConfig.APPLICATION_ID,
                userName = getFirstName(exportImportUseCase.getUserDetailsExportUseCase.getUserName()),
                mobileNo = exportImportUseCase.getUserDetailsExportUseCase.getUserMobileNumber()
            ) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
                openShareSheet(arrayListOf(it) ,"")
            }


        }
    }
   }catch (e:Exception){
       onEvent(LoaderEvent.UpdateLoaderState(false))
       BaselineLogger.e("ExportImportViewModel","exportOnlyLogFile :${e.message}")
   }
}
    fun compressEventData(title: String) {
        BaselineLogger.d("ExportImportViewModel","compressEventData ----")

        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                onEvent(LoaderEvent.UpdateLoaderState(true))
                val compression = ZipFileCompression()
                val fileUri = compression.compressBackupFiles(
                    BaselineCore.getAppContext(),
                    listOf(),
                    exportImportUseCase.getUserDetailsExportUseCase.getUserMobileNumber(),
                    exportImportUseCase.getUserDetailsExportUseCase.getUserID()
                )
               if(fileUri!=null) {
                   BaselineLogger.d("ExportImportViewModel","compressEventData ${fileUri.path}----")
                   openShareSheet(arrayListOf(fileUri), title)
               }
                CoreSharedPrefs.getInstance(BaselineCore.getAppContext()).setFileExported(true)
                onEvent(LoaderEvent.UpdateLoaderState(false))
            } catch (exception: Exception) {
                BaselineLogger.e("Compression", exception.message ?: "")
                exception.printStackTrace()
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    fun restartApp(context: Context, cls: Class<*>) {
        BaselineLogger.d("ExportImportViewModel","Restarting Application")
        context.startActivity(
            Intent(BaselineCore.getAppContext(), cls)
        )
        exitProcess(0)
    }

    fun importSelectedDB(uri: Uri, onImportSuccess: () -> Unit) {
        BaselineLogger.d("ExportImportViewModel","importSelectedDB ----")
        try {

        importDbFile(
            appContext = BaselineCore.getAppContext(),
            importedDbUri = uri,
            deleteDBName = NUDGE_BASELINE_DATABASE,
            applicationID = BuildConfig.APPLICATION_ID
        ) {
            BaselineLogger.d("ExportImportViewModel","importSelectedDB Success ----")
            onImportSuccess()
        }
        } catch (exception: Exception) {
            BaselineLogger.e("ExportImportViewModel", "importSelectedDB : ${exception.message}")
            exception.printStackTrace()
            onEvent(LoaderEvent.UpdateLoaderState(false))
        }
    }

    fun regenerateEvents(title: String) {
        onEvent(LoaderEvent.UpdateLoaderState(true))

        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {


                eventWriterHelperImpl.regenerateAllEvent()
                compressEventData(title)
                withContext(Dispatchers.Main) {
                    onEvent(LoaderEvent.UpdateLoaderState(false))
                }
            } catch (exception: Exception) {
                BaselineLogger.e("RegenerateEvent", exception.message ?: "")
                exception.printStackTrace()
                withContext(Dispatchers.Main) {
                    onEvent(LoaderEvent.UpdateLoaderState(false))
                }
            }

        }
    }
}
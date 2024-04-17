package com.nrlm.baselinesurvey.ui.setting.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nrlm.baselinesurvey.BuildConfig
import com.nrlm.baselinesurvey.NUDGE_BASELINE_DATABASE
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.ui.setting.domain.use_case.SettingBSUserCase
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.LogWriter
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nrlm.baselinesurvey.utils.uriFromFile
import com.nudge.core.ZIP_MIME_TYPE
import com.nudge.core.compression.ZipFileCompression
import com.nudge.core.exportOldData
import com.nudge.core.importDbFile
import com.nudge.core.model.SettingOptionModel
import com.nudge.core.preference.CoreSharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import kotlin.system.exitProcess

@HiltViewModel
class SettingBSViewModel @Inject constructor(
    private val settingBSUserCase: SettingBSUserCase
):BaseViewModel() {
    val _optionList = mutableStateOf<List<SettingOptionModel>>(emptyList())
    val optionList: State<List<SettingOptionModel>> get() = _optionList

    private val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val showLoadConfirmationDialog = mutableStateOf(false)
    val showRestartAppDialog = mutableStateOf(false)

    val loaderState: State<LoaderState> get() = _loaderState


    fun performLogout(onLogout: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val settingUseCaseResponse = settingBSUserCase.logoutUseCase.invoke()
            withContext(Dispatchers.Main) {
                onLogout(settingUseCaseResponse)
            }

        }
    }

    fun saveLanguagePageFrom() {
        settingBSUserCase.saveLanguageScreenOpenFromUseCase.invoke()
    }

    fun buildAndShareLogs() {
        BaselineLogger.d("SettingBSViewModel", "buildAndShareLogs---------------")
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            LogWriter.buildSupportLogAndShare(
                userMobileNo = settingBSUserCase.getUserDetailsUseCase.getUserMobileNumber(),
                userEmail = settingBSUserCase.getUserDetailsUseCase.getUserEmail())
        }
    }

    fun compressEventData(title: String) {

        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                onEvent(LoaderEvent.UpdateLoaderState(true))
                val compression = ZipFileCompression()
                val fileUri = compression.compressBackupFiles(
                    BaselineCore.getAppContext(),
                    listOf(),
                    settingBSUserCase.getUserDetailsUseCase.getUserMobileNumber()
                )

                val imageUri = compression.compressBackupImages(
                    BaselineCore.getAppContext(),
                    settingBSUserCase.getUserDetailsUseCase.getUserMobileNumber()
                )

                val zipDBFileDirectory = BaselineCore.getAppContext()
                    .getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.path

                val directory = File(zipDBFileDirectory)

                val zipFileList= directory.listFiles()
                    ?.filterNot { it.name.contains("Image") }
                    ?.filter { it.isFile && it.name.contains(getUserMobileNumber()) }

                val fileUriList: ArrayList<Uri> = arrayListOf()
                if(fileUri!=Uri.EMPTY)
                    fileUri?.let { fileUriList.add(it) }

                  if(imageUri!=Uri.EMPTY)
                      imageUri?.let { fileUriList.add(it) }

                if(zipFileList?.isNotEmpty() == true){
                    zipFileList.forEach { file ->
                        fileUriList.add(uriFromFile(BaselineCore.getAppContext(), file))
                    }
                }


                openShareSheet(fileUriList, title)
                CoreSharedPrefs.getInstance(BaselineCore.getAppContext()).setFileExported(true)
                onEvent(LoaderEvent.UpdateLoaderState(false))
            } catch (exception: Exception) {
                BaselineLogger.e("Compression", exception.message ?: "")
                exception.printStackTrace()
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private fun openShareSheet( fileUriList: ArrayList<Uri>?, title: String) {
        if(fileUriList?.isNotEmpty() == true){
            val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
            shareIntent.setType(ZIP_MIME_TYPE)
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUriList)
            shareIntent.putExtra(Intent.EXTRA_TITLE, title)
            val chooserIntent = Intent.createChooser(shareIntent, title)
            BaselineCore.startExternalApp(chooserIntent)
        }

    }

    override fun <T> onEvent(event: T) {
        when (event) {
            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }
    }

    fun exportDbAndImages(onExportSuccess: () -> Unit) {
        val userUniqueId = "${settingBSUserCase.getUserDetailsUseCase.getUserID()}_${settingBSUserCase.getUserDetailsUseCase.getUserMobileNumber()}"
        exportOldData(
            appContext = BaselineCore.getAppContext(),
            applicationID = BuildConfig.APPLICATION_ID,
            userUniqueId = userUniqueId,
            databaseName = NUDGE_BASELINE_DATABASE
        ) {
            onExportSuccess()
        }
    }

    fun importSelectedDB(uri: Uri,onImportSuccess:()->Unit) {
        importDbFile(appContext = BaselineCore.getAppContext(), importedDbUri = uri, deleteDBName = NUDGE_BASELINE_DATABASE){
           onImportSuccess()
        }


    }

    fun clearLocalDatabase(onPageChange:()->Unit){
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val result=settingBSUserCase.clearLocalDBUseCase.invoke()
            if(result){
                settingBSUserCase.logoutUseCase.setAllDataSyncStatus()
                withContext(Dispatchers.Main){
                    onPageChange()
                }
            }
        }
    }

    fun getUserMobileNumber():String{
        return settingBSUserCase.getUserDetailsUseCase.getUserMobileNumber()
    }

    fun restartApp(context: Context, cls: Class<*>) {

        context.startActivity(
            Intent(BaselineCore.getAppContext(), cls)
        )
        exitProcess(0)
    }

}
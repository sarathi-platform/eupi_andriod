package com.nrlm.baselinesurvey.ui.setting.viewmodel

import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.FileProvider
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.domain.EventWriterHelperImpl
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.NudgeBaselineDatabase
import com.nrlm.baselinesurvey.database.dao.SectionEntityDao
import com.nrlm.baselinesurvey.database.entity.SectionEntity
import com.nrlm.baselinesurvey.model.datamodel.toCsv
import com.nrlm.baselinesurvey.ui.setting.domain.use_case.SettingBSUserCase
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.LogWriter
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nudge.core.ZIP_MIME_TYPE
import com.nudge.core.compression.ZipFileCompression
import com.nudge.core.datamodel.BaseLineQnATableCSV
import com.nudge.core.exportcsv.CsvConfig
import com.nudge.core.exportcsv.ExportService
import com.nudge.core.exportcsv.Exports
import com.nudge.core.model.SettingOptionModel
import com.nudge.core.preference.CoreSharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingBSViewModel @Inject constructor(
    private val settingBSUserCase: SettingBSUserCase,
    private val sectionEntityDao: SectionEntityDao,
    private val eventWriterHelperImpl: EventWriterHelperImpl,
    val prefRepo: PrefRepo
):BaseViewModel() {
    val _optionList = mutableStateOf<List<SettingOptionModel>>(emptyList())
    val optionList: State<List<SettingOptionModel>> get() = _optionList

    private val _loaderState = mutableStateOf<LoaderState>(LoaderState(false))
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
            LogWriter.buildSupportLogAndShare(prefRepo)
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
                    prefRepo.getMobileNumber() ?: ""
                )

                val imageUri = compression.compressBackupImages(
                    BaselineCore.getAppContext(),
                    prefRepo.getMobileNumber() ?: ""
                )

                openShareSheet(imageUri, fileUri, title)
                CoreSharedPrefs.getInstance(BaselineCore.getAppContext()).setFileExported(true)
                onEvent(LoaderEvent.UpdateLoaderState(false))
            } catch (exception: Exception) {
                BaselineLogger.e("Compression", exception.message ?: "")
                exception.printStackTrace()
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }


    fun exportBaseLineQnA() {
        Log.i("PATH", "Check")
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                onEvent(LoaderEvent.UpdateLoaderState(true))
                val didiEntity = sectionEntityDao.getSectionsT()

                ExportService.export<BaseLineQnATableCSV>(
                    type = Exports.CSV(CsvConfig(prefix = "SECTION-${prefRepo.getMobileNumber()}", hostPath = BaselineCore.getAppContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath ?:  "")),
                    content = didiEntity.toCsv()
                ).catch { error ->
                    Log.i("PATH", "Error$error")
                    // handle error here
//                    BaselineLogger.e(TAG, "exportDidiTableToCsv error", error)
                }.collect{path ->
                    Log.i("PATH" , path)

                    // Open CSV file in a reader app
                    val file = File(path)
                    val uri = FileProvider.getUriForFile(BaselineCore.getAppContext(), BaselineCore.getAppContext().packageName + ".provider", file)
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(uri, "text/csv")
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    BaselineCore.startExternalApp(intent)

                }
//                val compression = ZipFileCompression()
//                val fileUri = compression.compressBackupFiles(
//                    BaselineCore.getAppContext(),
//                    listOf(),
//                    prefRepo.getMobileNumber() ?: ""
//                )
//
//                val imageUri = compression.compressBackupImages(
//                    BaselineCore.getAppContext(),
//                    prefRepo.getMobileNumber() ?: ""
//                )
//
//                openShareSheet(imageUri, fileUri, title)
//                CoreSharedPrefs.getInstance(BaselineCore.getAppContext()).setFileExported(true)
                onEvent(LoaderEvent.UpdateLoaderState(false))
            } catch (exception: Exception) {
                Log.i("PATH", "2Error${exception.message}")
//                BaselineLogger.e("ExportBaseLineQnA", exception.message ?: "")
                exception.printStackTrace()
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }
    private fun openShareSheet(imageUri: Uri?, fileUri: Uri?, title: String) {
        val fileUris = listOf(fileUri, imageUri)
        val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        shareIntent.setType(ZIP_MIME_TYPE)
        shareIntent.putExtra(Intent.EXTRA_STREAM, ArrayList(fileUris))
        shareIntent.putExtra(Intent.EXTRA_TITLE, title)
        val chooserIntent = Intent.createChooser(shareIntent, title)
        BaselineCore.startExternalApp(chooserIntent)
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
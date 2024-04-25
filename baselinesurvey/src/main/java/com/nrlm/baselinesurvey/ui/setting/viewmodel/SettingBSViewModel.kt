package com.nrlm.baselinesurvey.ui.setting.viewmodel

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.domain.EventWriterHelperImpl
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.ui.setting.domain.use_case.SettingBSUserCase
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.LogWriter
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nudge.core.ZIP_MIME_TYPE
import com.nudge.core.compression.ZipFileCompression
import com.nudge.core.model.SettingOptionModel
import com.nudge.core.preference.CoreSharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingBSViewModel @Inject constructor(
    private val settingBSUserCase: SettingBSUserCase,
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
                    settingBSUserCase.getUserDetailsUseCase.getMobileNo(),
                    userName = settingBSUserCase.getUserDetailsUseCase.getUserName(),

                    )

                val imageUri = compression.compressBackupImages(
                    BaselineCore.getAppContext(),
                    settingBSUserCase.getUserDetailsUseCase.getMobileNo(),
                    userName = settingBSUserCase.getUserDetailsUseCase.getUserName(),

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
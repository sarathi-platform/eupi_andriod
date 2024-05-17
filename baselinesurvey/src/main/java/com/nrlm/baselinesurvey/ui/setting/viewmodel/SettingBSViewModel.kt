package com.nrlm.baselinesurvey.ui.setting.viewmodel

import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.nrlm.baselinesurvey.BuildConfig
import com.nrlm.baselinesurvey.NUDGE_BASELINE_DATABASE
import com.nrlm.baselinesurvey.PREF_KEY_NAME
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.ui.setting.domain.use_case.SettingBSUserCase
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.LogWriter
import com.nrlm.baselinesurvey.utils.openShareSheet
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nudge.core.BLANK_STRING
import com.nudge.core.LOCAL_BACKUP_EXTENSION
import com.nudge.core.SARATHI_DIRECTORY_NAME
import com.nudge.core.ZIP_MIME_TYPE
import com.nudge.core.compression.ZipFileCompression
import com.nudge.core.exportAllOldImages
import com.nudge.core.exportDbFile
import com.nudge.core.getFirstName
import com.nudge.core.json
import com.nudge.core.model.SettingOptionModel
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.uriFromFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingBSViewModel @Inject constructor(
    private val settingBSUserCase: SettingBSUserCase,
    val prefRepo: PrefRepo
) : BaseViewModel() {
    val _optionList = mutableStateOf<List<SettingOptionModel>>(emptyList())
    val optionList: State<List<SettingOptionModel>> get() = _optionList


    private val _loaderState = mutableStateOf<LoaderState>(LoaderState(false))
    val loaderState: State<LoaderState> get() = _loaderState
    val showLogoutConfirmationDialog = mutableStateOf(false)



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

    fun compressEventData(title: String) {
        BaselineLogger.d("SettingBSViewModel", "compressEventData---------------")
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                onEvent(LoaderEvent.UpdateLoaderState(true))
                val fileUriList: ArrayList<Uri> = arrayListOf()
                val fileAndDbZipList = ArrayList<Pair<String, Uri?>>()
                val compression = ZipFileCompression()

                //Delete Old Image zips.
                compression.deleteOldFiles(
                    context = BaselineCore.getAppContext(),
                    fileNameReference = "${getFirstName(settingBSUserCase.getUserDetailsUseCase.getUserName())}_${getUserMobileNumber()}_Sarathi_Image",
                    folderName = getUserMobileNumber(),
                    fileType = SUFFIX_IMAGE_ZIP_FILE,
                    applicationId = BuildConfig.APPLICATION_ID,
                    checkInAppDirectory = true
                )

                //Delete old event zips.
                compression.deleteOldFiles(
                    context = BaselineCore.getAppContext(),
                    fileNameReference = "${getFirstName(settingBSUserCase.getUserDetailsUseCase.getUserName())}_${getUserMobileNumber()}_sarathi_",
                    folderName = getUserMobileNumber(),
                    fileType = SUFFIX_EVENT_ZIP_FILE
                )

                // Image Files and Zip
                val imageUri = exportAllOldImages(
                    appContext = BaselineCore.getAppContext(),
                    applicationID = BuildConfig.APPLICATION_ID,
                    mobileNo = settingBSUserCase.getUserDetailsUseCase.getUserMobileNumber(),
                    timeInMillSec = System.currentTimeMillis().toString(),
                    userName = getFirstName(settingBSUserCase.getUserDetailsUseCase.getUserName())
                )

                if(imageUri!=Uri.EMPTY) {
                    imageUri?.let {
                        fileUriList.add(it)
                        BaselineLogger.d("SettingBSViewModel_URI", "Image File Uri: ${it.path}---------------")
                    }
                }

                // Database File and URI
                val dbUri = exportDbFile(
                    appContext = BaselineCore.getAppContext(),
                    applicationID = BuildConfig.APPLICATION_ID,
                    databaseName = NUDGE_BASELINE_DATABASE
                )


                if(dbUri!= Uri.EMPTY){
                    dbUri?.let {
                        BaselineLogger.d("SettingBSViewModel", "Database File Uri: ${it.path}---------------")
                        fileAndDbZipList.add(Pair(NUDGE_BASELINE_DATABASE,it))
                    }
                }

                val eventFilePath =
                    File(Environment.DIRECTORY_DOCUMENTS + SARATHI_DIRECTORY_NAME + "/" + getUserMobileNumber())

                if(eventFilePath.exists() && eventFilePath.isDirectory){
                    val eventFiles= eventFilePath.listFiles()?.filter { it.isFile && it.name.contains("event") }
                    if (eventFiles != null) {
                        if(eventFiles.isNotEmpty()){
                            eventFiles.forEach {
                                fileAndDbZipList.add(Pair(it.name,it.toUri()))
                            }
                        }
                    }
                }

                // Add Log File



                val logFile= LogWriter.buildLogFile(appContext = BaselineCore.getAppContext()){}
                if (logFile != null) {
                    val logFileUri = uriFromFile(
                        BaselineCore.getAppContext(),
                        logFile,
                        BuildConfig.APPLICATION_ID
                    )
                    if (logFileUri != Uri.EMPTY) {
                        logFileUri.let {
                            fileAndDbZipList.add(Pair(logFile.name, it))
                            BaselineLogger.d(
                                "SettingBSViewModel",
                                "Log File Uri: ${it.path}---------------"
                            )

                        }
                    }
                }

                getSummaryFile()?.let {
                    fileAndDbZipList.add(it)
                }

                val zipFileName =
                    "${getFirstName(settingBSUserCase.getUserDetailsUseCase.getUserName())}_${getUserMobileNumber()}_sarathi_${System.currentTimeMillis()}"

                if (fileUriList.isNotEmpty()) {
                    val zipLogDbFileUri = compression.compressData(
                        BaselineCore.getAppContext(),
                        zipFileName,
                        Environment.DIRECTORY_DOCUMENTS + SARATHI_DIRECTORY_NAME + "/" + getUserMobileNumber(),
                        fileAndDbZipList,
                        getUserMobileNumber()
                    )
                    zipLogDbFileUri?.let {
                        if (it != Uri.EMPTY) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                fileUriList.add(it)
                            else fileUriList.add(uriFromFile(context = BaselineCore.getAppContext(),
                                applicationID = BuildConfig.APPLICATION_ID,
                                file = it.toFile()))
                        }
                    }
                }

                BaselineLogger.d("SettingBSViewModel", " Share Dialog Open ${fileUriList.json()}" )
                openShareSheet(fileUriList, title, ZIP_MIME_TYPE)
                CoreSharedPrefs.getInstance(BaselineCore.getAppContext()).setFileExported(true)
                onEvent(LoaderEvent.UpdateLoaderState(false))
            } catch (exception: Exception) {
                BaselineLogger.e("Compression Exception", exception.message ?: "")
                exception.printStackTrace()
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private suspend fun getSummaryFile(): Pair<String, Uri?>? {
        val summaryFileNameWithoutExtension = "Sarathi_${
            (prefRepo.getPref(
                PREF_KEY_NAME,
                BLANK_STRING
            ) ?: BLANK_STRING).trim().split(" ").first()
        }_${prefRepo.getUniqueUserIdentifier()}_summary_file"

        val summaryFileNameWithExtension = summaryFileNameWithoutExtension + LOCAL_BACKUP_EXTENSION

        return settingBSUserCase.getSummaryFileUseCase.invoke(
            userId = prefRepo.getUniqueUserIdentifier(),
            mobileNo = getUserMobileNumber(),
            fileNameWithoutExtension = summaryFileNameWithoutExtension,
            fileNameWithExtension = summaryFileNameWithExtension
        )
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

   fun getUserMobileNumber():String{
        return settingBSUserCase.getUserDetailsUseCase.getUserMobileNumber()
    }
}
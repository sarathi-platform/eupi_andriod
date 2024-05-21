package com.patsurvey.nudge.activities.settings.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.BuildConfig
import com.nrlm.baselinesurvey.NUDGE_BASELINE_DATABASE
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
import com.nrlm.baselinesurvey.database.NudgeBaselineDatabase
import com.patsurvey.nudge.activities.settings.domain.use_case.SettingBSUserCase
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.LogWriter
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nudge.core.LOCAL_BACKUP_EXTENSION
import com.nudge.core.SARATHI_DIRECTORY_NAME
import com.nudge.core.SUFFIX_EVENT_ZIP_FILE
import com.nudge.core.SUFFIX_IMAGE_ZIP_FILE
import com.nudge.core.ZIP_MIME_TYPE
import com.nudge.core.compression.ZipFileCompression
import com.nudge.core.exportAllOldImages
import com.nudge.core.exportDbFile
import com.nudge.core.getDefaultBackUpFileName
import com.nudge.core.getFirstName
import com.nudge.core.json
import com.nudge.core.model.SettingOptionModel
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.uriFromFile
import com.patsurvey.nudge.MyApplication
import com.patsurvey.nudge.activities.settings.domain.SettingTagEnum
import com.patsurvey.nudge.database.service.csv.ExportHelper
import com.patsurvey.nudge.utils.BPC_USER_TYPE
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.FORM_A_PDF_NAME
import com.patsurvey.nudge.utils.FORM_B_PDF_NAME
import com.patsurvey.nudge.utils.FORM_C_PDF_NAME
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PageFrom
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.UPCM_USER
import com.patsurvey.nudge.utils.VO_ENDORSEMENT_CONSTANT
import com.patsurvey.nudge.utils.WealthRank
import com.patsurvey.nudge.utils.openShareSheet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@HiltViewModel
class SettingBSViewModel @Inject constructor(
    private val settingBSUserCase: SettingBSUserCase,
    private val nudgeBaselineDatabase: NudgeBaselineDatabase,
    val exportHelper: ExportHelper,
    val prefBSRepo: PrefBSRepo
):BaseViewModel() {
    val _optionList = mutableStateOf<List<SettingOptionModel>>(emptyList())
    var showLogoutDialog = mutableStateOf(false)
    var showLoader = mutableStateOf(false)

    val optionList: State<List<SettingOptionModel>> get() = _optionList
    var userType:String= BLANK_STRING
    private val _loaderState = mutableStateOf<LoaderState>(LoaderState(isLoaderVisible = false))

    val formAAvailable = mutableStateOf(false)
    val formBAvailable = mutableStateOf(false)
    val formCAvailable = mutableStateOf(false)
    val loaderState: State<LoaderState> get() = _loaderState
    fun initOptions(context:Context) {
        userType = settingBSUserCase.getSettingOptionListUseCase.getUserType().toString()
        val villageId=settingBSUserCase.getSettingOptionListUseCase.getSelectedVillageId()
        val settingOpenFrom=settingBSUserCase.getSettingOptionListUseCase.settingOpenFrom()
        val list = ArrayList<SettingOptionModel>()

        list.add(
            SettingOptionModel(
                1,
                context.getString(R.string.profile),
                BLANK_STRING,
                SettingTagEnum.PROFILE.name
            )
        )
        if(userType != UPCM_USER){
            if(settingOpenFrom!= PageFrom.VILLAGE_PAGE.ordinal) {
                list.add(
                    SettingOptionModel(
                        2,
                        context.getString(R.string.forms),
                        BLANK_STRING,
                        SettingTagEnum.FORMS.name
                    )
                )
            }

            list.add(
                SettingOptionModel(
                    3,
                    context.getString(R.string.training_videos),
                    BLANK_STRING,
                    SettingTagEnum.TRAINING_VIDEOS.name
                )
            )
        }
        list.add(
            SettingOptionModel(
                4,
                context.getString(R.string.language_text),
                BLANK_STRING,
                SettingTagEnum.LANGUAGE.name
            )
        )
        list.add(
            SettingOptionModel(
                5,
                context.getString(R.string.export_backup_file),
                BLANK_STRING,
                SettingTagEnum.EXPORT_BACKUP_FILE.name
            )
        )
        list.add(
            SettingOptionModel(
                6,
                context.getString(R.string.backup_recovery),
                BLANK_STRING,
                SettingTagEnum.BACKUP_RECOVERY.name
            )
        )


        _optionList.value=list
        if(userType != UPCM_USER) {
            checkFormsAvailabilityForVillage(context, villageId)
        }
    }

    fun performLogout(context: Context, onLogout: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val settingUseCaseResponse = settingBSUserCase.logoutUseCase.invoke()
            if (userType != UPCM_USER) {
                exportLocalData(context)
            }
            delay(2000)
            withContext(Dispatchers.Main) {
               showLoader.value=false
                onLogout(settingUseCaseResponse)
            }
        }
    }

    suspend fun exportLocalData(context: Context) {
        exportHelper.exportAllData(context)
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

                // Add Summary File
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

    override fun <T> onEvent(event: T) {
        when (event) {
            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }
    }

    fun showLoaderForTime(time: Long) {
        onEvent(LoaderEvent.UpdateLoaderState(true))
        Timer().schedule(object : TimerTask() {
            override fun run() {
                job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                    withContext(Dispatchers.Main) {
                        onEvent(LoaderEvent.UpdateLoaderState(false))
                    }
                }
            }
        }, time)
    }

    fun checkFormsAvailabilityForVillage(context: Context, villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val formAFilePath =
                File("${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath}/${FORM_A_PDF_NAME}_${villageId}.pdf")
            val formBFilePath =
                File("${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath}/${FORM_B_PDF_NAME}_${villageId}.pdf")
            val formCFilePath =
                File("${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath}/${FORM_C_PDF_NAME}_${villageId}.pdf")

            var didiList=settingBSUserCase.getAllPoorDidiForVillageUseCase.getAllDidiForVillage(villageId)
            if(userType == BPC_USER_TYPE){
                 didiList=settingBSUserCase.getAllPoorDidiForVillageUseCase.getAllPoorDidiForVillage(villageId)
            }

            // Form A availability check
            if (formAFilePath.isFile && formAFilePath.exists()) {
                withContext(Dispatchers.Main) {
                    formAAvailable.value = true
                }
            } else {
                    if (didiList.any { it.wealth_ranking == WealthRank.POOR.rank && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal && !it.rankingEdit }
                    ) {
                        withContext(Dispatchers.Main) {
                            formAAvailable.value = true
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            formAAvailable.value = false
                        }
                    }
            }

            // Form B availability check
            if (formBFilePath.isFile && formBFilePath.exists()) {
                withContext(Dispatchers.Main) {
                    formBAvailable.value = true
                }
            } else {
                if (didiList.any { it.forVoEndorsement == 1 && !it.patEdit }
                ) {
                    withContext(Dispatchers.Main) {
                        formBAvailable.value = true
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        formBAvailable.value = false
                    }
                }
            }

            // Form C availability check
            if (formCFilePath.isFile && formCFilePath.exists()) {
                withContext(Dispatchers.Main) {
                    formCAvailable.value = true
                }
            } else {
                val stepList = settingBSUserCase.getAllPoorDidiForVillageUseCase.getAllStepsForVillage(villageId)
                val filteredStepList = stepList.filter { it.name.equals(VO_ENDORSEMENT_CONSTANT, true) }
                if (filteredStepList[0] != null) {
                    formCAvailable.value =
                        filteredStepList[0].isComplete == StepStatus.COMPLETED.ordinal
                } else {
                    formCAvailable.value = false
                }
            }



        }

    }
    private suspend fun getSummaryFile(): Pair<String, Uri?>? {
        val summaryFileNameWithoutExtension = "Sarathi_${
            getFirstName(settingBSUserCase.getUserDetailsUseCase.getUserName())
        }_${prefBSRepo.getUniqueUserIdentifier()}_summary_file"

        val summaryFileNameWithExtension = summaryFileNameWithoutExtension + LOCAL_BACKUP_EXTENSION

        return settingBSUserCase.getSummaryFileUseCase.invoke(
            userId = prefBSRepo.getUniqueUserIdentifier(),
            mobileNo = getUserMobileNumber(),
            fileNameWithoutExtension = summaryFileNameWithoutExtension,
            fileNameWithExtension = summaryFileNameWithExtension
        )
    }

    fun buildAndShareLogsForSelection() {
        NudgeLogger.d("SettingBSViewModel", "buildAndShareLogs SELECTION---------------")
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val context = MyApplication.applicationContext()
            settingBSUserCase.exportHandlerSettingUseCase.exportAllData(context)
            com.patsurvey.nudge.utils.LogWriter.buildSupportLogAndShare()
        }
    }



    fun getUserMobileNumber():String{
        return settingBSUserCase.getUserDetailsUseCase.getUserMobileNumber()
    }
}
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
import com.patsurvey.nudge.activities.settings.domain.use_case.SettingBSUserCase
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nudge.core.LOCAL_BACKUP_EXTENSION
import com.nudge.core.NUDGE_DATABASE
import com.nudge.core.SARATHI_DIRECTORY_NAME
import com.nudge.core.SUFFIX_EVENT_ZIP_FILE
import com.nudge.core.SUFFIX_IMAGE_ZIP_FILE
import com.nudge.core.ZIP_MIME_TYPE
import com.nudge.core.compression.ZipFileCompression
import com.nudge.core.exportAllOldImages
import com.nudge.core.exportDbFile
import com.nudge.core.exportLogFile
import com.nudge.core.getFirstName
import com.nudge.core.json
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.model.SettingOptionModel
import com.nudge.core.moduleNameAccToLoggedInUser
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.ui.events.ToastMessageEvent
import com.nudge.core.uriFromFile
import com.nudge.core.utils.LogWriter
import com.patsurvey.nudge.MyApplication
import com.patsurvey.nudge.activities.settings.domain.SettingTagEnum
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.CasteEntity
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.service.csv.ExportHelper
import com.patsurvey.nudge.utils.BPC_USER_TYPE
import com.patsurvey.nudge.utils.CRP_USER_TYPE
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.FORM_A_PDF_NAME
import com.patsurvey.nudge.utils.FORM_B_PDF_NAME
import com.patsurvey.nudge.utils.FORM_C_PDF_NAME
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_PAT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_VO_ENDORSEMENT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_WEALTH_RANKING_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PageFrom
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.PdfUtils
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.UPCM_USER
import com.patsurvey.nudge.utils.VO_ENDORSEMENT_CONSTANT
import com.patsurvey.nudge.utils.WealthRank
import com.patsurvey.nudge.utils.changeMilliDateToDate
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
    val exportHelper: ExportHelper,
    val prefBSRepo: PrefBSRepo,
    val prefRepo: PrefRepo
):BaseViewModel() {
    val _optionList = mutableStateOf<List<SettingOptionModel>>(emptyList())
    var showLogoutDialog = mutableStateOf(false)
    var showLoader = mutableStateOf(false)
    var applicationId= mutableStateOf(BLANK_STRING)
    lateinit var mAppContext:Context
    val optionList: State<List<SettingOptionModel>> get() = _optionList
    var userType:String= BLANK_STRING
    private val _loaderState = mutableStateOf<LoaderState>(LoaderState(isLoaderVisible = false))

    val formAAvailable = mutableStateOf(false)
    val formBAvailable = mutableStateOf(false)
    val formCAvailable = mutableStateOf(false)
    val loaderState: State<LoaderState> get() = _loaderState
    fun initOptions(context:Context) {
        applicationId.value= CoreAppDetails.getApplicationDetails()?.applicationID ?: BuildConfig.APPLICATION_ID
        userType = settingBSUserCase.getSettingOptionListUseCase.getUserType().toString()
        mAppContext = if(userType!= UPCM_USER) NudgeCore.getAppContext() else BaselineCore.getAppContext()

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
        if (userType != UPCM_USER) {
            if (settingOpenFrom != PageFrom.VILLAGE_PAGE.ordinal) {
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
        if(userType != UPCM_USER && settingOpenFrom != PageFrom.VILLAGE_PAGE.ordinal) {
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
        NudgeLogger.d("SettingBSViewModel", "compressEventData---------------")
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                onEvent(LoaderEvent.UpdateLoaderState(true))
                val fileUriList: ArrayList<Uri> = arrayListOf()
                val fileAndDbZipList = ArrayList<Pair<String, Uri?>>()
                val compression = ZipFileCompression()

                //Delete Old Image zips.
                compression.deleteOldFiles(
                    context = mAppContext,
                    fileNameReference = "${getFirstName(settingBSUserCase.getUserDetailsUseCase.getUserName())}_${getUserMobileNumber()}_Sarathi_Image",
                    folderName = getUserMobileNumber(),
                    fileType = SUFFIX_IMAGE_ZIP_FILE,
                    applicationId = applicationId.value,
                    checkInAppDirectory = true
                )

                //Delete old event zips.
                compression.deleteOldFiles(
                    context = mAppContext,
                    fileNameReference = "${getFirstName(settingBSUserCase.getUserDetailsUseCase.getUserName())}_${getUserMobileNumber()}_sarathi_",
                    folderName = getUserMobileNumber(),
                    fileType = SUFFIX_EVENT_ZIP_FILE
                )

                // Image Files and Zip
                val imageUri = exportAllOldImages(
                    appContext = mAppContext,
                    applicationID = applicationId.value,
                    mobileNo = settingBSUserCase.getUserDetailsUseCase.getUserMobileNumber(),
                    moduleName = moduleNameAccToLoggedInUser(loggedInUser = userType),
                    userName = getFirstName(settingBSUserCase.getUserDetailsUseCase.getUserName())
                )

                if(imageUri!=Uri.EMPTY) {
                    imageUri?.let {
                        fileUriList.add(it)
                        NudgeLogger.d("SettingBSViewModel_URI", "Image File Uri: ${it.path}---------------")
                    }
                }

                // Database File and URI
                val dbUri = exportDbFile(
                    appContext = mAppContext,
                    applicationID = applicationId.value,
                    databaseName = if(userType != UPCM_USER) NUDGE_DATABASE else NUDGE_BASELINE_DATABASE
                )


                if(dbUri!= Uri.EMPTY){
                    dbUri?.let {
                        NudgeLogger.d("SettingBSViewModel", "Database File Uri: ${it.path}---------------")
                        fileAndDbZipList.add(Pair(if(userType != UPCM_USER) NUDGE_DATABASE else NUDGE_BASELINE_DATABASE,it))
                    }
                }

                val eventFilePath =
                    File(Environment.DIRECTORY_DOCUMENTS + SARATHI_DIRECTORY_NAME + "/" + getUserMobileNumber() + "/" + moduleNameAccToLoggedInUser(userType))

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
                        mAppContext,
                        logFile,
                        applicationId.value
                    )
                    if (logFileUri != Uri.EMPTY) {
                        logFileUri.let {
                            fileAndDbZipList.add(Pair(logFile.name, it))
                            NudgeLogger.d(
                                "SettingBSViewModel",
                                "Log File Uri: ${it.path}---------------"
                            )

                        }
                    }
                }

                    if (userType == CRP_USER_TYPE) {
                        val selectedVillageId = prefRepo.getSelectedVillage().id
                        val casteList = settingBSUserCase.getCasteUseCase.getAllCasteForLanguage(
                            prefRepo.getAppLanguageId() ?: 2
                        )
                        var didiList: List<DidiEntity> =
                            settingBSUserCase.getAllPoorDidiForVillageUseCase.getAllDidiForVillage(
                                selectedVillageId
                            )

                        val isFormAGenerated = generateFormA(casteList, selectedVillageId, didiList)
                        addFormToUriList(
                            isFormAGenerated,
                            selectedVillageId,
                            FORM_A_PDF_NAME,
                            fileAndDbZipList
                        )

                        val isFormBGenerated = generateFormB(casteList, selectedVillageId, didiList)
                        addFormToUriList(
                            isFormBGenerated,
                            selectedVillageId,
                            FORM_B_PDF_NAME,
                            fileAndDbZipList
                        )

                        val isFormCGenerated = generateFormc(casteList, selectedVillageId, didiList)
                        addFormToUriList(
                            isFormCGenerated,
                            selectedVillageId,
                            FORM_C_PDF_NAME,
                            fileAndDbZipList
                        )
                    }

                // Add Summary File
                if(userType == UPCM_USER) {
                    getSummaryFile()?.let {
                        fileAndDbZipList.add(it)
                    }
                }

                val zipFileName =
                    "${getFirstName(settingBSUserCase.getUserDetailsUseCase.getUserName())}_${getUserMobileNumber()}_Sarathi_${System.currentTimeMillis()}"

                if (fileUriList.isNotEmpty()) {
                    val zipLogDbFileUri = compression.compressData(
                        mAppContext,
                        zipFileName,
                        Environment.DIRECTORY_DOCUMENTS + SARATHI_DIRECTORY_NAME + "/" + getUserMobileNumber() + "/${moduleNameAccToLoggedInUser(userType)}",
                        fileAndDbZipList,
                        getUserMobileNumber()
                    )
                    zipLogDbFileUri?.let {
                        if (it != Uri.EMPTY) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                fileUriList.add(it)
                            else fileUriList.add(uriFromFile(context = mAppContext,
                                applicationID = applicationId.value,
                                file = it.toFile()))
                        }
                    }
                }

                NudgeLogger.d("SettingBSViewModel", " Share Dialog Open ${fileUriList.json()}" )
                openShareSheet(fileUriList, title, ZIP_MIME_TYPE)
                CoreSharedPrefs.getInstance(mAppContext).setFileExported(true)
                onEvent(LoaderEvent.UpdateLoaderState(false))
            } catch (exception: Exception) {
                NudgeLogger.e("Compression Exception", exception.message ?: "")
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

    //TODO: Needs to update code for Common Export and Export Event File functionality for the Selection and Baseline
    fun buildAndShareLogsForSelection() {
        NudgeLogger.d("SettingBSViewModel", "buildAndShareLogs SELECTION---------------")
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val context = MyApplication.applicationContext()
            settingBSUserCase.exportHandlerSettingUseCase.exportAllData(context)
            com.patsurvey.nudge.utils.LogWriter.buildSupportLogAndShare()
        }
    }

    fun exportOnlyLogFile(context: Context){
        NudgeLogger.d("ExportImportViewModel","exportOnlyLogFile: ----")
        try {
            CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
                onEvent(LoaderEvent.UpdateLoaderState(true))
                val logFile= LogWriter.buildLogFile(appContext = mAppContext){
                    onEvent(LoaderEvent.UpdateLoaderState(false))
                    onEvent(ToastMessageEvent.ShowToastMessage(context.getString(R.string.no_logs_available)))
                }
                if (logFile != null) {
                    exportLogFile(
                        logFile,
                        appContext = mAppContext,
                        applicationID = applicationId.value,
                        userName = getFirstName(settingBSUserCase.getUserDetailsUseCase.getUserName()),
                        mobileNo = settingBSUserCase.getUserDetailsUseCase.getUserMobileNumber(),
                        moduleName = moduleNameAccToLoggedInUser(userType)
                    ) {
                        onEvent(LoaderEvent.UpdateLoaderState(false))
                        openShareSheet(convertURIAccToOS(it) ,"", type = ZIP_MIME_TYPE)
                    }


                }
            }
        }catch (e:Exception){
            onEvent(LoaderEvent.UpdateLoaderState(false))
            NudgeLogger.e("ExportImportViewModel","exportOnlyLogFile :${e.message}",e)
        }
    }

    private suspend fun generateFormA(
        casteList: List<CasteEntity>,
        selectedVillageId: Int,
        didiList: List<DidiEntity>
    ) = PdfUtils.getFormAPdf(
        mAppContext,
        villageEntity = prefRepo.getSelectedVillage(),
        casteList = casteList,
        didiDetailList = didiList,
        completionDate = changeMilliDateToDate(
            prefRepo.getPref(
                PREF_WEALTH_RANKING_COMPLETION_DATE_ + selectedVillageId, 0L
            )
        ) ?: BLANK_STRING
    )

    private suspend fun generateFormB(
        casteList: List<CasteEntity>,
        selectedVillageId: Int,
        didiList: List<DidiEntity>
    ) =
        PdfUtils.getFormBPdf(
            mAppContext, villageEntity = prefRepo.getSelectedVillage(),
            didiDetailList = didiList.filter { it.forVoEndorsement == 1 && it.section2Status == PatSurveyStatus.COMPLETED.ordinal && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal && !it.patEdit },
            casteList = casteList,
            completionDate = changeMilliDateToDate(
                prefRepo.getPref(
                    PREF_PAT_COMPLETION_DATE_ + selectedVillageId,
                    0L
                )
            ) ?: BLANK_STRING
        )

    private suspend fun generateFormc(
        casteList: List<CasteEntity>,
        selectedVillageId: Int,
        didiList: List<DidiEntity>
    ) =
        PdfUtils.getFormCPdf(
            mAppContext, villageEntity = prefRepo.getSelectedVillage(),
            didiDetailList = didiList.filter { it.forVoEndorsement == 1 && it.section2Status == PatSurveyStatus.COMPLETED.ordinal && it.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal },
            casteList = casteList,
            completionDate = changeMilliDateToDate(
                prefRepo.getPref(
                    PREF_VO_ENDORSEMENT_COMPLETION_DATE_ + selectedVillageId,
                    0L
                )
            ) ?: BLANK_STRING
        )

    private fun addFormToUriList(
        isFormGenerated: Boolean,
        selectedVillageId: Int,
        formName: String,
        uriList: ArrayList<Pair<String, Uri?>>
    ) {
        if (isFormGenerated) {
            val formFile = PdfUtils.getPdfPath(
                context = mAppContext,
                formName = formName,
                selectedVillageId
            )
            val formUri=uriFromFile(context = mAppContext, file = formFile, applicationID = applicationId.value)
           formUri?.let {
               if(it != Uri.EMPTY){
                   uriList.add(Pair(formFile.name,it))
               }
           }
        }
    }


    fun getUserMobileNumber():String{
        return settingBSUserCase.getUserDetailsUseCase.getUserMobileNumber()
    }

    private fun convertURIAccToOS(uri: Uri): ArrayList<Uri> {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return arrayListOf(uri)
        return arrayListOf(uriFromFile(mAppContext,uri.toFile(),applicationId.value))
    }
}
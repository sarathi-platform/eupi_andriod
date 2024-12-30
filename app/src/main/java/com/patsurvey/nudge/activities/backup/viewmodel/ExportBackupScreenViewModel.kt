package com.patsurvey.nudge.activities.backup.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.TextUtils
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
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.openShareSheet
import com.nrlm.baselinesurvey.utils.showCustomToast
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nudge.core.CoreDispatchers
import com.nudge.core.IMAGE
import com.nudge.core.LOCAL_BACKUP_EXTENSION
import com.nudge.core.NUDGE_DATABASE
import com.nudge.core.SARATHI
import com.nudge.core.SARATHI_DIRECTORY_NAME
import com.nudge.core.SUFFIX_EVENT_ZIP_FILE
import com.nudge.core.SUFFIX_IMAGE_ZIP_FILE
import com.nudge.core.ZIP_MIME_TYPE
import com.nudge.core.compression.ZipFileCompression
import com.nudge.core.database.entities.CasteEntity
import com.nudge.core.exportAllOldImages
import com.nudge.core.exportDatabase
import com.nudge.core.exportDbFile
import com.nudge.core.exportDbFiles
import com.nudge.core.exportLogFile
import com.nudge.core.exportOldData
import com.nudge.core.findImagesExistInPictureFolder
import com.nudge.core.getFirstName
import com.nudge.core.importDbFile
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.model.SettingOptionModel
import com.nudge.core.moduleNameAccToLoggedInUser
import com.nudge.core.openShareSheet
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.ui.events.ToastMessageEvent
import com.nudge.core.uriFromFile
import com.nudge.core.utils.LogWriter
import com.patsurvey.nudge.BuildConfig
import com.patsurvey.nudge.activities.backup.domain.use_case.ExportImportUseCase
import com.patsurvey.nudge.activities.settings.domain.use_case.SettingBSUserCase
import com.patsurvey.nudge.activities.ui.progress.domain.useCase.SelectionVillageUseCase
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.utils.CRP_USER_TYPE
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_PAT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_VO_ENDORSEMENT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_WEALTH_RANKING_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.PdfUtils
import com.patsurvey.nudge.utils.UPCM_USER
import com.patsurvey.nudge.utils.changeMilliDateToDate
import com.sarathi.dataloadingmangement.NUDGE_GRANT_DATABASE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.system.exitProcess

@HiltViewModel
class ExportBackupScreenViewModel @Inject constructor(
    private val exportImportUseCase: ExportImportUseCase,
    private val settingBSUserCase: SettingBSUserCase,
    val prefBSRepo: PrefBSRepo,
    val prefRepo: PrefRepo,
    val selectionVillageUseCase: SelectionVillageUseCase,
    ) : BaseViewModel() {
    var mAppContext: Context
    val showRestartAppDialog = mutableStateOf(false)
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
    fun getUserMobileNumber(): String {
        return settingBSUserCase.getUserDetailsUseCase.getUserMobileNumber()
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
    private suspend fun getSummaryFile(): Pair<String, Uri?>? {
        val summaryFileNameWithoutExtension = "${SARATHI}_${
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

    fun compressExportData(title: String) {
        NudgeLogger.d("SettingBSViewModel", "compressEventData---------------")
        CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {
            try {
                onEvent(LoaderEvent.UpdateLoaderState(true))
                val fileUriList = arrayListOf<Uri>()
                val fileAndDbZipList = ArrayList<Pair<String, Uri?>>()
                val compression = ZipFileCompression()

                deleteOldZips(compression)

                val isImageExistInFolder = findImagesExistInPictureFolder(
                    appContext = mAppContext,
                    applicationID = applicationId.value,
                    mobileNo = settingBSUserCase.getUserDetailsUseCase.getUserMobileNumber()
                )
                processImages(isImageExistInFolder, fileUriList)
                processDatabaseFiles(fileAndDbZipList)
                processLogFile(fileAndDbZipList)

                if (userType == CRP_USER_TYPE) {
                    processCRPForms(fileAndDbZipList)
                }

                if (userType == UPCM_USER) {
                    getSummaryFile()?.let { fileAndDbZipList.add(it) }
                }

                val zipFileName = generateZipFileName()
                val zipLogDbFileUri = compression.compressData(
                    context = mAppContext,
                    zipFileName = zipFileName,
                    filePathToZipped = Environment.DIRECTORY_DOCUMENTS + SARATHI_DIRECTORY_NAME + "/" + getUserMobileNumber(),
                    extraUris = fileAndDbZipList,
                    folderName = getUserMobileNumber()
                )
                zipLogDbFileUri?.let {
                    if (it != Uri.EMPTY) {
                        fileUriList.add(it)
                    }
                }

                fileUriList.forEach {
                    NudgeLogger.d("SettingBSViewModel", "Share Dialog Open Zip Files: ${it.path}")
                }
              openShareSheet(fileUriList, title, ZIP_MIME_TYPE, mAppContext)
                CoreSharedPrefs.getInstance(mAppContext).setFileExported(true)
                onEvent(LoaderEvent.UpdateLoaderState(false))
            } catch (exception: Exception) {
                NudgeLogger.e("Compression Exception", exception.message ?: "")
                exception.printStackTrace()
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }


    private suspend fun generateFormA(
        stateId: Int,
        casteList: List<CasteEntity>,
        selectedVillageId: Int,
        didiList: List<DidiEntity>,
        villageEntity: VillageEntity
    ) = PdfUtils.getFormAPdf(
        mAppContext,
        stateId,
        villageEntity = villageEntity,
        casteList = casteList,
        didiDetailList = didiList,
        completionDate = changeMilliDateToDate(
            prefRepo.getPref(
                PREF_WEALTH_RANKING_COMPLETION_DATE_ + selectedVillageId, 0L
            )
        ) ?: BLANK_STRING
    )

    private suspend fun generateFormB(
        stateId: Int,
        casteList: List<CasteEntity>,
        selectedVillageId: Int,
        didiList: List<DidiEntity>,
        villageEntity: VillageEntity,

        ) =
        PdfUtils.getFormBPdf(
            mAppContext,
            stateId,
            villageEntity = villageEntity,
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
        stateId: Int,
        casteList: List<CasteEntity>,
        selectedVillageId: Int,
        didiList: List<DidiEntity>,
        villageEntity: VillageEntity
    ) =
        PdfUtils.getFormCPdf(
            mAppContext,
            stateId,
            villageEntity = villageEntity,
            didiDetailList = didiList.filter { it.forVoEndorsement == 1 && it.section2Status == PatSurveyStatus.COMPLETED.ordinal && it.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal },
            casteList = casteList,
            completionDate = changeMilliDateToDate(
                prefRepo.getPref(
                    PREF_VO_ENDORSEMENT_COMPLETION_DATE_ + selectedVillageId,
                    0L
                )
            ) ?: BLANK_STRING
        )

    private suspend fun processDatabaseFiles(fileAndDbZipList: ArrayList<Pair<String, Uri?>>) {
        if (userType != UPCM_USER) {
            val dbUri = exportDbFile(
                appContext = mAppContext,
                applicationID = applicationId.value,
                databaseName = NUDGE_DATABASE
            )
            if (dbUri != Uri.EMPTY) {
                dbUri?.let {
                    NudgeLogger.d(
                        "SettingBSViewModel",
                        "Database File Uri: ${it.path}---------------"
                    )
                    fileAndDbZipList.add(Pair(NUDGE_DATABASE, it))
                }
            }
        } else {
            val dbUrisList = exportDbFiles(
                mAppContext,
                applicationId.value,
                listOf(NUDGE_BASELINE_DATABASE, com.nudge.core.NUDGE_GRANT_DATABASE)
            )
            if (dbUrisList.isNotEmpty()) {
                dbUrisList.forEach { dbUri ->
                    dbUri.second?.let {
                        NudgeLogger.d(
                            "SettingBSViewModel",
                            "Database File Uri: ${it.path}---------------"
                        )
                        fileAndDbZipList.add(Pair(dbUri.first, it))
                    }
                }
            }
        }
    }

    private suspend fun processLogFile(fileAndDbZipList: ArrayList<Pair<String, Uri?>>) {
        val logFile = LogWriter.buildLogFile(appContext = BaselineCore.getAppContext()) {}
        if (logFile != null) {
            val logFileUri = uriFromFile(mAppContext, logFile, applicationId.value)
            if (logFileUri != Uri.EMPTY) {
                logFileUri.let {
                    fileAndDbZipList.add(Pair(logFile.name, it))
                    NudgeLogger.d("SettingBSViewModel", "Log File Uri: ${it.path}---------------")
                }
            }
        }
    }

    private suspend fun processCRPForms(fileAndDbZipList: ArrayList<Pair<String, Uri?>>) {
        selectionVillageUseCase.getVillageListFromDb().distinctBy { it.id }
            .forEach { villageEntity ->
                val selectedVillageId = villageEntity.id
                val casteList = settingBSUserCase.getCasteUseCase.getAllCasteForLanguage(
            prefRepo.getAppLanguageId() ?: 2
        )
        val didiList =
            settingBSUserCase.getAllPoorDidiForVillageUseCase.getAllDidiForVillage(selectedVillageId)
        val formAFilePath =
            generateFormA(
                prefRepo.getStateId(),
                casteList,
                selectedVillageId,
                didiList,
                villageEntity
            )
        addFormToUriList(formAFilePath, fileAndDbZipList)

        val formBFilePath =
            generateFormB(
                prefRepo.getStateId(),
                casteList,
                selectedVillageId,
                didiList,
                villageEntity
            )
        addFormToUriList(formBFilePath, fileAndDbZipList)

        val formCFilePath =
            generateFormc(
                prefRepo.getStateId(),
                casteList,
                selectedVillageId,
                didiList,
                villageEntity
            )
        addFormToUriList(formCFilePath, fileAndDbZipList)
            }
    }

    private fun addFormToUriList(
        filePath: String,
        uris: ArrayList<Pair<String, Uri?>>
    ) {
        if (!TextUtils.isEmpty(filePath)) {

            val formFile = File(filePath)
            uris.add(
                Pair(
                    formFile.name, com.patsurvey.nudge.utils.uriFromFile(
                        NudgeCore.getAppContext(),
                        formFile
                    )
                )
            )
        }
    }

    private fun generateZipFileName(): String {
        return "${getFirstName(settingBSUserCase.getUserDetailsUseCase.getUserName())}_${getUserMobileNumber()}_${SARATHI}_${System.currentTimeMillis()}"
    }

    private suspend fun deleteOldZips(compression: ZipFileCompression) {
        compression.deleteOldFiles(
            context = mAppContext,
            fileNameReference = "${getFirstName(settingBSUserCase.getUserDetailsUseCase.getUserName())}_${getUserMobileNumber()}_${SARATHI}_$IMAGE",
            folderName = getUserMobileNumber(),
            fileType = SUFFIX_IMAGE_ZIP_FILE,
            applicationId = applicationId.value,
            checkInAppDirectory = true
        )

        compression.deleteOldFiles(
            context = mAppContext,
            fileNameReference = "${getFirstName(settingBSUserCase.getUserDetailsUseCase.getUserName())}_${getUserMobileNumber()}_${SARATHI}_",
            folderName = getUserMobileNumber(),
            fileType = SUFFIX_EVENT_ZIP_FILE
        )
    }

    private suspend fun processImages(isImageExistInFolder: Boolean, fileUriList: ArrayList<Uri>) {
        if (isImageExistInFolder) {
            val imageUri = exportAllOldImages(
                appContext = mAppContext,
                applicationID = applicationId.value,
                mobileNo = settingBSUserCase.getUserDetailsUseCase.getUserMobileNumber(),
                moduleName = moduleNameAccToLoggedInUser(loggedInUser = userType),
                userName = getFirstName(settingBSUserCase.getUserDetailsUseCase.getUserName())
            )

            if (imageUri != Uri.EMPTY) {
                imageUri?.let {
                    fileUriList.add(it)
                    NudgeLogger.d(
                        "SettingBSViewModel_URI",
                        "Image File Uri: ${it.path}---------------"
                    )
                }
            }
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

    fun restartApp(context: Context, cls: Class<*>) {
        BaselineLogger.d("ExportBackupViewModel", "Restarting Application")
        context.startActivity(
            Intent(mAppContext, cls)
        )
        exitProcess(0)
    }

    fun importSelectedDB(uri: Uri, onImportSuccess: () -> Unit) {
        BaselineLogger.d("ExportBackupViewModel", "importSelectedDB ----")
        try {

            importDbFile(
                appContext = mAppContext,
                importedDbUri = uri,
                deleteDBName = if (loggedInUserType.value == UPCM_USER) NUDGE_BASELINE_DATABASE else NUDGE_DATABASE,
                applicationID = applicationId.value
            ) {
                BaselineLogger.d("ExportBackupViewModel", "importSelectedDB Success ----")
                onImportSuccess()
            }
        } catch (exception: Exception) {
            BaselineLogger.e(
                "ExportBackupViewModel",
                "importSelectedDB : ${exception.message}",
                exception
            )
            onEvent(LoaderEvent.UpdateLoaderState(false))
        }
    }

}
package com.patsurvey.nudge.activities.settings.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toFile
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.NUDGE_BASELINE_DATABASE
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nudge.core.CoreDispatchers
import com.nudge.core.IMAGE
import com.nudge.core.LOCAL_BACKUP_EXTENSION
import com.nudge.core.NUDGE_DATABASE
import com.nudge.core.NUDGE_GRANT_DATABASE
import com.nudge.core.SARATHI
import com.nudge.core.SARATHI_DIRECTORY_NAME
import com.nudge.core.SUFFIX_EVENT_ZIP_FILE
import com.nudge.core.SUFFIX_IMAGE_ZIP_FILE
import com.nudge.core.SYNC_MANAGER_DATABASE
import com.nudge.core.ZIP_MIME_TYPE
import com.nudge.core.compression.ZipFileCompression
import com.nudge.core.database.entities.CasteEntity
import com.nudge.core.exportAllOldImages
import com.nudge.core.exportDbFiles
import com.nudge.core.exportLogFile
import com.nudge.core.findImagesExistInPictureFolder
import com.nudge.core.getFirstName
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.model.SettingOptionModel
import com.nudge.core.moduleNameAccToLoggedInUser
import com.nudge.core.openShareSheet
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.ui.events.ToastMessageEvent
import com.nudge.core.uriFromFile
import com.nudge.core.utils.CoreLogger
import com.nudge.core.utils.LogWriter
import com.nudge.syncmanager.utils.SYNC_WORKER_TAG
import com.patsurvey.nudge.BuildConfig
import com.patsurvey.nudge.MyApplication
import com.patsurvey.nudge.activities.settings.domain.SettingTagEnum
import com.patsurvey.nudge.activities.settings.domain.use_case.SettingBSUserCase
import com.patsurvey.nudge.activities.ui.progress.domain.useCase.SelectionVillageUseCase
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.service.csv.ExportHelper
import com.patsurvey.nudge.utils.CRP_USER_TYPE
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_PAT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_VO_ENDORSEMENT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_WEALTH_RANKING_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PageFrom
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.PdfUtils
import com.patsurvey.nudge.utils.UPCM_USER
import com.patsurvey.nudge.utils.changeMilliDateToDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject


@HiltViewModel
class SettingBSViewModel @Inject constructor(
    private val settingBSUserCase: SettingBSUserCase,
    val exportHelper: ExportHelper,
    val prefBSRepo: PrefBSRepo,
    val prefRepo: PrefRepo,
    val selectionVillageUseCase: SelectionVillageUseCase,
) : BaseViewModel() {
    val _optionList = mutableStateOf<List<SettingOptionModel>>(emptyList())
    val syncEventCount = mutableStateOf(0)
    var showLogoutDialog = mutableStateOf(false)
    var isSyncEnable = mutableStateOf(false)
    var showLoader = mutableStateOf(false)
    var applicationId = mutableStateOf(BLANK_STRING)
    var lastSyncTime = mutableStateOf(0L)
    lateinit var mAppContext: Context
    val optionList: State<List<SettingOptionModel>> get() = _optionList
    var userType: String = BLANK_STRING
    private val _loaderState = mutableStateOf<LoaderState>(LoaderState(isLoaderVisible = false))
    val workManager = WorkManager.getInstance(MyApplication.applicationContext())


    val loaderState: State<LoaderState> get() = _loaderState
    fun initOptions(context: Context) {
        applicationId.value =
            CoreAppDetails.getApplicationDetails()?.applicationID ?: BuildConfig.APPLICATION_ID
        userType = settingBSUserCase.getSettingOptionListUseCase.getUserType().toString()
        lastSyncTime.value = settingBSUserCase.getUserDetailsUseCase.getLastSyncTime()
        mAppContext =
            if (userType != UPCM_USER) NudgeCore.getAppContext() else BaselineCore.getAppContext()

        val settingOpenFrom = settingBSUserCase.getSettingOptionListUseCase.settingOpenFrom()
        val list = ArrayList<SettingOptionModel>()

        list.add(
            SettingOptionModel(
                1,
                context.getString(R.string.language_text),
                BLANK_STRING,
                SettingTagEnum.LANGUAGE.name,
                leadingIcon = R.drawable.ic_language
            )
        )
        if (userType != UPCM_USER) {
            if (settingOpenFrom != PageFrom.VILLAGE_PAGE.ordinal) {
                list.add(
                    SettingOptionModel(
                        2,
                        context.getString(R.string.forms),
                        BLANK_STRING,
                        SettingTagEnum.FORMS.name,
                        leadingIcon = R.drawable.ic_forms
                    )
                )
            }

            list.add(
                SettingOptionModel(
                    3,
                    context.getString(R.string.training_videos),
                    BLANK_STRING,
                    SettingTagEnum.TRAINING_VIDEOS.name,
                    leadingIcon = R.drawable.ic_bottom_task_icon
                )
            )
        } else {
            list.add(
                SettingOptionModel(
                    2,
                    context.getString(R.string.forms),
                    BLANK_STRING,
                    SettingTagEnum.FORMS.name,
                    leadingIcon = R.drawable.ic_forms
                )
            )
        }
        list.add(
            SettingOptionModel(
                5,
                context.getString(R.string.export_backup_file),
                BLANK_STRING,
                SettingTagEnum.EXPORT_BACKUP_FILE.name,
                leadingIcon = R.drawable.ic_backup_file,
                trailingIcon = R.drawable.ic_share_icon
            )
        )
        list.add(
            SettingOptionModel(
                6,
                context.getString(R.string.export_data),
                BLANK_STRING,
                SettingTagEnum.EXPORT_DATA_BACKUP_FILE.name,
                leadingIcon = R.drawable.ic_share_data
            )
        )
        list.add(
            SettingOptionModel(
                7,
                context.getString(R.string.backup_recovery),
                BLANK_STRING,
                SettingTagEnum.BACKUP_RECOVERY.name,
                leadingIcon = R.drawable.ic_backup_recovery
            )
        )
        list.add(
            SettingOptionModel(
                8,
                context.getString(R.string.profile),
                BLANK_STRING,
                SettingTagEnum.PROFILE.name,
                leadingIcon = R.drawable.ic_profile
            )
        )

        isSyncEnable.value = settingBSUserCase.getUserDetailsUseCase.isSyncEnable()
        _optionList.value=list
        fetchEventCount()
    }

    fun fetchEventCount() {
        CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {
            syncEventCount.value = settingBSUserCase.getSyncEventsUseCase.getTotalEventCount()
        }
    }

    fun performLogout(context: Context, onLogout: (Boolean) -> Unit) {
        CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {
            val settingUseCaseResponse = settingBSUserCase.logoutUseCase.invoke()
            delay(2000)
            cancelSyncUploadWorker()
            withContext(CoreDispatchers.mainDispatcher) {
               showLoader.value=false
                onLogout(settingUseCaseResponse)
            }
        }
    }

    fun saveLanguagePageFrom() {
        settingBSUserCase.saveLanguageScreenOpenFromUseCase.invoke()
    }


    fun compressEventData(title: String) {
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
                    getSummaryFile()?.let {
                        if (it.second != Uri.EMPTY) {
                            fileAndDbZipList.add(it)
                        }
                    }
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

    private suspend fun deleteOldZips(compression: ZipFileCompression) {
        compression.deleteOldFiles(
            context = mAppContext,
            fileNameReference = "${getFirstName(settingBSUserCase.getUserDetailsUseCase.getUserName())}_${getUserMobileNumber()}_${SARATHI}_${IMAGE}",
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

    private suspend fun processDatabaseFiles(fileAndDbZipList: ArrayList<Pair<String, Uri?>>) {
        val dbUrisList = exportDbFiles(
            mAppContext,
            applicationId.value,
            if (userType == UPCM_USER) listOf(
                NUDGE_BASELINE_DATABASE,
                NUDGE_GRANT_DATABASE,
                SYNC_MANAGER_DATABASE
            )
            else listOf(NUDGE_DATABASE, SYNC_MANAGER_DATABASE)
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
            settingBSUserCase.getAllPoorDidiForVillageUseCase.getAllDidiForVillage(villageEntity.id)
        val formAFilePath =
            generateFormA(
                prefRepo.getStateId(),
                casteList,
                selectedVillageId,
                didiList,
                villageEntity = villageEntity
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

    private fun generateZipFileName(): String {
        return "${getFirstName(settingBSUserCase.getUserDetailsUseCase.getUserName())}_${getUserMobileNumber()}_${SARATHI}_${System.currentTimeMillis()}"
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

    private suspend fun getSummaryFile(): Pair<String, Uri?>? {
        val summaryFileNameWithoutExtension = "${SARATHI}_${
            getFirstName(settingBSUserCase.getUserDetailsUseCase.getUserName())
        }_${prefBSRepo.getUniqueUserIdentifier()}_summary_file"

        val summaryFileNameWithExtension = summaryFileNameWithoutExtension + LOCAL_BACKUP_EXTENSION

        return settingBSUserCase.getSummaryFileUseCase.invoke(
            userId = prefBSRepo.getUniqueUserIdentifier(),
            mobileNo = getUserMobileNumber(),
            fileNameWithoutExtension = summaryFileNameWithoutExtension,
            fileNameWithExtension = summaryFileNameWithExtension,
            isBaselineV2 = settingBSUserCase.baselineV1CheckUseCase.isBaselineV2(
                stateId = settingBSUserCase.getUserDetailsUseCase.getStateId().toString()
            )
        )
    }


    fun exportOnlyLogFile(context: Context) {
        NudgeLogger.d("ExportImportViewModel", "exportOnlyLogFile: ----")
        try {
            CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {
                onEvent(LoaderEvent.UpdateLoaderState(true))
                val logFile = LogWriter.buildLogFile(appContext = mAppContext) {
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
                        openShareSheet(
                            convertURIAccToOS(it),
                            "",
                            type = ZIP_MIME_TYPE,
                            context = mAppContext
                        )
                    }


                }
            }
        } catch (e: Exception) {
            onEvent(LoaderEvent.UpdateLoaderState(false))
            NudgeLogger.e("ExportImportViewModel", "exportOnlyLogFile :${e.message}", e)
        }
    }

    private suspend fun generateFormA(
        stateId: Int,
        casteList: List<CasteEntity>,
        selectedVillageId: Int,
        didiList: List<DidiEntity>,
        villageEntity: VillageEntity,
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
        villageEntity: VillageEntity,
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

    fun getUserMobileNumber(): String {
        return settingBSUserCase.getUserDetailsUseCase.getUserMobileNumber()
    }

    private fun convertURIAccToOS(uri: Uri): ArrayList<Uri> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return arrayListOf(uri)
        return arrayListOf(uriFromFile(mAppContext, uri.toFile(), applicationId.value))
    }


    private fun cancelSyncUploadWorker() {
                workManager.cancelAllWorkByTag(SYNC_WORKER_TAG)
                CoreLogger.d(
                    CoreAppDetails.getApplicationContext(),
                    "SettingBSViewModel",
                    "CancelSyncUploadWorker :: Worker Cancelled with TAG : $SYNC_WORKER_TAG"
                )
    }

    fun syncWorkerRunning(): Boolean {
        val workInfo = workManager.getWorkInfosByTag(SYNC_WORKER_TAG)
            workInfo.get().find { it.tags.contains(SYNC_WORKER_TAG) } ?.let {
                return it.state == WorkInfo.State.RUNNING
           }?:return false
    }
}
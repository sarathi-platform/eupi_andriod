package com.patsurvey.nudge.activities.backup.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import com.google.gson.Gson
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.NUDGE_BASELINE_DATABASE
import com.nrlm.baselinesurvey.PREF_KEY_NAME
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.domain.EventWriterHelperImpl
import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
import com.nrlm.baselinesurvey.database.dao.OptionItemDao
import com.nrlm.baselinesurvey.database.dao.QuestionEntityDao
import com.nrlm.baselinesurvey.database.dao.SectionEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.entity.SectionEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventDto
import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventForFormQuestionDto
import com.nrlm.baselinesurvey.model.datamodel.toCSVSave
import com.nrlm.baselinesurvey.model.datamodel.toCsv
import com.nrlm.baselinesurvey.model.datamodel.toCsvR
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.openShareSheet
import com.nrlm.baselinesurvey.utils.showCustomToast
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nudge.core.BASELINE_MISSION_NAME
import com.nudge.core.CoreDispatchers
import com.nudge.core.DEFAULT_LANGUAGE_ID
import com.nudge.core.EXCEL_TYPE
import com.nudge.core.NUDGE_DATABASE
import com.nudge.core.SENSITIVE_INFO_TAG_ID
import com.nudge.core.SUBJECT_ADDRESS
import com.nudge.core.SUBJECT_COHORT_NAME
import com.nudge.core.SUBJECT_DADA_NAME
import com.nudge.core.SUBJECT_NAME
import com.nudge.core.SYNC_MANAGER_DATABASE
import com.nudge.core.VILLAGE_NAME
import com.nudge.core.ZIP_MIME_TYPE
import com.nudge.core.analytics.mixpanel.AnalyticsEvents
import com.nudge.core.compression.ZipFileCompression
import com.nudge.core.datamodel.BaseLineQnATableCSV
import com.nudge.core.datamodel.HamletQnATableCSV
import com.nudge.core.enums.AppConfigKeysEnum
import com.nudge.core.exportDatabase
import com.nudge.core.exportcsv.CsvConfig
import com.nudge.core.exportcsv.ExportService
import com.nudge.core.exportcsv.Exportable
import com.nudge.core.exportcsv.Exports
import com.nudge.core.getFirstName
import com.nudge.core.importDbFile
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.model.SettingOptionModel
import com.nudge.core.moduleNameAccToLoggedInUser
import com.nudge.core.parseStringToList
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.ui.events.ToastMessageEvent
import com.nudge.core.uriFromFile
import com.nudge.core.usecase.AnalyticsEventUseCase
import com.nudge.core.usecase.FetchAppConfigFromCacheOrDbUsecase
import com.nudge.core.usecase.FetchAppConfigFromNetworkUseCase
import com.nudge.core.utils.AESHelper
import com.nudge.core.value
import com.nudge.syncmanager.domain.usecase.SyncManagerUseCase
import com.patsurvey.nudge.BuildConfig
import com.patsurvey.nudge.SettingRepository
import com.patsurvey.nudge.activities.backup.domain.use_case.ExportImportUseCase
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.UPCM_USER
import com.sarathi.dataloadingmangement.NUDGE_GRANT_DATABASE
import com.sarathi.dataloadingmangement.NUMBER_ZERO
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.RegenerateGrantEventUsecase
import com.sarathi.dataloadingmangement.model.events.SaveAnswerEventQuestionItemDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import kotlin.system.exitProcess
import com.sarathi.dataloadingmangement.util.constants.QuestionType as QuestionTypeNew

@HiltViewModel
class ExportImportViewModel @Inject constructor(
    private val exportImportUseCase: ExportImportUseCase,
    private val eventWriterHelperImpl: EventWriterHelperImpl,
    private val sectionEntityDao: SectionEntityDao,
    private val surveyeeEntityDao: SurveyeeEntityDao,
    val prefBSRepo: PrefBSRepo,
    private val optionItemDao: OptionItemDao,
    private val questionEntityDao: QuestionEntityDao,
    private val settingRepository: SettingRepository,
    private val coreSharedPrefs: CoreSharedPrefs,
    private val regenerateGrantEventUsecase: RegenerateGrantEventUsecase,
    private val getTaskUseCase: GetTaskUseCase,
    private val analyticEventUseCase: AnalyticsEventUseCase,
    private val fetchAppConfigFromCacheOrDbUsecase: FetchAppConfigFromCacheOrDbUsecase,
    private val fetchAppConfigFromNetworkUseCase: FetchAppConfigFromNetworkUseCase
) : BaseViewModel() {
    var mAppContext: Context

    val _optionList = mutableStateOf<List<SettingOptionModel>>(emptyList())
    val optionList: State<List<SettingOptionModel>> get() = _optionList
    val showRestartAppDialog = mutableStateOf(false)
    val showConfirmationDialog = mutableStateOf(false)
    val selectedTag = mutableStateOf(BLANK_STRING)
    private val _loaderState = mutableStateOf<LoaderState>(LoaderState(false))
    val applicationId = mutableStateOf(BLANK_STRING)
    val loaderState: State<LoaderState> get() = _loaderState
    val loggedInUserType = mutableStateOf(BLANK_STRING)

    init {
        mAppContext = NudgeCore.getAppContext()
        applicationId.value =
            CoreAppDetails.getApplicationDetails()?.applicationID ?: BuildConfig.APPLICATION_ID
        _optionList.value = exportImportUseCase.getExportOptionListUseCase.fetchExportOptionList()
        loggedInUserType.value =
            exportImportUseCase.getUserDetailsExportUseCase.getLoggedInUserType()
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

    fun clearLocalDatabase(onPageChange: () -> Unit) {
        CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {
            try {
                val result = exportImportUseCase.clearLocalDBExportUseCase.invoke()
                if (result) {
                    exportImportUseCase.clearLocalDBExportUseCase.setAllDataSyncStatus()
                    withContext(CoreDispatchers.mainDispatcher) {
                        onPageChange()
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                BaselineLogger.e("ExportImportViewModel", "clearLocalDatabase : ${ex.message}", ex)
            }
        }
    }

    fun exportLocalDatabase(isNeedToShare:Boolean,onExportSuccess: (Uri) -> Unit) {
        BaselineLogger.d("ExportImportViewModel","exportLocalDatabase -----")
         try {
             onEvent(LoaderEvent.UpdateLoaderState(true))
             exportDatabase(
                 appContext = mAppContext,
                 applicationID = applicationId.value,
                 mobileNo = exportImportUseCase.getUserDetailsExportUseCase.getUserMobileNumber(),
                 databaseName = if (loggedInUserType.value == UPCM_USER) listOf(
                     NUDGE_BASELINE_DATABASE, NUDGE_GRANT_DATABASE,
                     SYNC_MANAGER_DATABASE
                 ) else listOf(NUDGE_DATABASE, SYNC_MANAGER_DATABASE),
                 userName = getFirstName(exportImportUseCase.getUserDetailsExportUseCase.getUserName()),
                 moduleName = moduleNameAccToLoggedInUser(loggedInUserType.value)
             ) {
                 BaselineLogger.d("ExportImportViewModel", "exportLocalDatabase : ${it.path}")
                 onExportLocalDbSuccess(isNeedToShare, it, onExportSuccess)
             }


         } catch (e: Exception) {
             onEvent(LoaderEvent.UpdateLoaderState(false))
             BaselineLogger.e("ExportImportViewModel", "exportLocalDatabase :${e.message}", e)
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
    fun loadServerDataAnalytic(){
        analyticEventUseCase.sendAnalyticsEvent(AnalyticsEvents.LOAD_SERVER_DATA.eventName)
    }
    fun appConfigDataAnalytic(){
        analyticEventUseCase.sendAnalyticsEvent(AnalyticsEvents.APP_CONFIG_LOG_FILE.eventName)

    }
    fun exportDataAnalytic(){
        analyticEventUseCase.sendAnalyticsEvent(AnalyticsEvents.IMPORT_DATA.eventName)
    }

    fun compressEventData(title: String) {
        BaselineLogger.d("ExportImportViewModel", "compressEventData ----")

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
                        "ExportImportViewModel",
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
        BaselineLogger.d("ExportImportViewModel", "Restarting Application")
        context.startActivity(
            Intent(mAppContext, cls)
        )
        exitProcess(0)
    }

    fun importSelectedDB(uri: Uri, onImportSuccess: () -> Unit) {
        BaselineLogger.d("ExportImportViewModel", "importSelectedDB ----")
        try {

            importDbFile(
                appContext = mAppContext,
                importedDbUri = uri,
                deleteDBName = if (loggedInUserType.value == UPCM_USER) NUDGE_BASELINE_DATABASE else NUDGE_DATABASE,
                applicationID = applicationId.value
            ) {
                BaselineLogger.d("ExportImportViewModel", "importSelectedDB Success ----")
                onImportSuccess()
            }
        } catch (exception: Exception) {
            BaselineLogger.e(
                "ExportImportViewModel",
                "importSelectedDB : ${exception.message}",
                exception
            )
            onEvent(LoaderEvent.UpdateLoaderState(false))
        }
    }

    fun regenerateEvents(title: String) {
        onEvent(LoaderEvent.UpdateLoaderState(true))

        CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {
            try {
                if (loggedInUserType.value == UPCM_USER) {

                    eventWriterHelperImpl.regenerateAllEvent(appContext = mAppContext)
                    regenerateGrantEventUsecase.invoke()
                } else {
                    settingRepository.regenerateAllEvent(coreSharedPrefs = coreSharedPrefs)
                }
                compressEventData(title)
                withContext(CoreDispatchers.mainDispatcher) {
                    onEvent(LoaderEvent.UpdateLoaderState(false))
                }
                analyticEventUseCase.sendAnalyticsEvent(AnalyticsEvents.REGENERATE_ALL_EVENT.eventName)
            } catch (exception: Exception) {
                BaselineLogger.e("RegenerateEvent", exception.message ?: "")
                exception.printStackTrace()
                withContext(CoreDispatchers.mainDispatcher) {
                    onEvent(LoaderEvent.UpdateLoaderState(false))
                }
            }

        }
    }

    fun exportBaseLineQnA(context: Context) {
        CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {
            try {
                onEvent(LoaderEvent.UpdateLoaderState(true))
                val dtoList = getSaveAnswerEvents()
                val dtoSaveFormList = generateDtoSaveFormList()
                val sectionList = sectionEntityDao.getSectionsT(
                    prefBSRepo.getUniqueUserIdentifier(),
                    DEFAULT_LANGUAGE_ID
                )
                val surveeyList =
                    surveyeeEntityDao.getAllDidiForQNA(prefBSRepo.getUniqueUserIdentifier())

                val baseLineQnATableCSV =
                    buildBaseLineQnATableCSV(dtoList, dtoSaveFormList, sectionList, surveeyList)

                val baseLineListQna = groupAndSortBySurveyId(baseLineQnATableCSV, 1)
                if (baseLineListQna.isNotEmpty()) {
                    BaselineLogger.d(
                        "ExportImportViewModel",
                        "Old BaseLineListQnaList : ${baseLineListQna.size}"
                    )
                }
                val hamletListQna = groupAndSortBySurveyId(baseLineQnATableCSV, 2)
                if (hamletListQna.isNotEmpty()) {
                    BaselineLogger.d(
                        "ExportImportViewModel",
                        "Old HamletListQnaList : ${hamletListQna.size}"
                    )
                }
                val title = generateTitle()
                val listPath = generateCsvFiles(baseLineListQna, hamletListQna, title, context)
                openShareSheet(fileUriList = listPath, title = title, type = EXCEL_TYPE)
                onEvent(LoaderEvent.UpdateLoaderState(false))
            } catch (exception: Exception) {
                handleError(exception, context)
            }
        }
    }

    /**
     * To export Survey Type Activity or New Baseline Questions Answers
     * Create and share CSV file
     */
    fun exportBaseLineQnAForSurveyTypeActivity(context: Context) {
        CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {
            try {
                onEvent(LoaderEvent.UpdateLoaderState(true))
                val baseLineQnATableCSV: ArrayList<BaseLineQnATableCSV> = arrayListOf()

                val quesAnswerList = regenerateGrantEventUsecase.fetchSurveyAnswerEvents()
                    ?.filter { it.question.questionType != QuestionTypeNew.MultiImage.name && it.question.questionType != QuestionTypeNew.SingleImage.name }

                quesAnswerList?.let { quesList ->
                    quesList.forEach { survey ->
                        val task = getTaskUseCase.getSubjectAttributes(survey.taskId)
                        //This method need to print the multiple options in seperate row in  CSV
                        val responsePair = findResponseAndSubQuestion(survey.question)
                        responsePair.forEach { pair ->
                        baseLineQnATableCSV.add(
                            BaseLineQnATableCSV(
                                id = survey.question.questionId.toString(),
                                surveyId = survey.surveyId,
                                sectionId = survey.sectionId,
                                subjectId = survey.subjectId,
                                house = task.find { it.key == SUBJECT_ADDRESS }?.value.value(),
                                orderId = survey.question.order,
                                section = survey.sectionName,
                                dadaName = task.find { it.key == SUBJECT_DADA_NAME }?.value.value(),
                                didiName = task.find { it.key == SUBJECT_NAME }?.value.value(),
                                question = if (survey.question.formId != NUMBER_ZERO) survey.question.formDescription else survey.question.questionDesc,
                                response = pair.first,
                                cohortName = task.find { it.key == SUBJECT_COHORT_NAME }?.value.value(),
                                subQuestion = if (survey.question.formId != NUMBER_ZERO) survey.question.questionDesc else pair.second,
                                villageName = task.find { it.key == VILLAGE_NAME }?.value.value(),
                                referenceId = survey.referenceId,
                                formOder = survey.question.formOder,
                                sortKey = survey.question.sortKey
                            )
                        )
                    }
                    }
                }

                val baseLineListQna = groupAndSortBySurveyId(baseLineQnATableCSV, 1)
                if (baseLineListQna.isNotEmpty()) {
                    BaselineLogger.d(
                        "ExportImportViewModel",
                        "BaseLineListQnaList : ${baseLineListQna.size}"
                    )
                }
                val hamletListQna = groupAndSortBySurveyId(baseLineQnATableCSV, 2)
                if (hamletListQna.isNotEmpty()) {
                    BaselineLogger.d(
                        "ExportImportViewModel",
                        "HamletListQnaList : ${hamletListQna.size}"
                    )
                }
                val title = generateTitle()
                val listPath = generateCsvFiles(baseLineListQna, hamletListQna, title, context)
                openShareSheet(fileUriList = listPath, title = title, type = EXCEL_TYPE)
                onEvent(LoaderEvent.UpdateLoaderState(false))
            } catch (exception: Exception) {
                handleError(exception, context)
            }
        }
    }

    private suspend fun findResponseAndSubQuestion(question: SaveAnswerEventQuestionItemDto): List<Pair<String, String>> {
        val responsePairList = ArrayList<Pair<String, String>>()
        //sorting the options using order by key
        question.options.sortedBy { it.order }.forEach { option ->
            var response = BLANK_STRING
            var optionDesc = BLANK_STRING
            if (QuestionTypeNew.optionDescriptionAllowInExport.contains(question.questionType.toLowerCase())) {
                optionDesc = option.optionDesc
            }
            response = if (question.tag.contains(SENSITIVE_INFO_TAG_ID)) AESHelper.decrypt(
                option.selectedValue.value(),
                fetchAppConfigFromCacheOrDbUsecase.getAESSecretKey()
            ) else option.selectedValue.value()
            responsePairList.add(Pair(response, optionDesc))
        }
        return responsePairList
    }
    private suspend fun getSaveAnswerEvents(): List<SaveAnswerEventDto> {
        val eventsList = eventWriterHelperImpl.generateResponseEvent()
        val payloadList = eventsList.map { it.request_payload }
        val dtoList = ArrayList<SaveAnswerEventDto>()
        payloadList.forEach { payload ->
            try {
                val dto = Gson().fromJson(payload, SaveAnswerEventDto::class.java)
                dtoList.add(dto)
            } catch (e: Exception) {
                BaselineLogger.e(
                    "ExportImportViewModel",
                    "Exception CSV SAVE ANSWER generate: ${e.message} ---------------",
                    e
                )
            }
        }
        return dtoList
    }

    private suspend fun generateDtoSaveFormList(): List<SaveAnswerEventForFormQuestionDto> {
        val formQuestionEvents = eventWriterHelperImpl.generateFormTypeEventsForCSV()
        val payloadFormQuestionEventsList = formQuestionEvents.map { it.request_payload }
        val dtoSaveFormList = ArrayList<SaveAnswerEventForFormQuestionDto>()
        payloadFormQuestionEventsList.forEach { payload ->
            try {
                val dto = Gson().fromJson(payload, SaveAnswerEventForFormQuestionDto::class.java)
                dtoSaveFormList.add(dto)
            } catch (e: Exception) {
                BaselineLogger.e(
                    "ExportImportViewModel",
                    "Exception CSV SAVE ANSWER FORM generate: ${e.message} ---------------",
                    e
                )
            }
        }
        return dtoSaveFormList
    }

    private suspend fun buildBaseLineQnATableCSV(
        dtoList: List<SaveAnswerEventDto>,
        dtoSaveFormList: List<SaveAnswerEventForFormQuestionDto>,
        sectionList: List<SectionEntity>,
        surveeList: List<SurveyeeEntity>
    ): List<BaseLineQnATableCSV> {
        val baseLineQnATableCSV = mutableListOf<BaseLineQnATableCSV>()
        baseLineQnATableCSV.addAll(
            dtoList.toCSVSave(
                sectionList,
                surveeList,
                optionItemDao,
                questionEntityDao,
                prefBSRepo.getUniqueUserIdentifier()
            )
        )
        baseLineQnATableCSV.addAll(
            dtoSaveFormList.toCsv(
                sectionList,
                surveeList,
                optionItemDao,
                questionEntityDao,
                prefBSRepo.getUniqueUserIdentifier()
            )
        )
        return baseLineQnATableCSV
    }

    private fun groupAndSortBySurveyId(
        baseLineQnATableCSV: List<BaseLineQnATableCSV>,
        surveyId: Int
    ): List<BaseLineQnATableCSV> {
        val filteredList = baseLineQnATableCSV.filter { it.surveyId == surveyId }
        return filteredList
            .sortedWith(
                compareBy(
                    { it.subjectId },   // First, sort by subjectId
                    { it.sectionId },   // Then, sort by sectionId
                    { it.formOder },    // Then, sort by formOder
                    { it.referenceId }, // Then, sort by referenceId
                    { it.orderId }// Finally, sort by sortKey
                )
            )
    }

    private fun generateTitle(): String {
        return "${
            prefBSRepo.getPref(
                PREF_KEY_NAME,
                BLANK_STRING
            ) ?: BLANK_STRING
        }-${prefBSRepo.getMobileNumber()}"
    }

    private suspend fun generateCsvFiles(
        baseLineListQna: List<BaseLineQnATableCSV>,
        hamletListQna: List<BaseLineQnATableCSV>,
        title: String,
        context: Context
    ): ArrayList<Uri>? {
        val listPath: ArrayList<Uri>? = ArrayList()
        if (!baseLineListQna.toCsvR().isNullOrEmpty()) {
            val baseLinePath = generateCsv("Baseline - $title", baseLineListQna.toCsvR(), null)
            baseLinePath?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) listPath?.add(it)
                else listPath?.add(
                    uriFromFile(
                        applicationID = applicationId.value,
                        context = context,
                        file = it.toFile()
                    )
                )
            }
        }
        if (!hamletListQna.toCsv().isNullOrEmpty()) {
            val hamletPath = generateCsv("Hamlet - $title", null, hamletListQna.toCsv())
            hamletPath?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) listPath?.add(it)
                else listPath?.add(
                    uriFromFile(
                        applicationID = applicationId.value,
                        context = context,
                        file = it.toFile()
                    )
                )
            }
        }
        return listPath
    }

    private suspend fun handleError(exception: Exception, context: Context) {
        exception.printStackTrace()
        withContext(CoreDispatchers.mainDispatcher) {
            showCustomToast(context, context.getString(R.string.no_data_available_at_the_moment))
        }
        BaselineLogger.e(
            "ExportImportViewModel",
            "Exception CSV generate work: ${exception.message} ---------------",
            exception
        )
        onEvent(LoaderEvent.UpdateLoaderState(false))
    }

    suspend fun generateCsv(
        title: String,
        baseLineListQna: List<BaseLineQnATableCSV>?,
        hamletListQna: List<HamletQnATableCSV>?,
    ): Uri? {
        val list = checkCSVList(hamletListQna, baseLineListQna)
        var uri: Uri? = null
        ExportService.export(
            type = Exports.CSV(
                CsvConfig(
                    prefix = title,
                    hostPath = mAppContext
                        .getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath
                        ?: ""
                )
            ),
            content = list,
        ).catch { error ->
            // handle error here
            BaselineLogger.e(
                "ExportImportViewModel",
                "Export CSV error: $error ---------------",
                error
            )
        }.collect { path ->
            val file = File(path)
            uri = FileProvider.getUriForFile(
                mAppContext,
                CoreAppDetails.getApplicationDetails()?.packageName + ".provider",
                file
            )
        }
        return uri
    }

    fun checkCSVList(
        hamletListQna: List<HamletQnATableCSV>?,
        baseLineListQna: List<BaseLineQnATableCSV>?
    ): List<Exportable> {
        val list = if (hamletListQna.isNullOrEmpty()) {
            baseLineListQna
        } else {
            hamletListQna
        }
        return list ?: emptyList()
    }

    /**
     * To validate Baseline V1 or V2 for Export Baseline Questions and Answers
     */

    fun exportOldAndNewBaselineQnA(context: Context) {
        CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {
            val baselineV1Ids =
                fetchAppConfigFromCacheOrDbUsecase.invokeFromPref(AppConfigKeysEnum.USE_BASELINE_V1.name)
                    .parseStringToList()
            val missionList = exportImportUseCase.getExportOptionListUseCase.fetchMissionsForUser()
            if (baselineV1Ids.contains(exportImportUseCase.getUserDetailsExportUseCase.getStateId())
                && missionList.any {
                    it.description.equals(
                        BASELINE_MISSION_NAME,
                        ignoreCase = true
                    )
                }
            ) {
                BaselineLogger.d("ExportImportViewModel", "Old Baseline Export")
                exportBaseLineQnA(context)
            } else {
                exportBaseLineQnAForSurveyTypeActivity(context)
                BaselineLogger.d("ExportImportViewModel", "New Baseline Export")

            }
            analyticEventUseCase.sendAnalyticsEvent(AnalyticsEvents.EXPORT_BASELINE_QNA.eventName)

        }
    }
    fun getMobileNumber() = exportImportUseCase.getUserDetailsExportUseCase.getUserMobileNumber()
    fun fetchAppConfig() {
        onEvent(LoaderEvent.UpdateLoaderState(true))
        CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {
            fetchAppConfigFromNetworkUseCase.invoke()
            onEvent(LoaderEvent.UpdateLoaderState(false))
        }
    }
}
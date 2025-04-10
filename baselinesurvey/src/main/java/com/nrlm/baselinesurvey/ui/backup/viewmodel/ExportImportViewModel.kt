package com.nrlm.baselinesurvey.ui.backup.viewmodel

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
import com.nrlm.baselinesurvey.BuildConfig
import com.nrlm.baselinesurvey.DEFAULT_LANGUAGE_ID
import com.nrlm.baselinesurvey.NUDGE_BASELINE_DATABASE
import com.nrlm.baselinesurvey.PREF_KEY_NAME
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.domain.EventWriterHelperImpl
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.MissionActivityDao
import com.nrlm.baselinesurvey.database.dao.OptionItemDao
import com.nrlm.baselinesurvey.database.dao.QuestionEntityDao
import com.nrlm.baselinesurvey.database.dao.SectionEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventDto
import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventForFormQuestionDto
import com.nrlm.baselinesurvey.model.datamodel.toCSVSave
import com.nrlm.baselinesurvey.model.datamodel.toCsv
import com.nrlm.baselinesurvey.model.datamodel.toCsvR
import com.nrlm.baselinesurvey.ui.backup.domain.use_case.ExportImportUseCase
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.LogWriter
import com.nrlm.baselinesurvey.utils.openShareSheet
import com.nrlm.baselinesurvey.utils.showCustomToast
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nudge.core.EXCEL_TYPE
import com.nudge.core.ZIP_MIME_TYPE
import com.nudge.core.compression.ZipFileCompression
import com.nudge.core.datamodel.BaseLineQnATableCSV
import com.nudge.core.datamodel.HamletQnATableCSV
import com.nudge.core.enums.EventType
import com.nudge.core.exportAllOldImages
import com.nudge.core.exportLogFile
import com.nudge.core.exportOldData
import com.nudge.core.exportcsv.CsvConfig
import com.nudge.core.exportcsv.ExportService
import com.nudge.core.exportcsv.Exportable
import com.nudge.core.exportcsv.Exports
import com.nudge.core.getFirstName
import com.nudge.core.importDbFile
import com.nudge.core.model.SettingOptionModel
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.toDate
import com.nudge.core.ui.events.ToastMessageEvent
import com.nudge.core.uriFromFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import kotlin.system.exitProcess

@HiltViewModel
class ExportImportViewModel @Inject constructor(
    private val exportImportUseCase: ExportImportUseCase,
    private val eventWriterHelperImpl: EventWriterHelperImpl,
    private val sectionEntityDao: SectionEntityDao,
    private val surveyeeEntityDao: SurveyeeEntityDao,
    private val optionItemDao: OptionItemDao,
    private val questionEntityDao: QuestionEntityDao,
    private val missionActivityDao: MissionActivityDao,
    val prefRepo: PrefRepo
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
                BaselineLogger.e("ExportImportViewModel","clearLocalDatabase : ${ex.message}",ex)
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
                 if (isNeedToShare) {
                     openShareSheet(convertURIAccToOS(it), "", type = ZIP_MIME_TYPE)
                 } else {
                     onExportSuccess(it)
                 }
             }
         }catch (e:Exception){
             onEvent(LoaderEvent.UpdateLoaderState(false))
            BaselineLogger.e("ExportImportViewModel","exportLocalDatabase :${e.message}",e)
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
                openShareSheet(convertURIAccToOS(imageZipUri),"Share All Images", type = ZIP_MIME_TYPE)
            }
        }
        }catch (e:Exception){
            onEvent(LoaderEvent.UpdateLoaderState(false))
            BaselineLogger.e("ExportImportViewModel","exportLocalImages :${e.message}",e)
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
                openShareSheet(convertURIAccToOS(it) ,"", type = ZIP_MIME_TYPE)
            }


        }
    }
   }catch (e:Exception){
       onEvent(LoaderEvent.UpdateLoaderState(false))
       BaselineLogger.e("ExportImportViewModel","exportOnlyLogFile :${e.message}",e)
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
                    exportImportUseCase.getUserDetailsExportUseCase.getUserName()
                )
               if(fileUri!=null) {
                   BaselineLogger.d("ExportImportViewModel","compressEventData ${fileUri.path}----")
                   openShareSheet(convertURIAccToOS(fileUri), title, type = ZIP_MIME_TYPE)
               }
                CoreSharedPrefs.getInstance(BaselineCore.getAppContext()).setFileExported(true)
                onEvent(LoaderEvent.UpdateLoaderState(false))
            } catch (exception: Exception) {
                BaselineLogger.e("Compression", exception.message ?: "",exception)
                exception.printStackTrace()
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private fun convertURIAccToOS(uri: Uri): ArrayList<Uri> {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return arrayListOf(uri)
       return arrayListOf(uriFromFile(BaselineCore.getAppContext(),uri.toFile(),BuildConfig.APPLICATION_ID))
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
            BaselineLogger.e("ExportImportViewModel", "importSelectedDB : ${exception.message}",exception)
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

    fun exportBaseLineQnA(context: Context) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                onEvent(LoaderEvent.UpdateLoaderState(true))
                val eventsList = eventWriterHelperImpl.generateResponseEvent()
                val payloadList = eventsList.map { it.request_payload }
                val dtoList = ArrayList<SaveAnswerEventDto>()
                payloadList.forEach { payload ->
                    try {
                        val dto = Gson().fromJson(payload, SaveAnswerEventDto::class.java)
                        dtoList.add(dto)
                    } catch (e: Exception) {
                        BaselineLogger.e("ExportImportViewModel", "Exception CSV SAVE ANSWER generate: ${e.message} ---------------", e)
                    }
                }
                val formQuestionEvents = eventWriterHelperImpl.generateFormTypeEventsForCSV()
                val payloadFormQuestionEventsList = formQuestionEvents.map { it.request_payload }
                val dtoSaveFormList = ArrayList<SaveAnswerEventForFormQuestionDto>()
                payloadFormQuestionEventsList.forEach { payload ->
                    try {
                        val dto =
                            Gson().fromJson(payload, SaveAnswerEventForFormQuestionDto::class.java)
                        dtoSaveFormList.add(dto)
                    } catch (e: Exception) {
                        BaselineLogger.e("ExportImportViewModel", "Exception CSV SAVE ANSWER FORM generate: ${e.message} ---------------", e)
                    }
                }
                val sectionList = sectionEntityDao.getSectionsT(
                    prefRepo.getUniqueUserIdentifier(),
                    DEFAULT_LANGUAGE_ID
                )
                val surveeList =
                    surveyeeEntityDao.getAllDidiForQNA(prefRepo.getUniqueUserIdentifier())
                val baseLineQnATableCSV = mutableListOf<BaseLineQnATableCSV>()

                baseLineQnATableCSV.addAll(dtoList.toCSVSave(sectionList, surveeList, optionItemDao, questionEntityDao, prefRepo.getUniqueUserIdentifier()))
                baseLineQnATableCSV.addAll(dtoSaveFormList.toCsv(sectionList, surveeList, optionItemDao, questionEntityDao, prefRepo.getUniqueUserIdentifier()))
                /*BaseLine*/
                val baseLineListQnaCSV = baseLineQnATableCSV.filter { it.surveyId == 1 }
                val baseLineQnATableCSVGroupBySectionId = baseLineListQnaCSV
                    .groupBy { it.sectionId }
                    .toList()
                    .sortedBy { it.first }
                    .flatMap { it.second.sortedBy { it.orderId } }
                val baseLineMap = baseLineQnATableCSVGroupBySectionId.groupBy { it.subjectId}
                val baseLineListQna = ArrayList<BaseLineQnATableCSV>()
                baseLineMap.forEach {
                    baseLineListQna.addAll(it.value)
                }
                /*Hamlet */
                val hamletListQnaCSV = baseLineQnATableCSV.filter { it.surveyId == 2}
                val hamletQnATableCSVGroupBySectionId = hamletListQnaCSV
                    .groupBy { it.sectionId }
                    .toList()
                    .sortedBy { it.first }
                    .flatMap { it.second.sortedBy { it.orderId } }
                val hamletMap = hamletQnATableCSVGroupBySectionId.groupBy { it.subjectId }
                val hamletListQna = ArrayList<BaseLineQnATableCSV>()
                hamletMap.forEach{
                    hamletListQna.addAll(it.value)
                }

                val title = "${
                    prefRepo.getPref(
                        PREF_KEY_NAME,
                        BLANK_STRING
                    ) ?: BLANK_STRING
                }-${prefRepo.getMobileNumber()}"

                val listPath: ArrayList<Uri>? = ArrayList()
                if (!baseLineListQna.toCsvR().isNullOrEmpty()) {
                    val baseLinePath = generateCsv(
                        title = "Baseline - $title",
                        baseLineListQna = baseLineListQna.toCsvR(),
                        hamletListQna = null
                    )
                    baseLinePath?.let {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            listPath?.add(it)
                        else listPath?.add(uriFromFile(applicationID = BuildConfig.APPLICATION_ID, context = context, file = it.toFile()))
                    }
                }

                if (!hamletListQna.toCsv().isNullOrEmpty()) {
                    val hamletPath = generateCsv(
                        title = "Hamlet - $title",
                        baseLineListQna = null,
                        hamletListQna = hamletListQna.toCsv()
                    )
                    hamletPath?.let {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            listPath?.add(it)
                        else listPath?.add(uriFromFile(applicationID = BuildConfig.APPLICATION_ID, context = context, file = it.toFile()))
                    }
                }

                openShareSheet(fileUriList = listPath, title = title, type = EXCEL_TYPE)
                onEvent(LoaderEvent.UpdateLoaderState(false))
            } catch (exception: Exception) {
                exception.printStackTrace()
                withContext(Dispatchers.Main) {showCustomToast(context, context.getString(R.string.no_data_available_at_the_moment))}
                BaselineLogger.e("ExportImportViewModel", "Exception CSV generate work: ${exception.message} ---------------", exception)
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
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
                    hostPath = BaselineCore.getAppContext()
                        .getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath
                        ?: ""
                )
            ),
            content = list,
        ).catch { error ->
            // handle error here
            BaselineLogger.e("ExportImportViewModel", "Export CSV error: $error ---------------", error)
        }.collect { path ->
            val file = File(path)
            uri = FileProvider.getUriForFile(
                BaselineCore.getAppContext(),
                BaselineCore.getAppContext().packageName + ".provider",
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

    fun markAllActivityInProgress(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {

            val userId = prefRepo.getUniqueUserIdentifier()

            val activities = missionActivityDao.getAllActivities(userId)

            activities.forEach { activity ->

                missionActivityDao.markActivityStart(
                    userId,
                    activity.missionId,
                    activity.activityId,
                    SectionStatus.INPROGRESS.name,
                    System.currentTimeMillis().toDate().toString()
                )

                val updateTaskStatusEvent =
                    eventWriterHelperImpl.createActivityStatusUpdateEvent(
                        missionId = activity.missionId,
                        activityId = activity.activityId,
                        status = SectionStatus.INPROGRESS
                    )
                exportImportUseCase.eventsWriterUseCase.invoke(
                    events = updateTaskStatusEvent,
                    eventType = EventType.STATEFUL
                )
            }

            withContext(Dispatchers.Main) {
                showCustomToast(
                    context,
                    context.getString(R.string.all_activities_marked_as_in_progress)
                )
            }
        }
    }
}
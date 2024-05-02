package com.nrlm.baselinesurvey.ui.backup.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.FileProvider
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
import com.nrlm.baselinesurvey.utils.showCustomToast
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nudge.core.EXCEL_TYPE
import com.nudge.core.ZIP_MIME_TYPE
import com.nudge.core.compression.ZipFileCompression
import com.nudge.core.datamodel.BaseLineQnATableCSV
import com.nudge.core.datamodel.HamletQnATableCSV
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
import com.nudge.core.ui.events.ToastMessageEvent
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
            val result=exportImportUseCase.clearLocalDBExportUseCase.invoke()
            if(result){
                exportImportUseCase.clearLocalDBExportUseCase.setAllDataSyncStatus()
                withContext(Dispatchers.Main){
                    onPageChange()
                }
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

                 if (isNeedToShare) {
                     openShareSheet(arrayListOf(it), "", type = ZIP_MIME_TYPE)
                     onExportSuccess(it)
                 } else {
                     onExportSuccess(it)
                 }
             }
         }catch (e:Exception){
             onEvent(LoaderEvent.UpdateLoaderState(false))
            BaselineLogger.e("ExportImportViewModel","exportLocalDatabase :${e.message}")
         }

    }

    private fun openShareSheet(fileUriList: ArrayList<Uri>?, title: String, type: String,) {
        if(fileUriList?.isNotEmpty() == true){
            val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
            shareIntent.setType(type)
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUriList)
            shareIntent.putExtra(Intent.EXTRA_TITLE, title)
            val chooserIntent = Intent.createChooser(shareIntent, title)
            BaselineCore.startExternalApp(chooserIntent)
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
                openShareSheet(arrayListOf(imageZipUri),"Share All Images", type = ZIP_MIME_TYPE)
            }
        }
        }catch (e:Exception){
            onEvent(LoaderEvent.UpdateLoaderState(false))
            BaselineLogger.e("ExportImportViewModel","exportLocalImages :${e.message}")
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
                openShareSheet(arrayListOf(it) ,"", type = ZIP_MIME_TYPE)
            }


        }
    }
   }catch (e:Exception){
       onEvent(LoaderEvent.UpdateLoaderState(false))
       BaselineLogger.e("ExportImportViewModel","exportOnlyLogFile :${e.message}")
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
                   openShareSheet(arrayListOf(fileUri), title, type = ZIP_MIME_TYPE)
               }
                CoreSharedPrefs.getInstance(BaselineCore.getAppContext()).setFileExported(true)
                onEvent(LoaderEvent.UpdateLoaderState(false))
            } catch (exception: Exception) {
                BaselineLogger.e("Compression", exception.message ?: "")
                exception.printStackTrace()
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
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
            BaselineLogger.e("ExportImportViewModel", "importSelectedDB : ${exception.message}")
            exception.printStackTrace()
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
                        BaselineLogger.e("ExportImportViewModel", "Exception CSV SAVE ANSWER generate: ${e.message} ---------------")
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
                        BaselineLogger.e("ExportImportViewModel", "Exception CSV SAVE ANSWER FORM generate: ${e.message} ---------------")
                    }
                }
                val sectionList = sectionEntityDao.getSectionsT(
                    prefRepo.getUniqueUserIdentifier(),
                    DEFAULT_LANGUAGE_ID
                )
                val surveeList =
                    surveyeeEntityDao.getAllDidiForQNA(prefRepo.getUniqueUserIdentifier())
                val baseLineQnATableCSV = mutableListOf<BaseLineQnATableCSV>()
                baseLineQnATableCSV.addAll(dtoList.toCSVSave(sectionList, surveeList))
                baseLineQnATableCSV.addAll(dtoSaveFormList.toCsv(sectionList, surveeList))
                /*BaseLine*/
                val baseLineListQnaCSV = baseLineQnATableCSV.filter { it.surveyId == 1 }
                val baseLineMap = baseLineListQnaCSV.groupBy { it.subjectId }
                val baseLineListQna = ArrayList<BaseLineQnATableCSV>()
                baseLineMap.forEach {
                    baseLineListQna.addAll(it.value)
                }
                /*Hamlet */
                val hamletListQnaCSV = baseLineQnATableCSV.filter { it.surveyId == 2}
                val hamletMap = hamletListQnaCSV.groupBy { it.subjectId }
                val hamletListQna = ArrayList<BaseLineQnATableCSV>()
                hamletMap.forEach{
                    hamletListQna.addAll(it.value)
                }

                val title = "${
                    prefRepo.getPref(
                        PREF_KEY_NAME,
                        com.nudge.core.BLANK_STRING
                    ) ?: com.nudge.core.BLANK_STRING
                }-${prefRepo.getMobileNumber()}"
                val baseLinePath = generateCsv(title = "Baseline - $title", baseLineListQna = baseLineListQna.toCsvR(), hamletListQna = null)
                val hamletPath = generateCsv(title = "Hamlet - $title", baseLineListQna = null, hamletListQna = hamletListQna.toCsv())
                val listPath: ArrayList<Uri>? = ArrayList()
                baseLinePath?.let { listPath?.add(it) }
                hamletPath?.let { listPath?.add(it) }
                openShareSheet(fileUriList = listPath, title = title, type = EXCEL_TYPE)
                onEvent(LoaderEvent.UpdateLoaderState(false))
            } catch (exception: Exception) {
                exception.printStackTrace()
                withContext(Dispatchers.Main) {showCustomToast(context, context.getString(R.string.no_data_available_at_the_moment))}
                BaselineLogger.e("ExportImportViewModel", "Exception CSV generate work: ${exception.message} ---------------")
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
            BaselineLogger.e("ExportImportViewModel", "Export CSV error: $error ---------------")
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
    fun checkCSVList(hamletListQna: List<HamletQnATableCSV>?, baseLineListQna: List<BaseLineQnATableCSV>?): List<Exportable> {
        val list = if (hamletListQna.isNullOrEmpty()) {
            baseLineListQna
        } else {
            hamletListQna
        }
        return list!!
    }
}
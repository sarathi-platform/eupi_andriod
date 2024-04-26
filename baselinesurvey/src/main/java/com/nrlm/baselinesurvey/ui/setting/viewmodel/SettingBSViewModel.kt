package com.nrlm.baselinesurvey.ui.setting.viewmodel

import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.nrlm.baselinesurvey.DEFAULT_LANGUAGE_ID
import com.nrlm.baselinesurvey.PREF_KEY_NAME
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
import com.nrlm.baselinesurvey.ui.setting.domain.use_case.SettingBSUserCase
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.LogWriter
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nudge.core.BLANK_STRING
import com.nudge.core.EXCEL_TYPE
import com.nudge.core.ZIP_MIME_TYPE
import com.nudge.core.compression.ZipFileCompression
import com.nudge.core.datamodel.BaseLineQnATableCSV
import com.nudge.core.datamodel.HamletQnATableCSV
import com.nudge.core.exportcsv.CsvConfig
import com.nudge.core.exportcsv.ExportService
import com.nudge.core.exportcsv.Exportable
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
    private val surveyeeEntityDao: SurveyeeEntityDao,
    private val eventWriterHelperImpl: EventWriterHelperImpl,
    val prefRepo: PrefRepo
) : BaseViewModel() {
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
                val listFileUri = listOf(fileUri, imageUri)
                openShareSheet(
                    fileUri = null,
                    listFileUri = listFileUri,
                    title = title,
                    type = ZIP_MIME_TYPE,
                    intentType = Intent.ACTION_SEND_MULTIPLE
                )
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
                        e.printStackTrace()
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
                        e.printStackTrace()
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
//                    listQna.add(BaseLineQnATableCSV())
//                    listQna.add(BaseLineQnATableCSV())
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
                        BLANK_STRING
                    ) ?: BLANK_STRING
                }-${prefRepo.getMobileNumber()}"
                val baseLinePath = generateCsv(title = "Baseline - $title", baseLineListQna = baseLineListQna.toCsvR(), hamletListQna = null)
                val hamletPath = generateCsv(title = "Hamlet - $title", baseLineListQna = null, hamletListQna = hamletListQna.toCsv())
                val listPath: ArrayList<Uri?> = ArrayList()
                listPath.add(baseLinePath)
                listPath.add(hamletPath)

                openShareSheet(
                    fileUri = null,
                    listFileUri = listPath,
                    title = title,
                    type = EXCEL_TYPE,
                    intentType = Intent.ACTION_SEND_MULTIPLE
                )
                onEvent(LoaderEvent.UpdateLoaderState(false))
            } catch (exception: Exception) {
                exception.printStackTrace()
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private fun openShareSheet(
        fileUri: Uri?,
        listFileUri: List<Uri?>?,
        title: String,
        type: String,
        intentType: String
    ) {
        val shareIntent = Intent(intentType)
        shareIntent.setType(type)
        if (listFileUri.isNullOrEmpty()) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
        } else {
            shareIntent.putExtra(Intent.EXTRA_STREAM, ArrayList(listFileUri))
        }
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
//             BaselineLogger.e(TAG, "exportDidiTableToCsv error", error)
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
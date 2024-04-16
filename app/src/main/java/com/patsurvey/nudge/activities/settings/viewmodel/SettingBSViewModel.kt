package com.patsurvey.nudge.activities.settings.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.NudgeBaselineDatabase
import com.patsurvey.nudge.activities.settings.domain.use_case.SettingBSUserCase
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.LogWriter
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nudge.core.ZIP_MIME_TYPE
import com.nudge.core.compression.ZipFileCompression
import com.nudge.core.getDefaultBackUpFileName
import com.nudge.core.model.SettingOptionModel
import com.nudge.core.preference.CoreSharedPrefs
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
    val prefRepo: PrefRepo
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
                2,
                context.getString(R.string.profile),
                BLANK_STRING,
                SettingTagEnum.PROFILE.name
            )
        )
        if(userType != UPCM_USER){
            if(settingOpenFrom!= PageFrom.VILLAGE_PAGE.ordinal) {
                list.add(
                    SettingOptionModel(
                        3,
                        context.getString(R.string.forms),
                        BLANK_STRING,
                        SettingTagEnum.FORMS.name
                    )
                )
            }

            list.add(
                SettingOptionModel(
                    4,
                    context.getString(R.string.training_videos),
                    BLANK_STRING,
                    SettingTagEnum.TRAINING_VIDEOS.name
                )
            )
        }
        list.add(
            SettingOptionModel(
                5,
                context.getString(R.string.language_text),
                BLANK_STRING,
                SettingTagEnum.LANGUAGE.name
            )
        )
        list.add(
            SettingOptionModel(
                6,
                context.getString(R.string.share_logs),
                BLANK_STRING,
                SettingTagEnum.SHARE_LOGS.name
            )
        )
        list.add(
            SettingOptionModel(
                7,
                context.getString(R.string.export_file),
                BLANK_STRING,
                SettingTagEnum.EXPORT_FILE.name
            )
        )
        _optionList.value=list

        checkFormsAvailabilityForVillage(context,villageId)
    }

    fun performLogout(context: Context, onLogout: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val settingUseCaseResponse = settingBSUserCase.logoutUseCase.invoke()
            if (userType == UPCM_USER) {
                clearLocalData()
            } else {
                exportLocalData(context)
                clearAccessToken()
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

    fun clearLocalData() {//TOdo move this logic to repository
        nudgeBaselineDatabase.contentEntityDao().deleteContent()
        nudgeBaselineDatabase.didiDao().deleteSurveyees()
        nudgeBaselineDatabase.activityTaskEntityDao().deleteActivityTask()
        nudgeBaselineDatabase.missionEntityDao().deleteMissions()
        nudgeBaselineDatabase.missionActivityEntityDao().deleteActivities()
        nudgeBaselineDatabase.optionItemDao().deleteOptions()
        nudgeBaselineDatabase.questionEntityDao().deleteAllQuestions()
        nudgeBaselineDatabase.sectionAnswerEntityDao().deleteAllSectionAnswer()
        nudgeBaselineDatabase.inputTypeQuestionAnswerDao().deleteAllInputTypeAnswers()
        nudgeBaselineDatabase.formQuestionResponseDao().deleteAllFormQuestions()
        nudgeBaselineDatabase.didiSectionProgressEntityDao().deleteAllSectionProgress()
        nudgeBaselineDatabase.villageListDao().deleteAllVilleges()
        nudgeBaselineDatabase.surveyEntityDao().deleteAllSurvey()
        nudgeBaselineDatabase.didiInfoEntityDao().deleteAllDidiInfo()
        clearSharedPref()
    }

    fun clearSharedPref() {
        val languageId = prefRepo.getAppLanguageId()
        val language = prefRepo.getAppLanguage()
        prefRepo.clearSharedPreference()
        prefRepo.saveAppLanguage(language)
        prefRepo.saveAppLanguageId(languageId)
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
                    MyApplication.applicationContext(),
                    prefRepo.getMobileNumber() ?: ""
                )

                val imageUri = compression.compressBackupImages(
                    MyApplication.applicationContext(),
                    prefRepo.getMobileNumber() ?: ""
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

    fun buildAndShareLogsForSelection() {
        NudgeLogger.d("SettingBSViewModel", "buildAndShareLogs SELECTION---------------")
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val context = MyApplication.applicationContext()
            settingBSUserCase.exportHandlerSettingUseCase.exportAllData(context)
            com.patsurvey.nudge.utils.LogWriter.buildSupportLogAndShare()
        }
    }

    fun clearAccessToken() {
        prefRepo.saveAccessToken("")
        CoreSharedPrefs.getInstance(NudgeCore.getAppContext()).setBackupFileName(
            getDefaultBackUpFileName(
                prefRepo.getMobileNumber() ?: ""
            )
        )
        CoreSharedPrefs.getInstance(NudgeCore.getAppContext()).setImageBackupFileName(
            getDefaultBackUpFileName(
                prefRepo.getMobileNumber() ?: ""
            )
        )
        CoreSharedPrefs.getInstance(NudgeCore.getAppContext()).setFileExported(false)
        prefRepo.setPreviousUserMobile(mobileNumber = prefRepo.getMobileNumber()?: BLANK_STRING)
        prefRepo.saveSettingOpenFrom(0)

    }
}
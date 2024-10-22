package com.patsurvey.nudge.activities.settings.viewmodel

import android.content.Context
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
import com.nudge.core.ZIP_MIME_TYPE
import com.nudge.core.compression.ZipFileCompression
import com.nudge.core.exportAllOldImages
import com.nudge.core.exportDbFile
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
import com.nudge.core.usecase.FetchAppConfigFromNetworkUseCase
import com.nudge.core.utils.LogWriter
import com.patsurvey.nudge.BuildConfig
import com.patsurvey.nudge.activities.settings.domain.SettingTagEnum
import com.patsurvey.nudge.activities.settings.domain.use_case.SettingBSUserCase
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.CasteEntity
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.service.csv.ExportHelper
import com.patsurvey.nudge.utils.BPC_USER_TYPE
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
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.UPCM_USER
import com.patsurvey.nudge.utils.VO_ENDORSEMENT_CONSTANT
import com.patsurvey.nudge.utils.WealthRank
import com.patsurvey.nudge.utils.changeMilliDateToDate
import com.sarathi.dataloadingmangement.FORM_E
import com.sarathi.dataloadingmangement.domain.use_case.FormUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetFormUiConfigUseCase
import com.sarathi.dataloadingmangement.model.uiModel.ActivityFormUIModel
import com.sarathi.dataloadingmangement.util.constants.GrantTaskFormSlots
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject
import com.patsurvey.nudge.R as appRes


@HiltViewModel
class SettingBSViewModel @Inject constructor(
    private val settingBSUserCase: SettingBSUserCase,
    private val getActivityUseCase: GetActivityUseCase,
    private val fetchAppConfigFromNetworkUseCase: FetchAppConfigFromNetworkUseCase,
    private val formUseCase: FormUseCase,
    val exportHelper: ExportHelper,
    val prefBSRepo: PrefBSRepo,
    val prefRepo: PrefRepo,
    val formUiConfigUseCase: GetFormUiConfigUseCase
) : BaseViewModel() {
    val _optionList = mutableStateOf<List<SettingOptionModel>>(emptyList())
    var showLogoutDialog = mutableStateOf(false)
    var showLoader = mutableStateOf(false)
    var applicationId = mutableStateOf(BLANK_STRING)
    lateinit var mAppContext: Context
    val optionList: State<List<SettingOptionModel>> get() = _optionList
    var userType: String = BLANK_STRING
    private val _loaderState = mutableStateOf<LoaderState>(LoaderState(isLoaderVisible = false))

    val formAAvailable = mutableStateOf(false)
    val formBAvailable = mutableStateOf(false)
    val formCAvailable = mutableStateOf(false)
    val formEAvailableList = mutableStateOf<List<Pair<Int, Boolean>>>(emptyList())
    val activityFormGenerateList = mutableStateOf<List<ActivityFormUIModel>>(emptyList())
    val activityFormGenerateNameMap = HashMap<Pair<Int, Int>, String>()


    val loaderState: State<LoaderState> get() = _loaderState
    fun initOptions(context: Context) {
        applicationId.value =
            CoreAppDetails.getApplicationDetails()?.applicationID ?: BuildConfig.APPLICATION_ID
        userType = settingBSUserCase.getSettingOptionListUseCase.getUserType().toString()
        mAppContext =
            if (userType != UPCM_USER) NudgeCore.getAppContext() else BaselineCore.getAppContext()
        getActivityFormGenerateList(onGetData = {
            if (activityFormGenerateList.value.isNotEmpty()) {
                checkFromEAvailable(activityFormGenerateList.value)
            }
        })
        val villageId = settingBSUserCase.getSettingOptionListUseCase.getSelectedVillageId()
        val settingOpenFrom = settingBSUserCase.getSettingOptionListUseCase.settingOpenFrom()
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
        } else {
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
                4,
                context.getString(R.string.language_text),
                BLANK_STRING,
                SettingTagEnum.LANGUAGE.name
            )
        )


        list.add(
            SettingOptionModel(
                5,
                context.getString(R.string.export_data),
                BLANK_STRING,
                SettingTagEnum.EXPORT_DATA_BACKUP_FILE.name
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
        list.add(
            SettingOptionModel(
                7,
                context.getString(appRes.string.refresh_config),
                BLANK_STRING,
                SettingTagEnum.APP_CONFIG.name

            )
        )

        _optionList.value = list
        if (userType != UPCM_USER && settingOpenFrom != PageFrom.VILLAGE_PAGE.ordinal) {
            checkFormsAvailabilityForVillage(context, villageId)
        }
    }

    fun performLogout(context: Context, onLogout: (Boolean) -> Unit) {
        CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {
            val settingUseCaseResponse = settingBSUserCase.logoutUseCase.invoke()
            if (userType != UPCM_USER) {
                exportLocalData(context)
            }
            delay(2000)
            withContext(CoreDispatchers.mainDispatcher) {
                showLoader.value = false
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
                job = CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {
                    withContext(CoreDispatchers.mainDispatcher) {
                        onEvent(LoaderEvent.UpdateLoaderState(false))
                    }
                }
            }
        }, time)
    }

    suspend fun checkFormAAvailability(context: Context, villageId: Int) {
        var didiList =
            settingBSUserCase.getAllPoorDidiForVillageUseCase.getAllDidiForVillage(villageId)
        if (userType == BPC_USER_TYPE) {
            didiList =
                settingBSUserCase.getAllPoorDidiForVillageUseCase.getAllPoorDidiForVillage(villageId)
        }
        if (didiList.any { it.wealth_ranking == WealthRank.POOR.rank && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal && !it.rankingEdit }
        ) {
            withContext(CoreDispatchers.mainDispatcher) {
                formAAvailable.value = true
            }
        } else {
            withContext(
                CoreDispatchers.mainDispatcher
            ) {
                formAAvailable.value = false
            }
        }
    }


    suspend fun checkFormBAvailability(context: Context, villageId: Int) {
        var didiList =
            settingBSUserCase.getAllPoorDidiForVillageUseCase.getAllDidiForVillage(villageId)
        if (userType == BPC_USER_TYPE) {
            didiList =
                settingBSUserCase.getAllPoorDidiForVillageUseCase.getAllPoorDidiForVillage(villageId)
        }

        if (didiList.any { it.forVoEndorsement == 1 && !it.patEdit }
        ) {
            withContext(CoreDispatchers.mainDispatcher) {
                formBAvailable.value = true
            }
        } else {
            withContext(CoreDispatchers.mainDispatcher) {
                formBAvailable.value = false
            }

        }
    }


    suspend fun checkFormCAvailability(context: Context, villageId: Int) {

        val stepList =
            settingBSUserCase.getAllPoorDidiForVillageUseCase.getAllStepsForVillage(villageId)
        val filteredStepList = stepList.filter { it.name.equals(VO_ENDORSEMENT_CONSTANT, true) }
        if (filteredStepList[0] != null) {
            formCAvailable.value =
                filteredStepList[0].isComplete == StepStatus.COMPLETED.ordinal
        } else {
            formCAvailable.value = false
        }
    }

    fun checkFormsAvailabilityForVillage(context: Context, villageId: Int) {
        job = CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {

            checkFormAAvailability(context = context, villageId = villageId)
            checkFormBAvailability(context = context, villageId = villageId)
            checkFormCAvailability(context = context, villageId = villageId)
        }

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




    fun getActivityFormGenerateList(onGetData: () -> Unit) {
        CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {
            activityFormGenerateList.value = getActivityUseCase.getActiveForm(formType = FORM_E)
            activityFormGenerateList.value.forEach {
                activityFormGenerateNameMap[Pair(it.missionId, it.activityId)] =
                    formUiConfigUseCase.getFormConfigValue(
                        key = GrantTaskFormSlots.TASK_PDF_FORM_NAME.name,
                        missionId = it.missionId,
                        activityId = it.activityId
                    )
            }
            onGetData()
        }
    }



    private fun convertURIAccToOS(uri: Uri): ArrayList<Uri> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return arrayListOf(uri)
        return arrayListOf(uriFromFile(mAppContext, uri.toFile(), applicationId.value))
    }

    private fun checkFromEAvailable(activityFormUIModelList: List<ActivityFormUIModel>) {
        if (activityFormUIModelList.isNotEmpty()) {
            CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {
                val pairFormList = ArrayList<Pair<Int, Boolean>>()
                activityFormUIModelList.forEachIndexed { index, activityFormUIModel ->
                    val isFromEAvailable = formUseCase.getOnlyGeneratedFormSummaryData(
                        activityId = activityFormUIModel.activityId,
                        isFormGenerated = true
                    ).isNotEmpty()
                    pairFormList.add(Pair(index, isFromEAvailable))
                }
                formEAvailableList.value = pairFormList
            }

        } else {
            formEAvailableList.value = emptyList()
        }
    }

    fun fetchhAppConfig() {
        showLoader.value = true
        CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {
            fetchAppConfigFromNetworkUseCase.invoke()
            showLoader.value = false

        }
    }
}
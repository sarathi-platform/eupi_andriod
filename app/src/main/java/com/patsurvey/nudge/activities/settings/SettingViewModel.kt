package com.patsurvey.nudge.activities.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.CountDownTimer
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.facebook.network.connectionclass.DeviceBandwidthSampler
import com.nudge.core.NUDGE_DATABASE
import com.nudge.core.ZIP_MIME_TYPE
import com.nudge.core.compression.ZipFileCompression
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.enums.NetworkSpeed
import com.nudge.core.exportOldData
import com.nudge.core.getDefaultBackUpFileName
import com.nudge.core.getDefaultImageBackUpFileName
import com.nudge.core.json
import com.nudge.core.moduleNameAccToLoggedInUser
import com.nudge.core.preference.CoreSharedPrefs
import com.patsurvey.nudge.BuildConfig
import com.patsurvey.nudge.MyApplication
import com.patsurvey.nudge.R
import com.patsurvey.nudge.SettingRepository
import com.patsurvey.nudge.SyncBPCDataOnServer
import com.patsurvey.nudge.SyncHelper
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.CasteEntity
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.BpcSummaryDao
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.LastSelectedTolaDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.PoorDidiListDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.UserDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.database.service.csv.ExportHelper
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.dataModel.SettingOptionModel
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.ConnectionMonitor
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.FORM_A_PDF_NAME
import com.patsurvey.nudge.utils.FORM_B_PDF_NAME
import com.patsurvey.nudge.utils.FORM_C_PDF_NAME
import com.patsurvey.nudge.utils.LAST_SYNC_TIME
import com.patsurvey.nudge.utils.LAST_UPDATE_TIME
import com.patsurvey.nudge.utils.LogWriter
import com.patsurvey.nudge.utils.LogWriter.getLogFile
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_BPC_DIDI_LIST_SYNCED_FOR_VILLAGE_
import com.patsurvey.nudge.utils.PREF_KEY_NAME
import com.patsurvey.nudge.utils.PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_
import com.patsurvey.nudge.utils.PREF_NEED_TO_POST_FORM_C_AND_D_
import com.patsurvey.nudge.utils.PREF_PAT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_VO_ENDORSEMENT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_WEALTH_RANKING_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.PdfUtils
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.SYNC_FAILED
import com.patsurvey.nudge.utils.SYNC_SUCCESSFULL
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.TolaStatus
import com.patsurvey.nudge.utils.WealthRank
import com.patsurvey.nudge.utils.changeMilliDateToDate
import com.patsurvey.nudge.utils.uriFromFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject
import android.net.Uri as Uri1


@HiltViewModel
class SettingViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val apiInterface: ApiService,
    val tolaDao: TolaDao,
    val casteListDao: CasteListDao,
    val lastSelectedTolaDao: LastSelectedTolaDao,
    val userDao: UserDao,
    val stepsListDao: StepsListDao,
    val villegeListDao: VillageListDao,
    val didiDao: DidiDao,
    val answerDao: AnswerDao,
    val numericAnswerDao: NumericAnswerDao,
    val questionDao: QuestionListDao,
    val bpcSummaryDao: BpcSummaryDao,
    val poorDidiListDao: PoorDidiListDao,
    val exportHelper: ExportHelper,
    val eventDao: EventsDao,
    val settingRepository: SettingRepository
) : BaseViewModel() {
    val formAAvailabe = mutableStateOf(false)
    val formBAvailabe = mutableStateOf(false)
    val formCAvailabe = mutableStateOf(false)
    val onLogoutError = mutableStateOf(false)
    var syncPercentage = mutableStateOf(0f)
    var syncBPCPercentage = mutableStateOf(0f)
    var stepOneSyncStatus = mutableStateOf(0)
    var stepTwoSyncStatus = mutableStateOf(0)
    var stepThreeSyncStatus = mutableStateOf(0)
    var stepFourSyncStatus = mutableStateOf(0)
    var stepFifthSyncStatus = mutableStateOf(0)
    var bpcSyncStatus = mutableStateOf(0)
    var hitApiStatus = mutableStateOf(0)
    var showLogoutDialog = mutableStateOf(false)
    var showAppRestartDialog = mutableStateOf(false)
    var syncErrorMessage = mutableStateOf("")
    val showLoadConfimationDialog = mutableStateOf(false)
    private val _optionList = MutableStateFlow(listOf<SettingOptionModel>())
    val optionList: StateFlow<List<SettingOptionModel>> get() = _optionList
    val showLoader = mutableStateOf(false)
    val showExportLoader = mutableStateOf(false)
    val showAPILoader = mutableStateOf(false)
    var showSyncDialog = mutableStateOf(false)
    var showBPCSyncDialog = mutableStateOf(false)

    val lastSyncTime = mutableStateOf(prefRepo.getPref(LAST_SYNC_TIME, 0L))
    var currentRetryCount = 3
    var syncHelper = SyncHelper(
        this@SettingViewModel,
        prefRepo,
        apiInterface,
        tolaDao,
        stepsListDao,
        exceptionHandler,
        villegeListDao,
        didiDao,
        job,
        showLoader,
        syncPercentage,
        answerDao,
        numericAnswerDao,
        questionDao
    )
    var bpcSyncHelper = SyncBPCDataOnServer(
        this@SettingViewModel,
        prefRepo,
        apiInterface,
        exceptionHandler,
        job,
        didiDao,
        stepsListDao,
        questionDao,
        syncBPCPercentage,
        answerDao,
        numericAnswerDao,
        villegeListDao
    )

    fun isFormAAvailableForVillage(context: Context, villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val formCFilePath =
                File("${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath}/${FORM_A_PDF_NAME}_${villageId}.pdf")
            if (formCFilePath.isFile && formCFilePath.exists()) {
                withContext(Dispatchers.Main) {
                    formAAvailabe.value = true
                }
            } else {
                if (prefRepo.isUserBPC()) {
                    if (poorDidiListDao.getAllPoorDidisForVillage(villageId = villageId)
                            .any { it.wealth_ranking == WealthRank.POOR.rank && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal && !it.rankingEdit }
                    ) {
                        withContext(Dispatchers.Main) {
                            formAAvailabe.value = true
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            formAAvailabe.value = false
                        }
                    }
                } else {
                    if (didiDao.getAllDidisForVillage(villageId = villageId)
                            .any { it.wealth_ranking == WealthRank.POOR.rank && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal && !it.rankingEdit }
                    ) {
                        withContext(Dispatchers.Main) {
                            formAAvailabe.value = true
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            formAAvailabe.value = false
                        }
                    }
                }
            }
        }

    }

    fun isFormBAvailableForVillage(context: Context, villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val formBFilePath =
                File("${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath}/${FORM_B_PDF_NAME}_${villageId}.pdf")
            if (formBFilePath.isFile && formBFilePath.exists()) {
                withContext(Dispatchers.Main) {
                    formBAvailabe.value = true
                }
            } else {
                if (prefRepo.isUserBPC()) {
                    if (poorDidiListDao.getAllPoorDidisForVillage(villageId = villageId)
                            .any { it.forVoEndorsement == 1 && !it.patEdit }
                    ) {
                        withContext(Dispatchers.Main) {
                            formBAvailabe.value = true
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            formBAvailabe.value = false
                        }
                    }
                } else {
                    if (didiDao.getAllDidisForVillage(villageId = villageId)
                            .any { it.forVoEndorsement == 1 && !it.patEdit }
                    ) {
                        withContext(Dispatchers.Main) {
                            formBAvailabe.value = true
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            formBAvailabe.value = false
                        }
                    }
                }
            }
        }


    }

    fun isFormCAvailableForVillage(context: Context, villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val formCFilePath =
                File("${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath}/${FORM_C_PDF_NAME}_${villageId}.pdf")
            if (formCFilePath.isFile && formCFilePath.exists()) {
                withContext(Dispatchers.Main) {
                    formCAvailabe.value = true
                }
            } else {
                val stepList = stepsListDao.getAllStepsForVillage(villageId)
                val filteredStepList = stepList.filter { it.name.equals("VO Endorsement", true) }
                if (filteredStepList[0] != null) {
                    formCAvailabe.value =
                        filteredStepList[0].isComplete == StepStatus.COMPLETED.ordinal
                } else {
                    formCAvailabe.value = false
                }
            }
        }
    }

    fun createSettingMenu(list: ArrayList<SettingOptionModel>) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.Main) {
                _optionList.value = list
            }
        }
    }


    fun isFirstStepNeedToBeSync(isNeedToBeSync: MutableState<Int>) {
        stepOneSyncStatus = isNeedToBeSync
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            if (tolaDao.fetchTolaNeedToPost(true, "", 0).isEmpty()
                && tolaDao.fetchPendingTola(true, "").isEmpty()
                && tolaDao.fetchAllTolaNeedToDelete(TolaStatus.TOLA_DELETED.ordinal).isEmpty()
                && tolaDao.fetchAllPendingTolaNeedToDelete(TolaStatus.TOLA_DELETED.ordinal, "")
                    .isEmpty()
                && tolaDao.fetchAllTolaNeedToUpdate(true, "", 0).isEmpty()
                && tolaDao.fetchAllPendingTolaNeedToUpdate(true, "").isEmpty()
                && isStepStatusSync(1)
            ) {
                NudgeLogger.d(
                    "SettingViewModel",
                    "isFirstStepNeedToBeSync -> isNeedToBeSync.value = 2"
                )
                withContext(Dispatchers.Main) {
                    isNeedToBeSync.value = 2
                }
            } else {
                NudgeLogger.d(
                    "SettingViewModel",
                    "isFirstStepNeedToBeSync -> isNeedToBeSync.value = 0"
                )
                isNeedToBeSync.value = 0
            }
        }
    }

    private fun isStepStatusSync(orderNumber: Int): Boolean {
        val stepList = stepsListDao.getAllStepsWithOrderNumber(orderNumber) ?: emptyList()
        var isSync = true
        for (step in stepList) {
            if (step.needToPost)
                isSync = false
        }
        NudgeLogger.e(
            "SettingViewModel",
            "step -> ${stepList.toString()},  orderNumber -> $orderNumber, status -> $isSync "
        )
        return isSync
    }

    fun isSecondStepNeedToBeSync(isNeedToBeSync: MutableState<Int>) {
        stepTwoSyncStatus = isNeedToBeSync
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val fetchAllDidiNeedToPostList = didiDao.fetchAllDidiNeedToPost(true, "", 0)
            val fetchPendingDidiList = didiDao.fetchPendingDidi(true, "")
            val fetchAllDidiNeedToDeleteList =
                didiDao.fetchAllDidiNeedToDelete(DidiStatus.DIID_DELETED.ordinal, true, "", 0)
            val fetchAllPendingDidiNeedToDeleteList = didiDao.fetchAllPendingDidiNeedToDelete(
                DidiStatus.DIID_DELETED.ordinal,
                ""
            )
            val fetchAllDidiNeedToUpdateList = didiDao.fetchAllDidiNeedToUpdate(true, "")
            val fetchAllPendingDidiNeedToUpdateList = didiDao.fetchAllPendingDidiNeedToUpdate(
                true,
                ""
            )
            NudgeLogger.d(
                "SettingViewModel",
                "isSecondStepNeedToBeSync -> fetchAllDidiNeedToPostList -> ${fetchAllDidiNeedToPostList.json()};; \n\n fetchPendingDidiList -> ${fetchPendingDidiList.json()};; " +
                        "\n\n fetchAllDidiNeedToDeleteList -> ${fetchAllDidiNeedToDeleteList.json()};; \n\n fetchAllPendingDidiNeedToDeleteList -> ${fetchAllPendingDidiNeedToDeleteList.json()};; " +
                        "\n\n fetchAllDidiNeedToUpdateList -> ${fetchAllDidiNeedToUpdateList.json()};; \n\n fetchAllPendingDidiNeedToUpdateList -> ${fetchAllPendingDidiNeedToUpdateList.json()}"
            )
            NudgeLogger.d(
                "SettingViewModel",
                "isSecondStepNeedToBeSync -> \n" +
                        "            fetchAllDidiNeedToPostList.isEmpty() -> ${fetchAllDidiNeedToPostList.isEmpty()}\n" +
                        "            && fetchPendingDidiList.isEmpty() -> ${fetchPendingDidiList.isEmpty()}\n" +
                        "            && fetchAllDidiNeedToDeleteList.isEmpty() -> ${fetchAllDidiNeedToDeleteList.isEmpty()}\n" +
                        "            && fetchAllPendingDidiNeedToDeleteList.isEmpty() -> ${fetchAllPendingDidiNeedToDeleteList.isEmpty()}\n" +
                        "            && fetchAllDidiNeedToUpdateList.isEmpty() -> ${fetchAllDidiNeedToUpdateList.isEmpty()}\n" +
                        "            && fetchAllPendingDidiNeedToUpdateList.isEmpty() -> ${fetchAllPendingDidiNeedToUpdateList.isEmpty()}"
            )

            if (fetchAllDidiNeedToPostList.isEmpty()
                && fetchPendingDidiList.isEmpty()
                && fetchAllDidiNeedToDeleteList.isEmpty()
                && fetchAllPendingDidiNeedToDeleteList.isEmpty()
                && fetchAllDidiNeedToUpdateList.isEmpty()
                && fetchAllPendingDidiNeedToUpdateList.isEmpty()
                && isStepStatusSync(2)
            ) {
                NudgeLogger.d(
                    "SettingViewModel",
                    "isSecondStepNeedToBeSync -> isNeedToBeSync.value = 2"
                )
                withContext(Dispatchers.Main) {
                    isNeedToBeSync.value = 2
                }
            } else {
                NudgeLogger.d(
                    "SettingViewModel",
                    "isSecondStepNeedToBeSync -> isNeedToBeSync.value = 0"
                )
                isNeedToBeSync.value = 0
            }
        }
    }

    fun isBPCScoreSaved(): Boolean {
        val villageList = villegeListDao.getAllVillages(prefRepo.getAppLanguageId() ?: 2)
        val isBpcScoreSavedList = mutableListOf<Boolean>()
        for (village in villageList) {
            val isBpcScoreSaved = prefRepo.getPref(
                PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ + village.id,
                false
            )
            NudgeLogger.d(
                "SettingViewModel",
                "isBPCScoreSaved: village.id -> ${village.id}, isBPCScoreSaved -> $isBpcScoreSaved"
            )
            isBpcScoreSavedList.add(isBpcScoreSaved)
        }
        if (isBpcScoreSavedList.contains(false))
            return false
        else
            return true
    }

    fun isThirdStepNeedToBeSync(isNeedToBeSync: MutableState<Int>) {
        stepThreeSyncStatus = isNeedToBeSync
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            if (didiDao.getAllNeedToPostDidiRankingDidis(true).isEmpty()
                && didiDao.fetchPendingWealthStatusDidi(true, "").isEmpty()
                && isStepStatusSync(3)
            ) {
                NudgeLogger.d(
                    "SettingViewModel",
                    "isThirdStepNeedToBeSync -> isNeedToBeSync.value = 2"
                )
                withContext(Dispatchers.Main) {
                    isNeedToBeSync.value = 2
                }
            } else {
                NudgeLogger.d(
                    "SettingViewModel",
                    "isThirdStepNeedToBeSync -> isNeedToBeSync.value = 0"
                )
                isNeedToBeSync.value = 0
            }
        }
    }

    fun isFourthStepNeedToBeSync(isNeedToBeSync: MutableState<Int>) {
        stepFourSyncStatus = isNeedToBeSync
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val fetchPATSurveyDidiList = answerDao.fetchPATSurveyDidiList()
            val fetchPendingPatStatusDidi = didiDao.fetchPendingPatStatusDidi(true, "")
            val fetchAllDidiNeedsToPostImage = didiDao.fetchAllDidiNeedsToPostImage(true)

            NudgeLogger.d(
                "SettingViewModel",
                "isFourthStepNeedToBeSync -> fetchPATSurveyDidiList -> ${fetchPATSurveyDidiList.json()};;" +
                        "\n\n fetchPendingPatStatusDidi -> ${fetchPendingPatStatusDidi.json()};; " +
                        "\n\n fetchAllDidiNeedsToPostImage -> ${fetchAllDidiNeedsToPostImage.json()};; "
            )

            NudgeLogger.d(
                "SettingViewModel",
                "isFourthStepNeedToBeSync -> fetchPATSurveyDidiList.isEmpty() -> ${fetchPATSurveyDidiList.isEmpty()};;" +
                        "\n\n fetchPendingPatStatusDidi.isEmpty() -> ${fetchPendingPatStatusDidi.isEmpty()};; " +
                        "\n\n fetchAllDidiNeedsToPostImage.isEmpty() -> ${fetchAllDidiNeedsToPostImage.isEmpty()};; "
            )

            if (fetchPATSurveyDidiList.isEmpty()
                && fetchPendingPatStatusDidi.isEmpty()
                && fetchAllDidiNeedsToPostImage.isEmpty()
                && isStepStatusSync(4)
            ) {
                NudgeLogger.d(
                    "SettingViewModel",
                    "isFourthStepNeedToBeSync -> isNeedToBeSync.value = 2"
                )
                withContext(Dispatchers.Main) {
                    isNeedToBeSync.value = 2
                }
            } else {
                NudgeLogger.d(
                    "SettingViewModel",
                    "isFourthStepNeedToBeSync -> isNeedToBeSync.value = 0"
                )
                isNeedToBeSync.value = 0
            }
        }
    }

    fun isFifthStepNeedToBeSync(isNeedToBeSync: MutableState<Int>) {
        stepFifthSyncStatus = isNeedToBeSync
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val getAllNeedToPostVoDidis = didiDao.getAllNeedToPostVoDidis(true, "")
            val fetchPendingVOStatusStatusDidi = didiDao.fetchPendingVOStatusStatusDidi(true, "")
            NudgeLogger.d(
                "SettingViewModel",
                "isFifthStepNeedToBeSync -> getAllNeedToPostVoDidis -> ${getAllNeedToPostVoDidis.json()};;" +
                        "\n\n fetchPendingVOStatusStatusDidi -> ${getAllNeedToPostVoDidis.json()};; "
            )
            NudgeLogger.d(
                "SettingViewModel",
                "isFifthStepNeedToBeSync -> getAllNeedToPostVoDidis.isEmpty() -> ${getAllNeedToPostVoDidis.isEmpty()};;" +
                        "\n\n fetchPendingVOStatusStatusDidi.isEmpty() -> ${getAllNeedToPostVoDidis.isEmpty()};; " +
                        "\n\n isStepStatusSync(5) -> ${isStepStatusSync(5)}" +
                        "\n\n !isFormNeedToBeUpload() -> ${!isFormNeedToBeUpload()}"
            )

            if (getAllNeedToPostVoDidis.isEmpty()
                && fetchPendingVOStatusStatusDidi.isEmpty()
                && isStepStatusSync(5)
                && !isFormNeedToBeUpload()
            ) {
                NudgeLogger.d(
                    "SettingViewModel",
                    "isFifthStepNeedToBeSync -> isNeedToBeSync.value = 2"
                )
                withContext(Dispatchers.Main) {
                    isNeedToBeSync.value = 2
                }
            } else {
                NudgeLogger.d(
                    "SettingViewModel",
                    "isFifthStepNeedToBeSync ->isNeedToBeSync.value = 0"
                )
                isNeedToBeSync.value = 0
            }
        }
    }

    private fun isFormNeedToBeUpload(): Boolean {
        val languageId = prefRepo.getAppLanguageId() ?: 2
        val villageList = villegeListDao.getAllVillages(languageId)
        val isFormUploadedList = mutableListOf<Boolean>()
        for (village in villageList) {
            val isFormUploadedForVillage = prefRepo.getPref(
                PREF_NEED_TO_POST_FORM_C_AND_D_ + prefRepo.getSelectedVillage().id, false
            )
            NudgeLogger.d(
                "SettingViewModel",
                "isFormNeedToBeUpload: village.id -> ${village.id}, isFormUploadedForVillage -> $isFormUploadedForVillage"
            )

            isFormUploadedList.add(isFormUploadedForVillage)
        }
        if (isFormUploadedList.contains(false))
            return false

        return true
    }

    override fun onServerError(error: ErrorModel?) {
        NudgeLogger.e("SettingViewModel", "server error called -> $error")
        NudgeLogger.e("SettingViewModel", "server error called -> ${hitApiStatus.value}")
        when (hitApiStatus.value) {
            1 -> {
                onLogoutError.value = true
            }

            2 -> {
                showLoader.value = false
                syncErrorMessage.value = SYNC_FAILED
                syncPercentage.value = 1f
//                showSyncDialog.value = false
                showAPILoader.value = false
                networkErrorMessage.value = error?.message.toString()
            }

            3 -> {
                networkErrorMessage.value = SYNC_FAILED
                syncBPCPercentage.value = 1f
                showSyncDialog.value = false
                showBPCSyncDialog.value = false
                bpcSyncStatus.value = 1
            }
        }
        /*job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            if (context != null) {
                showCustomToast(context, SYNC_FAILED)
                syncPercentage.value = 100f
                showSyncDialog.value = false
                showLoader.value = false
            }
        }*/
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        NudgeLogger.e(
            "SettingViewModel",
            "server error called -> ${errorModel?.code}, api: ${errorModel?.apiName}"
        )
        when (hitApiStatus.value) {
            1 -> {
                onLogoutError.value = true
            }

            2 -> {
                showLoader.value = false
                syncPercentage.value = 1f
//                showSyncDialog.value = false
                syncErrorMessage.value = SYNC_FAILED
                showAPILoader.value = false
                networkErrorMessage.value = errorModel?.message.toString()
            }

            3 -> {
                networkErrorMessage.value = SYNC_FAILED
                syncBPCPercentage.value = 1f
                showSyncDialog.value = false
                showBPCSyncDialog.value = false
                bpcSyncStatus.value = 1
            }
        }
    }

    fun syncDataOnServer(cxt: Context, syncDialog: MutableState<Boolean>) {
        NudgeLogger.e("SettingViewModel", "syncDataOnServer -> start")
        hitApiStatus.value = 2
        syncErrorMessage.value = ""
        showSyncDialog = syncDialog
        resetPosition()
        if (NudgeCore.isOnline()) {
            syncHelper.syncDataToServer(object :
                NetworkCallbackListener {
                override fun onSuccess() {
                    NudgeLogger.e("SettingViewModel", "syncDataOnServer -> onSuccess")
                    object : CountDownTimer(0, 1000) {
                        override fun onTick(p0: Long) {

                        }

                        override fun onFinish() {
                            networkErrorMessage.value = SYNC_SUCCESSFULL
                            syncPercentage.value = 1f
                            showLoader.value = false
                            val updatedSyncTime = System.currentTimeMillis()
                            lastSyncTime.value = updatedSyncTime
                            prefRepo.savePref(LAST_SYNC_TIME, updatedSyncTime)
                            NudgeLogger.e("SettingViewModel", "syncDataOnServer -> onFinish")
                        }
                    }.start()

                }

                override fun onFailed() {
                    if (currentRetryCount > 0) {
                        try {
                            syncHelper.startSyncTimer(this)
                            currentRetryCount--;

                        } catch (exception: Exception) {
                            syncHelper.startSyncTimer(this)
                            currentRetryCount--
                        }

                        NudgeLogger.e(
                            "SettingViewModel",
                            "syncDataOnServer -> onFailed Retrying current count is ${currentRetryCount}"
                        )
                    } else {
                        networkErrorMessage.value = SYNC_FAILED
                        syncErrorMessage.value = SYNC_FAILED
                        syncPercentage.value = 1f
                        NudgeLogger.e("SettingViewModel", "syncDataOnServer -> onFailed")
                    }
//                        showSyncDialog.value = false
//                        showLoader.value = false

                }
            })
        } else {
            NudgeLogger.e("SettingViewModel", "syncDataOnServer -> isInternetAvailable = false")
        }
        NudgeLogger.e("SettingViewModel", "syncDataOnServer -> end")
    }

    fun showLoaderForTime(time: Long) {
        showAPILoader.value = true
        Timer().schedule(object : TimerTask() {
            override fun run() {
                job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                    withContext(Dispatchers.Main) {
                        showAPILoader.value = false
                    }
                }
            }
        }, time)
    }

    fun syncBPCDataOnServer(
        cxt: Context,
        syncDialog: MutableState<Boolean>,
        syncBPCStatus: MutableState<Int>
    ) {
        NudgeLogger.e("SettingViewModel", "syncBPCDataOnServer -> start")
        syncErrorMessage.value =
            MyApplication.applicationContext().getString(R.string.do_not_close_app_message)
        bpcSyncStatus = syncBPCStatus
//        hitApiStatus.value = 3
        showBPCSyncDialog = syncDialog
        bpcSyncStatus.value = 2
        if (NudgeCore.isOnline()) {
            bpcSyncHelper.syncBPCDataToServer(object :
                NetworkCallbackListener {
                override fun onSuccess() {
                    NudgeLogger.e("SettingViewModel", "syncBPCDataOnServer -> onSuccess")
                    object : CountDownTimer(0, 1000) {
                        override fun onTick(p0: Long) {

                        }

                        override fun onFinish() {
                            networkErrorMessage.value = SYNC_SUCCESSFULL
                            val updatedSyncTime = System.currentTimeMillis()
                            lastSyncTime.value = updatedSyncTime
                            prefRepo.savePref(LAST_SYNC_TIME, updatedSyncTime)
                            syncBPCPercentage.value = 1f
                            bpcSyncStatus.value = 3
                            NudgeLogger.e("SettingViewModel", "syncBPCDataOnServer -> onFinish")

                        }
                    }.start()
                }

                override fun onFailed() {
                    networkErrorMessage.value = SYNC_FAILED
                    hitApiStatus.value = 3
                    syncBPCPercentage.value = 1f
                    bpcSyncStatus.value = 1
                    NudgeLogger.e("SettingViewModel", "syncBPCDataOnServer -> onFailed")

                }
            })
        } else {
            NudgeLogger.e("SettingViewModel", "syncBPCDataOnServer -> isInternetAvailable = false")

        }
        NudgeLogger.e("SettingViewModel", "syncBPCDataOnServer -> end")

    }

    private fun resetPosition() {
        NudgeLogger.e("SettingViewModel", "resetPosition called")
        syncPercentage.value = 0f
        stepOneSyncStatus.value = 0
        stepTwoSyncStatus.value = 0
        stepThreeSyncStatus.value = 0
        stepFourSyncStatus.value = 0
        stepFifthSyncStatus.value = 0
    }

    fun isDataNeedToBeSynced(
        stepOneStatus: MutableState<Int>,
        stepTwoStatus: MutableState<Int>,
        stepThreeStatus: MutableState<Int>,
        stepFourStatus: MutableState<Int>,
        stepFiveStatus: MutableState<Int>
    ) {
        NudgeLogger.e("SettingViewModel", "isDataNeedToBeSynced called")
        stepOneSyncStatus = stepOneStatus
        stepTwoSyncStatus = stepTwoStatus
        stepThreeSyncStatus = stepThreeStatus
        stepFourSyncStatus = stepFourStatus
        stepFifthSyncStatus = stepFiveStatus
        isFirstStepNeedToBeSync(stepOneSyncStatus)
        isSecondStepNeedToBeSync(stepTwoSyncStatus)
        isThirdStepNeedToBeSync(stepThreeSyncStatus)
        isFourthStepNeedToBeSync(stepFourSyncStatus)
        isFifthStepNeedToBeSync(stepFifthSyncStatus)
    }

    fun isBPCDataNeedToBeSynced(
        isBPCDataNeedToBeSynced: MutableState<Boolean>
    ) {
        NudgeLogger.e("SettingViewModel", "isBPCDataNeedToBeSynced called")
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            if (isBPCDidiSynced()/*prefRepo.getPref(PREF_BPC_DIDI_LIST_SYNCED_FOR_VILLAGE_ + prefRepo.getSelectedVillage().id, false)*/ //change this to check for all villages.
                && answerDao.fetchPATSurveyDidiList().isEmpty()
                && didiDao.fetchPendingPatStatusDidi(true, "").isEmpty()
                && didiDao.getAllNeedToPostBPCProcessDidi(true).isEmpty()
                && didiDao.getAllPendingNeedToPostBPCProcessDidi(true, "").isEmpty()
                && isStepStatusSync(6)
                && isBPCScoreSaved()
            ) {
                NudgeLogger.e(
                    "SettingViewModel",
                    "isBPCDataNeedToBeSynced -> isBPCDataNeedToBeSynced.value = false"
                )
                withContext(Dispatchers.Main) {
                    isBPCDataNeedToBeSynced.value = false
                }
            } else {
                NudgeLogger.e(
                    "SettingViewModel",
                    "isBPCDataNeedToBeSynced -> isBPCDataNeedToBeSynced.value = ${bpcSyncStatus.value == 0}"
                )
                isBPCDataNeedToBeSynced.value = bpcSyncStatus.value == 0
            }
        }
    }

    fun isBPCDidiSynced(): Boolean {
        val villageList =
            villegeListDao?.getAllVillages(prefRepo.getAppLanguageId() ?: 2) ?: emptyList()
        if (villageList.isNotEmpty()) {
            villageList.forEach { village ->
                NudgeLogger.d("SettingViewModel", "isBPCDidiSynced: villageId -> ${village.id}")
                val isBpcDidiListSyncedForVillage =
                    prefRepo.getPref(PREF_BPC_DIDI_LIST_SYNCED_FOR_VILLAGE_ + village.id, false)
                Log.d(
                    "SettingViewModel",
                    "isBPCDidiSynced: villageId -> ${village.id} isBpcDidiListSyncedForVillage: $isBpcDidiListSyncedForVillage"
                )
                if (!isBpcDidiListSyncedForVillage) {
                    NudgeLogger.d("SettingViewModel", "return false")
                    return false
                }
            }
            NudgeLogger.d("SettingViewModel", " return true after for loop")
            return true
        }
        NudgeLogger.d("SettingViewModel", " return true after for isNotEmptyCheck")
        return true
    }

    fun performLogout(networkCallbackListener: NetworkCallbackListener) {
        NudgeLogger.e("SettingViewModel", "performLogout called")
        showAPILoader.value = true
        hitApiStatus.value = 1
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val context = MyApplication.applicationContext()
            exportLocalData(context)
            delay(2000)
            try {
                val response = apiInterface.performLogout()
                if (response.status.equals(SUCCESS, true)) {
                    NudgeLogger.e("SettingViewModel", "performLogout SUCCESS")
                    withContext(Dispatchers.Main) {
                        networkCallbackListener.onSuccess()
                    }
                } else {
                    NudgeLogger.e("SettingViewModel", "performLogout FAIL")
                    withContext(Dispatchers.Main) {
                        networkCallbackListener.onFailed()
                    }
                }
            } catch (ex: Exception) {
                onCatchError(ex, ApiType.LOGOUT_API)
            }
        }
    }

    fun clearLocalDB(onPageChange:()->Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            tolaDao.deleteAllTola()
            didiDao.deleteAllDidi()
            lastSelectedTolaDao.deleteAllLastSelectedTola()
            numericAnswerDao.deleteAllNumericAnswers()
            answerDao.deleteAllAnswers()
            questionDao.deleteQuestionTable()
            stepsListDao.deleteAllStepsFromDB()
            villegeListDao.deleteAllVilleges()
            bpcSummaryDao.deleteAllSummary()
            poorDidiListDao.deleteAllDidis()
            prefRepo.savePref(LAST_UPDATE_TIME, 0L)
            clearEventWriterFileName()
            withContext(Dispatchers.Main) {
                onPageChange()
            }
        }
    }


    private fun clearSharedPreference() {
        val languageId = prefRepo.getAppLanguageId()
        val language = prefRepo.getAppLanguage()
        prefRepo.clearSharedPreference()
        prefRepo.saveAppLanguage(language)
        prefRepo.saveAppLanguageId(languageId)
    }

    fun clearAccessToken() {
        prefRepo.saveAccessToken("")
        clearEventWriterFileName()
        prefRepo.setPreviousUserMobile(mobileNumber = prefRepo.getMobileNumber())
        prefRepo.saveSettingOpenFrom(0)

    }

    private fun clearEventWriterFileName() {
        CoreSharedPrefs.getInstance(NudgeCore.getAppContext()).setBackupFileName(
            getDefaultBackUpFileName(
                prefRepo.getMobileNumber() ?: ""
            )
        )
        CoreSharedPrefs.getInstance(NudgeCore.getAppContext()).setImageBackupFileName(
            getDefaultImageBackUpFileName(
                prefRepo.getMobileNumber() ?: ""
            )
        )
        CoreSharedPrefs.getInstance(NudgeCore.getAppContext()).setFileExported(false)
    }

    fun buildAndShareLogs() {
        NudgeLogger.d("SettingViewModel", "buildAndShareLogs---------------")
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val context = MyApplication.applicationContext()
            exportLocalData(context)
            LogWriter.buildSupportLogAndShare()
        }
    }

    suspend fun exportLocalData(context: Context) {
        exportHelper.exportAllData(context)
    }

    fun syncAllPending(networkSpeed: NetworkSpeed) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            try {
                DeviceBandwidthSampler.getInstance().startSampling()
                // Open a stream to download the image from our URL.
                val connection =
                    URL("https://sarathi.lokos.in/write-api/file/view?fileName=25882_shibani%20Nama%20_CRP_2023-12-10.png").openConnection()
                connection.setUseCaches(false)
                connection.connect()
                val input = connection.getInputStream()
                try {
                    val buffer = ByteArray(1024)

                    // Do some busy waiting while the stream is open.
                    while (input.read(buffer) != -1) {
                    }
                } finally {
                    input.close()
                    DeviceBandwidthSampler.getInstance().stopSampling()

                }
            } catch (e: IOException) {
                Log.e("TAG", "Error while downloading image.")
            }
            Log.d("D", ConnectionMonitor.DoesNetworkHaveInternet.getNetworkStrength().toString())
            NudgeCore.getEventObserver()?.syncPendingEvent(NudgeCore.getAppContext(), networkSpeed)
        }
    }

    fun compressEventData(title: String) {

        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                showExportLoader.value = true
                val compression = ZipFileCompression()
                val fileUri = compression.compressBackupFiles(
                    NudgeCore.getAppContext(),
                    getFormPdfAndLogUri(),
                    prefRepo.getMobileNumber(),
                    userName = prefRepo.getPref(PREF_KEY_NAME, BLANK_STRING) ?: BLANK_STRING,
                    moduleName = BLANK_STRING
                )

                val imageUri = compression.compressBackupImages(
                    NudgeCore.getAppContext(),
                    prefRepo.getMobileNumber(),
                    userName = prefRepo.getPref(PREF_KEY_NAME, BLANK_STRING) ?: BLANK_STRING,
                )

                openShareSheet(imageUri, fileUri, title)
                CoreSharedPrefs.getInstance(NudgeCore.getAppContext()).setFileExported(true)
                showExportLoader.value = false


            } catch (exception: Exception) {
                NudgeLogger.e("Compression", exception.message ?: BLANK_STRING)
                exception.printStackTrace()
                showExportLoader.value = false

            }

        }
    }

    private suspend fun getFormPdfAndLogUri(): List<Pair<String, Uri?>> {
        val uris = ArrayList<Pair<String, Uri?>>()

        try {

            if (!prefRepo.isUserBPC()) {
                val selectedVillageId = prefRepo.getSelectedVillage().id
                val casteList = casteListDao.getAllCasteForLanguage(
                    prefRepo.getAppLanguageId() ?: 2
                )
                var didiList: List<DidiEntity> =
                    didiDao.getAllDidisForVillage(selectedVillageId)

                val isFormAGenerated = generateFormA(casteList, selectedVillageId, didiList)
                addFormToUriList(isFormAGenerated, selectedVillageId, FORM_A_PDF_NAME, uris)

                val isFormBGenerated = generateFormB(casteList, selectedVillageId, didiList)
                addFormToUriList(isFormBGenerated, selectedVillageId, FORM_B_PDF_NAME, uris)

                val isFormCGenerated = generateFormc(casteList, selectedVillageId, didiList)
                addFormToUriList(isFormCGenerated, selectedVillageId, FORM_C_PDF_NAME, uris)

            }
        } catch (exception: Exception) {
            NudgeLogger.e("GeneratingForm", exception.message ?: "")
        }
        try {
            addLogFileIntoUriList(uris)

        } catch (exception: Exception) {
            NudgeLogger.e("GeneratingForm", exception.message ?: "")
        }
        return uris
    }

    private suspend fun addLogFileIntoUriList(uris: ArrayList<Pair<String, Uri?>>) {
        val logFile = getLogFile()
        if (logFile != null) {
            uris.add(
                Pair(
                    logFile.name, uriFromFile(
                        NudgeCore.getAppContext(),
                        logFile
                    )
                )
            )
        }
    }

    private fun addFormToUriList(
        isFormGenerated: Boolean,
        selectedVillageId: Int,
        formName: String,
        uris: ArrayList<Pair<String, Uri?>>
    ) {
        if (isFormGenerated) {
            val formFile = PdfUtils.getPdfPath(
                context = NudgeCore.getAppContext(),
                formName = formName,
                selectedVillageId
            )

            uris.add(
                Pair(
                    formFile.name, uriFromFile(
                        NudgeCore.getAppContext(),
                        formFile
                    )
                )
            )
        }
    }

    private suspend fun generateFormc(
        casteList: List<CasteEntity>,
        selectedVillageId: Int,
        didiList: List<DidiEntity>
    ) =
        PdfUtils.getFormCPdf(
            NudgeCore.getAppContext(), villageEntity = prefRepo.getSelectedVillage(),
            didiDetailList = didiList.filter { it.forVoEndorsement == 1 && it.section2Status == PatSurveyStatus.COMPLETED.ordinal && it.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal },
            casteList = casteList,
            completionDate = changeMilliDateToDate(
                prefRepo.getPref(
                    PREF_VO_ENDORSEMENT_COMPLETION_DATE_ + selectedVillageId,
                    0L
                )
            ) ?: BLANK_STRING
        )


    private suspend fun generateFormB(
        casteList: List<CasteEntity>,
        selectedVillageId: Int,
        didiList: List<DidiEntity>
    ) =
        PdfUtils.getFormBPdf(
            NudgeCore.getAppContext(), villageEntity = prefRepo.getSelectedVillage(),
            didiDetailList = didiList.filter { it.forVoEndorsement == 1 && it.section2Status == PatSurveyStatus.COMPLETED.ordinal && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal && !it.patEdit },
            casteList = casteList,
            completionDate = changeMilliDateToDate(
                prefRepo.getPref(
                    PREF_PAT_COMPLETION_DATE_ + selectedVillageId,
                    0L
                )
            ) ?: BLANK_STRING
        )


    private suspend fun generateFormA(
        casteList: List<CasteEntity>,
        selectedVillageId: Int,
        didiList: List<DidiEntity>
    ) = PdfUtils.getFormAPdf(
        NudgeCore.getAppContext(),
        villageEntity = prefRepo.getSelectedVillage(),
        casteList = casteList,
        didiDetailList = didiList,
        completionDate = changeMilliDateToDate(
            prefRepo.getPref(
                PREF_WEALTH_RANKING_COMPLETION_DATE_ + selectedVillageId, 0L
            )
        ) ?: BLANK_STRING
    )


    private fun openShareSheet(imageUri: Uri1?, fileUri: Uri1?, title: String) {
        val fileUris = listOf(fileUri, imageUri)
        val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        shareIntent.setType(ZIP_MIME_TYPE)
        shareIntent.putExtra(Intent.EXTRA_STREAM, ArrayList(fileUris))
        shareIntent.putExtra(Intent.EXTRA_TITLE, title)
        val chooserIntent = Intent.createChooser(shareIntent, title)
        NudgeCore.startExternalApp(chooserIntent)
    }

    fun isSyncEnabled(): Boolean {
        return prefRepo.getISSyncEnabled()

    }

    fun clearSettingOpenFrom() {
        //    prefRepo.saveSettingOpenFrom(0)
    }

    fun exportDbAndImages(onExportSuccess: () -> Unit) {
        val userUniqueId = "${prefRepo.getUserId()}_${prefRepo.getMobileNumber()}"
        exportOldData(
            appContext = NudgeCore.getAppContext(),
            applicationID = BuildConfig.APPLICATION_ID,
            mobileNo = userUniqueId,
            databaseName = NUDGE_DATABASE,
            userName = BLANK_STRING,
            moduleName = BLANK_STRING
        ) {
            onExportSuccess()
        }
    }

    fun regenerateEventFile(title: String) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            settingRepository.regenerateAllEvent(CoreSharedPrefs.getInstance(NudgeCore.getAppContext()))
            compressEventData(title)
        }
    }


}
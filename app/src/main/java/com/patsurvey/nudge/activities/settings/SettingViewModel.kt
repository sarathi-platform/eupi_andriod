package com.patsurvey.nudge.activities.settings

import android.content.Context
import android.os.CountDownTimer
import android.os.Environment
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.MyApplication
import com.patsurvey.nudge.R
import com.patsurvey.nudge.SyncBPCDataOnServer
import com.patsurvey.nudge.SyncHelper
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.BpcNonSelectedDidiDao
import com.patsurvey.nudge.database.dao.BpcSelectedDidiDao
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
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.dataModel.SettingOptionModel
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.network.isInternetAvailable
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.FORM_A_PDF_NAME
import com.patsurvey.nudge.utils.FORM_B_PDF_NAME
import com.patsurvey.nudge.utils.FORM_C_PDF_NAME
import com.patsurvey.nudge.utils.LAST_SYNC_TIME
import com.patsurvey.nudge.utils.LogWriter
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_BPC_DIDI_LIST_SYNCED_FOR_VILLAGE_
import com.patsurvey.nudge.utils.PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_
import com.patsurvey.nudge.utils.PREF_NEED_TO_POST_FORM_C_AND_D_
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.SYNC_FAILED
import com.patsurvey.nudge.utils.SYNC_SUCCESSFULL
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.TolaStatus
import com.patsurvey.nudge.utils.WealthRank
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

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
    val bpcSelectedDidiDao: BpcSelectedDidiDao,
    val bpcNonSelectedDidiDao: BpcNonSelectedDidiDao,
    val bpcSummaryDao: BpcSummaryDao,
    val poorDidiListDao: PoorDidiListDao

):BaseViewModel() {
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
    var syncErrorMessage = mutableStateOf("")
    private val _optionList = MutableStateFlow(listOf<SettingOptionModel>())
    val optionList: StateFlow<List<SettingOptionModel>> get() = _optionList
    val showLoader = mutableStateOf(false)
    val showAPILoader = mutableStateOf(false)
    var showSyncDialog = mutableStateOf(false)
    var showBPCSyncDialog = mutableStateOf(false)

    val lastSyncTime = mutableStateOf(prefRepo.getPref(LAST_SYNC_TIME, 0L))

    var syncHelper = SyncHelper(this@SettingViewModel,prefRepo,apiInterface,tolaDao,stepsListDao,exceptionHandler, villegeListDao, didiDao,job,showLoader,syncPercentage,answerDao,
        numericAnswerDao,
        questionDao)
    var bpcSyncHelper = SyncBPCDataOnServer(this@SettingViewModel,prefRepo,apiInterface,exceptionHandler,job,bpcSelectedDidiDao,didiDao,stepsListDao, questionDao,syncBPCPercentage, answerDao,numericAnswerDao,villegeListDao)
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
                    if (poorDidiListDao.getAllPoorDidisForVillage(villageId = villageId).any { it.wealth_ranking == WealthRank.POOR.rank && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal && !it.rankingEdit }) {
                        withContext(Dispatchers.Main) {
                            formAAvailabe.value = true
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            formAAvailabe.value = false
                        }
                    }
                } else {
                    if (didiDao.getAllDidisForVillage(villageId = villageId).any { it.wealth_ranking == WealthRank.POOR.rank && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal && !it.rankingEdit }
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
        /*val formAFilePath = File("${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath}/${FORM_A_PDF_NAME}_${villageId}.pdf")
        formAAvailabe.value = formAFilePath.isFile && formAFilePath.exists()*/
        /*job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList = stepsListDao.getAllStepsForVillage(villageId)
            val filteredStepList = stepList.filter { it.name.equals("Participatory Wealth Ranking", true) }
            if (filteredStepList[0] != null) {
                formAAvailabe.value = filteredStepList[0].isComplete == StepStatus.COMPLETED.ordinal
            } else {
                formAAvailabe.value = false
            }
        }*/
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
                    if (poorDidiListDao.getAllPoorDidisForVillage(villageId = villageId).any { it.forVoEndorsement == 1 && !it.patEdit }
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
                    if (didiDao.getAllDidisForVillage(villageId = villageId).any { it.forVoEndorsement == 1 && !it.patEdit }
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

        /*val formBFilePath = File("${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath}/${FORM_B_PDF_NAME}_${villageId}.pdf")
        formBAvailabe.value = formBFilePath.isFile && formBFilePath.exists()*/
        /*job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList = stepsListDao.getAllStepsForVillage(villageId)
            val filteredStepList = stepList.filter { it.name.equals("Pat Survey", true) }
            if (filteredStepList[0] != null) {
                formBAvailabe.value = filteredStepList[0].isComplete == StepStatus.COMPLETED.ordinal
            } else {
                formBAvailabe.value = false
            }
        }*/
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
                    formCAvailabe.value = filteredStepList[0].isComplete == StepStatus.COMPLETED.ordinal
                } else {
                    formCAvailabe.value = false
                }
            }
        }

        /*job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList = stepsListDao.getAllStepsForVillage(villageId)
            val filteredStepList = stepList.filter { it.name.equals("VO Endorsement", true) }
            if (filteredStepList[0] != null) {
                formCAvailabe.value = filteredStepList[0].isComplete == StepStatus.COMPLETED.ordinal
            } else {
                formCAvailabe.value = false
            }
        }*/
    }

    fun createSettingMenu(list:ArrayList<SettingOptionModel>){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.Main){
                _optionList.value = list
            }
        }
    }


    fun isFirstStepNeedToBeSync(isNeedToBeSync : MutableState<Int>) {
        stepOneSyncStatus = isNeedToBeSync
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            if (tolaDao.fetchTolaNeedToPost(true, "",0).isEmpty()
                && tolaDao.fetchPendingTola(true, "").isEmpty()
                && tolaDao.fetchAllTolaNeedToDelete(TolaStatus.TOLA_DELETED.ordinal).isEmpty()
                && tolaDao.fetchAllPendingTolaNeedToDelete(TolaStatus.TOLA_DELETED.ordinal,"").isEmpty()
                && tolaDao.fetchAllTolaNeedToUpdate(true,"",0).isEmpty()
                && tolaDao.fetchAllPendingTolaNeedToUpdate(true,"").isEmpty()
                && isStepStatusSync(1)) {
                NudgeLogger.d("SettingViewModel", "isFirstStepNeedToBeSync -> isNeedToBeSync.value = 2")
                withContext(Dispatchers.Main) {
                    isNeedToBeSync.value = 2
                }
            } else {
                NudgeLogger.d("SettingViewModel", "isFirstStepNeedToBeSync -> isNeedToBeSync.value = 0")
                isNeedToBeSync.value = 0
            }
        }
    }

    private fun isStepStatusSync(orderNumber : Int) : Boolean{
        val stepList = stepsListDao.getAllStepsWithOrderNumber(orderNumber)
        var isSync = true
        for (step in stepList){
            if(step.needToPost)
                isSync =  false
        }
        NudgeLogger.e("SettingViewModel", "step -> $orderNumber, status -> $isSync ")
        return isSync
    }

    fun isSecondStepNeedToBeSync(isNeedToBeSync : MutableState<Int>) {
        stepTwoSyncStatus = isNeedToBeSync
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
        if(didiDao.fetchAllDidiNeedToPost(true,"").isEmpty()
            && didiDao.fetchPendingDidi(true,"").isEmpty()
            && didiDao.fetchAllDidiNeedToDelete(DidiStatus.DIID_DELETED.ordinal).isEmpty()
            && didiDao.fetchAllPendingDidiNeedToDelete(DidiStatus.DIID_DELETED.ordinal,"",0).isEmpty()
            && didiDao.fetchAllDidiNeedToUpdate(true,"",0).isEmpty()
            && didiDao.fetchAllPendingDidiNeedToUpdate(true,"",0).isEmpty()
            && isStepStatusSync(2)) {
            NudgeLogger.d("SettingViewModel", "isSecondStepNeedToBeSync -> isNeedToBeSync.value = 2")
                withContext(Dispatchers.Main) {
                    isNeedToBeSync.value = 2
                }
            } else {
                NudgeLogger.d("SettingViewModel", "isSecondStepNeedToBeSync -> isNeedToBeSync.value = 0")
                isNeedToBeSync.value = 0
            }
        }
    }

    private fun isBPCScoreSaved() : Boolean{
        val villageList = villegeListDao.getAllVillages(prefRepo.getAppLanguageId()?:0)
        for(village in villageList) {
            val isBpcScoreSaved = prefRepo.getPref(
                PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ + village.id,
                false

            )
            if(isBpcScoreSaved)
                return isBpcScoreSaved
            NudgeLogger.d("SettingViewModel", "isBPCScoreSaved -> $isBpcScoreSaved")
        }
        return false
    }

    fun isThirdStepNeedToBeSync(isNeedToBeSync : MutableState<Int>){
        stepThreeSyncStatus = isNeedToBeSync
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            if (didiDao.getAllNeedToPostDidiRanking(true).isEmpty()
                && didiDao.fetchPendingWealthStatusDidi(true, "").isEmpty()
                && isStepStatusSync(3)
            ) {
                NudgeLogger.d("SettingViewModel", "isThirdStepNeedToBeSync -> isNeedToBeSync.value = 2")
                withContext(Dispatchers.Main) {
                    isNeedToBeSync.value = 2
                }
            } else {
                NudgeLogger.d("SettingViewModel", "isThirdStepNeedToBeSync -> isNeedToBeSync.value = 0")
                isNeedToBeSync.value = 0
            }
        }
    }

    fun isFourthStepNeedToBeSync(isNeedToBeSync : MutableState<Int>) {
        stepFourSyncStatus = isNeedToBeSync
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            if (answerDao.fetchPATSurveyDidiList().isEmpty()
                && didiDao.fetchPendingPatStatusDidi(true, "").isEmpty()
                && didiDao.fetchAllDidiNeedsToPostImage(true).isEmpty()
                && isStepStatusSync(4)
            ) {
                NudgeLogger.d("SettingViewModel", "isFourthStepNeedToBeSync -> isNeedToBeSync.value = 2")
                withContext(Dispatchers.Main) {
                    isNeedToBeSync.value = 2
                }
            } else {
                NudgeLogger.d("SettingViewModel", "isFourthStepNeedToBeSync -> isNeedToBeSync.value = 0")
                isNeedToBeSync.value = 0
            }
        }
    }

    fun isFifthStepNeedToBeSync(isNeedToBeSync : MutableState<Int>) {
        stepFifthSyncStatus = isNeedToBeSync
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            if (didiDao.getAllNeedToPostVoDidi(
                    needsToPostVo  = true,
                    villageId = prefRepo.getSelectedVillage().id
                ).isEmpty()
                && isStepStatusSync(5)
                && !isFormNeedToBeUpload()
            ) {
                NudgeLogger.d("SettingViewModel", "isFifthStepNeedToBeSync -> isNeedToBeSync.value = 2")
                withContext(Dispatchers.Main) {
                    isNeedToBeSync.value = 2
                }
            } else {
                NudgeLogger.d("SettingViewModel", "isFifthStepNeedToBeSync ->isNeedToBeSync.value = 0")
                isNeedToBeSync.value = 0
            }
        }
    }

    private fun isFormNeedToBeUpload(): Boolean {
        val languageId = prefRepo.getAppLanguageId()?:2
        val villageList = villegeListDao.getAllVillages(languageId)
        for(village in villageList){
            if(prefRepo.getPref(
                PREF_NEED_TO_POST_FORM_C_AND_D_ + prefRepo.getSelectedVillage().id,false))
              return true
        }
        return false
    }

    override fun onServerError(error: ErrorModel?) {
        NudgeLogger.e("SettingViewModel","server error called -> $error")
        NudgeLogger.e("SettingViewModel","server error called -> ${hitApiStatus.value}")
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
        NudgeLogger.e("SettingViewModel","server error called -> ${errorModel?.code}, api: ${errorModel?.apiName}")
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

/*    fun getStepOneSize(stepOneSize : MutableState<String>) {
        syncHelper.getStepOneDataSizeInSync(stepOneSize)
    }

    fun getStepTwoSize(stepTwoSize : MutableState<String>) {
        syncHelper.getStepTwoDataSizeInSync(stepTwoSize)
    }

    fun getStepThreeSize(stepThreeSize : MutableState<String>) {
        syncHelper.getStepThreeDataSizeInSync(stepThreeSize)
    }

    fun getStepFourSize(stepFourSize : MutableState<String>) {
        syncHelper.getStepFourDataSizeInSync(stepFourSize)
    }

    fun getStepFiveSize(stepFiveSize : MutableState<String>) {
        syncHelper.getStepFiveDataSizeInSync(stepFiveSize)
    }*/

    fun syncDataOnServer(cxt: Context,syncDialog : MutableState<Boolean>) {
        NudgeLogger.e("SettingViewModel","syncDataOnServer -> start")
        hitApiStatus.value = 2
        syncErrorMessage.value = ""
        showSyncDialog = syncDialog
        resetPosition()
        if(isInternetAvailable(cxt)){
            syncHelper.syncDataToServer(object :
                NetworkCallbackListener {
                    override fun onSuccess() {
                        NudgeLogger.e("SettingViewModel","syncDataOnServer -> onSuccess")
                        object: CountDownTimer(0, 1000){
                            override fun onTick(p0: Long) {

                            }
                            override fun onFinish() {
                                networkErrorMessage.value = SYNC_SUCCESSFULL
                                syncPercentage.value = 1f
                                showLoader.value = false
                                val updatedSyncTime = System.currentTimeMillis()
                                lastSyncTime.value = updatedSyncTime
                                prefRepo.savePref(LAST_SYNC_TIME, updatedSyncTime)
                                NudgeLogger.e("SettingViewModel","syncDataOnServer -> onFinish")
                            }
                        }.start()

                    }

                    override fun onFailed() {
                        networkErrorMessage.value = SYNC_FAILED
                        syncErrorMessage.value = SYNC_FAILED
                        syncPercentage.value = 1f
//                        showSyncDialog.value = false
//                        showLoader.value = false
                        NudgeLogger.e("SettingViewModel","syncDataOnServer -> onFailed")
                    }
            })
        } else {
            NudgeLogger.e("SettingViewModel","syncDataOnServer -> isInternetAvailable = false")
        }
        NudgeLogger.e("SettingViewModel","syncDataOnServer -> end")
    }

    fun showLoaderForTime(time : Long){
        showAPILoader.value = true
        Timer().schedule(object : TimerTask(){
            override fun run() {
                job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                    withContext(Dispatchers.Main) {
                        showAPILoader.value = false
                    }
                }
            }
        },time)
    }

    fun syncBPCDataOnServer(cxt: Context,syncDialog : MutableState<Boolean>,syncBPCStatus : MutableState<Int>) {
        NudgeLogger.e("SettingViewModel","syncBPCDataOnServer -> start")
        syncErrorMessage.value = MyApplication.applicationContext().getString(R.string.do_not_close_app_message)
        bpcSyncStatus = syncBPCStatus
//        hitApiStatus.value = 3
        showBPCSyncDialog = syncDialog
        bpcSyncStatus.value = 2
        if(isInternetAvailable(cxt)){
            bpcSyncHelper.syncBPCDataToServer(object :
                NetworkCallbackListener {
                override fun onSuccess() {
                    NudgeLogger.e("SettingViewModel","syncBPCDataOnServer -> onSuccess")
                    object: CountDownTimer(0, 1000){
                        override fun onTick(p0: Long) {

                        }
                        override fun onFinish() {
                            networkErrorMessage.value = SYNC_SUCCESSFULL
                            val updatedSyncTime = System.currentTimeMillis()
                            lastSyncTime.value = updatedSyncTime
                            prefRepo.savePref(LAST_SYNC_TIME, updatedSyncTime)
                            syncBPCPercentage.value = 1f
                            bpcSyncStatus.value = 3
                            NudgeLogger.e("SettingViewModel","syncBPCDataOnServer -> onFinish")

                        }
                    }.start()
                }

                override fun onFailed() {
                    networkErrorMessage.value = SYNC_FAILED
                    hitApiStatus.value = 3
                    syncBPCPercentage.value = 1f
                    bpcSyncStatus.value = 1
                    NudgeLogger.e("SettingViewModel","syncBPCDataOnServer -> onFailed")

                }
            })
        } else {
            NudgeLogger.e("SettingViewModel","syncBPCDataOnServer -> isInternetAvailable = false")

        }
        NudgeLogger.e("SettingViewModel","syncBPCDataOnServer -> end")

    }

    private fun resetPosition() {
        NudgeLogger.e("SettingViewModel","resetPosition called")
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
        NudgeLogger.e("SettingViewModel","isDataNeedToBeSynced called")
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
        NudgeLogger.e("SettingViewModel","isBPCDataNeedToBeSynced called")
//        bpcSyncStatus = localBpcSyncStatus
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
//            val didiIDList =
            if(prefRepo.getPref(PREF_BPC_DIDI_LIST_SYNCED_FOR_VILLAGE_ + prefRepo.getSelectedVillage().id, false)
                && answerDao.fetchPATSurveyDidiList(prefRepo.getSelectedVillage().id).isEmpty()
                && didiDao.fetchPendingPatStatusDidi(true,"").isEmpty()
                && didiDao.getAllNeedToPostBPCProcessDidi(true).isEmpty()
                && didiDao.getAllPendingNeedToPostBPCProcessDidi(true,"").isEmpty()
                && isStepStatusSync(6)
                && isBPCScoreSaved()){
                NudgeLogger.e("SettingViewModel","isBPCDataNeedToBeSynced -> isBPCDataNeedToBeSynced.value = false")
                withContext(Dispatchers.Main) {
                    isBPCDataNeedToBeSynced.value = false
                }
            } else {
                NudgeLogger.e("SettingViewModel","isBPCDataNeedToBeSynced -> isBPCDataNeedToBeSynced.value = ${bpcSyncStatus.value == 0}")
                isBPCDataNeedToBeSynced.value = bpcSyncStatus.value == 0
            }
        }
    }

    fun performLogout(networkCallbackListener: NetworkCallbackListener){
        NudgeLogger.e("SettingViewModel","performLogout called")
        showAPILoader.value = true
        hitApiStatus.value = 1
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            delay(1000)
            try {
                val response = apiInterface.performLogout()
                if (response.status.equals(SUCCESS, true)) {
                    NudgeLogger.e("SettingViewModel","performLogout SUCCESS")
                    withContext(Dispatchers.Main) {
                        networkCallbackListener.onSuccess()
                    }
                } else {
                    NudgeLogger.e("SettingViewModel","performLogout FAIL")
                    withContext(Dispatchers.Main) {
                        networkCallbackListener.onFailed()
                    }
                }
            } catch (ex: Exception) {
                onCatchError(ex, ApiType.LOGOUT_API)
            }
        }
    }

    fun clearLocalDB(context: Context, logout: MutableState<Boolean>) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            casteListDao.deleteCasteTable()
            tolaDao.deleteAllTola()
            didiDao.deleteAllDidi()
            lastSelectedTolaDao.deleteAllLastSelectedTola()
            numericAnswerDao.deleteAllNumericAnswers()
            answerDao.deleteAllAnswers()
            questionDao.deleteQuestionTable()
            stepsListDao.deleteAllStepsFromDB()
            userDao.deleteAllUserDetail()
            villegeListDao.deleteAllVilleges()
            bpcSelectedDidiDao.deleteAllDidis()
            bpcNonSelectedDidiDao.deleteAllDidis()
            bpcSummaryDao.deleteAllSummary()
            poorDidiListDao.deleteAllDidis()
            clearSharedPreference()
            //cleared cache in case of logout
//            context.cacheDir.deleteRecursively()
            withContext(Dispatchers.Main){
                showAPILoader.value = false
                logout.value = true
                onLogoutError.value = false
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

    fun buildAndShareLogs() {
        NudgeLogger.d("SettingViewModel", "buildAndShareLogs---------------")
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            LogWriter.buildSupportLogAndShare()
        }
    }
}
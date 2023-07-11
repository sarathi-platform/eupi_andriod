package com.patsurvey.nudge.activities.settings

import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.LAST_SYNC_TIME
import com.patsurvey.nudge.utils.LogWriter
import com.patsurvey.nudge.utils.PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.SYNC_FAILED
import com.patsurvey.nudge.utils.SYNC_SUCCESSFULL
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.TolaStatus
import com.patsurvey.nudge.utils.getStepStatusFromOrdinal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    var bpcSyncHelper = SyncBPCDataOnServer(this@SettingViewModel,prefRepo,apiInterface,exceptionHandler,job,bpcSelectedDidiDao,didiDao,stepsListDao, questionDao,syncBPCPercentage, answerDao,numericAnswerDao)
    fun isFormAAvailableForVillage(villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList = stepsListDao.getAllStepsForVillage(villageId)
            val filteredStepList = stepList.filter { it.name.equals("Participatory Wealth Ranking", true) }
            if (filteredStepList[0] != null) {
                formAAvailabe.value = filteredStepList[0].isComplete == StepStatus.COMPLETED.ordinal
            } else {
                formAAvailabe.value = false
            }
        }
    }
    fun isFormBAvailableForVillage(villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList = stepsListDao.getAllStepsForVillage(villageId)
            val filteredStepList = stepList.filter { it.name.equals("Pat Survey", true) }
            if (filteredStepList[0] != null) {
                formBAvailabe.value = filteredStepList[0].isComplete == StepStatus.COMPLETED.ordinal
            } else {
                formBAvailabe.value = false
            }
        }
    }

    fun isFormCAvailableForVillage(villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList = stepsListDao.getAllStepsForVillage(villageId)
            val filteredStepList = stepList.filter { it.name.equals("VO Endorsement", true) }
            if (filteredStepList[0] != null) {
                formCAvailabe.value = filteredStepList[0].isComplete == StepStatus.COMPLETED.ordinal
            } else {
                formCAvailabe.value = false
            }
        }
    }

    fun createSettingMenu(list:ArrayList<SettingOptionModel>){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO){
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
                && isStatusStepStatusSync(0)) {
                withContext(Dispatchers.Main) {
                    isNeedToBeSync.value = 2
                }
            } else
                isNeedToBeSync.value = 0
        }
    }

    fun isStatusStepStatusSync(step : Int) : Boolean{
        Log.e("step","->$step")
        val villageId = prefRepo.getSelectedVillage().id
        val stepList = stepsListDao.getAllStepsForVillage(villageId)
        Log.e("status","-> "+stepList[step].status)
        Log.e("iscomplete","-> "+getStepStatusFromOrdinal(stepList[step].isComplete))
        return !stepList[step].needToPost
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
            && isStatusStepStatusSync(1)) {
                withContext(Dispatchers.Main) {
                    isNeedToBeSync.value = 2
                }
            } else
                isNeedToBeSync.value = 0
        }
    }

    fun isBPCScoreSaved() : Boolean{
        return prefRepo.getPref(PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ + prefRepo.getSelectedVillage().id,false)
    }

    fun isThirdStepNeedToBeSync(isNeedToBeSync : MutableState<Int>){
        stepThreeSyncStatus = isNeedToBeSync
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            if (didiDao.getAllNeedToPostDidiRanking(true).isEmpty()
                && didiDao.fetchPendingWealthStatusDidi(true, "").isEmpty()
                && isStatusStepStatusSync(2)
            ) {
                withContext(Dispatchers.Main) {
                    isNeedToBeSync.value = 2
                }
            } else
                isNeedToBeSync.value = 0
        }
    }

    fun isFourthStepNeedToBeSync(isNeedToBeSync : MutableState<Int>) {
        stepFourSyncStatus = isNeedToBeSync
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            if (answerDao.fetchPATSurveyDidiList().isEmpty()
                && didiDao.fetchPendingPatStatusDidi(true, "").isEmpty()
                && isStatusStepStatusSync(3)
            ) {
                withContext(Dispatchers.Main) {
                    isNeedToBeSync.value = 2
                }
            } else
                isNeedToBeSync.value = 0
        }
    }

    fun isFifthStepNeedToBeSync(isNeedToBeSync : MutableState<Int>) {
        stepFifthSyncStatus = isNeedToBeSync
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            if (didiDao.getAllNeedToPostPATDidi(
                    needsToPostPAT = true,
                    villageId = prefRepo.getSelectedVillage().id
                ).isEmpty()
                && didiDao.getAllNeedToPostPATDidi(
                    needsToPostPAT = true,
                    villageId = prefRepo.getSelectedVillage().id
                ).isEmpty()
                && isStatusStepStatusSync(4)
            ) {
                withContext(Dispatchers.Main) {
                    isNeedToBeSync.value = 2
                }
            } else
                isNeedToBeSync.value = 0
        }
    }

    override fun onServerError(error: ErrorModel?) {
        Log.e("server error","called, $error")
        when (hitApiStatus.value) {
            1 -> onLogoutError.value = true
            2 -> {
                showLoader.value = false
                syncPercentage.value = 1f
                showSyncDialog.value = false
                showAPILoader.value = false
                networkErrorMessage.value = error?.message.toString()
            }
            3 -> {
                networkErrorMessage.value = SYNC_FAILED
                syncBPCPercentage.value = 1f
                showSyncDialog.value = false
                showBPCSyncDialog.value = false
                bpcSyncStatus.value = 3
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
        Log.e("server error","called, ${errorModel?.code}, api: ${errorModel?.apiName}")
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
        hitApiStatus.value = 2
        showSyncDialog = syncDialog
        resetPosition()
        if(isInternetAvailable(cxt)){
            syncHelper.syncDataToServer(object :
                NetworkCallbackListener {
                    override fun onSuccess() {
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
                            }
                        }.start()

                    }

                    override fun onFailed() {
                        networkErrorMessage.value = SYNC_FAILED
                        syncPercentage.value = 1f
                        showSyncDialog.value = false
                        showLoader.value = false
                    }
            })
        }
    }

    fun syncBPCDataOnServer(cxt: Context,syncDialog : MutableState<Boolean>,syncBPCStatus : MutableState<Int>) {
        bpcSyncStatus = syncBPCStatus
        hitApiStatus.value = 3
        showBPCSyncDialog = syncDialog
        bpcSyncStatus.value = 2
        if(isInternetAvailable(cxt)){
            bpcSyncHelper.syncBPCDataToServer(object :
                NetworkCallbackListener {
                override fun onSuccess() {
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
                        }
                    }.start()
                }

                override fun onFailed() {
                    networkErrorMessage.value = SYNC_FAILED
                    syncBPCPercentage.value = 1f
                    bpcSyncStatus.value = 3
                }
            })
        }
    }

    private fun resetPosition() {
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
//        bpcSyncStatus = localBpcSyncStatus
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
//            val didiIDList =
            if(!bpcSyncHelper.isBPCDidiNeedToBeReplaced()
                && answerDao.fetchPATSurveyDidiList(prefRepo.getSelectedVillage().id).isEmpty()
                && didiDao.fetchPendingPatStatusDidi(true,"").isEmpty()
                && didiDao.getAllNeedToPostBPCProcessDidi(true, prefRepo.getSelectedVillage().id).isEmpty()
                && didiDao.getAllPendingNeedToPostBPCProcessDidi(true,prefRepo.getSelectedVillage().id,"").isEmpty()
                && isStatusStepStatusSync(5)
                && isBPCScoreSaved()){
                withContext(Dispatchers.Main) {
                    isBPCDataNeedToBeSynced.value = false
                }
            } else {
                isBPCDataNeedToBeSynced.value = bpcSyncStatus.value == 0
            }
        }
    }

    fun performLogout(networkCallbackListener: NetworkCallbackListener){
        showAPILoader.value = true
        hitApiStatus.value = 1
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = apiInterface.performLogout()
            if (response.status.equals(SUCCESS, true)) {
                withContext(Dispatchers.Main) {
                    networkCallbackListener.onSuccess()
                }
            } else {
                withContext(Dispatchers.Main) {
                    networkCallbackListener.onFailed()
                }
            }
        }
    }

    fun clearLocalDB(context: Context, logout: MutableState<Boolean>) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
//            casteListDao.deleteCasteTable()
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
            clearSharedPreference()
            //cleared cache in case of logout
            context.cacheDir.deleteRecursively()
            withContext(Dispatchers.Main){
                showAPILoader.value = false
                logout.value = true
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
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            LogWriter.buildSupportLogAndShare()
        }
    }
}
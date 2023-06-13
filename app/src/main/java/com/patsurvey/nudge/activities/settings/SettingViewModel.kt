package com.patsurvey.nudge.activities.settings

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.SyncHelper
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.dataModel.SettingOptionModel
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.network.isInternetAvailable
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.utils.SYNC_FAILED
import com.patsurvey.nudge.utils.SYNC_SUCCESSFULL
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.showCustomToast
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
    val stepsListDao: StepsListDao,
    val villegeListDao: VillageListDao,
    val didiDao: DidiDao,
    val answerDao: AnswerDao,
    val numericAnswerDao: NumericAnswerDao,
    val questionDao: QuestionListDao

):BaseViewModel() {
    val formAAvailabe = mutableStateOf(false)
    val formBAvailabe = mutableStateOf(false)
    val formCAvailabe = mutableStateOf(false)
    var syncPercentage = mutableStateOf(0f)
    var stepOneSyncStatus = mutableStateOf(0)
    var stepTwoSyncStatus = mutableStateOf(0)
    var stepThreeSyncStatus = mutableStateOf(0)
    var stepFourSyncStatus = mutableStateOf(0)
    var stepFifthSyncStatus = mutableStateOf(0)
    var context : Context? = null
    private val _optionList = MutableStateFlow(listOf<SettingOptionModel>())
    val optionList: StateFlow<List<SettingOptionModel>> get() = _optionList
    val showLoader = mutableStateOf(false)
    var showSyncDialog = mutableStateOf(false)


    var syncHelper = SyncHelper(this@SettingViewModel,prefRepo,apiInterface,tolaDao,stepsListDao,exceptionHandler, villegeListDao, didiDao,job,showLoader,syncPercentage,answerDao,
        numericAnswerDao,
        questionDao)
    fun isFormAAvailableForVillage(villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList = stepsListDao.getAllStepsForVillage(villageId)
            val filteredStepList = stepList.filter { it.name == "Participatory Wealth Ranking" }
            val stepId = if (filteredStepList.isNotEmpty()) filteredStepList[0].id else -1
            if (stepId > 0) {
                val step = stepsListDao.getStepForVillage(stepId, villageId)
                formAAvailabe.value = step.status == StepStatus.COMPLETED.name
            } else {
                formAAvailabe.value = false
            }
        }
    }
    fun isFormBAvailableForVillage(villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList = stepsListDao.getAllStepsForVillage(villageId)
            val filteredStepList = stepList.filter { it.name == "Pat Survey" }
            val stepId = if (filteredStepList.isNotEmpty()) filteredStepList[0].id else -1
            if (stepId > 0) {
                val step = stepsListDao.getStepForVillage(stepId, villageId)
                formBAvailabe.value = step.status == StepStatus.COMPLETED.name
            } else {
                formBAvailabe.value = false
            }
        }
    }

    fun isFormCAvailableForVillage(villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList = stepsListDao.getAllStepsForVillage(villageId)
            val filteredStepList = stepList.filter { it.name == "VO Endorsement" }
            val stepId = if (filteredStepList.isNotEmpty()) filteredStepList[0].id else -1
            if (stepId > 0) {
                val step = stepsListDao.getStepForVillage(stepId, villageId)
                formCAvailabe.value = step.status == StepStatus.COMPLETED.name
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
            if (tolaDao.fetchTolaNeedToPost(true, "").isEmpty()
                && tolaDao.fetchPendingTola(true, "").isEmpty())
            {
                withContext(Dispatchers.Main) {
                    isNeedToBeSync.value = 2
                }
            } else
                isNeedToBeSync.value = 0
        }
    }

    fun isSecondStepNeedToBeSync(isNeedToBeSync : MutableState<Int>) {
        stepTwoSyncStatus = isNeedToBeSync
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
        if(didiDao.fetchAllDidiNeedToPost(true,"").isEmpty()
            && didiDao.fetchPendingDidi(true,"").isEmpty()) {
                withContext(Dispatchers.Main) {
                    isNeedToBeSync.value = 2
                }
            } else
                isNeedToBeSync.value = 0
        }
    }

    fun isThirdStepNeedToBeSync(isNeedToBeSync : MutableState<Int>){
        stepThreeSyncStatus = isNeedToBeSync
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            if (didiDao.getAllNeedToPostDidiRanking(true).isEmpty()
                && didiDao.fetchPendingWealthStatusDidi(true, "").isEmpty()
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
            if (answerDao.fetchPATSurveyDidiList(prefRepo.getSelectedVillage().id).isEmpty()
                && didiDao.fetchPendingPatStatusDidi(true, "").isEmpty()
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
            ) {
                withContext(Dispatchers.Main) {
                    isNeedToBeSync.value = 2
                }
            } else
                isNeedToBeSync.value = 0
        }
    }

    override fun onServerError(error: ErrorModel?) {
        Log.e("server error","called")
        showLoader.value = false
        syncPercentage.value = 100f
        showSyncDialog.value = false
        networkErrorMessage.value = error?.message.toString()
        /*job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            if (context != null) {
                showCustomToast(context, SYNC_FAILED)
                syncPercentage.value = 100f
                showSyncDialog.value = false
                showLoader.value = false
            }
        }*/
    }

    fun getStepOneSize(stepOneSize : MutableState<String>) {
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
    }

    fun syncDataOnServer(cxt: Context,syncDialog : MutableState<Boolean>) {
        context = cxt
        showSyncDialog = syncDialog
        resetPosition()
        if(isInternetAvailable(cxt)){
            syncHelper.syncDataToServer(object :
                NetworkCallbackListener {
                    override fun onSuccess() {
                        networkErrorMessage.value = SYNC_SUCCESSFULL
//                        showCustomToast(cxt, SYNC_SUCCESSFULL)
                        syncPercentage.value = 100f
                        showSyncDialog.value = false
                        showLoader.value = false
                    }

                    override fun onFailed() {
//                        showCustomToast(cxt, SYNC_FAILED)
                        networkErrorMessage.value = SYNC_FAILED
                        syncPercentage.value = 100f
                        showSyncDialog.value = false
                        showLoader.value = false
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
//        showSyncDialog.value = false
//        showLoader.value = false
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

}
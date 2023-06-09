package com.patsurvey.nudge.activities.settings

import android.content.Context
import androidx.compose.runtime.MutableState
import com.patsurvey.nudge.SyncHelper
import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.dao.*
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.dataModel.SettingOptionModel
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.network.isInternetAvailable
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
                && tolaDao.fetchPendingTola(true, "").isEmpty()) {
                    isNeedToBeSync.value = 2
            }
        }
    }

    fun isSecondStepNeedToBeSync(isNeedToBeSync : MutableState<Int>) {
        stepTwoSyncStatus = isNeedToBeSync
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
        if(didiDao.fetchAllDidiNeedToPost(true,"").isEmpty()
            && didiDao.fetchPendingDidi(true,"").isEmpty()) {
                isNeedToBeSync.value = 2
            }
        }
    }

    fun isThirdStepNeedToBeSync(isNeedToBeSync : MutableState<Int>){
        stepThreeSyncStatus = isNeedToBeSync
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            if (didiDao.getAllNeedToPostDidiRanking(true).isEmpty()
                && didiDao.fetchPendingWealthStatusDidi(true, "").isEmpty()
            ) {
                isNeedToBeSync.value = 2
            }
        }
    }

    fun isFourthStepNeedToBeSync(isNeedToBeSync : MutableState<Int>) {
        stepFourSyncStatus = isNeedToBeSync
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            if (answerDao.fetchPATSurveyDidiList(prefRepo.getSelectedVillage().id).isEmpty()
                && didiDao.fetchPendingPatStatusDidi(true, "").isEmpty()
            ) {
                isNeedToBeSync.value = 2
            }
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
                isNeedToBeSync.value = 2
            }
        }
    }

    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
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

    fun syncDataOnServer(context: Context,syncDialog : MutableState<Boolean>) {
        showSyncDialog = syncDialog
        syncHelper = SyncHelper(this@SettingViewModel,prefRepo,apiInterface,tolaDao,stepsListDao,exceptionHandler, villegeListDao, didiDao,job,showLoader,syncPercentage,answerDao,
            numericAnswerDao,
            questionDao)
        if(isInternetAvailable(context)){
            syncHelper.syncDataToServer(object :
                NetworkCallbackListener {
                    override fun onSuccess() {
                        showCustomToast(context, SYNC_SUCCESSFULL)
                        syncPercentage.value = 100f
                        showSyncDialog.value = false
                        showLoader.value = false
                    }

                    override fun onFailed() {
                        showCustomToast(context, SYNC_FAILED)
                        syncPercentage.value = 100f
                        showSyncDialog.value = false
                        showLoader.value = false
                    }
            })
        }
    }

}
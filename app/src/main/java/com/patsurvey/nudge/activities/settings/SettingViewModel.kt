package com.patsurvey.nudge.activities.settings

import android.content.Context
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
    private val _optionList = MutableStateFlow(listOf<SettingOptionModel>())
    val optionList: StateFlow<List<SettingOptionModel>> get() = _optionList

    val showLoader = mutableStateOf(false)
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
    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }

    fun syncDataOnServer(context: Context) {
        if(isInternetAvailable(context)){
            val syncHelper = SyncHelper(prefRepo,apiInterface,tolaDao,stepsListDao,exceptionHandler, villegeListDao, didiDao,job,showLoader,syncPercentage,answerDao,
            numericAnswerDao,
            questionDao)
            syncHelper.syncDataToServer(object :
                NetworkCallbackListener {
                    override fun onSuccess() {
                        showLoader.value = false
                        showCustomToast(context, SYNC_SUCCESSFULL)
                        syncPercentage.value = 100f
                    }

                    override fun onFailed() {
                        showCustomToast(context, SYNC_FAILED)
                        showLoader.value = false
                        syncPercentage.value = 100f
                    }
            })
        }
    }

}
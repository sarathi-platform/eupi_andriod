package com.patsurvey.nudge.activities.settings

import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.model.dataModel.SettingOptionModel
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.utils.StepStatus
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
    val stepsListDao: StepsListDao
):BaseViewModel() {
    val formAAvailabe = mutableStateOf(false)
    val formBAvailabe = mutableStateOf(false)
    val formCAvailabe = mutableStateOf(false)
    private val _optionList = MutableStateFlow(listOf<SettingOptionModel>())
    val optionList: StateFlow<List<SettingOptionModel>> get() = _optionList

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
}
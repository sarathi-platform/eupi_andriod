package com.patsurvey.nudge.activities.ui.progress

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewModelScope
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.model.dataModel.VillageListModal
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.FAIL
import com.patsurvey.nudge.utils.SUCCESS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ProgressScreenViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val apiInterface: ApiService,
    val stepsListDao: StepsListDao
): BaseViewModel() {

    private val _stepsList = MutableStateFlow(listOf<StepListEntity>())
    private val _villagList = MutableStateFlow(listOf<VillageListModal>())
    val stepList: StateFlow<List<StepListEntity>> get() = _stepsList
    val villageList: StateFlow<List<VillageListModal>> get() = _villagList
    val stepSelected = mutableStateOf(0)
    val villageSelected = mutableStateOf(-1)

    val showLoader = mutableStateOf(false)

    fun isLoggedIn() = (prefRepo.getAccessToken()?.isNotEmpty() == true)

    init {
        villageSelected.value = prefRepo.getSelectedVillage() ?: -1
        fetchStepsList()
        createVillaeList()
    }

    private fun getStepsList() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList = stepsListDao.getAllSteps()
            withContext(Dispatchers.IO) {
                _stepsList.value = stepList
            }
        }
    }

    private fun fetchStepsList() {
        showLoader.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                try {
                val response = apiInterface.getStepsList()
                withContext(Dispatchers.IO){
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.let {
                            it.stepList.forEach { step ->
                               /* if (index == 2)
                                    stepsListDao.insert(StepListEntity(id = step.id, orderNumber = 4, name = step.name, isComplete = false, needToPost = false))
                                else if (index == 3)
                                    stepsListDao.insert(StepListEntity(id = step.id, orderNumber = 3, name = step.name, isComplete = false, needToPost = false))
                                else*/
                                    stepsListDao.insert(StepListEntity(id = step.id, orderNumber = step.orderNumber, name = step.name, isComplete = false, needToPost = false))


                            }
                            prefRepo.savePref("progremName", it.programName) //TODO saving this in pref for now will move it to user table after User API is integrated
                            delay(2000L)
                            getStepsList()
                            showLoader.value = false
                        }
                        if (response.data == null)
                            showLoader.value = false
                    } else if (response.status.equals(FAIL, true)){
                        withContext(Dispatchers.Main) {
                            showLoader.value = false
                        }
                    }
                    else {
                        onError("Error : ${response.message}")
                        showLoader.value = false
                    }
                }
            } catch (ex: Exception) {
                onError("Exception : ${ex.localizedMessage}")
                    showLoader.value = false
                }

            }
    }

    private fun createVillaeList() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val sampleVillageList = arrayListOf<VillageListModal>()
                sampleVillageList.add(VillageListModal(villageName = "Sundar Pahar", voName = "Sundar Pahar Mahila Mandal"))
                sampleVillageList.add(VillageListModal(villageName = "Sundar Pahar 1", voName = "Sundar Pahar Mahila Mandal"))
                sampleVillageList.add(VillageListModal(villageName = "Sundar Pahar 2", voName = "Sundar Pahar Mahila Mandal"))
                sampleVillageList.add(VillageListModal(villageName = "Sundar Pahar 3", voName = "Sundar Pahar Mahila Mandal"))
                sampleVillageList.add(VillageListModal(villageName = "Sundar Pahar 4", voName = "Sundar Pahar Mahila Mandal"))
                sampleVillageList.add(VillageListModal(villageName = "Sundar Pahar 5", voName = "Sundar Pahar Mahila Mandal"))
                sampleVillageList.add(VillageListModal(villageName = "Sundar Pahar 6", voName = "Sundar Pahar Mahila Mandal"))
                sampleVillageList.add(VillageListModal(villageName = "Sundar Pahar 7", voName = "Sundar Pahar Mahila Mandal"))
                _villagList.emit(sampleVillageList)
            }
        }
    }

    /*private fun createStepsList() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val sampleStepList = arrayListOf<StepsListWithStatusModel>()
                sampleStepList.add(StepsListWithStatusModel(id = 1, name = "Transect Walk", orderNumber = 1, isComplete = false))
                sampleStepList.add(StepsListWithStatusModel(id = 2, name = "Social Mapping", orderNumber = 2, isComplete = false))
                sampleStepList.add(StepsListWithStatusModel(id = 3, name = "Participatory Wealth Ranking", orderNumber = 3, isComplete = false))
                sampleStepList.add(StepsListWithStatusModel(id = 4, name = "Pat Survey", orderNumber = 4, isComplete = false))
                sampleStepList.add(StepsListWithStatusModel(id = 5, name = "VO Endorsementk", orderNumber = 5, isComplete = false))
                sampleStepList.add(StepsListWithStatusModel(id = 6, name = "BMP Approval", orderNumber = 6, isComplete = false))
                _stepsList.emit(sampleStepList)
                stepList.value.sortedBy {
                    it.orderNumber
                }
            }
        }
    }*/

}
package com.patsurvey.nudge.activities.ui.progress

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ProgressScreenViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val apiInterface: ApiService,
    val stepsListDao: StepsListDao,
    val villageListDao: VillageListDao,
    val casteListDao: CasteListDao
) : BaseViewModel() {

    private val _stepsList = MutableStateFlow(listOf<StepListEntity>())
    private val _villagList = MutableStateFlow(listOf<VillageEntity>())
    val stepList: StateFlow<List<StepListEntity>> get() = _stepsList
    val villageList: StateFlow<List<VillageEntity>> get() = _villagList
    val stepSelected = mutableStateOf(0)
    val villageSelected = mutableStateOf(0)
    val selectedText = mutableStateOf("Select Village")

    val showLoader = mutableStateOf(false)

    fun isLoggedIn() = (prefRepo.getAccessToken()?.isNotEmpty() == true)

    init {
        fetchVillageList()
        fetchCasteList()
    }

    private fun checkAndUpdateCompletedStepsForVillage() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val villageId = prefRepo.getSelectedVillage().id
            val updatedCompletedStepList = mutableListOf<Int>()
            stepList.value.forEach {
                if (it.isComplete == StepStatus.COMPLETED.ordinal) {
                    updatedCompletedStepList.add(it.id)
                }
            }
            villageListDao.updateLastCompleteStep(villageId, updatedCompletedStepList)
        }
    }

     fun getStepsList(villageId:Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList = stepsListDao.getAllStepsForVillage(villageId)
            withContext(Dispatchers.IO) {
                _stepsList.value = stepList
                
            }
        }
    }

    private fun fetchCasteList(){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = apiInterface.getCasteList(prefRepo.getAppLanguageId()?:0)
                withContext(Dispatchers.IO){
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.let { casteListDao.insertAll(it) }
                    }else{
                        onError(tag = "ProgressScreenViewModel", "Error : ${response.message}")
                    }
                }
            }catch (ex:Exception){
                onError(tag = "ProgressScreenViewModel", "Error : ${ex.localizedMessage}")
            }
        }
    }



    fun fetchVillageList(){
        showLoader.value = true
        job=viewModelScope.launch {
            withContext(Dispatchers.IO){
                val villageList=villageListDao.getAllVillages()
                _villagList.value = villageList
                withContext(Dispatchers.Main){
                    villageList.mapIndexed { index, villageEntity ->
                        if(prefRepo.getSelectedVillage().id==villageEntity.id){
                            villageSelected.value=index
                        }
                    }
                    selectedText.value = prefRepo.getSelectedVillage().name
                    getStepsList(prefRepo.getSelectedVillage().id)
                    showLoader.value = false
                }
            }
        }
    }

    fun isStepComplete(stepId: Int,villageId: Int): LiveData<Int> {
        return stepsListDao.isStepCompleteLive(stepId,villageId)
    }

    fun updateSelectedStep(stepId: Int) {
        val currentStepIndex = stepList.value.map { it.stepId }.indexOf(stepId)
        stepSelected.value = when (currentStepIndex) {
            in 0..4 -> currentStepIndex + 1
            5 -> {
                currentStepIndex
            }
            else -> {
                0
            }
        }
    }

    fun updateSelectedVillage(selectedVillageEntity: VillageEntity) {
        prefRepo.saveSelectedVillage(selectedVillageEntity)
    }
    fun getTolaCount() = prefRepo.getPref(TOLA_COUNT, 0)
    fun getDidiCount() = prefRepo.getPref(DIDI_COUNT, 0)

}
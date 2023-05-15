package com.patsurvey.nudge.activities.ui.progress

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.VillageEntity
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
    val villageListDao: VillageListDao
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
        getVillaeList() {
            fetchStepsList()
        }
//        checkAndUpdateCompletedStepsForVillage()
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

    private fun getStepsList() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList = stepsListDao.getAllSteps()
            withContext(Dispatchers.IO) {
                _stepsList.value = stepList
            }
        }
    }

    fun fetchStepsList() {
        showLoader.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = apiInterface.getStepsList()
                val localStepsList = stepsListDao.getAllSteps()
                withContext(Dispatchers.IO) {
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.let {
                            if (localStepsList.isEmpty() && !StepListEntity.same(
                                    localStepsList,
                                    StepListEntity.convertFromModelToEntity(it.stepList)
                                )
                            ) {
                                it.stepList.forEach { step ->
                                    stepsListDao.insert(
                                        StepListEntity(
                                            id = step.id,
                                            orderNumber = step.orderNumber,
                                            name = step.name,
                                            isComplete = StepStatus.NOT_STARTED.ordinal,
                                            needToPost = true
                                        )
                                    )
                                }
                            }
                            prefRepo.savePref(
                                PREF_PROGRAM_NAME,
                                it.programName
                            ) //TODO saving this in pref for now will move it to user table after User API is integrated
                            delay(2000L)
                            getStepsList()
                            showLoader.value = false
                        }
                        if (response.data == null)
                            showLoader.value = false
                    } else if (response.status.equals(FAIL, true)) {
                        withContext(Dispatchers.Main) {
                            showLoader.value = false
                        }
                    }
                    else {
                        onError(tag = "ProgressScreenViewModel", "Error : ${response.message}")
                        showLoader.value = false
                    }
                }
            } catch (ex: Exception) {
                onError(tag = "ProgressScreenViewModel", "Exception : ${ex.localizedMessage}")
                showLoader.value = false
            }
        }
    }

    fun getVillaeList(success: () -> Unit) {
        showLoader.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val villageList = villageListDao.getAllVillages()
                withContext(Dispatchers.IO) {
                    _villagList.value = villageList
                    withContext(Dispatchers.Main) {
                        villageList.mapIndexed { index, villageEntity ->
                            if(prefRepo.getSelectedVillage().id==villageEntity.id){
                                villageSelected.value=index
                            }
                        }
                        selectedText.value = prefRepo.getSelectedVillage().name
                        showLoader.value = false
                    }
                    success()
                }
            } catch (ex: Exception) {
                onError(tag = "ProgressScreenViewModel", "Exception : ${ex.localizedMessage}")
                showLoader.value = false
            }

        }
    }

    fun isStepComplete(stepId: Int): LiveData<Int> {
        return stepsListDao.isStepCompleteLive(stepId)
    }

    fun updateSelectedStep(stepId: Int) {
        val currentStepIndex = stepList.value.map { it.id }.indexOf(stepId)
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
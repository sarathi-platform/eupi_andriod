package com.patsurvey.nudge.activities.ui.progress

import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.UserDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.FAIL
import com.patsurvey.nudge.utils.PREF_PROGRAM_NAME
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
    val stepsListDao: StepsListDao,
    val villageListDao: VillageListDao
): BaseViewModel() {

    private val _stepsList = MutableStateFlow(listOf<StepListEntity>())
    private val _villagList = MutableStateFlow(listOf<VillageEntity>())
    val stepList: StateFlow<List<StepListEntity>> get() = _stepsList
    val villageList: StateFlow<List<VillageEntity>> get() = _villagList
    val stepSelected = mutableStateOf(0)
    val villageSelected = mutableStateOf(-1)


    val showLoader = mutableStateOf(false)

    fun isLoggedIn() = (prefRepo.getAccessToken()?.isNotEmpty() == true)

    init {
        getVillaeList() {
            fetchStepsList()
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

    private fun fetchStepsList() {
        showLoader.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                try {
                val response = apiInterface.getStepsList()
                withContext(Dispatchers.IO){
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.let {
                            it.stepList.forEach { step ->
                                stepsListDao.insert(StepListEntity(id = step.id, orderNumber = step.orderNumber, name = step.name, isComplete = false, needToPost = false))
                            }
                            prefRepo.savePref(PREF_PROGRAM_NAME, it.programName) //TODO saving this in pref for now will move it to user table after User API is integrated
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

    private fun getVillaeList(success: () -> Unit) {
        showLoader.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val villageList = villageListDao.getAllVillages()
                withContext(Dispatchers.IO){
                    _villagList.value = villageList
                    withContext(Dispatchers.Main) {
                        villageSelected.value = prefRepo.getSelectedVillage() ?: -1
                        showLoader.value = false
                    }
                    success()
                }
            } catch (ex: Exception) {
                onError("Exception : ${ex.localizedMessage}")
                showLoader.value = false
            }

        }
    }

}
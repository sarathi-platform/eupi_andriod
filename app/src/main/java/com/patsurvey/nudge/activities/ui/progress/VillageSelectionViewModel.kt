package com.patsurvey.nudge.activities.ui.progress


import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
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
class VillageSelectionViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val apiService: ApiService,
    val villageListDao: VillageListDao,
    val stepsListDao: StepsListDao
) : BaseViewModel() {

    private val _villagList = MutableStateFlow(listOf<VillageEntity>())
    val villageList: StateFlow<List<VillageEntity>> get() = _villagList

    val villageSelected = mutableStateOf(-1)
    val showLoader = mutableStateOf(false)

    fun isLoggedIn() = (prefRepo.getAccessToken()?.isNotEmpty() == true)

    init {
        fetchUserDetails(){
            fetchVillageList()
        }
    }
    fun fetchVillageList(){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                withContext(Dispatchers.IO) {
                    val villageList=villageListDao.getAllVillages()
                    val localStepsList = stepsListDao.getAllSteps()
                    if(localStepsList.isNotEmpty()){
                        stepsListDao.deleteAllStepsFromDB()
                    }
                    villageList.forEach { village->
                        launch {

                            val response=apiService.getStepsList(village.id)
                            if(response.status.equals(SUCCESS,true)){
                                response.data?.let {
                                    
                                    it.stepList.forEach { steps->
                                        steps.villageId=village.id
                                    }
                                    stepsListDao.insertAll(it.stepList)
                                    prefRepo.savePref(
                                        PREF_PROGRAM_NAME,
                                        it.programName
                                    )
                                    showLoader.value = false

                                }


                            }
                            else {
                                onError(tag = "ProgressScreenViewModel", "Error : ${response.message}")
                            }

                        }
                    }
                }
            } catch (ex: Exception) {
                onError(tag = "VillageSelectionViewModel", "Exception : ${ex.localizedMessage}")
                showLoader.value = false
            }
        }
    }


    fun updateSelectedVillage() {
        prefRepo.saveSelectedVillage(villageList.value[villageSelected.value])
    }

    private fun fetchUserDetails(apiSuccess:()->Unit) {
        showLoader.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = apiService.userAndVillageListAPI(prefRepo.getAppLanguageId() ?: 1)
                withContext(Dispatchers.IO) {
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.let {
                            prefRepo.savePref(PREF_KEY_USER_NAME, it.username)
                            prefRepo.savePref(PREF_KEY_NAME, it.name)
                            prefRepo.savePref(PREF_KEY_EMAIL, it.email)
                            prefRepo.savePref(PREF_KEY_IDENTITY_NUMBER, it.identityNumber)
                            prefRepo.savePref(PREF_KEY_PROFILE_IMAGE, it.profileImage)
                            villageListDao.insertAll(it.villageList)
                            _villagList.emit(villageListDao.getAllVillages())
                            apiSuccess()
                        }
                        withContext(Dispatchers.Main) {
                            showLoader.value = false
                        }
                        if (response.data == null)
                            showLoader.value = false
                    } else if (response.status.equals(FAIL, true)) {
                        withContext(Dispatchers.Main) {
                            showLoader.value = false
                        }
                    } else {
                        onError(tag = "VillageSelectionViewModel", "Error : ${response.message}")
                        showLoader.value = false
                    }
                }
            } catch (ex: Exception) {
                onError(tag = "VillageSelectionViewModel", "Exception : ${ex.localizedMessage}")
                showLoader.value = false
            }
        }
    }
}
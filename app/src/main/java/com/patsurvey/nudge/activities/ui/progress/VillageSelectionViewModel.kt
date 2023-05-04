package com.patsurvey.nudge.activities.ui.progress

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.model.dataModel.VillageListModal
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.SUCCESS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class VillageSelectionViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val apiService: ApiService
): BaseViewModel() {

    private val _villagList = MutableStateFlow(listOf<VillageListModal>())
    val villageList: StateFlow<List<VillageListModal>> get() = _villagList

    val villageSelected = mutableStateOf(-1)

    fun isLoggedIn() = (prefRepo.getAccessToken()?.isNotEmpty() == true)

    init {
        getUserAndVillageList()
    }

    fun updateSelectedVillage(){
        prefRepo.saveSelectedVillage(villageSelected.value)
    }

    fun getUserAndVillageList(){
        job=CoroutineScope(Dispatchers.IO + exceptionHandler).launch{
            val response=apiService.userAndVillageListAPI()
            withContext(Dispatchers.IO){
                if(response.status.equals(SUCCESS,true)){
                    Log.d("TAG", "getUserAndVillageList:${response.data.toString()} ")
                }else{
                    onError("Error : ${response.message} ")
                }
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

}
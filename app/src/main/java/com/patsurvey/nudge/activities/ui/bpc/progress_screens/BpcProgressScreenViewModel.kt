package com.patsurvey.nudge.activities.ui.bpc.progress_screens

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.BpcSummaryEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.BpcSummaryDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.network.model.ErrorModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BpcProgressScreenViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val villageListDao: VillageListDao,
    val stepsListDao: StepsListDao,
    val bpcSummaryDao: BpcSummaryDao
): BaseViewModel() {

    private val _villagList = MutableStateFlow(listOf<VillageEntity>())
    val villageList: StateFlow<List<VillageEntity>> get() = _villagList

    private val _summaryData = MutableStateFlow(BpcSummaryEntity.getEmptySummary())
    val summaryData: StateFlow<BpcSummaryEntity> get() = _summaryData

    val showLoader = mutableStateOf(false)

    val villageSelected = mutableStateOf(0)
    val selectedText = mutableStateOf("Select Village")


    fun isLoggedIn() = (prefRepo.getAccessToken()?.isNotEmpty() == true)

    init {
        fetchVillageList()
        fetchBpcSummaryData()
    }

    private fun fetchBpcSummaryData() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val summary = bpcSummaryDao.getBpcSummaryForVillage(prefRepo.getSelectedVillage().id)
            _summaryData.value = summary
        }
    }

    fun fetchVillageList(){
        showLoader.value = true
        job=viewModelScope.launch {
            withContext(Dispatchers.IO){
                val villageList=villageListDao.getAllVillages()
//                val tolaDBList=tolaDao.getAllTolasForVillage(prefRepo.getSelectedVillage().id)
                _villagList.value = villageList
//                _tolaList.emit(tolaDBList)
//                _didiList.emit(didiDao.getAllDidisForVillage(prefRepo.getSelectedVillage().id))
                withContext(Dispatchers.Main){
                    villageList.mapIndexed { index, villageEntity ->
                        if(prefRepo.getSelectedVillage().id==villageEntity.id){
                            villageSelected.value=index
                        }
                    }
                    selectedText.value = prefRepo.getSelectedVillage().name
//                    getStepsList(prefRepo.getSelectedVillage().id)
                    showLoader.value = false
                }
            }
        }
    }

    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }

}

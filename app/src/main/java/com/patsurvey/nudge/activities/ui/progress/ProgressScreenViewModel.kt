package com.patsurvey.nudge.activities.ui.progress

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.model.dataModel.StepsListModal
import com.patsurvey.nudge.model.dataModel.VillageListModal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProgressScreenViewModel @Inject constructor(

): BaseViewModel() {

    private val _stepsList = MutableStateFlow(listOf<StepsListModal>())
    private val _villagList = MutableStateFlow(listOf<VillageListModal>())
    val stepList: StateFlow<List<StepsListModal>> get() = _stepsList
    val villageList: StateFlow<List<VillageListModal>> get() = _villagList
    val stepSelected = mutableStateOf(0)
    val villageSelected = mutableStateOf(-1)

    init {
        createStepsList()
        createVillaeList()
    }

    private fun createVillaeList() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val sampleVillageList = arrayListOf<VillageListModal>()
                sampleVillageList.add(VillageListModal(villageName = "Sundar Pahar", voName = "Sundar Pahar Mahila Mandal"))
                sampleVillageList.add(VillageListModal(villageName = "Sundar Pahar", voName = "Sundar Pahar Mahila Mandal"))
                sampleVillageList.add(VillageListModal(villageName = "Sundar Pahar", voName = "Sundar Pahar Mahila Mandal"))
                sampleVillageList.add(VillageListModal(villageName = "Sundar Pahar", voName = "Sundar Pahar Mahila Mandal"))
                sampleVillageList.add(VillageListModal(villageName = "Sundar Pahar", voName = "Sundar Pahar Mahila Mandal"))
                sampleVillageList.add(VillageListModal(villageName = "Sundar Pahar", voName = "Sundar Pahar Mahila Mandal"))
                sampleVillageList.add(VillageListModal(villageName = "Sundar Pahar", voName = "Sundar Pahar Mahila Mandal"))
                sampleVillageList.add(VillageListModal(villageName = "Sundar Pahar", voName = "Sundar Pahar Mahila Mandal"))
                _villagList.emit(sampleVillageList)
            }
        }
    }

    private fun createStepsList() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val sampleStepList = arrayListOf<StepsListModal>()
                sampleStepList.add(StepsListModal(stepName = "Transect Walk", stepNo = 1, isCompleted = false))
                sampleStepList.add(StepsListModal(stepName = "Social Mapping", stepNo = 2, isCompleted = false))
                sampleStepList.add(StepsListModal(stepName = "Participatory Wealth Ranking", stepNo = 3, isCompleted = false))
                sampleStepList.add(StepsListModal(stepName = "Pat Survey", stepNo = 4, isCompleted = false))
                sampleStepList.add(StepsListModal(stepName = "VO Endorsementk", stepNo = 5, isCompleted = false))
                sampleStepList.add(StepsListModal(stepName = "BMP Approval", stepNo = 6, isCompleted = false))
                _stepsList.emit(sampleStepList)
            }
        }
    }

}
package com.patsurvey.nudge.activities.ui.bpc.bpc_didi_list_screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.nudge.core.enums.EventType
import com.patsurvey.nudge.activities.ui.bpc.ReplaceHelper
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.getPatScoreEventName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BpcDidiListViewModel @Inject constructor(
    val repository: BPCDidiListRepository
): BaseViewModel() {

    val pendingDidiCount = mutableStateOf(0)
    private val _selectedDidiList = MutableStateFlow(listOf<DidiEntity>())
    val selectedDidiList: StateFlow<List<DidiEntity>> get() = _selectedDidiList

    private val _tolaList = MutableStateFlow(listOf<TolaEntity>())
    val tolaList: StateFlow<List<TolaEntity>> get() = _tolaList

    var filterDidiList by mutableStateOf(listOf<DidiEntity>())

    private var _inclusiveQueList = MutableStateFlow(listOf<SectionAnswerEntity>())
    val inclusiveQueList: StateFlow<List<SectionAnswerEntity>> get() = _inclusiveQueList

    var tolaMapList by mutableStateOf(mapOf<String, List<DidiEntity>>())
        private set

    var filterTolaMapList by mutableStateOf(mapOf<String, List<DidiEntity>>())
        private set

    var villageId: Int = -1
    var stepId: Int = -1

    private var _markedNotAvailable = MutableStateFlow(mutableListOf<Int>())
    val isStepComplete = mutableStateOf(false)

    val showLoader = mutableStateOf(false)

    init {
        villageId = repository.prefRepo.getSelectedVillage().id
        fetchDidiListForBPC()
        checkIfStepIsComplete()
    }

    private fun checkIfStepIsComplete() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList = repository.getAllStepsForVillage()
            isStepComplete.value = stepList.sortedBy { it.orderNumber }.last().isComplete == StepStatus.COMPLETED.ordinal
        }
    }

    fun fetchDidiListForBPC(){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val localDidiList = repository.getAllDidisForVillage()
            _selectedDidiList.value = localDidiList
            _tolaList.emit(repository.getAllTolasForVillage())
            filterDidiList = selectedDidiList.value
            pendingDidiCount.value = filterDidiList.filter { it.patSurveyStatus == PatSurveyStatus.NOT_STARTED.ordinal || it.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal }.size
        }
    }

    fun filterList() {
        val map = mutableMapOf<String, MutableList<DidiEntity>>()
        selectedDidiList.value.forEachIndexed { _, didiDetailsModel ->
            if (map.containsKey(didiDetailsModel.cohortName)) {
                map[didiDetailsModel.cohortName]?.add(didiDetailsModel)
            } else {
                map[didiDetailsModel.cohortName] = mutableListOf(didiDetailsModel)
            }
        }
        tolaMapList = map
        filterTolaMapList = map

    }

    fun performQuery(query: String, isTolaFilterSelected: Boolean) {
        try {
            if (!isTolaFilterSelected) {
                filterDidiList = if (query.isNotEmpty()) {
                    val filteredList = ArrayList<DidiEntity>()
                    selectedDidiList.value.forEach { didi ->
                        if (didi.name.lowercase().contains(query.lowercase())) {
                            filteredList.add(didi)
                        }
                    }
                    filteredList
                } else {
                    selectedDidiList.value
                }
            } else {
                if (query.isNotEmpty()) {
                    val fList = mutableMapOf<String, MutableList<DidiEntity>>()
                    tolaMapList.keys.forEach { key ->
                        val newDidiList = ArrayList<DidiEntity>()
                        tolaMapList[key]?.forEach { didi ->
                            if (didi.name.lowercase().contains(query.lowercase())) {
                                newDidiList.add(didi)
                            }
                        }
                        if (newDidiList.isNotEmpty())
                            fList[key] = newDidiList
                    }
                    filterTolaMapList = fList
                } else {
                    filterTolaMapList = tolaMapList
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            Log.e("BpcDidiListViewModel", "performQuery -> Exception1 : performQuery: ${ex.message}")
        }

    }

    fun setDidiAsUnavailable(didiId: Int) {
        _markedNotAvailable.value = _markedNotAvailable.value.also {
            it.add(didiId)
        }
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val didiEntity = repository.getDidiFromDB(didiId)
            val didiPatProgress = didiEntity.patSurveyStatus
            if (didiPatProgress == PatSurveyStatus.INPROGRESS.ordinal || didiPatProgress == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) {
                _selectedDidiList.value[_selectedDidiList.value.map { it.id }
                    .indexOf(didiId)].patSurveyStatus =
                    PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal
                repository.updateQuesSectionStatus(
                    didiId,
                    PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal
                )
            } else {
                _selectedDidiList.value[_selectedDidiList.value.map { it.id }
                    .indexOf(didiId)].patSurveyStatus =
                    PatSurveyStatus.NOT_AVAILABLE.ordinal
                repository.updateQuesSectionStatus(didiId, PatSurveyStatus.NOT_AVAILABLE.ordinal)
            }
            repository.updateNeedToPostPAT(true, didiId)
            pendingDidiCount.value =
                repository.getAllPendingPATDidisCount()
            val updatedDidiEntity = repository.getDidiFromDB(didiId)

            repository.saveEvent(
                eventItem = updatedDidiEntity,
                eventName = getPatScoreEventName(
                    updatedDidiEntity,
                    repository.prefRepo.isUserBPC()
                ),
                EventType.STATEFUL
            )
        }

    }

    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        TODO("Not yet implemented")
    }

    fun replaceDidi(index: Int, didiId: Int) {
        ReplaceHelper.didiToBeReplaced.value = Pair(index, didiId)
    }

    fun getPatStepStatus(stepId: Int, callBack: (isComplete: Boolean) -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepStatus = repository.isStepComplete(stepId, villageId)
            withContext(Dispatchers.Main) {
                if (stepStatus == StepStatus.COMPLETED.ordinal) {
                    delay(100)
                    callBack(true)
                } else {
                    delay(100)
                    callBack(false)
                }
            }
        }
    }

    fun isStepComplete(callBack: (stepId: Int, isComplete: Boolean) -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val step  = repository.getAllStepsForVillage().sortedBy { it.orderNumber }.last()
            val isComplete = step.isComplete
            withContext(Dispatchers.Main){
                if (isComplete == StepStatus.COMPLETED.ordinal)
                    callBack(step.id, true)
                else
                    callBack(step.id,false)
            }
        }
    }

}

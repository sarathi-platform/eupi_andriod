package com.patsurvey.nudge.activities.ui.vo_endorsement

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.StepStatus
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
class VoEndorsementScreenViewModel @Inject constructor(
    val repository: VoEndorsementScreenRepository
): BaseViewModel() {

    val pendingDidiCount = mutableStateOf(0)
    private val _didiList = MutableStateFlow(listOf<DidiEntity>())
    val didiList: StateFlow<List<DidiEntity>> get() = _didiList

    private var _filterDidiList = MutableStateFlow(listOf<DidiEntity>())
    val filterDidiList: StateFlow<List<DidiEntity>> get() = _filterDidiList

    private var _inclusiveQueList = MutableStateFlow(listOf<SectionAnswerEntity>())
    val  inclusiveQueList: StateFlow<List<SectionAnswerEntity>> get() = _inclusiveQueList

    var tolaMapList by mutableStateOf(mapOf<String, List<DidiEntity>>())
        private set

    var filterTolaMapList by mutableStateOf(mapOf<String, List<DidiEntity>>())
        private set

    var villageId: Int = -1
    var stepId: Int = -1

    val showLoader = mutableStateOf(false)

    init {
        villageId = repository.prefRepo.getSelectedVillage().id
        fetchDidisFromDB()
    }

    fun fetchDidisFromDB() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                _didiList.value = repository.fetchVOEndorseStatusDidi()
                _filterDidiList.value = didiList.value

            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.e("VoEndorsementScreenViewModel", "fetchDidisFromDB -> fetchDidisFromDB Exception: ${ex.message}")
            }
        }
    }
    fun updateFilterDidiList() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _filterDidiList.value.forEach {
                val didiDetails = repository.getDidiFromDB(it.id)
                if (didiDetails != null)
                    it.voEndorsementStatus = didiDetails.voEndorsementStatus
            }

            withContext(Dispatchers.Main) {
                pendingDidiCount.value =
                    didiList.value.filter { it.voEndorsementStatus == DidiEndorsementStatus.NOT_STARTED.ordinal }.size

            }
        }
    }

    fun filterList() {
        val map = mutableMapOf<String, MutableList<DidiEntity>>()
        didiList.value.forEachIndexed { _, didiDetailsModel ->
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
                _filterDidiList.value = if (query.isNotEmpty()) {
                    val filteredList = ArrayList<DidiEntity>()
                    didiList.value.forEach { didi ->
                        if (didi.name.lowercase().contains(query.lowercase())) {
                            filteredList.add(didi)
                        }
                    }
                    filteredList
                } else {
                    didiList.value
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
        }catch (ex:Exception){
            ex.printStackTrace()
            Log.e("VoEndorsementScreenViewModel", "performQuery -> Exception1 : performQuery: ${ex.message}" )
        }

    }


    fun getVoEndorsementStepStatus(stepId: Int, callBack: (isComplete: Boolean) -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepStatus = repository.checkVoEndorsementStepStatus(stepId, villageId)
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
    override fun onServerError(error: ErrorModel?) {
        TODO("Not yet implemented")
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        TODO("Not yet implemented")
    }
}
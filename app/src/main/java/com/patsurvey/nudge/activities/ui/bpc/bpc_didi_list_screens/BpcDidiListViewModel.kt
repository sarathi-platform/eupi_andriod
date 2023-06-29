package com.patsurvey.nudge.activities.ui.bpc.bpc_didi_list_screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.patsurvey.nudge.activities.ui.bpc.ReplaceHelper
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.BpcSelectedDidiEntity
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.dao.BpcNonSelectedDidiDao
import com.patsurvey.nudge.database.dao.BpcSelectedDidiDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.TAG
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
    val prefRepo: PrefRepo,
    val didiDao: DidiDao,
    val tolaDao: TolaDao,
    val stepsListDao: StepsListDao,
    val bpcNonSelectedDidiDao: BpcNonSelectedDidiDao,
    val bpcSelectedDidiDao: BpcSelectedDidiDao
): BaseViewModel() {

    val pendingDidiCount = mutableStateOf(0)
    private val _selectedDidiList = MutableStateFlow(listOf<BpcSelectedDidiEntity>())
    val selectedDidiList: StateFlow<List<BpcSelectedDidiEntity>> get() = _selectedDidiList

    private val _tolaList = MutableStateFlow(listOf<TolaEntity>())
    val tolaList: StateFlow<List<TolaEntity>> get() = _tolaList

    var filterDidiList by mutableStateOf(listOf<BpcSelectedDidiEntity>())

    private var _inclusiveQueList = MutableStateFlow(listOf<SectionAnswerEntity>())
    val inclusiveQueList: StateFlow<List<SectionAnswerEntity>> get() = _inclusiveQueList

    var tolaMapList by mutableStateOf(mapOf<String, List<BpcSelectedDidiEntity>>())
        private set

    var filterTolaMapList by mutableStateOf(mapOf<String, List<BpcSelectedDidiEntity>>())
        private set

    var villageId: Int = -1
    var stepId: Int = -1

    private var _markedNotAvailable = MutableStateFlow(mutableListOf<Int>())

    init {
        villageId = prefRepo.getSelectedVillage().id
        fetchDidiFromDb()
    }

    fun fetchDidiFromDb() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val localSeletctedDidiList =
                bpcSelectedDidiDao.fetchAllSelectedDidiForVillage(prefRepo.getSelectedVillage().id)
            val localUnselectedDidiList =
                bpcNonSelectedDidiDao.fetchAllDidisForVillage(prefRepo.getSelectedVillage().id)
            _selectedDidiList.value = localSeletctedDidiList
            val filterdNonSelectedList = localUnselectedDidiList.filter { it.isAlsoSelected }
            if (filterdNonSelectedList.isNotEmpty()) {
                _selectedDidiList.value =
                    _selectedDidiList.value.toMutableList().also { selectedList ->
                        val selectedDidisFromBackupList = mutableListOf<BpcSelectedDidiEntity>()
                        filterdNonSelectedList.forEach {
                            selectedDidisFromBackupList.add(
                                BpcSelectedDidiEntity.getSelectedDidiEntityFromNonSelectedEntity(
                                    it
                                )
                            )
                        }
                        if (ReplaceHelper.didiToBeReplaced.value.first != -1 && ReplaceHelper.didiToBeReplaced.value.second != -1)
                            selectedList.add(ReplaceHelper.didiToBeReplaced.value.first, selectedDidisFromBackupList[0])
                        else {
                            selectedList.addAll(selectedDidisFromBackupList)
                        }
                    }
            }
            _tolaList.emit(tolaDao.getAllTolasForVillage(prefRepo.getSelectedVillage().id))
            filterDidiList = selectedDidiList.value
            pendingDidiCount.value =
                bpcSelectedDidiDao.getAllPendingPATDidisCount(prefRepo.getSelectedVillage().id)
        }
    }

    fun filterList() {
        val map = mutableMapOf<String, MutableList<BpcSelectedDidiEntity>>()
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
                    val filteredList = ArrayList<BpcSelectedDidiEntity>()
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
                    val fList = mutableMapOf<String, MutableList<BpcSelectedDidiEntity>>()
                    tolaMapList.keys.forEach { key ->
                        val newDidiList = ArrayList<BpcSelectedDidiEntity>()
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
            Log.e(TAG, "Exception1 : performQuery: ${ex.message}")
        }

    }

    fun setDidiAsUnavailable(didiId: Int) {
        _markedNotAvailable.value = _markedNotAvailable.value.also {
            it.add(didiId)
        }
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiPatProgress = didiDao.getDidi(didiId).patSurveyStatus
            if (didiPatProgress == PatSurveyStatus.INPROGRESS.ordinal || didiPatProgress == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) {
                _selectedDidiList.value[_selectedDidiList.value.map { it.id }
                    .indexOf(didiId)].patSurveyStatus =
                    PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal
                didiDao.updateQuesSectionStatus(
                    didiId,
                    PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal
                )
            } else {
                _selectedDidiList.value[_selectedDidiList.value.map { it.id }
                    .indexOf(didiId)].patSurveyStatus =
                    PatSurveyStatus.NOT_AVAILABLE.ordinal
                didiDao.updateQuesSectionStatus(didiId, PatSurveyStatus.NOT_AVAILABLE.ordinal)
            }
            didiDao.updateNeedToPostPAT(true, didiId, prefRepo.getSelectedVillage().id)
            pendingDidiCount.value =
                didiDao.getAllPendingPATDidisCount(prefRepo.getSelectedVillage().id)
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

    fun addDidiForPatIdRequired(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiEntityList = didiDao.getAllDidisForVillage(prefRepo.getSelectedVillage().id)
            val didiEntity = filterDidiList[filterDidiList.map { it.id }.indexOf(didiId)]
            if (!didiEntityList.map { it.id }.contains(didiEntity.id)) {
                didiDao.insertDidi(
                    DidiEntity(
                        id = didiEntity.id,
                        serverId = didiEntity.serverId,
                        name = didiEntity.name,
                        address = didiEntity.address,
                        guardianName = didiEntity.guardianName,
                        relationship = didiEntity.relationship,
                        castId = didiEntity.castId,
                        castName = didiEntity.castName,
                        cohortId = didiEntity.cohortId,
                        cohortName = didiEntity.cohortName,
                        villageId = didiEntity.villageId,
                        wealth_ranking = didiEntity.wealth_ranking,
                        needsToPost = didiEntity.needsToPost,
                        localPath = didiEntity.localPath,
                        createdDate = didiEntity.createdDate,
                        modifiedDate = didiEntity.modifiedDate,
                        activeStatus = didiEntity.activeStatus,
                        patSurveyStatus = didiEntity.patSurveyStatus,
                        section1Status = didiEntity.section1Status,
                        section2Status = didiEntity.section2Status,
                        beneficiaryProcessStatus = didiEntity.beneficiaryProcessStatus,
                        shgFlag = didiEntity.shgFlag,
                        bpcComment = didiEntity.bpcComment,
                        bpcScore = didiEntity.bpcScore,
                        crpScore = didiEntity.crpScore,
                        crpComment = didiEntity.crpComment,
                        needsToPostBPCProcessStatus = true,
                    )
                )
            }
        }
    }

    fun getPatStepStatus(stepId: Int, callBack: (isComplete: Boolean) -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepStatus = stepsListDao.isStepComplete(stepId, villageId)
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
            val step  = stepsListDao.getAllStepsForVillage(prefRepo.getSelectedVillage().id).sortedBy { it.orderNumber }.last()
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

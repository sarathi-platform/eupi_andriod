package com.patsurvey.nudge.activities.ui.bpc.bpc_add_more_did_screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.patsurvey.nudge.activities.ui.bpc.ReplaceHelper
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.BpcNonSelectedDidiEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.dao.BpcNonSelectedDidiDao
import com.patsurvey.nudge.database.dao.BpcSelectedDidiDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.utils.BpcDidiSelectionStatus
import com.patsurvey.nudge.utils.PatSurveyStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BpcAddMoreDidiViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val tolaDao: TolaDao,
    val bpcSelectedDidiDao: BpcSelectedDidiDao,
    val bpcNonSelectedDidiDao: BpcNonSelectedDidiDao
): BaseViewModel() {

    val pendingDidiCount = mutableStateOf(0)
    private val _nonSelectedDidiList = MutableStateFlow(listOf<BpcNonSelectedDidiEntity>())
    val nonSelectedDidiList: StateFlow<List<BpcNonSelectedDidiEntity>> get() = _nonSelectedDidiList

    private val _tolaList = MutableStateFlow(listOf<TolaEntity>())
    val tolaList: StateFlow<List<TolaEntity>> get() = _tolaList

    var filterDidiList by mutableStateOf(listOf<BpcNonSelectedDidiEntity>())

    private var _inclusiveQueList = MutableStateFlow(listOf<SectionAnswerEntity>())
    val  inclusiveQueList: StateFlow<List<SectionAnswerEntity>> get() = _inclusiveQueList

    var tolaMapList by mutableStateOf(mapOf<String, List<BpcNonSelectedDidiEntity>>())
        private set

    var filterTolaMapList by mutableStateOf(mapOf<String, List<BpcNonSelectedDidiEntity>>())
        private set

    var villageId: Int = -1
    var stepId: Int = -1

    private var _markedNotAvailable = MutableStateFlow(mutableListOf<Int>())

    init {
        villageId = prefRepo.getSelectedVillage().id
//        fetchDidiFromDb()
    }

    fun fetchDidiFromDb() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val localNonSeletctedDidiList = bpcNonSelectedDidiDao.fetchAllNonSelectedDidiForVillage(prefRepo.getSelectedVillage().id)
//            val localUnselectedDidiList = bpcNonSelectedDidiDao.fetchAllNonSelectedDidiForVillage(prefRepo.getSelectedVillage().id)
            _nonSelectedDidiList.value = localNonSeletctedDidiList
            _tolaList.emit(tolaDao.getAllTolasForVillage(prefRepo.getSelectedVillage().id))
            filterDidiList = nonSelectedDidiList.value
//            pendingDidiCount.value = bpcSelectedDidiDao.getAllPendingPATDidisCount(prefRepo.getSelectedVillage().id)
        }
    }

    fun filterList() {
        val map = mutableMapOf<String, MutableList<BpcNonSelectedDidiEntity>>()
        nonSelectedDidiList.value.forEachIndexed { _, didiDetailsModel ->
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
                    val filteredList = ArrayList<BpcNonSelectedDidiEntity>()
                    nonSelectedDidiList.value.forEach { didi ->
                        if (didi.name.lowercase().contains(query.lowercase())) {
                            filteredList.add(didi)
                        }
                    }
                    filteredList
                } else {
                    nonSelectedDidiList.value
                }
            } else {
                if (query.isNotEmpty()) {
                    val fList = mutableMapOf<String, MutableList<BpcNonSelectedDidiEntity>>()
                    tolaMapList.keys.forEach { key ->
                        val newDidiList = ArrayList<BpcNonSelectedDidiEntity>()
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
        }catch (ex: Exception){
            ex.printStackTrace()
            Log.e("BpcAddMoreDidiViewModel", "performQuery -> Exception1 : performQuery: ${ex.message}" )
        }

    }

    override fun onServerError(error: ErrorModel?) {
        TODO("Not yet implemented")
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        TODO("Not yet implemented")
    }

    fun markCheckedDidisSelected(checkedIds: SnapshotStateList<Int>) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            checkedIds.forEach {  didiId ->
                bpcNonSelectedDidiDao.markDidiSelected(didiId, BpcDidiSelectionStatus.SELECTED.ordinal)
            }
        }
    }

    fun replaceDidi(checkedIds: SnapshotStateList<Int>) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            checkedIds.forEach {  didiId ->
                bpcNonSelectedDidiDao.markDidiSelected(didiId, BpcDidiSelectionStatus.SELECTED.ordinal)
                ReplaceHelper.didiForReplacement = bpcNonSelectedDidiDao.getNonSelectedDidi(didiId)
//                if (didiToBeReplaced.first != -1 && didiToBeReplaced.second != -1) {
                val isDidiInSelectedDao = bpcSelectedDidiDao.isDidiAvailableInSelectedTable(ReplaceHelper.didiToBeReplaced.value.second)
                if (isDidiInSelectedDao > 0) {
                    bpcSelectedDidiDao.markDidiSelected(
                        ReplaceHelper.didiToBeReplaced.value.second,
                        BpcDidiSelectionStatus.REPLACED.ordinal
                    )
                    bpcSelectedDidiDao.updateSelDidiPatSurveyStatus(ReplaceHelper.didiToBeReplaced.value.second, PatSurveyStatus.NOT_AVAILABLE.ordinal)
                } else {
                    bpcNonSelectedDidiDao.markDidiSelected(
                        ReplaceHelper.didiToBeReplaced.value.second,
                        BpcDidiSelectionStatus.REPLACED.ordinal
                    )
                    bpcNonSelectedDidiDao.updateNonSelDidiPatSurveyStatus(ReplaceHelper.didiToBeReplaced.value.second, PatSurveyStatus.NOT_AVAILABLE.ordinal)
                }
//                }
//                removeDidiFromSelectedList(bpcSelectedDidiDao)
            }
        }
    }
}
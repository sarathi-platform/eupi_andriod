package com.patsurvey.nudge.activities.ui.bpc.bpc_add_more_did_screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_STATE_ID
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
    val tolaDao: TolaDao
): BaseViewModel() {

    val pendingDidiCount = mutableStateOf(0)
    private val _nonSelectedDidiList = MutableStateFlow(listOf<DidiEntity>())
    val nonSelectedDidiList: StateFlow<List<DidiEntity>> get() = _nonSelectedDidiList

    private val _tolaList = MutableStateFlow(listOf<TolaEntity>())
    val tolaList: StateFlow<List<TolaEntity>> get() = _tolaList

    var filterDidiList by mutableStateOf(listOf<DidiEntity>())

    private var _inclusiveQueList = MutableStateFlow(listOf<SectionAnswerEntity>())
    val  inclusiveQueList: StateFlow<List<SectionAnswerEntity>> get() = _inclusiveQueList

    var tolaMapList by mutableStateOf(mapOf<String, List<DidiEntity>>())
        private set

    var filterTolaMapList by mutableStateOf(mapOf<String, List<DidiEntity>>())
        private set

    var villageId: Int = -1
    var stepId: Int = -1

    private var _markedNotAvailable = MutableStateFlow(mutableListOf<Int>())

    init {
        villageId = prefRepo.getSelectedVillage().id
//        fetchDidiFromDb()
    }
   fun getStateId():Int{
       return prefRepo.getStateId()
   }

    fun fetchDidiFromDb() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _tolaList.emit(tolaDao.getAllTolasForVillage(prefRepo.getSelectedVillage().id))
            filterDidiList = nonSelectedDidiList.value
        }
    }

    fun filterList() {
        val map = mutableMapOf<String, MutableList<DidiEntity>>()
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
                    val filteredList = ArrayList<DidiEntity>()
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
//                bpcNonSelectedDidiDao.markDidiSelected(didiId, BpcDidiSelectionStatus.SELECTED.ordinal)
            }
        }
    }

   /* fun replaceDidi(checkedIds: SnapshotStateList<Int>) {
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
    }*/
}
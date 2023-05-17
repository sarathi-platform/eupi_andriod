package com.patsurvey.nudge.activities.ui.socialmapping

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.dao.*
import com.patsurvey.nudge.intefaces.LocalDbListener
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.WealthRank
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WealthRankingViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val tolaDao: TolaDao,
    val didiDao: DidiDao,
    val stepsListDao: StepsListDao,
    val villageListDao: VillageListDao,
    val lastSelectedTolaDao: LastSelectedTolaDao,
    val apiService: ApiService
):BaseViewModel() {
    private val _expandedCardIdsList = MutableStateFlow(listOf<Int>())
    private val _didiList = MutableStateFlow(listOf<DidiEntity>())
    val expandedCardIdsList: StateFlow<List<Int>> get() = _expandedCardIdsList
    val didiList: StateFlow<List<DidiEntity>> get() = _didiList

    val shouldShowBottomButton = mutableStateOf(false)

    var filterDidiList by mutableStateOf(listOf<DidiEntity>())
        private set

    var tolaMapList by mutableStateOf(mapOf<String, List<DidiEntity>>())
        private set

    var filterTolaMapList by mutableStateOf(mapOf<String, List<DidiEntity>>())
        private set

    var villageId: Int = -1
    var stepId: Int = -1

    val showLoader = mutableStateOf(false)

    init {
        villageId = prefRepo.getSelectedVillage().id
        fetchDidisFromDB()
    }
    fun fetchDidisFromDB(){
        showLoader.value = true
        job= CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO){
                _didiList.emit(didiDao.getAllDidisForVillage(villageId))
                filterDidiList=didiList.value
                showLoader.value = false
            }
        }
    }


    fun onCardArrowClicked(cardId: Int) {
        _expandedCardIdsList.value = _expandedCardIdsList.value.toMutableList().also { list ->
            if (list.contains(cardId)) list.remove(cardId) else {
                list.clear()
                list.add(cardId)
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
        filterTolaMapList=map

    }

    fun performQuery(query: String, isTolaFilterSelected:Boolean) {
        if(!isTolaFilterSelected){
            filterDidiList = if(query.isNotEmpty()) {
                val filteredList = ArrayList<DidiEntity>()
                didiList.value.forEach { didi ->
                    if (didi.name.lowercase().contains(query.lowercase())) {
                        filteredList.add(didi)
                    }
                }
                filteredList
            }else {
                didiList.value
            }
        }else{
            if(query.isNotEmpty()){
                val fList= mutableMapOf<String, MutableList<DidiEntity>>()
                tolaMapList.keys.forEach { key->
                    val newDidiList= ArrayList<DidiEntity>()
                    tolaMapList[key]?.forEach { didi->
                        if (didi.name.lowercase().contains(query.lowercase())) {
                            newDidiList.add(didi)
                        }
                    }
                    if(newDidiList.isNotEmpty())
                        fList[key]=newDidiList
                }
                filterTolaMapList=fList
            }else{
                filterTolaMapList=tolaMapList
            }
        }
    }

    fun updateDidiRankInDb(id: Int, rank: String) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val updatedDidiList = didiList.value
            didiDao.updateDidiRank(id, rank)
            val remainingDidi = didiDao.getUnrankedDidiCount(villageId)
            updatedDidiList[id].wealth_ranking = rank
            _didiList.emit(updatedDidiList)
            withContext(Dispatchers.Main) {
                shouldShowBottomButton.value = remainingDidi == 0
            }
            onError("WealthRankingViewModel", "here is error")
        }
    }

}
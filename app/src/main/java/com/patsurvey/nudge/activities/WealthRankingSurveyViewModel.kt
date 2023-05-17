package com.patsurvey.nudge.activities

import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.*
import com.patsurvey.nudge.network.interfaces.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WealthRankingSurveyViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val tolaDao: TolaDao,
    val didiDao: DidiDao,
    val stepsListDao: StepsListDao,
    val villageListDao: VillageListDao,
    val lastSelectedTolaDao: LastSelectedTolaDao,
    val apiService: ApiService
): BaseViewModel() {

    private val _didiList = MutableStateFlow(listOf<DidiEntity>())
    val didiList: StateFlow<List<DidiEntity>> get() = _didiList
    private val _expandedCardIdsList = MutableStateFlow(listOf<Int>())
    val expandedCardIdsList: StateFlow<List<Int>> get() = _expandedCardIdsList


    var villageId: Int = -1
    var stepId: Int = -1

    var selectedVillage: VillageEntity? = null

    init {
        selectedVillage = prefRepo.getSelectedVillage()
        villageId = selectedVillage?.id ?: -1
        fetchDidisFromDB()
    }

    fun fetchDidisFromDB(){
        job= CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO){
                _didiList.emit(didiDao.getAllDidisForVillage(villageId))
            }
        }
    }

    fun onCardArrowClicked(cardId: Int) {
        _expandedCardIdsList.value = _expandedCardIdsList.value.toMutableList().also { list ->
            if (list.contains(cardId)) {
                list.remove(cardId)
            } else {
                list.add(cardId)
            }
        }
    }

}

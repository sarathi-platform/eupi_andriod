package com.patsurvey.nudge.activities.ui.socialmapping

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.patsurvey.nudge.CheckDBStatus
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.converters.BeneficiaryProcessStatusModel
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.LastSelectedTolaDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.request.EditDidiWealthRankingRequest
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.network.model.ErrorModelWithApi
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.StepType
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
) : BaseViewModel() {
    private val _expandedCardIdsList = MutableStateFlow(listOf<Int>())
    private val _didiList = MutableStateFlow(listOf<DidiEntity>())
    val expandedCardIdsList: StateFlow<List<Int>> get() = _expandedCardIdsList
    val didiList: StateFlow<List<DidiEntity>> get() = _didiList

    val shouldShowBottomButton = mutableStateOf(didiList.value.any { it.wealth_ranking == WealthRank.NOT_RANKED.rank })

    private var _filterDidiList = MutableStateFlow(listOf<DidiEntity>())
    val filterDidiList: StateFlow<List<DidiEntity>> get() = _filterDidiList

    var tolaMapList by mutableStateOf(mapOf<String, List<DidiEntity>>())
        private set

    var filterTolaMapList by mutableStateOf(mapOf<String, List<DidiEntity>>())
        private set

    var villageId: Int = -1
    var stepId: Int = -1
    val isTolaSynced = mutableStateOf(0)
    val isDidiSynced = mutableStateOf(0)

    val showLoader = mutableStateOf(false)

    init {
        villageId = prefRepo.getSelectedVillage().id
        fetchDidisFromDB()
    }

    fun fetchDidisFromDB() {
        showLoader.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
                _didiList.emit(didiDao.getAllDidisForVillage(villageId))
                _filterDidiList.value = didiList.value
                showLoader.value = false
            }
        }
    }


    fun onCardArrowClicked(cardId: Int) {
        _expandedCardIdsList.value = _expandedCardIdsList.value.toMutableList().also { list ->
            if (list.contains(cardId))
                list.remove(cardId)
            else {
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
        filterTolaMapList = map

    }

    fun performQuery(query: String, isTolaFilterSelected: Boolean) {
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
    }

    fun updateDidiRankInDb(didiEntity: DidiEntity, rank: String, networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                var didiId=didiEntity.id
                val updatedDidiList = didiList.value
                if(didiEntity.serverId == 0){
                    didiDao.updateDidiRank(didiEntity.id, rank)
                    didiDao.updateDidiNeedToPostWealthRank(didiEntity.id,true)
                    didiDao.updateModifiedDate(System.currentTimeMillis(),didiEntity.id)
                    didiDao.updateBeneficiaryProcessStatus(
                        didiEntity.id, listOf(
                            BeneficiaryProcessStatusModel(
                                StepType.TRANSECT_WALK.name,
                                StepStatus.COMPLETED.name
                            ),
                            BeneficiaryProcessStatusModel(
                                StepType.SOCIAL_MAPPING.name,
                                StepStatus.COMPLETED.name
                            ),
                            BeneficiaryProcessStatusModel(StepType.WEALTH_RANKING.name, rank)
                        )
                    )
                }else{
                     didiId=didiEntity.serverId
                    didiDao.updateDidiRankUsingServerId(didiEntity.serverId, rank)
                    didiDao.updateDidiNeedToPostWealthRankServerId(didiEntity.serverId,true)
                    didiDao.updateModifiedDateServerId(System.currentTimeMillis(),didiEntity.serverId)
                    didiDao.updateBeneficiaryProcessStatusServerId(
                        didiEntity.serverId, listOf(
                            BeneficiaryProcessStatusModel(
                                StepType.TRANSECT_WALK.name,
                                StepStatus.COMPLETED.name
                            ),
                            BeneficiaryProcessStatusModel(
                                StepType.SOCIAL_MAPPING.name,
                                StepStatus.COMPLETED.name
                            ),
                            BeneficiaryProcessStatusModel(StepType.WEALTH_RANKING.name, rank)
                        )
                    )
                }


                updatedDidiList[updatedDidiList.map { it.serverId }.indexOf(didiId)].wealth_ranking = rank
                _didiList.value = updatedDidiList
//                onError("WealthRankingViewModel", "here is error")
                CheckDBStatus(this@WealthRankingViewModel).isFirstStepNeedToBeSync(tolaDao){
                    isTolaSynced.value=it
                }
                CheckDBStatus(this@WealthRankingViewModel).isSecondStepNeedToBeSync(didiDao){
                    isDidiSynced.value=it
                }
                if(isTolaSynced.value == 2 && isDidiSynced.value == 2) {
                    withContext(Dispatchers.IO) {
                        val updateWealthRankResponse = apiService.updateDidiRanking(
                            listOf(
                                EditDidiWealthRankingRequest(
                                    didiId,
                                    StepType.WEALTH_RANKING.name,
                                    rank,
                                    localModifiedDate = System.currentTimeMillis() ?:0
                                ),
                                EditDidiWealthRankingRequest(
                                    didiId,
                                    StepType.SOCIAL_MAPPING.name,
                                    StepStatus.COMPLETED.name,
                                    localModifiedDate = System.currentTimeMillis() ?:0
                                )
                            )
                        )
                        if (updateWealthRankResponse.status.equals(SUCCESS, true)) {
                            if (didiEntity.serverId == 0) {
                                didiDao.setNeedToPostRanking(didiEntity.id, false)
                            } else
                                didiDao.setNeedToPostRankingServerId(didiEntity.serverId, false)
                        } else {
                            networkCallbackListener.onFailed()
                        }
                    }
                }
            } catch (ex: Exception) {
                Log.e("exception",ex.printStackTrace().toString())
                Log.e("exception",ex.toString())
                networkCallbackListener.onFailed()
                onError("WealthRankingViewModel", "here is error: ${ex.message} \n${ex.stackTrace}")
            }
        }
    }

    fun getWealthRankingStepStatus(stepId: Int, callBack: (isComplete: Boolean) -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepStatus = stepsListDao.isStepComplete(stepId, villageId)
            withContext(Dispatchers.Main) {
                if (stepStatus == StepStatus.COMPLETED.ordinal) {
                    callBack(true)
                } else {
                    callBack(false)
                }
            }
        }
    }

    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        TODO("Not yet implemented")
    }

    fun closeLastCard(cardId: Int) {
        _expandedCardIdsList.value = _expandedCardIdsList.value.toMutableList().also { list ->
            list.clear()
        }
    }

}
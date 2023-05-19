package com.patsurvey.nudge.activities

import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.*
import com.patsurvey.nudge.model.request.EditDidiWealthRankingRequest
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
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

    val showBottomButton = mutableStateOf(true)

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

    fun callWorkFlowAPI(villageId: Int,stepId: Int){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val dbResponse=stepsListDao.getStepForVillage(villageId, stepId)
                if(dbResponse.workFlowId>0){
                    val response = apiService.editWorkFlow(
                        listOf(
                            EditWorkFlowRequest(dbResponse.workFlowId, StepStatus.COMPLETED.name)
                        ) )
                    withContext(Dispatchers.IO){
                        if (response.status.equals(SUCCESS, true)) {
                            response.data?.let {
                                stepsListDao.updateWorkflowId(stepId,dbResponse.workFlowId,villageId,it[0].status)
                            }
                        }else{
                            onError(tag = "ProgressScreenViewModel", "Error : ${response.message}")
                        }
                    }
                }

            }catch (ex:Exception){
                onError(tag = "ProgressScreenViewModel", "Error : ${ex.localizedMessage}")
            }
        }
    }



    fun markWealthRakningComplete(villageId: Int, stepId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val existingList = villageListDao.getVillage(villageId).steps_completed
            val updatedCompletedStepsList = mutableListOf<Int>()
            if (!existingList.isNullOrEmpty()) {
                existingList.forEach {
                    updatedCompletedStepsList.add(it)
                }
            }
            updatedCompletedStepsList.add(stepId)
            villageListDao.updateLastCompleteStep(villageId, updatedCompletedStepsList)
            stepsListDao.markStepAsCompleteOrInProgress(stepId, StepStatus.COMPLETED.ordinal,villageId)
            val stepDetails=stepsListDao.getStepForVillage(villageId, stepId)
            if(stepDetails.orderNumber<stepsListDao.getAllSteps().size){
                stepsListDao.markStepAsInProgress((stepDetails.orderNumber+1),StepStatus.INPROGRESS.ordinal,villageId)
            }
        }
    }

    fun saveWealthRankingCompletionDate() {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val date = dateFormat.format(currentTime)
        prefRepo.savePref(PREF_WEALTH_RANKING_COMPLETION_DATE, date)
    }

    fun getWealthRankingStepStatus(stepId: Int, callBack: (isComplete: Boolean)->Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepStatus = stepsListDao.isStepComplete(stepId, prefRepo.getSelectedVillage().id)
            withContext(Dispatchers.Main) {
                if (stepStatus == StepStatus.COMPLETED.ordinal) {
                    callBack(true)
                } else {
                    callBack(false)
                }
            }
        }
    }

    fun updateWealthRankingToNetwork(){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO){
                val needToPostDidiList=didiDao.getAllNeedToPostDidiRanking(true,prefRepo.getSelectedVillage().id)
                if(needToPostDidiList.isNotEmpty()){
                    needToPostDidiList.forEach { didi->
                        launch {
                            didi.wealth_ranking?.let {
                                val updateWealthRankResponse=apiService.updateDidiRanking(
                                    listOf(
                                    EditDidiWealthRankingRequest(didi.id,StepType.WEALTH_RANKING.name,didi.wealth_ranking)))

                                if(updateWealthRankResponse.status.equals(SUCCESS,true)){
                                   didiDao.setNeedToPostRanking(didi.id,false)
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}

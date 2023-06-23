package com.patsurvey.nudge.activities.ui.bpc.progress_screens

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.BpcSummaryEntity
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.BpcSelectedDidiDao
import com.patsurvey.nudge.database.dao.BpcSummaryDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.request.AddWorkFlowRequest
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.StepStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BpcProgressScreenViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val apiService: ApiService,
    val villageListDao: VillageListDao,
    val stepsListDao: StepsListDao,
    val didiDao: DidiDao,
    val bpcSummaryDao: BpcSummaryDao,
    val bpcSelectedDidiDao: BpcSelectedDidiDao,
    val questionListDao: QuestionListDao,
    val answerDao: AnswerDao
): BaseViewModel() {

    private val _stepsList = MutableStateFlow(listOf<StepListEntity>())
    val stepList: StateFlow<List<StepListEntity>> get() = _stepsList
    private val _villagList = MutableStateFlow(listOf<VillageEntity>())
    val villageList: StateFlow<List<VillageEntity>> get() = _villagList

    private val _summaryData = MutableStateFlow(BpcSummaryEntity.getEmptySummary())
    val summaryData: StateFlow<BpcSummaryEntity> get() = _summaryData

    val showLoader = mutableStateOf(false)

    val villageSelected = mutableStateOf(0)
    val selectedText = mutableStateOf("Select Village")

    val bpcCompletedDidiCount = mutableStateOf(0)

    fun isLoggedIn() = (prefRepo.getAccessToken()?.isNotEmpty() == true)

    init {
        fetchVillageList()
        fetchBpcSummaryData(prefRepo.getSelectedVillage().id)
    }

    fun fetchBpcSummaryData(villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val summary = bpcSummaryDao.getBpcSummaryForVillage(villageId)
            _summaryData.value = summary
        }
    }

    fun fetchVillageList(){
        showLoader.value = true
        job=viewModelScope.launch {
            withContext(Dispatchers.IO){
                val villageList=villageListDao.getAllVillages()
                val stepList = stepsListDao.getAllStepsForVillage(prefRepo.getSelectedVillage().id)
//                val tolaDBList=tolaDao.getAllTolasForVillage(prefRepo.getSelectedVillage().id)
                _villagList.value = villageList
//                _tolaList.emit(tolaDBList)
//                _didiList.emit(didiDao.getAllDidisForVillage(prefRepo.getSelectedVillage().id))
                withContext(Dispatchers.Main){
                    villageList.mapIndexed { index, villageEntity ->
                        if(prefRepo.getSelectedVillage().id==villageEntity.id){
                            villageSelected.value=index
                        }
                    }
                    _stepsList.value = stepList
                    selectedText.value = prefRepo.getSelectedVillage().name
//                    getStepsList(prefRepo.getSelectedVillage().id)
                    showLoader.value = false
                }
            }
        }
    }

    fun isStepComplete(stepId: Int,villageId: Int): LiveData<Int> {
        return stepsListDao.isStepCompleteLive(stepId,villageId)
    }

    fun updateSelectedVillage(selectedVillageEntity: VillageEntity) {
        prefRepo.saveSelectedVillage(selectedVillageEntity)
    }

    fun callWorkFlowAPI(){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val dbResponse=stepsListDao.getAllStepsForVillage(prefRepo.getSelectedVillage().id)
                val bpcStep = dbResponse.sortedBy { it.orderNumber }.last()
                if(bpcStep.workFlowId==0){
                    val response = apiService.addWorkFlow(
                        listOf(
                            AddWorkFlowRequest(
                                StepStatus.INPROGRESS.name,prefRepo.getSelectedVillage().id,
                                bpcStep.programId,bpcStep.id)
                        ) )
                    withContext(Dispatchers.IO){
                        if (response.status.equals(SUCCESS, true)) {
                            response.data?.let {
                                stepsListDao.updateWorkflowId(bpcStep.id, it[0].id, prefRepo.getSelectedVillage().id, it[0].status)
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

    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        TODO("Not yet implemented")
    }
    fun addDidisToDidiDaoIfNeeded() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiEntityList = didiDao.getAllDidisForVillage(prefRepo.getSelectedVillage().id)
            val selectedDidiList = bpcSelectedDidiDao.fetchAllSelectedDidiForVillage(prefRepo.getSelectedVillage().id)
            selectedDidiList.forEach { didiEntity->
                if (!didiEntityList.map { it.id }.contains(didiEntity.id)) {
                    didiDao.insertDidi(
                        DidiEntity.getDidiEntityFromSelectedDidiEntity(didiEntity)
                    )
                }
            }
        }
    }

    fun getBpcCompletedDidiCount() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.getAllDidisForVillage(prefRepo.getSelectedVillage().id)
            val verifiedDidiCount = didiList.filter { it.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && it.section2Status == PatSurveyStatus.COMPLETED.ordinal }.size
            withContext(Dispatchers.Main) {
                bpcCompletedDidiCount.value = verifiedDidiCount
            }
        }
    }

}

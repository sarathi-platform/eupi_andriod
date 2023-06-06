package com.patsurvey.nudge.activities.ui.progress


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.*
import com.patsurvey.nudge.model.request.AddWorkFlowRequest
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProgressScreenViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val apiInterface: ApiService,
    val stepsListDao: StepsListDao,
    val villageListDao: VillageListDao,
    val tolaDao: TolaDao,
    val didiDao: DidiDao,
    val casteListDao: CasteListDao,
    val answerDao: AnswerDao,
    val numericAnswerDao: NumericAnswerDao,
    val questionDao: QuestionListDao
) : BaseViewModel() {

    private val _stepsList = MutableStateFlow(listOf<StepListEntity>())
    private val _villagList = MutableStateFlow(listOf<VillageEntity>())
    private val _tolaList = MutableStateFlow(listOf<TolaEntity>())
    val tolaList: StateFlow<List<TolaEntity>> get() = _tolaList
    private val _didiList = MutableStateFlow(listOf<DidiEntity>())
    val didiList: StateFlow<List<DidiEntity>> get() = _didiList
    val stepList: StateFlow<List<StepListEntity>> get() = _stepsList
    val villageList: StateFlow<List<VillageEntity>> get() = _villagList
    val stepSelected = mutableStateOf(0)
    val villageSelected = mutableStateOf(0)
    val tolaCount = mutableStateOf(0)
    val didiCount = mutableStateOf(0)
    val poorDidiCount = mutableStateOf(0)
    val ultrPoorDidiCount = mutableStateOf(0)
    val endorsedDidiCount = mutableStateOf(0)
    val selectedText = mutableStateOf("Select Village")

    val showLoader = mutableStateOf(false)

    fun isLoggedIn() = (prefRepo.getAccessToken()?.isNotEmpty() == true)

    init {
        fetchVillageList()
    }

    private fun checkAndUpdateCompletedStepsForVillage() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val villageId = prefRepo.getSelectedVillage().id
            val updatedCompletedStepList = mutableListOf<Int>()
            stepList.value.forEach {
                if (it.isComplete == StepStatus.COMPLETED.ordinal) {
                    updatedCompletedStepList.add(it.id)
                }
            }
            villageListDao.updateLastCompleteStep(villageId, updatedCompletedStepList)
        }
    }

     fun getStepsList(villageId:Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList = stepsListDao.getAllStepsForVillage(villageId)
            val mDidiList = didiDao.getAllDidisForVillage(villageId)
            val mTolaList = tolaDao.getAllTolasForVillage(villageId)
            _tolaList.emit(mTolaList)
            _didiList.emit(mDidiList)
            val dbInProgressStep=stepsListDao.fetchLastInProgressStep(villageId,StepStatus.COMPLETED.ordinal)
            if(dbInProgressStep!=null){
                if(stepList.size>dbInProgressStep.orderNumber)
                    stepsListDao.markStepAsInProgress((dbInProgressStep.orderNumber+1),StepStatus.INPROGRESS.ordinal,villageId)
            }else{
                stepsListDao.markStepAsInProgress(1,StepStatus.INPROGRESS.ordinal,villageId)
            }
            withContext(Dispatchers.IO) {
                _stepsList.value = stepList
                tolaCount.value=_tolaList.value.size
                didiCount.value=didiList.value.size
                poorDidiCount.value = didiList.value.filter { it.wealth_ranking == WealthRank.POOR.rank }.size
                ultrPoorDidiCount.value = didiList.value.filter { it.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && it.section2Status == PatSurveyStatus.COMPLETED.ordinal }.size
                endorsedDidiCount.value = didiList.value.filter { it.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal }.size
                showLoader.value = false
            }
        }
    }





    fun fetchVillageList(){
        showLoader.value = true
        job=viewModelScope.launch {
            withContext(Dispatchers.IO){
                val villageList=villageListDao.getAllVillages()
                val tolaDBList=tolaDao.getAllTolasForVillage(prefRepo.getSelectedVillage().id)
                _villagList.value = villageList
                _tolaList.emit(tolaDBList)
                _didiList.emit(didiDao.getAllDidisForVillage(prefRepo.getSelectedVillage().id))
                withContext(Dispatchers.Main){
                    villageList.mapIndexed { index, villageEntity ->
                        if(prefRepo.getSelectedVillage().id==villageEntity.id){
                            villageSelected.value=index
                        }
                    }
                    selectedText.value = prefRepo.getSelectedVillage().name
                    getStepsList(prefRepo.getSelectedVillage().id)
                    showLoader.value = false
                }
            }
        }
    }

    fun isStepComplete(stepId: Int,villageId: Int): LiveData<Int> {
        return stepsListDao.isStepCompleteLive(stepId,villageId)
    }

    fun updateSelectedStep(stepId: Int) {
        val currentStepIndex = stepList.value.map { it.stepId }.indexOf(stepId)
        stepSelected.value = when (currentStepIndex) {
            in 0..4 -> currentStepIndex + 1
            5 -> {
                currentStepIndex
            }
            else -> {
                0
            }
        }
    }

    fun updateSelectedVillage(selectedVillageEntity: VillageEntity) {
        prefRepo.saveSelectedVillage(selectedVillageEntity)
    }
    fun findInProgressStep(villageId: Int){
        job= CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val dbInProgressStep=stepsListDao.fetchLastInProgressStep(villageId,StepStatus.COMPLETED.ordinal)
            if(dbInProgressStep!=null){
                if(stepList.value.size>dbInProgressStep.orderNumber)
                    stepsListDao.markStepAsInProgress((dbInProgressStep.orderNumber+1),StepStatus.INPROGRESS.ordinal,villageId)
            }else{
                stepsListDao.markStepAsInProgress(1,StepStatus.INPROGRESS.ordinal,villageId)
            }
        }

    }

     fun callWorkFlowAPI(villageId: Int,stepId: Int,programId:Int){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val dbResponse=stepsListDao.getStepForVillage(villageId, stepId)
                if(dbResponse.workFlowId==0){
                    val response = apiInterface.addWorkFlow(
                        listOf(AddWorkFlowRequest(StepStatus.INPROGRESS.name,villageId,
                            programId,stepId)) )
                    withContext(Dispatchers.IO){
                        if (response.status.equals(SUCCESS, true)) {
                            response.data?.let {
                                stepsListDao.updateWorkflowId(stepId,it[0].id,villageId,it[0].status)
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
        showLoader.value = false
    }

}
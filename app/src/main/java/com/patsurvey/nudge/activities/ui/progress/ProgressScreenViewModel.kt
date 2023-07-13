package com.patsurvey.nudge.activities.ui.progress


import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.request.AddWorkFlowRequest
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.EMPTY_TOLA_NAME
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.WealthRank
import com.patsurvey.nudge.utils.updateLastSyncTime
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

    val isVoEndorsementComplete = mutableStateOf(mutableMapOf<Int, Boolean>())

    fun isLoggedIn() = (prefRepo.getAccessToken()?.isNotEmpty() == true)

    init {
        fetchVillageList()
        setVoEndorsementCompleteForVillages()
    }

    fun setVoEndorsementCompleteForVillages() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                villageList.value.forEach { village ->
                    val stepList = stepsListDao.getAllStepsForVillage(village.id)
                    isVoEndorsementComplete.value[village.id] = (stepList.sortedBy { it.orderNumber }[4].isComplete == StepStatus.COMPLETED.ordinal)
                }
            } catch (ex: Exception) {
                Log.d("TAG", "setVoEndorsementCompleteForVillages: exception -> $ex")
            }

        }
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
            _tolaList.value = mTolaList
            _didiList.value = mDidiList
            val dbInProgressStep=stepsListDao.fetchLastInProgressStep(villageId,StepStatus.COMPLETED.ordinal)
            if(dbInProgressStep!=null){
                if(stepList.size>dbInProgressStep.orderNumber) {
                    stepsListDao.markStepAsInProgress(
                        (dbInProgressStep.orderNumber + 1),
                        StepStatus.INPROGRESS.ordinal,
                        villageId
                    )
                }
            }else{
                stepsListDao.markStepAsInProgress(1,StepStatus.INPROGRESS.ordinal,villageId)
            }
            withContext(Dispatchers.IO) {
                _stepsList.value = stepList
                tolaCount.value=_tolaList.value.filter { it.name != EMPTY_TOLA_NAME }.size
                didiCount.value=didiList.value.filter { it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal }.size
                poorDidiCount.value = didiList.value.filter { it.wealth_ranking == WealthRank.POOR.rank && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal }.size
                ultrPoorDidiCount.value = didiList.value.filter { it.forVoEndorsement==1 && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal}.size
                endorsedDidiCount.value = didiList.value.filter { it.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal }.size
            }
        }
    }





    fun fetchVillageList(){
        showLoader.value = true
        job=viewModelScope.launch {
            withContext(Dispatchers.IO){
                var villageList = emptyList<VillageEntity>()
                val villageCountList= villageListDao.getAllVillages(prefRepo.getAppLanguageId()?:2)
                villageList = villageCountList.ifEmpty {
                    villageListDao.getAllVillages(2)
                }

                Log.d("TAG", "fetchVillageList size: ${villageList.size} ")

                val tolaDBList=tolaDao.getAllTolasForVillage(prefRepo.getSelectedVillage().id)
                _villagList.value = villageList
                _tolaList.emit(tolaDBList)
                _didiList.emit(didiDao.getAllDidisForVillage(prefRepo.getSelectedVillage().id))
                withContext(Dispatchers.Main) {
                    Log.d("TAG", "fetchVillageList VillageList Size: ${villageList.size} ")
                    if (villageList.isNotEmpty()) {
                        villageList.mapIndexed { index, villageEntity ->
                            if (prefRepo.getSelectedVillage().id == villageEntity.id) {
                                villageSelected.value = index
                                selectedText.value = villageEntity.name
                            }
                        }
//                    selectedText.value = villageList[villageList.map { it.id }.indexOf(prefRepo.getSelectedVillage().id)].name
//                    selectedText.value = villageList[villageSelected.value].name
                        getStepsList(prefRepo.getSelectedVillage().id)
                    }
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
//                stepsListDao.updateNeedToPost(dbInProgressStep.id, true)
            }else{
                stepsListDao.markStepAsInProgress(1,StepStatus.INPROGRESS.ordinal,villageId)
            }
        }

    }

     fun callWorkFlowAPI(villageId: Int,stepId: Int,programId:Int){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val dbResponse=stepsListDao.getStepForVillage(villageId, stepId)
                NudgeLogger.d("ProgressScreenViewModel", "callWorkFlowAPI -> dbResponse: ${dbResponse.toString()}")
                if(dbResponse.workFlowId==0){
                    val workFlowRequest = listOf(
                        AddWorkFlowRequest(
                            StepStatus.INPROGRESS.name, villageId,
                            programId, stepId
                        )
                    )
                    NudgeLogger.d(
                        "ProgressScreenViewModel",
                        "callWorkFlowAPI -> workFlowRequest: ${workFlowRequest}"
                    )
                    val response = apiInterface.addWorkFlow(workFlowRequest)
                    NudgeLogger.d(
                        "ProgressScreenViewModel",
                        "callWorkFlowAPI -> response: ${response}"
                    )
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.let {
                            NudgeLogger.d("ProgressScreenViewModel", "callWorkFlowAPI -> stepsListDao.updateWorkflowId before for stepId: $stepId, workFlowId: ${it[0].id}")
                            stepsListDao.updateWorkflowId(stepId, it[0].id, villageId, it[0].status)
                            NudgeLogger.d("ProgressScreenViewModel", "callWorkFlowAPI -> stepsListDao.updateWorkflowId after for stepId: $stepId, villageId: $villageId, workFlowId: ${it[0].id}")
                            NudgeLogger.d("ProgressScreenViewModel", "callWorkFlowAPI -> stepsListDao.updateNeedToPost before for stepId: $stepId, villageId: $villageId, needToPost: false")
                            stepsListDao.updateNeedToPost(stepId, villageId, false)
                            NudgeLogger.d("ProgressScreenViewModel", "callWorkFlowAPI -> stepsListDao.updateNeedToPost after for stepId: $stepId, villageId: $villageId, needToPost: false")
                        }
                    } else {
                        onError(tag = "ProgressScreenViewModel", "Error : ${response.message}")
                    }
                    if (!response.lastSyncTime.isNullOrEmpty()) {
                        updateLastSyncTime(prefRepo, response.lastSyncTime)
                    }
                }

            }catch (ex:Exception){
                onCatchError(ex, ApiType.ADD_WORK_FLOW_API)
                onError(tag = "ProgressScreenViewModel", "Error : ${ex.localizedMessage}")
            }
        }
    }
    override fun onServerError(error: ErrorModel?) {
        showLoader.value = false
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        TODO("Not yet implemented")
    }
}
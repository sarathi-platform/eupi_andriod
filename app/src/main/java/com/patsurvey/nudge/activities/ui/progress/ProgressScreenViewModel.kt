package com.patsurvey.nudge.activities.ui.progress


import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.patsurvey.nudge.MyApplication
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.request.AddWorkFlowRequest
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.EMPTY_TOLA_NAME
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.WealthRank
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
class ProgressScreenViewModel @Inject constructor(
    private val progressScreenRepository: ProgressScreenRepository
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

    fun isLoggedIn() = (progressScreenRepository.getAccessToken()?.isNotEmpty() == true)

    fun init() {
        showLoader.value = true
        MyApplication.appScopeLaunch(Dispatchers.IO) {
            fetchVillageList()
            delay(100)
            setVoEndorsementCompleteForVillages()
            delay(200)
            withContext(Dispatchers.Main) {
                showLoader.value = false
            }
        }
    }

    fun setVoEndorsementCompleteForVillages() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                villageList.value.forEach { village ->
                    val stepList = progressScreenRepository.getAllStepsForVillage(village.id)
                    isVoEndorsementComplete.value[village.id] =
                        (stepList.sortedBy { it.orderNumber }[4].isComplete == StepStatus.COMPLETED.ordinal)
                }
            } catch (ex: Exception) {
                Log.d("TAG", "setVoEndorsementCompleteForVillages: exception -> $ex")
            }
        }
    }

    private fun checkAndUpdateCompletedStepsForVillage() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val villageId = progressScreenRepository.getSelectedVillage().id
            val updatedCompletedStepList = mutableListOf<Int>()
            stepList.value.forEach {
                if (it.isComplete == StepStatus.COMPLETED.ordinal) {
                    updatedCompletedStepList.add(it.id)
                }
            }
            progressScreenRepository.updateLastCompleteStep(villageId, updatedCompletedStepList)
        }
    }

    fun getStepsList(villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList = progressScreenRepository.getAllStepsForVillage(villageId)
            val mDidiList = progressScreenRepository.getAllDidisForVillage(villageId)
            val mTolaList = progressScreenRepository.getAllTolasForVillage(villageId)
            _tolaList.value = mTolaList
            _didiList.value = mDidiList
            val dbInProgressStep = progressScreenRepository.fetchLastInProgressStep(
                villageId,
                StepStatus.COMPLETED.ordinal
            )
            if (dbInProgressStep != null) {
                if (stepList.size > dbInProgressStep.orderNumber) {
                    progressScreenRepository.markStepAsInProgress(
                        (dbInProgressStep.orderNumber + 1),
                        StepStatus.INPROGRESS.ordinal,
                        villageId
                    )
                }
            } else {
                progressScreenRepository.markStepAsInProgress(
                    1,
                    StepStatus.INPROGRESS.ordinal,
                    villageId
                )
            }
            withContext(Dispatchers.IO) {
                _stepsList.value = stepList
                tolaCount.value = _tolaList.value.filter { it.name != EMPTY_TOLA_NAME }.size
                didiCount.value =
                    didiList.value.filter { it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal }.size
                poorDidiCount.value =
                    didiList.value.filter { it.wealth_ranking == WealthRank.POOR.rank && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal }.size
                ultrPoorDidiCount.value = didiList.value.filter {
                    it.forVoEndorsement == 1 && it.section2Status == PatSurveyStatus.COMPLETED.ordinal
                            && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal
                }.size
                endorsedDidiCount.value = didiList.value.filter {
                    it.forVoEndorsement == 1 && it.section2Status == PatSurveyStatus.COMPLETED.ordinal
                            && it.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal
                }.size
            }
        }
    }


    fun fetchVillageList() {
        job = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var villageList = emptyList<VillageEntity>()
                val villageCountList = progressScreenRepository.getAllVillages(
                    progressScreenRepository.getAppLanguageId() ?: 2
                )
                villageList = villageCountList.ifEmpty {
                    progressScreenRepository.getAllVillages(2)
                }

                NudgeLogger.d(
                    "ProgressScreenViewModel",
                    "fetchVillageList size: ${villageList.size} "
                )

                val tolaDBList =
                    progressScreenRepository.getAllTolasForVillage(progressScreenRepository.getSelectedVillage().id)
                _villagList.value = villageList
                _tolaList.emit(tolaDBList)
                _didiList.emit(
                    progressScreenRepository.getAllDidisForVillage(
                        progressScreenRepository.getSelectedVillage().id
                    )
                )
                withContext(Dispatchers.Main) {
                    NudgeLogger.d(
                        "ProgressScreenViewModel",
                        "fetchVillageList VillageList  $villageList Size: ${villageList.size} "
                    )
                    if (villageList.isNotEmpty()) {
                        villageList.mapIndexed { index, villageEntity ->
                            if (progressScreenRepository.getSelectedVillage().id == villageEntity.id) {
                                villageSelected.value = index
                                selectedText.value = villageEntity.name
                            }
                        }
//                    selectedText.value = villageList[villageList.map { it.id }.indexOf(prefRepo.getSelectedVillage().id)].name
//                    selectedText.value = villageList[villageSelected.value].name
                        getStepsList(progressScreenRepository.getSelectedVillage().id)
                    }
//                    showLoader.value = false
                }
            }
        }
    }

    fun isStepComplete(stepId: Int, villageId: Int): LiveData<Int> {
        return progressScreenRepository.isStepCompleteLiveForCrp(stepId, villageId)
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
        progressScreenRepository.saveSelectedVillage(selectedVillageEntity)
    }

    fun findInProgressStep(villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val dbInProgressStep = progressScreenRepository.fetchLastInProgressStep(
                villageId,
                StepStatus.COMPLETED.ordinal
            )
            if (dbInProgressStep != null) {
                if (stepList.value.size > dbInProgressStep.orderNumber)
                    progressScreenRepository.markStepAsInProgress(
                        (dbInProgressStep.orderNumber + 1),
                        StepStatus.INPROGRESS.ordinal,
                        villageId
                    )
//                stepsListDao.updateNeedToPost(dbInProgressStep.id, true)
            } else {
                progressScreenRepository.markStepAsInProgress(
                    1,
                    StepStatus.INPROGRESS.ordinal,
                    villageId
                )
            }
        }
    }

    fun callWorkFlowAPI(villageId: Int, stepId: Int, programId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val dbResponse = progressScreenRepository.getStepForVillage(villageId, stepId)

                NudgeLogger.d(
                    "ProgressScreenViewModel",
                    "callWorkFlowAPI -> dbResponse: ${dbResponse.toString()}"
                )
                if (dbResponse.workFlowId == 0) {
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
                    val response = progressScreenRepository.addWorkFlow(workFlowRequest)
                    NudgeLogger.d(
                        "ProgressScreenViewModel",
                        "callWorkFlowAPI -> response: status: ${response.status}, message: ${response.message}, data: ${response.data.toString()}"
                    )
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.let {
                            NudgeLogger.d(
                                "ProgressScreenViewModel",
                                "callWorkFlowAPI -> stepsListDao.updateWorkflowId before for stepId: $stepId, workFlowId: ${it[0].id}"
                            )
                            progressScreenRepository.updateWorkflowId(
                                stepId,
                                it[0].id,
                                villageId,
                                it[0].status
                            )
                            NudgeLogger.d(
                                "ProgressScreenViewModel",
                                "callWorkFlowAPI -> stepsListDao.updateWorkflowId after for stepId: $stepId, villageId: $villageId, workFlowId: ${it[0].id}"
                            )
                            NudgeLogger.d(
                                "ProgressScreenViewModel",
                                "callWorkFlowAPI -> stepsListDao.updateNeedToPost before for stepId: $stepId, villageId: $villageId, needToPost: false"
                            )
                            progressScreenRepository.updateNeedToPost(stepId, villageId, false)
                            NudgeLogger.d(
                                "ProgressScreenViewModel",
                                "callWorkFlowAPI -> stepsListDao.updateNeedToPost after for stepId: $stepId, villageId: $villageId, needToPost: false"
                            )
                        }
                    } else {
                        onError(tag = "ProgressScreenViewModel", "Error : ${response.message}")
                    }
                    if (!response.lastSyncTime.isNullOrEmpty()) {
                        progressScreenRepository.updateLastSyncTime(response.lastSyncTime)
                    }
                } else {
                    NudgeLogger.d("ProgressScreenViewModel", "callWorkFlowAPI -> workFlowId != 0")
                }

            } catch (ex: Exception) {
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

    fun isUserBPC(): Boolean {
        return progressScreenRepository.isUserBPC()
    }

    fun savePref(key: String, value: String) {
        progressScreenRepository.savePref(key, value)
    }

    fun savePref(key: String, value: Boolean) {
        progressScreenRepository.savePref(key, value)
    }

    fun saveSettingOpenFrom(openFrom: Int) {
        progressScreenRepository.saveSettingOpenFrom(openFrom)
    }

    fun getPref(key: String, defaultValue: String): String? {
        return progressScreenRepository.getPref(key, defaultValue)
    }

    fun saveFromPage(pageFrom: String) {
        progressScreenRepository.saveFromPage(pageFrom)
    }
    fun updateWorkflowStatusInEvent(stepStatus: String, stepId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            updateWorkflowStatus(stepStatus,stepId)
        }
    }
     override suspend fun updateWorkflowStatus(stepStatus: String, stepId: Int) {


            val stepListEntity = progressScreenRepository.getStepForVillage(
                progressScreenRepository.prefRepo.getSelectedVillage().id,
                stepId,

                )
            if (stepListEntity.workFlowId == 0) {
                val updateWorkflowEvent = progressScreenRepository.createStepUpdateEvent(
                    stepStatus,
                    stepListEntity,
                    progressScreenRepository.prefRepo.getMobileNumber() ?: BLANK_STRING
                )
                progressScreenRepository.writeEventIntoLogFile(updateWorkflowEvent)
            }

    }

    fun saveWorkflowEventIntoDb(villageId: Int, stepId: Int) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepEntity = progressScreenRepository.getStepForVillage(villageId = villageId, stepId = stepId)
            if (stepEntity.workFlowId == 0) {

                val updateWorkflowEvent = progressScreenRepository.createWorkflowEvent(
                    eventItem = stepEntity,
                    StepStatus.INPROGRESS,
                    EventName.WORKFLOW_STATUS_UPDATE,
                    EventType.STATEFUL,
                    progressScreenRepository.prefRepo
                )
                updateWorkflowEvent?.let { event ->
                    progressScreenRepository.insertEventIntoDb(event, emptyList())
                }

            }
        }
    }

}
package com.patsurvey.nudge.activities

import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.CheckDBStatus
import com.patsurvey.nudge.MyApplication.Companion.appScopeLaunch
import com.patsurvey.nudge.activities.settings.TransactionIdRequest
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.request.EditDidiWealthRankingRequest
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.FORM_C
import com.patsurvey.nudge.utils.FORM_D
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_FORM_PATH
import com.patsurvey.nudge.utils.PREF_WEALTH_RANKING_COMPLETION_DATE_
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.StepType
import com.patsurvey.nudge.utils.VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_
import com.patsurvey.nudge.utils.longToString
import com.patsurvey.nudge.utils.updateLastSyncTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@HiltViewModel
class WealthRankingSurveyViewModel @Inject constructor(
    val repository: WealthRankingSurveyRepository
) : BaseViewModel() {

    private val _didiList = MutableStateFlow(listOf<DidiEntity>())
    val didiList: StateFlow<List<DidiEntity>> get() = _didiList
    private val _expandedCardIdsList = MutableStateFlow(listOf<Int>())
    val expandedCardIdsList: StateFlow<List<Int>> get() = _expandedCardIdsList

    val showBottomButton = mutableStateOf(true)

    var villageId: Int = -1
    var stepId: Int = -1
    val isTolaSynced = mutableStateOf(0)
    val isDidiSynced = mutableStateOf(0)
    var selectedVillage: VillageEntity? = null

    init {
        selectedVillage = repository.prefRepo.getSelectedVillage()
        villageId = selectedVillage?.id ?: -1
        fetchDidisFromDB()
        CheckDBStatus(this@WealthRankingSurveyViewModel).isFirstStepNeedToBeSync(repository.tolaDao) {
            isTolaSynced.value = it
        }
        CheckDBStatus(this@WealthRankingSurveyViewModel).isSecondStepNeedToBeSync(repository.didiDao) {
            isDidiSynced.value = it
        }
    }

    fun fetchDidisFromDB() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
                _didiList.emit(repository.getAllDidisForVillage(villageId))
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

    fun callWorkFlowAPI(
        villageId: Int,
        stepId: Int,
        networkCallbackListener: NetworkCallbackListener
    ) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            NudgeLogger.d("WealthRankingSurveyViewModel", "callWorkFlowAPI -> called")
            try {
                val dbResponse = repository.getStepForVillage(villageId, stepId)
                NudgeLogger.d("WealthRankingSurveyViewModel", "callWorkFlowAPI -> dbResponse = $dbResponse")
                val stepList = repository.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }
                NudgeLogger.d("WealthRankingSurveyViewModel", "callWorkFlowAPI -> stepList = $stepList")
                if (dbResponse.workFlowId > 0) {
                    val primaryWorkFlowRequest = listOf(
                        EditWorkFlowRequest(stepList[stepList.map { it.orderNumber }.indexOf(3)].workFlowId,
                            StepStatus.COMPLETED.name, longToString(repository.prefRepo.getPref(PREF_WEALTH_RANKING_COMPLETION_DATE_+repository.prefRepo.getSelectedVillage().id,System.currentTimeMillis()))
                        )
                    )
                    NudgeLogger.d("WealthRankingSurveyViewModel", "callWorkFlowAPI -> primaryWorkFlowRequest = $primaryWorkFlowRequest")
                    val response = repository.editWorkFlow(primaryWorkFlowRequest)
                    NudgeLogger.d("WealthRankingSurveyViewModel", "callWorkFlowAPI -> response: status = ${response.status}, message = ${response.message}, data = ${response.data.toString()} \n")

                        if (response.status.equals(SUCCESS, true)) {
                            response.data?.let {
                                NudgeLogger.d("WealthRankingSurveyViewModel", "callWorkFlowAPI -> stepsListDao.updateWorkflowId before: id: ${
                                    stepList[stepList.map { it.orderNumber }.indexOf(3)].id
                                }, workFlowId: ${stepList[stepList.map { it.orderNumber }.indexOf(3)].workFlowId}, status: ${it[0].status}")
                                repository.updateWorkflowId(
                                    stepList[stepList.map { it.orderNumber }.indexOf(3)].id,
                                    stepList[stepList.map { it.orderNumber }.indexOf(3)].workFlowId,
                                    villageId,
                                    it[0].status
                                )
                                NudgeLogger.d("WealthRankingSurveyViewModel", "callWorkFlowAPI -> stepsListDao.updateWorkflowId after")

                                NudgeLogger.d("WealthRankingSurveyViewModel", "callWorkFlowAPI -> stepsListDao.updateNeedToPost before ")
                                repository.updateNeedToPost(stepList[stepList.map { it.orderNumber }.indexOf(3)].id, villageId, false)
                                NudgeLogger.d("WealthRankingSurveyViewModel", "callWorkFlowAPI -> stepsListDao.updateNeedToPost after")
                            }
                        } else {
                            NudgeLogger.d("WealthRankingSurveyViewModel", "callWorkFlowAPI -> response: onFailed")
                            networkCallbackListener.onFailed()
                            onError(tag = "WealthRankingSurveyViewModel", "Error : ${response.message}")
                        }

                        if(!response.lastSyncTime.isNullOrEmpty()){
                            updateLastSyncTime(repository.prefRepo,response.lastSyncTime)
                        }

                }
                try {
                    stepList.forEach { step ->
                        NudgeLogger.d("WealthRankingSurveyViewModel", "callWorkFlowAPI -> step = $step")
                        NudgeLogger.d("WealthRankingSurveyViewModel", "callWorkFlowAPI -> " +
                                "step.orderNumber > 3 && step.workFlowId > 0: " +
                                "${step.orderNumber > 3} && ${step.workFlowId > 0}")
                        if (step.orderNumber > 3 &&  step.workFlowId > 0) {

                            val inProgressStepRequest =  listOf(
                                EditWorkFlowRequest(
                                    step.workFlowId,
                                    StepStatus.INPROGRESS.name
                                )
                            )
                            NudgeLogger.d("WealthRankingSurveyViewModel", "callWorkFlowAPI -> inProgressStepRequest = $inProgressStepRequest")
                            val inProgressStepResponse = repository.editWorkFlow(inProgressStepRequest)

                            NudgeLogger.d("WealthRankingSurveyViewModel", "callWorkFlowAPI -> inProgressStepResponse: status = ${inProgressStepResponse.status}, " +
                                    "message = ${inProgressStepResponse.message}, data = ${inProgressStepResponse.data.toString()} \n")

                            if (inProgressStepResponse.status.equals(SUCCESS, true)) {
                                inProgressStepResponse.data?.let {
                                    NudgeLogger.d("WealthRankingSurveyViewModel", "callWorkFlowAPI -> stepsListDao.updateWorkflowId before stepId: ${step.id}, workflowId: ${step.workFlowId}, status: ${it[0].status}")
                                    repository.updateWorkflowId(
                                        step.id,
                                        step.workFlowId,
                                        villageId,
                                        it[0].status
                                    )
                                    NudgeLogger.d("WealthRankingSurveyViewModel", "callWorkFlowAPI -> stepsListDao.updateWorkflowId after")

                                    NudgeLogger.d("WealthRankingSurveyViewModel", "callWorkFlowAPI -> stepsListDao.updateNeedToPost before stepId: ${step.id}")
                                    repository.updateNeedToPost(step.id, villageId, false)
                                    NudgeLogger.d("WealthRankingSurveyViewModel", "callWorkFlowAPI -> stepsListDao.updateNeedToPost after")

                                }

                            }
                            if(!inProgressStepResponse.lastSyncTime.isNullOrEmpty()){
                                updateLastSyncTime(repository.prefRepo,inProgressStepResponse.lastSyncTime)
                            }
                        }
                    }
                } catch (ex: Exception) {
                    onCatchError(ex, ApiType.WORK_FLOW_API)
                }
            } catch (ex: Exception) {
                networkCallbackListener.onFailed()
                onError(tag = "WealthRankingSurveyViewModel", "Error : ${ex.localizedMessage}")
                onCatchError(ex, ApiType.WORK_FLOW_API)
            }
        }
    }


    fun markWealthRakningComplete(villageId: Int, stepId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val existingList = repository.getVillage(villageId).steps_completed
            val updatedCompletedStepsList = mutableListOf<Int>()
            if (!existingList.isNullOrEmpty()) {
                existingList.forEach {
                    updatedCompletedStepsList.add(it)
                }
            }
            updatedCompletedStepsList.add(stepId)
            repository.updateLastCompleteStep(villageId, updatedCompletedStepsList)
            repository.markStepAsCompleteOrInProgress(
                stepId,
                StepStatus.COMPLETED.ordinal,
                villageId
            )
            repository.updateNeedToPost(stepId, villageId, true)
            val stepDetails = repository.getStepForVillage(villageId, stepId)
            if (stepDetails.orderNumber < repository.getAllSteps().size) {
                repository.markStepAsInProgress(
                    (stepDetails.orderNumber + 1),
                    StepStatus.INPROGRESS.ordinal,
                    villageId
                )
                repository.updateNeedToPost(stepDetails.id, villageId, true)
                repository. prefRepo.savePref("$VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_${villageId}", false)
                for (i in 1..5) {
                    repository.prefRepo.savePref(getFormPathKey(getFormSubPath(FORM_C, i)), "")
                    repository.prefRepo.savePref(getFormPathKey(getFormSubPath(FORM_D, i)), "")
                }
            }
        }
    }

    fun saveWealthRankingCompletionDate() {
        val currentTime = System.currentTimeMillis()
        repository.prefRepo.savePref(PREF_WEALTH_RANKING_COMPLETION_DATE_+repository.prefRepo.getSelectedVillage().id, currentTime)
    }

    fun getWealthRankingStepStatus(stepId: Int, callBack: (isComplete: Boolean) -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepStatus = repository.isStepComplete(stepId)
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
        NudgeLogger.d("WealthRankingSurveyViewModel", "onServerError -> onServerError: message = ${error?.message}")
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        NudgeLogger.d("WealthRankingSurveyViewModel", "onServerError -> onServerError: message = ${errorModel?.message}, api = ${errorModel?.apiName?.name}")
    }

    fun updateWealthRankingToNetwork(networkCallbackListener: NetworkCallbackListener) {
        job = appScopeLaunch (Dispatchers.IO + exceptionHandler) {
            try {
                val needToPostDidiList = repository.getAllNeedToPostDidiRanking(true)
                NudgeLogger.d("WealthRankingSurveyViewModel", "updateWealthRankingToNetwork -> needToPostDidiList: $needToPostDidiList")
                if (needToPostDidiList.isNotEmpty()) {
                    val didiWealthRequestList = arrayListOf<EditDidiWealthRankingRequest>()
                    val didiStepRequestList = arrayListOf<EditDidiWealthRankingRequest>()
                    needToPostDidiList.forEach { didi ->
                        didiWealthRequestList.add(EditDidiWealthRankingRequest(didi.serverId, StepType.WEALTH_RANKING.name,didi.wealth_ranking, rankingEdit = false, localModifiedDate = System.currentTimeMillis(),  name = didi.name,
                            address = didi.address,
                            guardianName = didi.guardianName,
                            villageId = didi.villageId,))
                        didiStepRequestList.add(EditDidiWealthRankingRequest(didi.serverId, StepType.SOCIAL_MAPPING.name,StepStatus.COMPLETED.name, rankingEdit = false, localModifiedDate = System.currentTimeMillis(),   name = didi.name,
                            address = didi.address,
                            guardianName = didi.guardianName,
                            villageId = didi.villageId,))
                    }
                    didiWealthRequestList.addAll(didiStepRequestList)
                    NudgeLogger.d("WealthRankingSurveyViewModel", "updateWealthRankingToNetwork -> didiRequestList: $didiWealthRequestList")
                    val updateWealthRankResponse = repository.updateDidiRanking(didiWealthRequestList)
                    NudgeLogger.d("WealthRankingSurveyViewModel", "updateWealthRankingToNetwork -> response: status = ${updateWealthRankResponse.status}, message = ${updateWealthRankResponse.message}, data = ${updateWealthRankResponse.data.toString()}")
                    if (updateWealthRankResponse.status.equals(SUCCESS, true)) {
                        if(updateWealthRankResponse.data?.get(0)?.transactionId.isNullOrEmpty()) {
                            needToPostDidiList.forEach{didi ->
                                repository.setNeedToPostRanking(didi.id, false)
                            }
                            networkCallbackListener.onSuccess()
                        } else {
                            val size = needToPostDidiList.indices
                            for(i in size) {
                                val serverResponseDidi = updateWealthRankResponse.data?.get(i)
                                val localDidi = needToPostDidiList[i]
                                serverResponseDidi?.transactionId?.let {
                                    repository.updateDidiTransactionId(localDidi.id,
                                        it
                                    )
                                }
                            }
                            checkDidiWealthStatus(networkCallbackListener)
                        }
                    } else {
                        networkCallbackListener.onFailed()
                    }
                } else {
                    networkCallbackListener.onSuccess()
                }
            } catch (ex: Exception) {
                networkCallbackListener.onFailed()
                NudgeLogger.d("WealthRankingSurveyViewModel", "updateWealthRankingToNetwork -> onFailed")
                onError(
                    "WealthRankingSurveyViewModel",
                    "onError: ${ex.message}, \n${ex.stackTrace}"
                )
                onCatchError(ex, ApiType.DIDI_EDIT_API)
            }
        }
    }

    private fun checkDidiWealthStatus(networkCallbackListener: NetworkCallbackListener) {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                job = appScopeLaunch (Dispatchers.IO + exceptionHandler) {
                    try {
                        val didiList = repository.fetchPendingWealthStatusDidi(true, "")
                        if (didiList.isNotEmpty()) {
                            val ids: ArrayList<String> = arrayListOf()
                            didiList.forEach { didi ->
                                didi.transactionId?.let { ids.add(it) }
                            }
                            NudgeLogger.d("WealthRankingSurveyViewModel", "updateWealthRankingToNetwork -> didiList: $didiList")
                            val response = repository.getPendingStatus(TransactionIdRequest("", ids))
                            NudgeLogger.d("WealthRankingSurveyViewModel", "updateWealthRankingToNetwork -> response: ${response.toString()}")
                            if (response.status.equals(SUCCESS, true)) {
                                response.data?.forEach { transactionIdResponse ->
                                    didiList.forEach { didi ->
                                        if (transactionIdResponse.transactionId == didi.transactionId) {
                                            repository.updateDidiNeedToPostWealthRank(didi.id, false)
                                            repository.updateDidiTransactionId(didi.id, "")
                                        }
                                    }
                                }
                                NudgeLogger.d("WealthRankingSurveyViewModel", "updateWealthRankingToNetwork -> onSuccess")
                                networkCallbackListener.onSuccess()
                            } else {
                                NudgeLogger.d("WealthRankingSurveyViewModel", "updateWealthRankingToNetwork -> onFailed")
                                networkCallbackListener.onFailed()
                            }
                            if(!response.lastSyncTime.isNullOrEmpty()){
                                updateLastSyncTime(repository.prefRepo,response.lastSyncTime)
                            }
                        } else {
                            networkCallbackListener.onSuccess()
                        }
                    } catch (ex: Exception) {
                        networkCallbackListener.onFailed()
                        NudgeLogger.d("WealthRankingSurveyViewModel", "updateWealthRankingToNetwork -> onFailed")
                        onError(
                            "WealthRankingSurveyViewModel",
                            "onError: ${ex.message}, \n${ex.stackTrace}"
                        )
                        onCatchError(ex, ApiType.STATUS_CALL_BACK_API)
                    }
                }
            }
        },10000)
    }

    fun checkIfLastStepIsComplete(currentStepId: Int, callBack: (isPreviousStepComplete: Boolean) -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList = repository.getAllStepsForVillage(repository.prefRepo.getSelectedVillage().id)
            val currentStepIndex = stepList.map { it.id }.indexOf(currentStepId)

            withContext(Dispatchers.Main) {
                callBack(stepList.sortedBy { it.orderNumber }[currentStepIndex - 1].isComplete == StepStatus.COMPLETED.ordinal)
            }
        }
    }


    fun getFormPathKey(subPath: String): String {
        //val subPath formPictureScreenViewModel.pageItemClicked.value
        //"${PREF_FORM_PATH}_${formPictureScreenViewModel.prefRepo.getSelectedVillage().name}_${subPath}"
        return "${PREF_FORM_PATH}_${repository.prefRepo.getSelectedVillage().id}_${subPath}"
    }

    fun getFormSubPath(formName: String, pageNumber: Int): String {
        return "${formName}_page_$pageNumber"
    }

    fun updateWealthRankingFlagForDidis() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val villageId = repository.prefRepo.getSelectedVillage().id
            repository.updateRankEditFlag(villageId, false)
        }
    }

}

package com.patsurvey.nudge.activities

import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.CheckDBStatus
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
import com.patsurvey.nudge.utils.PREF_FORM_PATH
import com.patsurvey.nudge.utils.PREF_WEALTH_RANKING_COMPLETION_DATE
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.StepType
import com.patsurvey.nudge.utils.VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@HiltViewModel
class WealthRankingSurveyViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val tolaDao: TolaDao,
    val didiDao: DidiDao,
    val stepsListDao: StepsListDao,
    val villageListDao: VillageListDao,
    val answerDao: AnswerDao,
    val numericAnswerDao: NumericAnswerDao,
    val questionDao: QuestionListDao,
    val apiService: ApiService
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
        selectedVillage = prefRepo.getSelectedVillage()
        villageId = selectedVillage?.id ?: -1
        fetchDidisFromDB()
        CheckDBStatus(this@WealthRankingSurveyViewModel).isFirstStepNeedToBeSync(tolaDao){
            isTolaSynced.value=it
        }
        CheckDBStatus(this@WealthRankingSurveyViewModel).isSecondStepNeedToBeSync(didiDao){
            isDidiSynced.value = it
        }
    }

    fun fetchDidisFromDB() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
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

    fun callWorkFlowAPI(
        villageId: Int,
        stepId: Int,
        networkCallbackListener: NetworkCallbackListener
    ) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val dbResponse = stepsListDao.getStepForVillage(villageId, stepId)
                val stepList = stepsListDao.getAllStepsForVillage(villageId)
                if (dbResponse.workFlowId > 0) {
                    val response = apiService.editWorkFlow(
                        listOf(
                            EditWorkFlowRequest(dbResponse.workFlowId, StepStatus.COMPLETED.name)
                        )
                    )
                    withContext(Dispatchers.IO) {
                        if (response.status.equals(SUCCESS, true)) {
                            response.data?.let {
                                stepsListDao.updateWorkflowId(
                                    stepId,
                                    dbResponse.workFlowId,
                                    villageId,
                                    it[0].status
                                )
                            }
                            stepsListDao.updateNeedToPost(stepId, villageId, false)
                        } else {
                            networkCallbackListener.onFailed()
                            onError(tag = "ProgressScreenViewModel", "Error : ${response.message}")
                        }
                    }
                }
                launch {
                    try {
                        stepList.forEach { step ->
                            if (step.id != stepId && step.orderNumber > dbResponse.orderNumber && step.workFlowId > 0) {
                                val inProgressStepResponse = apiService.editWorkFlow(
                                    listOf(
                                        EditWorkFlowRequest(
                                            step.workFlowId,
                                            StepStatus.INPROGRESS.name
                                        )
                                    )
                                )
                                if (inProgressStepResponse.status.equals(SUCCESS, true)) {
                                    inProgressStepResponse.data?.let {
                                        stepsListDao.updateWorkflowId(
                                            step.id,
                                            step.workFlowId,
                                            villageId,
                                            it[0].status
                                        )
                                    }
                                    stepsListDao.updateNeedToPost(step.id, villageId, false)
                                }
                            }
                        }
                    } catch (ex: Exception) {
                        onCatchError(ex, ApiType.WORK_FLOW_API)
                    }
                }
            } catch (ex: Exception) {
                networkCallbackListener.onFailed()
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
            stepsListDao.markStepAsCompleteOrInProgress(
                stepId,
                StepStatus.COMPLETED.ordinal,
                villageId
            )
            stepsListDao.updateNeedToPost(stepId, villageId, true)
            val stepDetails = stepsListDao.getStepForVillage(villageId, stepId)
            if (stepDetails.orderNumber < stepsListDao.getAllSteps().size) {
                stepsListDao.markStepAsInProgress(
                    (stepDetails.orderNumber + 1),
                    StepStatus.INPROGRESS.ordinal,
                    villageId
                )
                stepsListDao.updateNeedToPost(stepDetails.id, villageId, true)
                prefRepo.savePref("$VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_${villageId}", false)
                for (i in 1..5) {
                    prefRepo.savePref(getFormPathKey(getFormSubPath(FORM_C, i)), "")
                    prefRepo.savePref(getFormPathKey(getFormSubPath(FORM_D, i)), "")
                }
            }
        }
    }

    fun saveWealthRankingCompletionDate() {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val date = dateFormat.format(currentTime)
        prefRepo.savePref(PREF_WEALTH_RANKING_COMPLETION_DATE, date)
    }

    fun getWealthRankingStepStatus(stepId: Int, callBack: (isComplete: Boolean) -> Unit) {
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

    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        TODO("Not yet implemented")
    }

    fun updateWealthRankingToNetwork(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                withContext(Dispatchers.IO) {
                    val needToPostDidiList =
                        didiDao.getAllNeedToPostDidiRanking(true, prefRepo.getSelectedVillage().id)
                    if (needToPostDidiList.isNotEmpty()) {
                        val didiRequestList = arrayListOf<EditDidiWealthRankingRequest>()
                        needToPostDidiList.forEach { didi ->
                            didiRequestList.add(EditDidiWealthRankingRequest(didi.serverId, StepType.WEALTH_RANKING.name,didi.wealth_ranking, localModifiedDate = System.currentTimeMillis()))
                            didiRequestList.add(EditDidiWealthRankingRequest(didi.serverId, StepType.SOCIAL_MAPPING.name,StepStatus.COMPLETED.name, localModifiedDate = System.currentTimeMillis()))
                        }
                        val updateWealthRankResponse = apiService.updateDidiRanking(didiRequestList)
                        if (updateWealthRankResponse.status.equals(SUCCESS, true)) {
                            if(updateWealthRankResponse.data?.get(0)?.transactionId.isNullOrEmpty()) {
                                needToPostDidiList.forEach{didi ->
                                    didiDao.setNeedToPostRanking(didi.id, false)
                                }
                            } else {
                                val size = updateWealthRankResponse.data?.indices
                                if (size != null) {
                                    for(i in size) {
                                        val serverResponseDidi = updateWealthRankResponse.data.get(i)
                                        val localDidi = needToPostDidiList[i]
                                        serverResponseDidi.transactionId?.let {
                                            didiDao.updateDidiTransactionId(localDidi.id,
                                                it
                                            )
                                        }
                                    }
                                    checkDidiWealthStatus()
                                }
                            }
                        } else {
                            networkCallbackListener.onFailed()
                        }
                    }
                }
            } catch (ex: Exception) {
                onCatchError(ex)
                networkCallbackListener.onFailed()
                onError(
                    "WealthRankingSurveyViewModel",
                    "onError: ${ex.message}, \n${ex.stackTrace}"
                )
            }
        }
    }

    private fun checkDidiWealthStatus() {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                    val didiList = didiDao.fetchPendingWealthStatusDidi(true, "")
                    if (didiList.isNotEmpty()) {
                        val ids: ArrayList<String> = arrayListOf()
                        didiList.forEach { didi ->
                            didi.transactionId?.let { ids.add(it) }
                        }
                        val response = apiService.getPendingStatus(TransactionIdRequest("", ids))
                        if (response.status.equals(SUCCESS, true)) {
                            response.data?.forEach { transactionIdResponse ->
                                didiList.forEach { didi ->
                                    if (transactionIdResponse.transactionId == didi.transactionId) {
                                        didiDao.updateDidiNeedToPostWealthRank(didi.id, false)
                                        didiDao.updateDidiTransactionId(didi.id, "")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },10000)
    }

    fun checkIfLastStepIsComplete(currentStepId: Int, callBack: (isPreviousStepComplete: Boolean) -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList = stepsListDao.getAllStepsForVillage(prefRepo.getSelectedVillage().id)
            val currentStepIndex = stepList.map { it.id }.indexOf(currentStepId)

            withContext(Dispatchers.Main) {
                callBack(stepList.sortedBy { it.orderNumber }[currentStepIndex - 1].isComplete == StepStatus.COMPLETED.ordinal)
            }
        }
    }


    fun getFormPathKey(subPath: String): String {
        //val subPath formPictureScreenViewModel.pageItemClicked.value
        //"${PREF_FORM_PATH}_${formPictureScreenViewModel.prefRepo.getSelectedVillage().name}_${subPath}"
        return "${PREF_FORM_PATH}_${prefRepo.getSelectedVillage().name}_${subPath}"
    }

    fun getFormSubPath(formName: String, pageNumber: Int): String {
        return "${formName}_page_$pageNumber"
    }

}

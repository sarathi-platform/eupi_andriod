package com.patsurvey.nudge.activities.survey

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.CheckDBStatus
import com.patsurvey.nudge.activities.settings.TransactionIdRequest
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.converters.BeneficiaryProcessStatusModel
import com.patsurvey.nudge.database.dao.*
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.request.AnswerDetailDTOListItem
import com.patsurvey.nudge.model.request.BpcUpdateSelectedDidiRequest
import com.patsurvey.nudge.model.request.EditDidiWealthRankingRequest
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.model.request.PATSummarySaveRequest
import com.patsurvey.nudge.model.request.SaveMatchSummaryRequest
import com.patsurvey.nudge.model.response.OptionsItem
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
class SurveySummaryViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val didiDao: DidiDao,
    val tolaDao: TolaDao,
    val stepsListDao: StepsListDao,
    val answerDao: AnswerDao,
    val numericAnswerDao: NumericAnswerDao,
    val questionDao: QuestionListDao,
    val villageListDao: VillageListDao,
    val apiService: ApiService,
    val bpcSelectedDidiDao: BpcSelectedDidiDao,
    val bpcNonSelectedDidiDao: BpcNonSelectedDidiDao
) : BaseViewModel() {

    private val _didiList = MutableStateFlow(listOf<DidiEntity>())
    val didiList: StateFlow<List<DidiEntity>> get() = _didiList
    val notAvailableCount = mutableStateOf(0)
    val isTolaSynced = mutableStateOf(0)
    val isDidiSynced = mutableStateOf(0)
    val isDidiRankingSynced = mutableStateOf(0)
    val isDidiPATSynced = mutableStateOf(0)

    init {
        if (prefRepo.isUserBPC()) {
            fetchDidisForBpcFromDB()
        } else {
            fetchDidisFromDB()
        }
    }

    private fun fetchDidisForBpcFromDB() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val selectedVillage = prefRepo.getSelectedVillage()
            val didiList = mutableListOf<DidiEntity>()
            val patCompletedDidiList = didiDao.getAllDidisForVillage(selectedVillage.id)
            val replacedDidiFromSelectedDao = bpcSelectedDidiDao.fetchAllDidisForVillage(selectedVillage.id)
            val replacedDidiFromNonSelectedDao = bpcNonSelectedDidiDao.fetchAllDidisForVillage(selectedVillage.id)

            didiList.addAll(patCompletedDidiList)
            val filteredReplacedDidiFromSelectedDao = replacedDidiFromSelectedDao.filter { it.isAlsoSelected == BpcDidiSelectionStatus.REPLACED.ordinal && it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal }
            val filteredReplacedDidiFromNonSelectedDao = replacedDidiFromNonSelectedDao.filter { it.isAlsoSelected == BpcDidiSelectionStatus.REPLACED.ordinal && it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal }

            filteredReplacedDidiFromSelectedDao.forEach {
                didiList.add(DidiEntity.getDidiEntityFromSelectedDidiEntityForBpc(it))
            }
            filteredReplacedDidiFromNonSelectedDao.forEach {
                didiList.add(DidiEntity.getDidiEntityFromNonSelectedDidiEntityForBpc(it))
            }

            _didiList.value = didiList

        }
    }

    fun fetchDidisFromDB(){
        job= CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO){
                Log.d("TAG", "fetchDidisFromDB: Called")
                _didiList.emit(didiDao.getAllDidisForVillage(prefRepo.getSelectedVillage().id))
                CheckDBStatus(this@SurveySummaryViewModel).isFirstStepNeedToBeSync(tolaDao){
                    isTolaSynced.value =it
                }
                CheckDBStatus(this@SurveySummaryViewModel).isSecondStepNeedToBeSync(didiDao){
                    isDidiSynced.value=it
                }
                CheckDBStatus(this@SurveySummaryViewModel).isThirdStepNeedToBeSync(didiDao){
                    isDidiRankingSynced.value=it
                }
                CheckDBStatus(this@SurveySummaryViewModel).isFourthStepNeedToBeSync(answerDao, didiDao, prefRepo){
                    isDidiPATSynced.value=it
                }
                notAvailableCount.value = didiDao.fetchNotAvailableDidis(prefRepo.getSelectedVillage().id)
            }
        }
    }

    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        TODO("Not yet implemented")
    }

    @SuppressLint("SuspiciousIndentation")
    fun savePATSummeryToServer(networkCallbackListener: NetworkCallbackListener){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                withContext(Dispatchers.IO){
                    var optionList= emptyList<OptionsItem>()
                    var answeredDidiList:ArrayList<PATSummarySaveRequest> = arrayListOf()
                    var scoreDidiList:ArrayList<EditDidiWealthRankingRequest> = arrayListOf()
                    var surveyId =0

                    val didiIDList= answerDao.fetchPATSurveyDidiList(prefRepo.getSelectedVillage().id)
                    if(didiIDList.isNotEmpty()){
                        didiIDList.forEach { didi->
                            Log.d(TAG, "savePATSummeryToServer Save: ${didi.id} :: ${didi.patSurveyStatus}")
                            var qList:ArrayList<AnswerDetailDTOListItem> = arrayListOf()
                            val needToPostQuestionsList=answerDao.getAllNeedToPostQuesForDidi(didi.id)
                            if(needToPostQuestionsList.isNotEmpty()){
                                needToPostQuestionsList.forEach {
                                    surveyId= questionDao.getQuestion(it.questionId).surveyId?:0
                                    if(!it.type.equals(QuestionType.Numeric_Field.name,true)){
                                        optionList= listOf(
                                            OptionsItem(optionId = it.optionId,
                                                optionValue = it.optionValue,
                                                count = 0,
                                                summary = it.summary,
                                                display = it.answerValue,
                                                weight = 0,
                                                isSelected = false
                                            )
                                        )
                                    } else {
                                        val numOptionList = numericAnswerDao.getSingleQueOptions(
                                            it.questionId,
                                            it.didiId
                                        )
                                        val tList: ArrayList<OptionsItem> = arrayListOf()
                                        if (numOptionList.isNotEmpty()) {
                                            numOptionList.forEach { numOption ->
                                                tList.add(
                                                    OptionsItem(
                                                        optionId = numOption.optionId,
                                                        optionValue = 0,
                                                        count = numOption.count,
                                                        summary = it.summary,
                                                        display = it.answerValue,
                                                        weight = numOption.weight,
                                                        isSelected = false
                                                    )
                                                )
                                            }
                                            optionList = tList
                                        }else{
                                            tList.add(
                                                OptionsItem(
                                                    optionId = it.optionId,
                                                    optionValue = 0,
                                                    count = 0,
                                                    summary = it.summary,
                                                    display = it.answerValue,
                                                    weight = it.weight,
                                                    isSelected = false
                                                )
                                            )

                                            optionList = tList
                                        }
                                    }
                                    try {
                                        qList.add(
                                            AnswerDetailDTOListItem(
                                                questionId = it.questionId,
                                                section = it.actionType,
                                                options = optionList
                                            )
                                        )
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                            val passingMark=questionDao.getPassingScore()
                            scoreDidiList.add(
                                EditDidiWealthRankingRequest(
                                    id = if (didi.serverId == 0) didi.id else didi.serverId,
                                    score = didi.score,
                                    comment = if(didi.score< passingMark) LOW_SCORE else {
                                                 if(didi.patSurveyStatus==PatSurveyStatus.COMPLETED.ordinal && didi.section2Status==PatSurveyStatus.NOT_STARTED.ordinal){
                                                        TYPE_EXCLUSION
                                                            }else BLANK_STRING},
                                    type = if (prefRepo.isUserBPC()) BPC_SURVEY_CONSTANT else PAT_SURVEY,
                                    result = if (didi.forVoEndorsement == 0) DIDI_REJECTED else COMPLETED_STRING
                                )
                            )
                            answeredDidiList.add(
                                PATSummarySaveRequest(
                                    villageId = prefRepo.getSelectedVillage().id,
                                    surveyId = surveyId,
                                    beneficiaryId = if (didi.serverId == 0) didi.id else didi.serverId,
                                    languageId = prefRepo.getAppLanguageId() ?: 2,
                                    stateId = prefRepo.getSelectedVillage().stateId,
                                    totalScore = 0,
                                    userType = if (prefRepo.isUserBPC()) USER_BPC else USER_CRP,
                                    beneficiaryName = didi.name,
                                    answerDetailDTOList = qList,
                                    patSurveyStatus = didi.patSurveyStatus,
                                    section2Status = didi.section2Status,
                                    section1Status = didi.section1Status
                                )
                            )
                        }
                        if(answeredDidiList.isNotEmpty()){
                            withContext(Dispatchers.IO){
                                val saveAPIResponse= apiService.savePATSurveyToServer(answeredDidiList)
                                if(saveAPIResponse.status.equals(SUCCESS,true)){
                                    if(saveAPIResponse.data?.get(0)?.transactionId.isNullOrEmpty()) {
                                        didiIDList.forEach { didiItem ->
                                            didiDao.updateNeedToPostPAT(
                                                false,
                                                didiItem.id,
                                                prefRepo.getSelectedVillage().id
                                            )
                                        }
                                    } else {
                                        for(i in didiIDList.indices) {
                                            saveAPIResponse.data?.get(i)?.let {
                                                didiDao.updateDidiTransactionId(
                                                    didiIDList[i].id,
                                                    it.transactionId
                                                )
                                            }
                                        }

                                    }
                                    networkCallbackListener.onSuccess()
                                    checkDidiPatStatus()
                                } else {
                                    networkCallbackListener.onFailed()
                                }
                                apiService.updateDidiScore(scoreDidiList)
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                networkCallbackListener.onFailed()
                ex.printStackTrace()
                onCatchError(ex)
            }
        }
    }

    private fun checkDidiPatStatus() {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                    val didiList = didiDao.fetchPendingPatStatusDidi(true, "")
                    if (didiList.isNotEmpty()) {
                        val ids: ArrayList<String> = arrayListOf()
                        didiList.forEach { didi ->
                            didi.transactionId?.let { ids.add(it) }
                        }
                        val response =
                            apiService.getPendingStatusForPat(TransactionIdRequest("PAT", ids))
                        if (response.status.equals(SUCCESS, true)) {
                            response.data?.forEach { transactionIdResponse ->
                                didiList.forEach { didi ->
                                    if (transactionIdResponse.transactionId == didi.transactionId) {
                                        didiDao.updateDidiNeedToPostPat(didi.id, false)
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

    fun callWorkFlowAPI(villageId: Int,stepId: Int, networkCallbackListener: NetworkCallbackListener){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val dbResponse=stepsListDao.getStepForVillage(villageId, stepId)
                val stepList = stepsListDao.getAllStepsForVillage(villageId)
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
                            stepsListDao.updateNeedToPost(stepId, false)
                        }else{
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
                                    stepsListDao.updateNeedToPost(step.id, false)
                                }
                            }
                        }
                    } catch (ex: Exception) {
                        onCatchError(ex, ApiType.WORK_FLOW_API)
                    }
                }
            }catch (ex:Exception){
                networkCallbackListener.onFailed()
                onError(tag = "ProgressScreenViewModel", "Error : ${ex.localizedMessage}")
            }
        }
    }

    fun markPatComplete(villageId: Int, stepId: Int) {
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
            stepsListDao.updateNeedToPost(stepId, true)
            val stepDetails = stepsListDao.getStepForVillage(villageId, stepId)
            if (stepDetails.orderNumber < stepsListDao.getAllSteps().size) {
                stepsListDao.markStepAsInProgress(
                    (stepDetails.orderNumber + 1),
                    StepStatus.INPROGRESS.ordinal,
                    villageId
                )
                stepsListDao.updateNeedToPost(stepDetails.id, true)
                prefRepo.savePref("$VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_${villageId}", false)
                for (i in 1..5) {
                    prefRepo.savePref(getFormPathKey(getFormSubPath(FORM_C, i)), "")
                    prefRepo.savePref(getFormPathKey(getFormSubPath(FORM_D, i)), "")
                }
            }
        }
    }

    fun markBpcVerificationComplete(villageId: Int, stepId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val bpcStepId = stepsListDao.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }.last().id
            stepsListDao.markStepAsCompleteOrInProgress(
                stepId = bpcStepId,
                isComplete = StepStatus.COMPLETED.ordinal,
                villageId = villageId
            )
        }
    }

    fun savePatCompletionDate() {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val date = dateFormat.format(currentTime)
        prefRepo.savePref(PREF_PAT_COMPLETION_DATE, date)
    }

    fun saveBpcPatCompletionDate() {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val date = dateFormat.format(currentTime)
        prefRepo.savePref(PREF_BPC_PAT_COMPLETION_DATE, date)
    }

    fun markVoEndorsementComplete(villageId: Int, stepId: Int) {
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
            stepsListDao.updateNeedToPost(stepId, true)
            val stepDetails = stepsListDao.getStepForVillage(villageId, stepId)
            if (stepDetails.orderNumber < stepsListDao.getAllSteps().size) {
                stepsListDao.markStepAsInProgress(
                    (stepDetails.orderNumber + 1),
                    StepStatus.INPROGRESS.ordinal,
                    villageId
                )
                stepsListDao.updateNeedToPost(stepDetails.id, true)
            }
            prefRepo.savePref("$VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_${villageId}", true)
        }
    }

    fun saveVoEndorsementDate() {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val date = dateFormat.format(currentTime)
        prefRepo.savePref(PREF_VO_ENDORSEMENT_COMPLETION_DATE, date)
    }

    fun updateDidiPatStatus() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _didiList.value.forEach { didi ->
                if (didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal) {
                    val existingProcessStatus = didi.beneficiaryProcessStatus
                    var updatedStatus = mutableListOf<BeneficiaryProcessStatusModel>()
                    existingProcessStatus?.forEach {
                        updatedStatus.add(it)
                    }
                    updatedStatus.add(
                        BeneficiaryProcessStatusModel(
                            if (prefRepo.isUserBPC()) BPC_SURVEY_CONSTANT else PAT_SURVEY,
                            "COMPLETED"
                        )
                    )
                    didiDao.updateBeneficiaryProcessStatus(didi.id, updatedStatus)
                } else if (didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal) {
                    val existingProcessStatus = didi.beneficiaryProcessStatus
                    var updatedStatus = mutableListOf<BeneficiaryProcessStatusModel>()
                    existingProcessStatus?.forEach {
                        updatedStatus.add(it)
                    }
                    updatedStatus.add(
                        BeneficiaryProcessStatusModel(
                            if (prefRepo.isUserBPC()) BPC_SURVEY_CONSTANT else PAT_SURVEY,
                            "NOT_AVAILABLE"
                        )
                    )
                    didiDao.updateBeneficiaryProcessStatus(didi.id, updatedStatus)
                } else {
                    didiDao.updateNeedToPostPAT(false, didi.id, didi.villageId)
                }
            }
        }
    }

    fun updateDidiVoEndorsementStatus() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _didiList.value.forEach { didi ->
                if (didi.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal) {
                    val existingProcessStatus = didi.beneficiaryProcessStatus
                    var updatedStatus = mutableListOf<BeneficiaryProcessStatusModel>()
                    existingProcessStatus?.forEach {
                        updatedStatus.add(it)
                    }
                    updatedStatus.add(BeneficiaryProcessStatusModel("VO_ENDORSEMENT", "ACCEPTED"))
                    didiDao.updateBeneficiaryProcessStatus(didi.id, updatedStatus)
                } else if (didi.voEndorsementStatus == DidiEndorsementStatus.REJECTED.ordinal) {
                    val existingProcessStatus = didi.beneficiaryProcessStatus
                    var updatedStatus = mutableListOf<BeneficiaryProcessStatusModel>()
                    existingProcessStatus?.forEach {
                        updatedStatus.add(it)
                    }
                    updatedStatus.add(BeneficiaryProcessStatusModel("VO_ENDORSEMENT", "REJECTED"))
                    didiDao.updateBeneficiaryProcessStatus(didi.id, updatedStatus)
                } else {
                    didiDao.updateNeedToPostVO(false, didiId = didi.id, didi.villageId)
                }
            }
        }
    }

    /*fun updatePatStatusToNetwork(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                withContext(Dispatchers.IO) {
                    val needToPostDidiList = didiDao.getAllNeedToPostPATDidi(
                        needsToPostPAT = true,
                        villageId = prefRepo.getSelectedVillage().id
                    )
                    if (needToPostDidiList.isNotEmpty()) {
                        needToPostDidiList.forEach { didi ->
                            launch {
                                didi.patSurveyStatus.let {
                                    if (it == PatSurveyStatus.COMPLETED.ordinal) {
                                        val updateWealthRankResponse = apiService.updateDidiRanking(
                                            listOf(
                                                EditDidiWealthRankingRequest(
                                                    didi.id,
                                                    StepType.PAT_SURVEY.name,
                                                    PatSurveyStatus.COMPLETED.name
                                                ),
                                            )
                                        )
                                        if (updateWealthRankResponse.status.equals(SUCCESS, true)) {
                                            didiDao.updateNeedToPostPAT(
                                                false,
                                                didi.id,
                                                didi.villageId
                                            )
                                        } else {
                                            networkCallbackListener.onFailed()
                                        }
                                    } else if (it == PatSurveyStatus.NOT_AVAILABLE.ordinal) {
                                        val updateWealthRankResponse = apiService.updateDidiRanking(
                                            listOf(
                                                EditDidiWealthRankingRequest(
                                                    didi.id,
                                                    StepType.PAT_SURVEY.name,
                                                    PatSurveyStatus.NOT_AVAILABLE.name
                                                ),
                                            )
                                        )
                                        if (updateWealthRankResponse.status.equals(SUCCESS, true)) {
                                            didiDao.updateNeedToPostPAT(
                                                false,
                                                didi.id,
                                                didi.villageId
                                            )
                                        } else {
                                            networkCallbackListener.onFailed()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                onCatchError(ex)
                networkCallbackListener.onFailed()
                onError(
                    "SurveySummaryViewModel",
                    "updatePatStatusToNetwork-> onError: ${ex.message}, \n${ex.stackTrace}"
                )
            }
        }
    }

    fun updateVoStatusToNetwork(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                withContext(Dispatchers.IO) {
                    val needToPostDidiList = didiDao.getAllNeedToPostPATDidi(
                        needsToPostPAT = true,
                        villageId = prefRepo.getSelectedVillage().id
                    )
                    if (needToPostDidiList.isNotEmpty()) {
                        needToPostDidiList.forEach { didi ->
                            launch {
                                didi.voEndorsementStatus.let {
                                    if (it == DidiEndorsementStatus.ENDORSED.ordinal) {
                                        val updateWealthRankResponse = apiService.updateDidiRanking(
                                            listOf(
                                                EditDidiWealthRankingRequest(
                                                    didi.id,
                                                    StepType.VO_ENDROSEMENT.name,
                                                    ACCEPTED
                                                ),
                                            )
                                        )
                                        if (updateWealthRankResponse.status.equals(SUCCESS, true)) {
                                            didiDao.updateNeedToPostVO(
                                                false,
                                                didi.id,
                                                didi.villageId
                                            )
                                        } else {
                                            networkCallbackListener.onFailed()
                                        }
                                    } else if (it == DidiEndorsementStatus.REJECTED.ordinal) {
                                        val updateWealthRankResponse = apiService.updateDidiRanking(
                                            listOf(
                                                EditDidiWealthRankingRequest(
                                                    didi.id,
                                                    StepType.VO_ENDROSEMENT.name,
                                                    DidiEndorsementStatus.REJECTED.name
                                                ),
                                            )
                                        )
                                        if (updateWealthRankResponse.status.equals(SUCCESS, true)) {
                                            didiDao.updateNeedToPostVO(
                                                false,
                                                didi.id,
                                                didi.villageId
                                            )
                                        } else {
                                            networkCallbackListener.onFailed()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                onCatchError(ex)
                networkCallbackListener.onFailed()
                onError(
                    "SurveySummaryViewModel",
                    "updateVoStatusToNetwork-> onError: ${ex.message}, \n${ex.stackTrace}"
                )
            }
        }
    }*/

    fun checkIfLastStepIsComplete(
        currentStepId: Int,
        callBack: (isPreviousStepComplete: Boolean) -> Unit
    ) {
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

    fun updateBpcPatStatusToNetwork(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val needToPostPatdidi =
                didiDao.getAllNeedToPostPATDidi(true, prefRepo.getSelectedVillage().id)
            val passingScore = questionDao.getPassingScore()
            if (!needToPostPatdidi.isNullOrEmpty()) {
                needToPostPatdidi.forEach { didi ->
                    if (didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && didi.section2Status == PatSurveyStatus.COMPLETED.ordinal) {
                        launch {
                            try {
                                val updatedPatResponse = apiService.updateDidiRanking(
                                    listOf(
                                        EditDidiWealthRankingRequest(
                                            id = didi.serverId,
                                            type = BPC_SURVEY_CONSTANT,
                                            result = PatSurveyStatus.COMPLETED.name,
                                            score = didi.score,
                                            comment = if ((didi.score
                                                    ?: 0.0) < passingScore
                                            ) LOW_SCORE else "",
                                            localModifiedDate = System.currentTimeMillis()
                                        )
                                    )
                                )
                                if (updatedPatResponse.status.equals(SUCCESS, true)) {
                                    didiDao.updateNeedToPostPAT(
                                        needsToPostPAT = false,
                                        didiId = didi.id,
                                        didi.villageId
                                    )
                                    didiDao.updateNeedsToPostBPCProcessStatus(false,didi.id)
                                } else {
                                    networkCallbackListener.onFailed()
                                }
                            } catch (ex: Exception) {
                                onCatchError(ex, ApiType.DIDI_EDIT_API)
                            }
                        }
                    } else
                        if (didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && didi.section2Status == PatSurveyStatus.NOT_STARTED.ordinal) {
                            launch {
                                try {
                                    val updatedPatResponse = apiService.updateDidiRanking(
                                        listOf(
                                            EditDidiWealthRankingRequest(
                                                id = didi.serverId,
                                                type = BPC_SURVEY_CONSTANT,
                                                result = PatSurveyStatus.NOT_AVAILABLE.name,
                                                score = 0.0,
                                                comment = TYPE_EXCLUSION,
                                                localModifiedDate = System.currentTimeMillis()
                                            )
                                        )
                                    )
                                    if (updatedPatResponse.status.equals(SUCCESS, true)) {
                                        didiDao.updateNeedToPostPAT(
                                            needsToPostPAT = false,
                                            didiId = didi.id,
                                            didi.villageId
                                        )
                                    } else {
                                        networkCallbackListener.onFailed()
                                    }
                                } catch (ex: Exception) {
                                    onCatchError(ex, ApiType.DIDI_EDIT_API)
                                }
                            }
                        }
                }
            }
        }
    }

    fun sendBpcUpdatedDidiList(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val villagId = prefRepo.getSelectedVillage().id
            val oldDidiList = bpcSelectedDidiDao.fetchAllDidisForVillage(villageId = villagId)
            val updatedList = didiDao.getAllDidisForVillage(villageId = villagId)
            try {
                val oldBeneficiaryIdSelected = mutableListOf<Int>()
                oldDidiList.forEach {
                    oldBeneficiaryIdSelected.add(it.serverId)
                }
                val newBeneficiaryIdSelected = mutableListOf<Int>()
                updatedList.forEach {
                    newBeneficiaryIdSelected.add(it.serverId)
                }
                val updateSelectedDidiResponse = apiService.sendSelectedDidiList(
                    BpcUpdateSelectedDidiRequest(
                        oldBeneficiaryIdSelected = oldBeneficiaryIdSelected,
                        newBeneficiaryIdSelected = newBeneficiaryIdSelected,
                        villageId = villagId
                    )
                )
                if (updateSelectedDidiResponse.status.equals(SUCCESS, true)) {
                    Log.d("SurveySummaryViewModel", "sendBpcUpdatedDidiList: $SUCCESS")
                } else {
                    Log.d("SurveySummaryViewModel", "sendBpcUpdatedDidiList: $FAIL")
                    networkCallbackListener.onFailed()
                }
            } catch (ex: Exception) {
                onCatchError(ex, ApiType.BPC_UPDATE_DIDI_LIST_API)
            }
        }
    }

    fun callWorkFlowAPIForBpc(villageId: Int, stepId: Int, networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val stepList = stepsListDao.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }
                val bpcStep = stepList.last()
                if(bpcStep.workFlowId>0){
                    val response = apiService.editWorkFlow(
                        listOf(
                            EditWorkFlowRequest(bpcStep.workFlowId, StepStatus.COMPLETED.name)
                        ) )
                    withContext(Dispatchers.IO){
                        if (response.status.equals(SUCCESS, true)) {
                            response.data?.let {
                                stepsListDao.updateWorkflowId(bpcStep.id, bpcStep.workFlowId,villageId,it[0].status)
                            }
                            stepsListDao.updateNeedToPost(bpcStep.id, false)
                        }else{
                            networkCallbackListener.onFailed()
                            onError(tag = "ProgressScreenViewModel", "Error : ${response.message}")
                        }
                    }
                }
            }catch (ex:Exception){
                networkCallbackListener.onFailed()
                onError(tag = "ProgressScreenViewModel", "Error : ${ex.localizedMessage}")
            }
        }
    }
    fun sendBpcMatchScore(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val villageId = prefRepo.getSelectedVillage().id
                val passingScore = questionDao.getPassingScore()
                val bpcStep = stepsListDao.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }.last()
                val matchedCount = didiList.value.filter {
                    (it.score ?: 0.0) >= passingScore.toDouble()
                            && (it.crpScore ?: 0.0) >= passingScore.toDouble() }.size

                val matchPercentage = ((matchedCount.toFloat()/didiList.value.size.toFloat()) * 100).toInt()
                val saveMatchSummaryRequest = SaveMatchSummaryRequest(
                    programId = bpcStep.programId,
                    score = matchPercentage,
                    villageId = villageId
                )
                val requestList = arrayListOf(saveMatchSummaryRequest)
                val saveMatchSummaryResponse = apiService.saveMatchSummary(requestList)
                if (saveMatchSummaryResponse.status.equals(SUCCESS, true)){
                    networkCallbackListener.onSuccess()
                } else {
                    prefRepo.savePref(PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ + villageId, false)
                    networkCallbackListener.onFailed()
                }
            } catch (ex: Exception){
                prefRepo.savePref(PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ + prefRepo.getSelectedVillage().id, false)
                onCatchError(ex, ApiType.BPC_SAVE_MATCH_PERCENTAGE_API)
            }
        }
    }

}

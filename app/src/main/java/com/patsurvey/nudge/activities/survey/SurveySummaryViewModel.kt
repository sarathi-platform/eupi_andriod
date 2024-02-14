package com.patsurvey.nudge.activities.survey

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.patsurvey.nudge.CheckDBStatus
import com.patsurvey.nudge.MyApplication.Companion.appScopeLaunch
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.settings.TransactionIdRequest
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.converters.BeneficiaryProcessStatusModel
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.dataModel.PATDidiStatusModel
import com.patsurvey.nudge.model.request.AnswerDetailDTOListItem
import com.patsurvey.nudge.model.request.EditDidiWealthRankingRequest
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.model.request.PATSummarySaveRequest
import com.patsurvey.nudge.model.request.SaveMatchSummaryRequest
import com.patsurvey.nudge.model.response.OptionsItem
import com.patsurvey.nudge.utils.AbleBodiedFlag
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.BPC_SURVEY_CONSTANT
import com.patsurvey.nudge.utils.COMPLETED_STRING
import com.patsurvey.nudge.utils.DIDI_NOT_AVAILABLE
import com.patsurvey.nudge.utils.DIDI_REJECTED
import com.patsurvey.nudge.utils.ExclusionType
import com.patsurvey.nudge.utils.FLAG_RATIO
import com.patsurvey.nudge.utils.FLAG_WEIGHT
import com.patsurvey.nudge.utils.LOW_SCORE
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PAT_SURVEY
import com.patsurvey.nudge.utils.PREF_BPC_PAT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_
import com.patsurvey.nudge.utils.PREF_PAT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.QuestionType
import com.patsurvey.nudge.utils.SHGFlag
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.USER_BPC
import com.patsurvey.nudge.utils.USER_CRP
import com.patsurvey.nudge.utils.VERIFIED_STRING
import com.patsurvey.nudge.utils.WealthRank
import com.patsurvey.nudge.utils.calculateScore
import com.patsurvey.nudge.utils.longToString
import com.patsurvey.nudge.utils.toWeightageRatio
import com.patsurvey.nudge.utils.updateLastSyncTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
class SurveySummaryViewModel @Inject constructor(
   val repository: SurveySummaryRepository
) : BaseViewModel() {

    private val _didiList = MutableStateFlow(listOf<DidiEntity>())
    private val _didiCountList = MutableStateFlow(listOf<String>())
    val didiList: StateFlow<List<DidiEntity>> get() = _didiList
    val didiCountList: StateFlow<List<String>> get() = _didiCountList
    val notAvailableCount = mutableStateOf(0)
    val isTolaSynced = mutableStateOf(0)
    val isDidiSynced = mutableStateOf(0)
    val isDidiRankingSynced = mutableStateOf(0)
    val isDidiPATSynced = mutableStateOf(0)
    val totalPatDidiCount = mutableStateOf(0)
    val notAvailableDidiCount = mutableStateOf(0)
    val voEndorseDidiCount = mutableStateOf(0)
    val exclusiveQuesCount = mutableStateOf(0)
    val inclusiveQuesCount = mutableStateOf(0)
    val isVOEndorsementComplete = mutableStateOf(false)
    val villageEntity = mutableStateOf<VillageEntity?>(null)

    init {
        if (repository.prefRepo.isUserBPC()) {
            fetchDidisForBpcFromDB()
        } else {
            fetchDidisFromDB()
        }
        setVillage(repository.prefRepo.getSelectedVillage().id)
    }

     fun fetchDidisForBpcFromDB() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val selectedVillage = repository.prefRepo.getSelectedVillage()
            val didiList = mutableListOf<DidiEntity>()
            val patCompletedDidiList = repository.getAllDidisForVillage(villageId = selectedVillage.id)
            didiList.addAll(patCompletedDidiList)
            _didiList.value = didiList

        }
    }

    fun getSelectedVillage() = repository.getSelectedVillage()

    private fun setVillage(villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch{
            try {
                var village = repository.fetchVillageDetailsForLanguage(villageId)
                   var steps =repository.getStepByOrder(5)
                withContext(Dispatchers.Main) {
                    villageEntity.value = village
                    steps?.let {
                        isVOEndorsementComplete.value = it.isComplete == StepStatus.COMPLETED.ordinal
                    }

                }

            }catch (ex:Exception){
                ex.printStackTrace()
                NudgeLogger.d("SurveySummaryViewModel", "setVillage Error: ${ex.message} " )
            }
        }
    }

    fun fetchDidisFromDB(){
        job= CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO){
                _didiList.emit(repository.getAllDidisForVillage())
                CheckDBStatus(this@SurveySummaryViewModel).isFirstStepNeedToBeSync(repository.tolaDao) {
                    isTolaSynced.value = it
                }
                CheckDBStatus(this@SurveySummaryViewModel).isSecondStepNeedToBeSync(repository.didiDao) {
                    isDidiSynced.value = it
                }
                CheckDBStatus(this@SurveySummaryViewModel).isThirdStepNeedToBeSync(repository.didiDao){
                    isDidiRankingSynced.value=it
                }
                CheckDBStatus(this@SurveySummaryViewModel).isFourthStepNeedToBeSync(repository.answerDao, repository.didiDao, repository.prefRepo){
                    isDidiPATSynced.value=it
                }
                notAvailableCount.value = repository.fetchNotAvailableDidis()
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
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            try {
                var didiIDList = emptyList<PATDidiStatusModel>()
                didiIDList = repository.fetchPATSurveyDidiList()
                Log.d("TAG", "savePATSummeryToServer before ListSize: ${didiIDList.size}")

                var optionList: List<OptionsItem>
                var answeredDidiList: ArrayList<PATSummarySaveRequest> = arrayListOf()
                var scoreDidiList: ArrayList<EditDidiWealthRankingRequest> = arrayListOf()
                var surveyId = 0


                try {
                     didiIDList = repository.fetchPATSurveyDidiList()

                }catch (ex:Exception){
                    ex.printStackTrace()
                }
                Log.d("TAG", "savePATSummeryToServer ListSize: ${didiIDList.size}")
                if (didiIDList.isNotEmpty()) {
                    didiIDList.forEach { didi ->
                        NudgeLogger.d(
                            "SurveySummaryViewModel",
                            "savePATSummeryToServer didiId: ${didi.id} :: didi.patSurveyStatus: ${didi.patSurveyStatus}"
                        )
                        calculateDidiScore(didiId = didi.id)
                        delay(100)
                        didi.score = repository.getDidiScoreFromDb(didi.id)
                        var qList: ArrayList<AnswerDetailDTOListItem> = arrayListOf()
                        val needToPostQuestionsList = repository.getAllNeedToPostQuesForDidi(didi.id)
                        if (needToPostQuestionsList.isNotEmpty()) {
                            needToPostQuestionsList.forEach {
                                surveyId = repository.getQuestion(it.questionId).surveyId ?: 0
                                if (!it.type.equals(QuestionType.Numeric_Field.name, true)) {
                                    optionList = listOf(
                                        OptionsItem(
                                            optionId = it.optionId,
                                            optionValue = it.optionValue,
                                            count = 0,
                                            summary = it.summary,
                                            display = it.answerValue,
                                            weight = it.weight,
                                            isSelected = false
                                        )
                                    )
                                } else {
                                    val numOptionList = repository.getSingleQueOptions(
                                        questionId = it.questionId,
                                       didiId = it.didiId
                                    )
                                    val tList: ArrayList<OptionsItem> = arrayListOf()
                                    if (numOptionList.isNotEmpty()) {
                                        numOptionList.forEach { numOption ->
                                            tList.add(
                                                OptionsItem(
                                                    optionId = numOption.optionId,
                                                    optionValue = numOption.optionValue,
                                                    count = numOption.count,
                                                    summary = it.summary,
                                                    display = it.answerValue,
                                                    weight = numOption.weight,
                                                    isSelected = false
                                                )
                                            )
                                        }
                                        optionList = tList
                                    } else {
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
                                            options = optionList,
                                            assetAmount = it.assetAmount
                                        )
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                        val passingMark = repository.getPassingScore()
                        var comment = BLANK_STRING
                        comment =
                            if (didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) {
                                PatSurveyStatus.NOT_AVAILABLE.name
                            } else if (didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal) {
                                BLANK_STRING
                            } else {
                                if ((didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && didi.section2Status == PatSurveyStatus.NOT_STARTED.ordinal)
                                    || (didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && didi.patExclusionStatus != ExclusionType.NO_EXCLUSION.ordinal)) {
                                    TYPE_EXCLUSION
                                } else {
                                    if (didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && didi.section2Status == PatSurveyStatus.COMPLETED.ordinal && didi.score < passingMark) {
                                        LOW_SCORE
                                    } else {
                                        BLANK_STRING
                                    }
                                }
                            }
                        scoreDidiList.add(
                            EditDidiWealthRankingRequest(
                                id = if (didi.serverId == 0) didi.id else didi.serverId,
                                score = didi.score,
                                comment = comment,
                                type = if (repository.prefRepo.isUserBPC()) BPC_SURVEY_CONSTANT else PAT_SURVEY,
                                result = if (didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) {
                                    DIDI_NOT_AVAILABLE
                                } else if (didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal) {
                                    PatSurveyStatus.INPROGRESS.name
                                } else {
                                    if (didi.forVoEndorsement == 0 || didi.patExclusionStatus != ExclusionType.NO_EXCLUSION.ordinal) DIDI_REJECTED else {
                                        if (repository.prefRepo.isUserBPC())
                                            VERIFIED_STRING
                                        else
                                            COMPLETED_STRING
                                    }
                                },
                                rankingEdit = false,
                                shgFlag = SHGFlag.fromInt(didi.shgFlag).name,
                                ableBodiedFlag = AbleBodiedFlag.fromInt(didi.ableBodiedFlag).name
                            )
                        )
                        answeredDidiList.add(
                            PATSummarySaveRequest(
                                villageId = repository.prefRepo.getSelectedVillage().id,
                                surveyId = surveyId,
                                beneficiaryId = if (didi.serverId == 0) didi.id else didi.serverId,
                                languageId = repository.prefRepo.getAppLanguageId() ?: 2,
                                stateId = repository.prefRepo.getSelectedVillage().stateId,
                                totalScore = didi.score,
                                userType = if (repository.prefRepo.isUserBPC()) USER_BPC else USER_CRP,
                                beneficiaryName = didi.name,
                                answerDetailDTOList = qList,
                                patSurveyStatus = didi.patSurveyStatus,
                                section2Status = didi.section2Status,
                                section1Status = didi.section1Status,
                                patExclusionStatus=didi.patExclusionStatus,
                                shgFlag = didi.shgFlag
                            )
                        )
                    }
                    if (answeredDidiList.isNotEmpty()) {
                        withContext(Dispatchers.IO) {
                            val saveAPIResponse = repository.savePATSurveyToServer(answeredDidiList)
                            if (saveAPIResponse.status.equals(SUCCESS, true)) {
                                if (saveAPIResponse.data?.get(0)?.transactionId.isNullOrEmpty()) {
                                    didiIDList.forEach { didiItem ->
                                        repository.updateNeedToPostPAT(
                                            needsToPostPAT = false,
                                            didiId = didiItem.id,
                                            villageId = repository.prefRepo.getSelectedVillage().id
                                        )
                                    }
                                    networkCallbackListener.onSuccess()
                                } else {
                                    for (i in didiIDList.indices) {
                                        saveAPIResponse.data?.get(i)?.let {
                                            repository.updateDidiTransactionId(
                                                id=didiIDList[i].id,
                                                transactionId= it.transactionId
                                            )
                                        }
                                    }
                                    checkDidiPatStatus()
                                }
                            } else {
                                networkCallbackListener.onFailed()
                            }
                            if (!saveAPIResponse.lastSyncTime.isNullOrEmpty()) {
                                updateLastSyncTime(repository.prefRepo, saveAPIResponse.lastSyncTime)
                            }
                            val updateScoreResponse = repository.updateDidiScore(scoreDidiList)
                        }
                    }
                }
            } catch (ex: Exception) {
                NudgeLogger.e("SurveySummaryViewModel", "savePATSummeryToServer: onFailed =>", ex)
                networkCallbackListener.onFailed()
                ex.printStackTrace()
                onCatchError(ex, ApiType.CRP_PAT_SAVE_ANSWER_SUMMARY)
            }
        }
    }

    private fun checkDidiPatStatus() {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                    val didiList = repository.fetchPendingPatStatusDidi(true, "")
                    if (didiList.isNotEmpty()) {
                        val ids: ArrayList<String> = arrayListOf()
                        didiList.forEach { didi ->
                            didi.transactionId?.let { ids.add(it) }
                        }
                        val response =
                            repository.getPendingStatusForPat(TransactionIdRequest("PAT", ids))
                        if (response.status.equals(SUCCESS, true)) {
                            response.data?.forEach { transactionIdResponse ->
                                didiList.forEach { didi ->
                                    if (transactionIdResponse.transactionId == didi.transactionId) {
                                        repository.updateDidiNeedToPostPat(didi.id, false)
                                        repository.updateDidiTransactionId(
                                            id=didi.id,
                                            transactionId= BLANK_STRING
                                        )
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
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            NudgeLogger.d("SurveySummaryViewModel", "callWorkFlowAPI -> called")
            try {
                val dbResponse=repository.getStepForVillage(villageId, stepId)
                NudgeLogger.d("SurveySummaryViewModel", "callWorkFlowAPI -> dbResponse = $dbResponse")
                val stepList = repository.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }
                NudgeLogger.d("SurveySummaryViewModel", "callWorkFlowAPI -> stepList = $stepList \n\n")
                if (dbResponse.workFlowId > 0) {
                    val primaryWorkFlowRequest =
                        listOf(EditWorkFlowRequest(
                            if (!repository.prefRepo.isUserBPC()) stepList[stepList.map { it.orderNumber }
                                .indexOf(4)].workFlowId else stepList[stepList.map { it.orderNumber }
                                .indexOf(6)].workFlowId, StepStatus.COMPLETED.name,
                            longToString(repository.prefRepo.getPref(PREF_PAT_COMPLETION_DATE_+repository.prefRepo.getSelectedVillage().id,System.currentTimeMillis()))

                        ))
                    NudgeLogger.d(
                        "SurveySummaryViewModel",
                        "callWorkFlowAPI -> primaryWorkFlowRequest = $primaryWorkFlowRequest"
                    )
                    val response = repository.editWorkFlow(
                        primaryWorkFlowRequest
                    )
                    NudgeLogger.d(
                        "SurveySummaryViewModel",
                        "callWorkFlowAPI -> response: status = ${response.status}, message = ${response.message}, data = ${response.data.toString()} \n"
                    )
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.let { primaryWorkFlowResponse ->
                            //Here programsProcessId == id for our local db.
                            repository.updateWorkflowId(
                                stepId = primaryWorkFlowResponse[0].programsProcessId,
                                workflowId = primaryWorkFlowResponse[0].id,
                                villageId = villageId,
                                status = primaryWorkFlowResponse[0].status
                            )
                        }
                        NudgeLogger.d("SurveySummaryViewModel", "callWorkFlowAPI -> stepsListDao.updateNeedToPost before: for primaryWorkFlowResponse\n")

                        repository.updateNeedToPost(
                            id = if (!repository.prefRepo.isUserBPC())
                                stepList[stepList.map { it.orderNumber }.indexOf(4)].id
                            else
                                stepList[stepList.map { it.orderNumber }.indexOf(6)].id,
                            villageId = villageId,
                            needsToPost = false
                        )

                        NudgeLogger.d("SurveySummaryViewModel", "callWorkFlowAPI -> stepsListDao.updateNeedToPost after: for primaryWorkFlowResponse\n")

                        networkCallbackListener.onSuccess()
                    } else {
                        NudgeLogger.d("SurveySummaryViewModel", "callWorkFlowAPI -> response: onFailed")
                        networkCallbackListener.onFailed()
                        onError(tag = "SurveySummaryViewModel", "Error : ${response.message}")
                    }

                    if (!response.lastSyncTime.isNullOrEmpty()) {
                        updateLastSyncTime(repository.prefRepo, response.lastSyncTime)
                    }

                }
                try {
                    NudgeLogger.d("SurveySummaryViewModel", "callWorkFlowAPI -> second try = called")
                    stepList.forEach { step ->
                        if (!repository.prefRepo.isUserBPC()) {
                            NudgeLogger.d("SurveySummaryViewModel", "callWorkFlowAPI -> !prefRepo.isUserBPC() = true")
                            NudgeLogger.d("SurveySummaryViewModel", "callWorkFlowAPI -> step = $step")
                            NudgeLogger.d("SurveySummaryViewModel", "callWorkFlowAPI -> " +
                                    "step.orderNumber > 4 && step.workFlowId > 0: " +
                                    "${step.orderNumber > 4} && ${step.workFlowId > 0}")
                            if (step.orderNumber > 4 &&  step.workFlowId > 0) {
                                val inProgressStepRequest = listOf(
                                    EditWorkFlowRequest(
                                        step.workFlowId,
                                        StepStatus.INPROGRESS.name
                                    )
                                )
                                NudgeLogger.d("SurveySummaryViewModel", "callWorkFlowAPI -> inProgressStepRequest = $inProgressStepRequest")

                                val inProgressStepResponse = repository.editWorkFlow(inProgressStepRequest)

                                NudgeLogger.d("SurveySummaryViewModel", "callWorkFlowAPI -> inProgressStepResponse: status = ${inProgressStepResponse.status}, message = ${inProgressStepResponse.message}, data = ${inProgressStepResponse.data.toString()}")


                                if (inProgressStepResponse.status.equals(SUCCESS, true)) {
                                    inProgressStepResponse.data?.let {
                                        repository.updateWorkflowId(
                                            step.id,
                                            step.workFlowId,
                                            villageId,
                                            it[0].status
                                        )
                                    }
                                    NudgeLogger.d("AddDidiViewModel", "callWorkFlowAPI -> stepsListDao.updateNeedToPost before for inProgressStepResponse")

                                    repository.updateNeedToPost(step.id, villageId, false)

                                    NudgeLogger.d("AddDidiViewModel", "callWorkFlowAPI -> stepsListDao.updateNeedToPost after for inProgressStepResponse")

                                    networkCallbackListener.onSuccess()
                                } else {
                                    NudgeLogger.d("SurveySummaryViewModel", "callWorkFlowAPI -> inProgressStepResponse = FAIL")
                                    networkCallbackListener.onFailed()
                                }
                                if (!inProgressStepResponse.lastSyncTime.isNullOrEmpty()) {
                                    updateLastSyncTime(repository.prefRepo, inProgressStepResponse.lastSyncTime)
                                }
                            }
                        } else {
                            networkCallbackListener.onSuccess()
                            NudgeLogger.d("SurveySummaryViewModel", "callWorkFlowAPI -> !prefRepo.isUserBPC() = false")
                        }
                    }
                } catch (ex: Exception) {
                    onCatchError(ex, ApiType.WORK_FLOW_API)
                }
            }catch (ex:Exception){
                networkCallbackListener.onFailed()
                onError(tag = "ProgressScreenViewModel", "Error : ${ex.localizedMessage}")
            }
        }
    }

    fun markPatComplete(villageId: Int, stepId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val existingList = repository.getVillage(villageId).steps_completed
            val updatedCompletedStepsList = mutableListOf<Int>()
            if (!existingList.isNullOrEmpty()) {
                existingList.forEach {
                    updatedCompletedStepsList.add(it)
                }
            }
            updatedCompletedStepsList.add(stepId)
            repository.markStepComplete(villageId = villageId, stepId = stepId, updatedCompletedStepsList)
            updateWorkflowStatus(StepStatus.COMPLETED, villageId = villageId, stepId)

        }
    }

    fun markBpcVerificationComplete(villageId: Int, stepId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val bpcStepId = repository.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }.last().id
            repository.markBPCStepComplete(
                stepId = bpcStepId,
                isComplete = StepStatus.COMPLETED.ordinal,
                villageId = villageId
            )
            updateWorkflowStatus(StepStatus.COMPLETED, villageId = villageId, bpcStepId)
        }
    }

    fun savePatCompletionDate() {
        val currentTime = System.currentTimeMillis()
        repository.prefRepo.savePref(PREF_PAT_COMPLETION_DATE_ + repository.prefRepo.getSelectedVillage().id, currentTime)
    }

    fun saveBpcPatCompletionDate() {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val date = dateFormat.format(currentTime)
        repository.prefRepo.savePref(PREF_BPC_PAT_COMPLETION_DATE_, date)
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
                            if (repository.prefRepo.isUserBPC()) BPC_SURVEY_CONSTANT else PAT_SURVEY,
                            "COMPLETED"
                        )
                    )
                    repository.updateBeneficiaryProcessStatus(didi.id, updatedStatus)
                } else if (didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) {
                    val existingProcessStatus = didi.beneficiaryProcessStatus
                    var updatedStatus = mutableListOf<BeneficiaryProcessStatusModel>()
                    existingProcessStatus?.forEach {
                        updatedStatus.add(it)
                    }
                    updatedStatus.add(
                        BeneficiaryProcessStatusModel(
                            if (repository.prefRepo.isUserBPC()) BPC_SURVEY_CONSTANT else PAT_SURVEY,
                            "NOT_AVAILABLE"
                        )
                    )
                    repository.updateBeneficiaryProcessStatus(didi.id, updatedStatus)
                } else {
                    repository.updateNeedToPostPAT(
                        needsToPostPAT = false,
                        didiId = didi.id,
                        villageId = didi.villageId
                    )
                }
            }
        }
    }

    fun checkIfLastStepIsComplete(
        currentStepId: Int,
        callBack: (isPreviousStepComplete: Boolean) -> Unit
    ) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList = repository.getAllStepsForVillage(repository.prefRepo.getSelectedVillage().id)
            val currentStepIndex = stepList.map { it.id }.indexOf(currentStepId)

            withContext(Dispatchers.Main) {
                callBack(stepList.sortedBy { it.orderNumber }[currentStepIndex - 1].isComplete == StepStatus.COMPLETED.ordinal)
            }
        }
    }



    fun getFormSubPath(formName: String, pageNumber: Int): String {
        return "${formName}_page_$pageNumber"
    }

    fun updateBpcPatStatusToNetwork(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val needToPostPatdidi =
                repository.getAllNeedToPostPATDidi(true)
            val passingScore = repository.getPassingScore()
            if (!needToPostPatdidi.isNullOrEmpty()) {
                needToPostPatdidi.forEach { didi ->
                    if (didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && didi.section2Status == PatSurveyStatus.COMPLETED.ordinal) {
                        launch {
                            try {
                                val updatedPatResponse = repository.updateDidiRanking(
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
                                    repository.updateNeedToPostPAT(
                                        needsToPostPAT = false,
                                        didiId = didi.id,
                                        didi.villageId
                                    )
                                    repository.updateNeedsToPostBPCProcessStatus(false, didi.id)
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
                                    val updatedPatResponse = repository.updateDidiRanking(
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
                                        repository.updateNeedToPostPAT(
                                            needsToPostPAT = false,
                                            didiId = didi.id,
                                            didi.villageId
                                        )
                                        repository.updateNeedsToPostBPCProcessStatus(false, didi.id)
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

    fun callWorkFlowAPIForBpc(villageId: Int, stepId: Int, networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val stepList = repository.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }
                val bpcStep = stepList.last()
                if(bpcStep.workFlowId>0){
                    val response = repository.editWorkFlow(
                        listOf(
                            EditWorkFlowRequest(bpcStep.workFlowId, StepStatus.COMPLETED.name)
                        ) )
                    withContext(Dispatchers.IO){
                        if (response.status.equals(SUCCESS, true)) {
                            response.data?.let {
                                repository.updateWorkflowId(bpcStep.id, bpcStep.workFlowId,villageId,it[0].status)
                            }
                            repository.updateNeedToPost(bpcStep.id, villageId, false)
                        }else{
                            networkCallbackListener.onFailed()
                            onError(tag = "ProgressScreenViewModel", "Error : ${response.message}")
                        }

                        if(!response.lastSyncTime.isNullOrEmpty()){
                            updateLastSyncTime(repository.prefRepo,response.lastSyncTime)
                        }
                    }
                }
            }catch (ex:Exception){
                networkCallbackListener.onFailed()
                onError(tag = "ProgressScreenViewModel", "Error : ${ex.localizedMessage}")
            }
        }
    }

    fun writeBpcMatchScoreEvent() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val villageId = repository.prefRepo.getSelectedVillage().id
            val passingScore = repository.getPassingScore()
            val bpcStep =
                repository.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }.last()

            repository.writeBpcMatchScoreEvent(villageId, passingScore, bpcStep, didiList.value)
        }
    }
    fun sendBpcMatchScore(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val villageId = repository.prefRepo.getSelectedVillage().id
                val passingScore = repository.getPassingScore()
                val bpcStep = repository.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }.last()

                insertBpcMatchScoreEvent(villageId, passingScore, bpcStep, didiList.value)


                repository.writeBpcMatchScoreEvent(villageId, passingScore, bpcStep, didiList.value)

                val matchPercentage = calculateMatchPercentage(didiList.value.filter { it.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal }, passingScore)
                val saveMatchSummaryRequest = SaveMatchSummaryRequest(
                    programId = bpcStep.programId,
                    score = matchPercentage,
                    villageId = villageId,
                    didiNotAvailableCountBPC = didiList.value.filter { it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal
                            || it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal }.size
                )
                val requestList = arrayListOf(saveMatchSummaryRequest)
                val saveMatchSummaryResponse = repository.saveMatchSummary(requestList)
                if (saveMatchSummaryResponse.status.equals(SUCCESS, true)){
                    repository.prefRepo.savePref(PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ + repository.prefRepo.getSelectedVillage().id, true)
                    networkCallbackListener.onSuccess()
                } else {
                    repository.prefRepo.savePref(PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ + villageId, false)
                    networkCallbackListener.onFailed()
                }
                if(!saveMatchSummaryResponse.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(repository.prefRepo,saveMatchSummaryResponse.lastSyncTime)
                }
            } catch (ex: Exception){
                repository.prefRepo.savePref(PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ + repository.prefRepo.getSelectedVillage().id, false)
                onCatchError(ex, ApiType.BPC_SAVE_MATCH_PERCENTAGE_API)
            }
        }
    }

    fun calculateMatchPercentage(didiList: List<DidiEntity>, questionPassingScore: Int): Int {
        val matchedCount = didiList.filter {
            (it.score ?: 0.0) >= questionPassingScore.toDouble()
                    && (it.crpScore ?: 0.0) >= questionPassingScore.toDouble() }.size

        return if (didiList.isNotEmpty() && matchedCount != 0) ((matchedCount.toFloat()/didiList.size.toFloat()) * 100).toInt() else 0
    }

    @SuppressLint("StringFormatMatches")
    fun prepareDidiCountList(context:Context){
        val list:ArrayList<String> = arrayListOf()
        context?.let {

            totalPatDidiCount.value=didiList.value.filter { (it.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal ||
                    it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) && it.patEdit }.size
            if(repository.prefRepo.isUserBPC()){
                totalPatDidiCount.value= didiList.value.filter {  it.wealth_ranking == WealthRank.POOR.rank && it.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && it.patEdit}.size
            }
            notAvailableDidiCount.value= didiList.value.filter {(it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) && it.patEdit }.size
            voEndorseDidiCount.value = didiList.value.filter { it.forVoEndorsement ==1 && it.section2Status == PatSurveyStatus.COMPLETED.ordinal && it.patEdit }.size
            if(repository.prefRepo.isUserBPC()){
                voEndorseDidiCount.value = totalPatDidiCount.value
            }

            if(totalPatDidiCount.value>1)
              list.add(context.getString(R.string.pat_completed_for_didi_plural,totalPatDidiCount.value))
            else list.add(context.getString(R.string.pat_completed_for_didi_singular,totalPatDidiCount.value))

            if(notAvailableDidiCount.value>1)
                list.add(context.getString(R.string.pat_marked_not_available_plural,notAvailableDidiCount.value))
            else list.add(context.getString(R.string.pat_marked_not_available_singular,notAvailableDidiCount.value))

            if(repository.prefRepo.isUserBPC()){
                if(voEndorseDidiCount.value>1)
                    list.add(context.getString(R.string.pat_didi_sent_to_bpm_approval_singular,voEndorseDidiCount.value))
                else list.add(context.getString(R.string.pat_didi_sent_to_bpm_approval_plural,voEndorseDidiCount.value))

            }else{
                if(voEndorseDidiCount.value>1)
                    list.add(context.getString(R.string.pat_didi_sent_to_vo_endorsement_plural,voEndorseDidiCount.value))
                else list.add(context.getString(R.string.pat_didi_sent_to_vo_endorsement_singular,voEndorseDidiCount.value))

            }

            if(list.isNotEmpty()){
                _didiCountList.value=list
            }
        }
    }

    private fun calculateDidiScore(didiId: Int) {
        NudgeLogger.d("SyncHelper", "calculateDidiScore didiId: ${didiId}")
        var passingMark = 0
        var isDidiAccepted = false
        var comment = LOW_SCORE
        val yesQuesCount = repository.fetchOptionYesCount(
            didiId = didiId,
            QuestionType.RadioButton.name,
            TYPE_EXCLUSION
        )
        val _inclusiveQueList = repository.getAllInclusiveQues(didiId = didiId)
        Log.d("TAG", "calculateDidiScoreForDidi: $yesQuesCount :: $didiId")
        if (yesQuesCount > 0) {
            repository.updateParticularDidiScore(
                score = 0.0,
                comment = TYPE_EXCLUSION,
                didiId = didiId,
                isDidiAccepted = false
            )
            repository.updateVOEndorsementDidiStatus(
                repository.prefRepo.getSelectedVillage().id,
                didiId,
                0
            )
        }else {
        if (_inclusiveQueList.isNotEmpty()) {
            var totalWightWithoutNumQue = repository.getTotalWeightWithoutNumQues(didiId)
            NudgeLogger.d(
                "PatSectionSummaryViewModel",
                "calculateDidiScore: $totalWightWithoutNumQue"
            )
            val numQueList =
                _inclusiveQueList.filter { it.type == QuestionType.Numeric_Field.name }
            if (numQueList.isNotEmpty()) {
                numQueList.forEach { answer ->
                    val numQue = repository.getQuestion(answer.questionId)
                    passingMark = numQue.surveyPassingMark ?: 0
                    if (numQue.questionFlag?.equals(FLAG_WEIGHT, true) == true) {
                        val weightList = toWeightageRatio(numQue.json.toString())
                        if (weightList.isNotEmpty()) {
                            val newScore = calculateScore(
                                weightList,
                                answer.totalAssetAmount?.toDouble() ?: 0.0,
                                false
                            )
                            totalWightWithoutNumQue += newScore
                            NudgeLogger.d(
                                "PatSectionSummaryViewModel",
                                "calculateDidiScore: totalWightWithoutNumQue += newScore -> $totalWightWithoutNumQue"
                            )
                        }
                    } else if (numQue.questionFlag?.equals(FLAG_RATIO, true) == true) {
                        val ratioList = toWeightageRatio(numQue.json.toString())
                        val newScore = calculateScore(
                            ratioList,
                            answer.totalAssetAmount?.toDouble() ?: 0.0,
                            true
                        )
                        totalWightWithoutNumQue += newScore
                        NudgeLogger.d(
                            "PatSectionSummaryViewModel",
                            "calculateDidiScore: for Flag FLAG_RATIO totalWightWithoutNumQue += newScore -> $totalWightWithoutNumQue"
                        )
                    }
                }
            }
            // TotalScore


            if (totalWightWithoutNumQue >= passingMark) {
                isDidiAccepted = true
                comment = BLANK_STRING
                repository.updateVOEndorsementDidiStatus(
                    repository.prefRepo.getSelectedVillage().id,
                    didiId,
                    1
                )
            } else {
                isDidiAccepted = false
                repository.updateVOEndorsementDidiStatus(
                    repository.prefRepo.getSelectedVillage().id,
                    didiId,
                    0
                )
            }
            NudgeLogger.d(
                "SyncHelper",
                "calculateDidiScore totalWightWithoutNumQue: $totalWightWithoutNumQue"
            )
            Log.d("TAG", "calculateDidiScoreForDidi: $totalWightWithoutNumQue :: $isDidiAccepted :: $didiId" )
            repository.updateParticularDidiScore(
                score = totalWightWithoutNumQue,
                comment = comment,
                didiId = didiId,
                isDidiAccepted = isDidiAccepted
            )
        } else {
            repository.updateParticularDidiScore(
                score = 0.0,
                comment = TYPE_EXCLUSION,
                didiId = didiId,
                isDidiAccepted = false
            )
        }
      }
    }

    fun updatePatEditFlag() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            repository.updatePatEditFlag(repository.prefRepo.getSelectedVillage().id, false)
        }
    }

    fun updateNeedsToPostBPCProcessStatus(didiId: Int, status: Boolean) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            repository.updateNeedsToPostBPCProcessStatus(status, didiId)
        }
    }

    private suspend fun insertBpcMatchScoreEvent(
        villageId: Int,
        passingScore: Int,
        bpcStep: StepListEntity,
        didiList: List<DidiEntity>
    ) {
        val eventItem = SaveMatchSummaryRequest.getSaveMatchSummaryRequestForBpc(
            villageId = villageId,
            stepListEntity = bpcStep,
            didiList = didiList,
            questionPassionScore = passingScore
        )

        repository.saveEvent(eventItem, EventName.SAVE_BPC_MATCH_SCORE, EventType.STATEFUL)

    }

    fun saveWorkflowEventIntoDb(stepStatus: StepStatus, villageId: Int, stepId: Int) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepEntity =
                repository.getStepForVillage(villageId = villageId, stepId = stepId)
            val updateWorkflowEvent = repository.createWorkflowEvent(
                eventItem = stepEntity,
                stepStatus = stepStatus,
                eventName = EventName.WORKFLOW_STATUS_UPDATE,
                eventType = EventType.STATEFUL,
                prefRepo = repository.prefRepo
            )
            updateWorkflowEvent?.let { event ->
                repository.insertEventIntoDb(event, emptyList())
            }
        }
    }

    override suspend fun updateWorkflowStatus(stepStatus: StepStatus, villageId: Int, stepId: Int) {
        val stepEntity =
            repository.getStepForVillage(villageId = villageId, stepId = stepId)
        val updateWorkflowEvent = repository.createWorkflowEvent(
            eventItem = stepEntity,
            stepStatus = stepStatus,
            eventName = EventName.WORKFLOW_STATUS_UPDATE,
            eventType = EventType.STATEFUL,
            prefRepo = repository.prefRepo
        )
        updateWorkflowEvent?.let { event ->
            repository.saveEventToMultipleSources(event)
        }
        }

    override fun addRankingFlagEditEvent(isUserBpc: Boolean, stepId: Int) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val stepEntity =
                repository.getStepForVillage(
                    villageId = repository.prefRepo.getSelectedVillage().id,
                    stepId = stepId
                )

            val addRankingFlagEditEvent = repository.createRankingFlagEditEvent(
                stepEntity,
                villageId = repository.prefRepo.getSelectedVillage().id,
                stepType = if (isUserBpc) BPC_SURVEY_CONSTANT else PAT_SURVEY,
                repository.prefRepo.getMobileNumber() ?: BLANK_STRING,
                repository.prefRepo.getUserId()
            )

            repository.saveEventToMultipleSources(addRankingFlagEditEvent)
        }
    }


}

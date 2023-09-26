package com.patsurvey.nudge.activities.survey

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.CheckDBStatus
import com.patsurvey.nudge.MyApplication.Companion.appScopeLaunch
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.settings.TransactionIdRequest
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.converters.BeneficiaryProcessStatusModel
import com.patsurvey.nudge.database.dao.*
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.dataModel.PATDidiStatusModel
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
import kotlinx.coroutines.delay
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
    val bpcNonSelectedDidiDao: BpcNonSelectedDidiDao,
    val questionListDao: QuestionListDao
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
        if (prefRepo.isUserBPC()) {
            fetchDidisForBpcFromDB()
        } else {
            fetchDidisFromDB()
        }
        setVillage(prefRepo.getSelectedVillage().id)
    }

     fun fetchDidisForBpcFromDB() {
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

    fun setVillage(villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch{
            var village = villageListDao.fetchVillageDetailsForLanguage(villageId, prefRepo.getAppLanguageId() ?: 2) ?: villageListDao.getVillage(villageId)
            val voEndorsementStep =stepsListDao.getStepByOrder(5,prefRepo.getSelectedVillage().id)
            withContext(Dispatchers.IO){
                isVOEndorsementComplete.value = voEndorsementStep.isComplete == StepStatus.COMPLETED.ordinal
            }
            withContext(Dispatchers.Main) {
                villageEntity.value = village
            }
        }
    }

    fun fetchDidisFromDB(){
        job= CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO){
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
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            try {
                var didiIDList = emptyList<PATDidiStatusModel>()
                didiIDList = answerDao.fetchPATSurveyDidiList(prefRepo.getSelectedVillage().id)
                Log.d("TAG", "savePATSummeryToServer before ListSize: ${didiIDList.size}")

                var optionList: List<OptionsItem>
                var answeredDidiList: ArrayList<PATSummarySaveRequest> = arrayListOf()
                var scoreDidiList: ArrayList<EditDidiWealthRankingRequest> = arrayListOf()
                var surveyId = 0


                try {
                     didiIDList = answerDao.fetchPATSurveyDidiList(prefRepo.getSelectedVillage().id)

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
                        didi.score = didiDao.getDidiScoreFromDb(didi.id)
                        var qList: ArrayList<AnswerDetailDTOListItem> = arrayListOf()
                        val needToPostQuestionsList = answerDao.getAllNeedToPostQuesForDidi(didi.id)
                        if (needToPostQuestionsList.isNotEmpty()) {
                            needToPostQuestionsList.forEach {
                                surveyId = questionDao.getQuestion(it.questionId).surveyId ?: 0
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
                        val passingMark = questionDao.getPassingScore()
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
                                type = if (prefRepo.isUserBPC()) BPC_SURVEY_CONSTANT else PAT_SURVEY,
                                result = if (didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) {
                                    DIDI_NOT_AVAILABLE
                                } else if (didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal) {
                                    PatSurveyStatus.INPROGRESS.name
                                } else {
                                    if (didi.forVoEndorsement == 0 || didi.patExclusionStatus != ExclusionType.NO_EXCLUSION.ordinal) DIDI_REJECTED else {
                                        if (prefRepo.isUserBPC())
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
                                villageId = prefRepo.getSelectedVillage().id,
                                surveyId = surveyId,
                                beneficiaryId = if (didi.serverId == 0) didi.id else didi.serverId,
                                languageId = prefRepo.getAppLanguageId() ?: 2,
                                stateId = prefRepo.getSelectedVillage().stateId,
                                totalScore = didi.score,
                                userType = if (prefRepo.isUserBPC()) USER_BPC else USER_CRP,
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
                            val saveAPIResponse = apiService.savePATSurveyToServer(answeredDidiList)
                            if (saveAPIResponse.status.equals(SUCCESS, true)) {
                                if (saveAPIResponse.data?.get(0)?.transactionId.isNullOrEmpty()) {
                                    didiIDList.forEach { didiItem ->
                                        didiDao.updateNeedToPostPAT(
                                            false,
                                            didiItem.id,
                                            prefRepo.getSelectedVillage().id
                                        )
                                    }
                                    networkCallbackListener.onSuccess()
                                } else {
                                    for (i in didiIDList.indices) {
                                        saveAPIResponse.data?.get(i)?.let {
                                            didiDao.updateDidiTransactionId(
                                                didiIDList[i].id,
                                                it.transactionId
                                            )
                                        }
                                    }
                                    checkDidiPatStatus()
                                }
                            } else {
                                networkCallbackListener.onFailed()
                            }
                            if (!saveAPIResponse.lastSyncTime.isNullOrEmpty()) {
                                updateLastSyncTime(prefRepo, saveAPIResponse.lastSyncTime)
                            }
                            val updateScoreResponse = apiService.updateDidiScore(scoreDidiList)
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
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            NudgeLogger.d("SurveySummaryViewModel", "callWorkFlowAPI -> called")
            try {
                val dbResponse=stepsListDao.getStepForVillage(villageId, stepId)
                NudgeLogger.d("SurveySummaryViewModel", "callWorkFlowAPI -> dbResponse = $dbResponse")
                val stepList = stepsListDao.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }
                NudgeLogger.d("SurveySummaryViewModel", "callWorkFlowAPI -> stepList = $stepList \n\n")
                if (dbResponse.workFlowId > 0) {
                    val primaryWorkFlowRequest =
                        listOf(EditWorkFlowRequest(
                            if (!prefRepo.isUserBPC()) stepList[stepList.map { it.orderNumber }
                                .indexOf(4)].workFlowId else stepList[stepList.map { it.orderNumber }
                                .indexOf(6)].workFlowId, StepStatus.COMPLETED.name,
                            longToString(prefRepo.getPref(PREF_PAT_COMPLETION_DATE_+prefRepo.getSelectedVillage().id,System.currentTimeMillis()))

                        ))
                    NudgeLogger.d(
                        "SurveySummaryViewModel",
                        "callWorkFlowAPI -> primaryWorkFlowRequest = $primaryWorkFlowRequest"
                    )
                    val response = apiService.editWorkFlow(
                        primaryWorkFlowRequest
                    )
                    NudgeLogger.d(
                        "SurveySummaryViewModel",
                        "callWorkFlowAPI -> response: status = ${response.status}, message = ${response.message}, data = ${response.data.toString()} \n"
                    )
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.let { primaryWorkFlowResponse ->
                            //Here programsProcessId == id for our local db.
                            stepsListDao.updateWorkflowId(
                                stepId = primaryWorkFlowResponse[0].programsProcessId,
                                workflowId = primaryWorkFlowResponse[0].id,
                                villageId = villageId,
                                status = primaryWorkFlowResponse[0].status
                            )
                        }
                        NudgeLogger.d("SurveySummaryViewModel", "callWorkFlowAPI -> stepsListDao.updateNeedToPost before: for primaryWorkFlowResponse\n")

                        stepsListDao.updateNeedToPost(
                            id = if (!prefRepo.isUserBPC())
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
                        updateLastSyncTime(prefRepo, response.lastSyncTime)
                    }

                }
                try {
                    NudgeLogger.d("SurveySummaryViewModel", "callWorkFlowAPI -> second try = called")
                    stepList.forEach { step ->
                        if (!prefRepo.isUserBPC()) {
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

                                val inProgressStepResponse = apiService.editWorkFlow(inProgressStepRequest)

                                NudgeLogger.d("SurveySummaryViewModel", "callWorkFlowAPI -> inProgressStepResponse: status = ${inProgressStepResponse.status}, message = ${inProgressStepResponse.message}, data = ${inProgressStepResponse.data.toString()}")


                                if (inProgressStepResponse.status.equals(SUCCESS, true)) {
                                    inProgressStepResponse.data?.let {
                                        stepsListDao.updateWorkflowId(
                                            step.id,
                                            step.workFlowId,
                                            villageId,
                                            it[0].status
                                        )
                                    }
                                    NudgeLogger.d("AddDidiViewModel", "callWorkFlowAPI -> stepsListDao.updateNeedToPost before for inProgressStepResponse")

                                    stepsListDao.updateNeedToPost(step.id, villageId, false)

                                    NudgeLogger.d("AddDidiViewModel", "callWorkFlowAPI -> stepsListDao.updateNeedToPost after for inProgressStepResponse")

                                    networkCallbackListener.onSuccess()
                                } else {
                                    NudgeLogger.d("SurveySummaryViewModel", "callWorkFlowAPI -> inProgressStepResponse = FAIL")
                                    networkCallbackListener.onFailed()
                                }
                                if (!inProgressStepResponse.lastSyncTime.isNullOrEmpty()) {
                                    updateLastSyncTime(prefRepo, inProgressStepResponse.lastSyncTime)
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
        prefRepo.savePref(PREF_PAT_COMPLETION_DATE_+prefRepo.getSelectedVillage().id, currentTime)
    }

    fun saveBpcPatCompletionDate() {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val date = dateFormat.format(currentTime)
        prefRepo.savePref(PREF_BPC_PAT_COMPLETION_DATE_, date)
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
            stepsListDao.updateNeedToPost(stepId, villageId, true)
            val stepDetails = stepsListDao.getStepForVillage(villageId, stepId)
            if (stepDetails.orderNumber < stepsListDao.getAllSteps().size) {
                stepsListDao.markStepAsInProgress(
                    (stepDetails.orderNumber + 1),
                    StepStatus.INPROGRESS.ordinal,
                    villageId
                )
                stepsListDao.updateNeedToPost(stepDetails.id, villageId, true)
            }
            prefRepo.savePref("$VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_${villageId}", true)
        }
    }

    fun saveVoEndorsementDate() {
        val currentTime = System.currentTimeMillis()
//        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
//        val date = dateFormat.format(currentTime)
        prefRepo.savePref(PREF_VO_ENDORSEMENT_COMPLETION_DATE_, currentTime.toString())
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
                } else if (didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) {
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
        return "${PREF_FORM_PATH}_${prefRepo.getSelectedVillage().id}_${subPath}"
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
            val villageId = prefRepo.getSelectedVillage().id
            val oldDidiList = bpcSelectedDidiDao.fetchAllDidisForVillage(villageId = villageId)
            val updatedList = didiDao.getAllDidisForVillage(villageId = villageId)
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
                        villageId = villageId
                    )
                )
                if (updateSelectedDidiResponse.status.equals(SUCCESS, true)) {
                    Log.d("SurveySummaryViewModel", "sendBpcUpdatedDidiList: $SUCCESS")
                    prefRepo.savePref(PREF_BPC_DIDI_LIST_SYNCED_FOR_VILLAGE_ + villageId, true)

                } else {
                    Log.d("SurveySummaryViewModel", "sendBpcUpdatedDidiList: $FAIL")
                    prefRepo.savePref(PREF_BPC_DIDI_LIST_SYNCED_FOR_VILLAGE_ + villageId, false)
                    networkCallbackListener.onFailed()
                }
                if(!updateSelectedDidiResponse.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,updateSelectedDidiResponse.lastSyncTime)
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
                            stepsListDao.updateNeedToPost(bpcStep.id, villageId, false)
                        }else{
                            networkCallbackListener.onFailed()
                            onError(tag = "ProgressScreenViewModel", "Error : ${response.message}")
                        }

                        if(!response.lastSyncTime.isNullOrEmpty()){
                            updateLastSyncTime(prefRepo,response.lastSyncTime)
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
                val matchPercentage = calculateMatchPercentage(didiList.value.filter { it.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal }, passingScore)
                val saveMatchSummaryRequest = SaveMatchSummaryRequest(
                    programId = bpcStep.programId,
                    score = matchPercentage,
                    villageId = villageId,
                    didiNotAvailableCountBPC = didiList.value.filter { it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal
                            || it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal }.size
                )
                val requestList = arrayListOf(saveMatchSummaryRequest)
                val saveMatchSummaryResponse = apiService.saveMatchSummary(requestList)
                if (saveMatchSummaryResponse.status.equals(SUCCESS, true)){
                    prefRepo.savePref(PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ + prefRepo.getSelectedVillage().id, true)
                    networkCallbackListener.onSuccess()
                } else {
                    prefRepo.savePref(PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ + villageId, false)
                    networkCallbackListener.onFailed()
                }
                if(!saveMatchSummaryResponse.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,saveMatchSummaryResponse.lastSyncTime)
                }
            } catch (ex: Exception){
                prefRepo.savePref(PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ + prefRepo.getSelectedVillage().id, false)
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
            if(prefRepo.isUserBPC()){
                totalPatDidiCount.value= didiList.value.filter {  it.wealth_ranking == WealthRank.POOR.rank && it.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && it.patEdit}.size
            }
            notAvailableDidiCount.value= didiList.value.filter {(it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) && it.patEdit }.size
            voEndorseDidiCount.value = didiList.value.filter { it.forVoEndorsement ==1 && it.section2Status == PatSurveyStatus.COMPLETED.ordinal && it.patEdit }.size
            if(prefRepo.isUserBPC()){
                voEndorseDidiCount.value = totalPatDidiCount.value
            }

            if(totalPatDidiCount.value>1)
              list.add(context.getString(R.string.pat_completed_for_didi_plural,totalPatDidiCount.value))
            else list.add(context.getString(R.string.pat_completed_for_didi_singular,totalPatDidiCount.value))

            if(notAvailableDidiCount.value>1)
                list.add(context.getString(R.string.pat_marked_not_available_plural,notAvailableDidiCount.value))
            else list.add(context.getString(R.string.pat_marked_not_available_singular,notAvailableDidiCount.value))

            if(prefRepo.isUserBPC()){
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
        val yesQuesCount = answerDao.fetchOptionYesCount(
            didiId = didiId,
            QuestionType.RadioButton.name,
            TYPE_EXCLUSION
        )
        val _inclusiveQueList = answerDao.getAllInclusiveQues(didiId = didiId)
        if (yesQuesCount > 0) {
            didiDao.updateDidiScore(
                score = 0.0,
                comment = TYPE_EXCLUSION,
                didiId = didiId,
                isDidiAccepted = false
            )
            didiDao.updateVOEndorsementDidiStatus(
                prefRepo.getSelectedVillage().id,
                didiId,
                0
            )
            if (prefRepo.isUserBPC()) {
                bpcSelectedDidiDao.updateSelDidiScore(
                    score = 0.0,
                    comment = TYPE_EXCLUSION,
                    didiId = didiId,
                )
            }
        }else {
        if (_inclusiveQueList.isNotEmpty()) {
            var totalWightWithoutNumQue = answerDao.getTotalWeightWithoutNumQues(didiId)
            NudgeLogger.d(
                "PatSectionSummaryViewModel",
                "calculateDidiScore: $totalWightWithoutNumQue"
            )
            val numQueList =
                _inclusiveQueList.filter { it.type == QuestionType.Numeric_Field.name }
            if (numQueList.isNotEmpty()) {
                numQueList.forEach { answer ->
                    val numQue = questionDao.getQuestion(answer.questionId)
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
                didiDao.updateVOEndorsementDidiStatus(
                    prefRepo.getSelectedVillage().id,
                    didiId,
                    1
                )
            } else {
                isDidiAccepted = false
                didiDao.updateVOEndorsementDidiStatus(
                    prefRepo.getSelectedVillage().id,
                    didiId,
                    0
                )
            }
            NudgeLogger.d(
                "SyncHelper",
                "calculateDidiScore totalWightWithoutNumQue: $totalWightWithoutNumQue"
            )
            Log.d("TAG", "calculateDidiScore: totalWightWithoutNumQue $totalWightWithoutNumQue :: $isDidiAccepted")
            didiDao.updateDidiScore(
                score = totalWightWithoutNumQue,
                comment = comment,
                didiId = didiId,
                isDidiAccepted = isDidiAccepted
            )
            if (prefRepo.isUserBPC()) {
                bpcSelectedDidiDao.updateSelDidiScore(
                    score = totalWightWithoutNumQue,
                    comment = comment,
                    didiId = didiId,
                )
            }
        } else {
            didiDao.updateDidiScore(
                score = 0.0,
                comment = TYPE_EXCLUSION,
                didiId = didiId,
                isDidiAccepted = false
            )
            if (prefRepo.isUserBPC()) {
                bpcSelectedDidiDao.updateSelDidiScore(
                    score = 0.0,
                    comment = TYPE_EXCLUSION,
                    didiId = didiId,
                )
            }
        }
      }
    }
    fun validateDidiToNavigate(didiId: Int,onNavigateToSummary:(Int)->Unit){
        job = CoroutineScope(Dispatchers.IO +exceptionHandler).launch{
            val questionExclusionAnswered = answerDao.getAnswerForDidi(didiId = didiId, actionType = TYPE_EXCLUSION)
            val questionInclusionAnswered = answerDao.getAnswerForDidi(didiId = didiId, actionType = TYPE_INCLUSION)
            val quesList = questionListDao.getAllQuestionsForLanguage(prefRepo.getAppLanguageId()?:2)
            val yesQuesCount = answerDao.fetchOptionYesCount(didiId = didiId,QuestionType.RadioButton.name,TYPE_EXCLUSION)
            exclusiveQuesCount.value = quesList.filter { it.actionType == TYPE_EXCLUSION }.size
            inclusiveQuesCount.value = quesList.filter { it.actionType == TYPE_INCLUSION }.size
            if(questionInclusionAnswered.isNotEmpty()){
                if(inclusiveQuesCount.value == questionInclusionAnswered.size){
                    withContext(Dispatchers.Main){
                        if(yesQuesCount>0){
                            onNavigateToSummary(SummaryNavigation.SECTION_1_PAGE.ordinal)
                        }else onNavigateToSummary(SummaryNavigation.SECTION_2_PAGE.ordinal)
                    }
                }else{
                    withContext(Dispatchers.Main){
                        onNavigateToSummary(SummaryNavigation.DIDI_CAMERA_PAGE.ordinal)
                    }
                }
            }else{
                if(questionExclusionAnswered.isNotEmpty()){
                    if(exclusiveQuesCount.value == questionExclusionAnswered.size){
                        withContext(Dispatchers.Main){
                            onNavigateToSummary(SummaryNavigation.SECTION_1_PAGE.ordinal)
                        }
                    }else{
                        withContext(Dispatchers.Main){
                            onNavigateToSummary(SummaryNavigation.DIDI_CAMERA_PAGE.ordinal)
                        }
                    }
                }else {
                    withContext(Dispatchers.Main){
                        onNavigateToSummary(SummaryNavigation.DIDI_CAMERA_PAGE.ordinal)
                    }
                }
            }
        }
    }

    fun updatePatEditFlag() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            didiDao.updatePatEditFlag(prefRepo.getSelectedVillage().id, false)
        }
    }

    /* fun updatePatEditFlagForDidis() {
         job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
             val villageId = prefRepo.getSelectedVillage().id
             didiDao.updatePatEditFlag(villageId, false)
         }
     }*/


}

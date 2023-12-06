package com.patsurvey.nudge

import android.annotation.SuppressLint
import androidx.compose.runtime.MutableState
import com.google.gson.Gson
import com.patsurvey.nudge.activities.settings.SettingViewModel
import com.patsurvey.nudge.activities.settings.TransactionIdRequest
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.dao.*
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.request.*
import com.patsurvey.nudge.model.response.OptionsItem
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class SyncBPCDataOnServer(val settingViewModel: SettingViewModel,
                          val prefRepo: PrefRepo,
                          val apiService: ApiService,
                          val exceptionHandler : CoroutineExceptionHandler,
                          var job: Job?,
                          val didiDao: DidiDao,
                          val stepsListDao: StepsListDao,
                          val questionDao: QuestionListDao,
                          var syncPercentage : MutableState<Float>,
                          val answerDao: AnswerDao,
                          val numericAnswerDao: NumericAnswerDao,
                          val villageListDao : VillageListDao){

    private var isPending = 0
    private val pendingTimerTime:Long = 10000

    fun syncBPCDataToServer(networkCallbackListener: NetworkCallbackListener){
//        sendBpcUpdatedDidiList(networkCallbackListener)
        savePATSummeryToServer(networkCallbackListener)
    }

    // step 1
    private fun sendBpcUpdatedDidiList(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.Main) {
                delay(1000)
                syncPercentage.value = 0f
            }
            if(isBPCDidiNeedToBeReplaced()) {
                val villageList = villageListDao.getAllVillages(prefRepo.getAppLanguageId() ?: 2)
                for(village in villageList) {
                    val villageId = village.id
                    val updatedList = didiDao.getAllDidisForVillage(villageId = villageId)
                    try {
                        val oldBeneficiaryIdSelected = mutableListOf<Int>()

                        NudgeLogger.d(
                            "SyncBPCDataOnServer",
                            "sendBpcUpdatedDidiList: newBeneficiaryIdSelected -> $oldBeneficiaryIdSelected"
                        )
                        val newBeneficiaryIdSelected = mutableListOf<Int>()
                        updatedList.forEach {
                            newBeneficiaryIdSelected.add(it.serverId)
                        }
                        NudgeLogger.d(
                            "SyncBPCDataOnServer",
                            "sendBpcUpdatedDidiList: newBeneficiaryIdSelected -> $newBeneficiaryIdSelected"
                        )
                        val updateSelectedDidiResponse = apiService.sendSelectedDidiList(
                            BpcUpdateSelectedDidiRequest(
                                oldBeneficiaryIdSelected = oldBeneficiaryIdSelected,
                                newBeneficiaryIdSelected = newBeneficiaryIdSelected,
                                villageId = villageId
                            )
                        )
                        if (updateSelectedDidiResponse.status.equals(SUCCESS, true)) {
                            NudgeLogger.d("SyncBPCDataOnServer", "sendBpcUpdatedDidiList: $SUCCESS")
                            prefRepo.savePref(
                                PREF_BPC_DIDI_LIST_SYNCED_FOR_VILLAGE_ + villageId,
                                true
                            )
                        } else {
                            NudgeLogger.d("SyncBPCDataOnServer", "sendBpcUpdatedDidiList: $FAIL")
                            prefRepo.savePref(
                                PREF_BPC_DIDI_LIST_SYNCED_FOR_VILLAGE_ + villageId,
                                false
                            )
                            withContext(Dispatchers.Main) {
                                networkCallbackListener.onFailed()
                            }
                        }
                        if (!updateSelectedDidiResponse.lastSyncTime.isNullOrEmpty()) {
                            updateLastSyncTime(prefRepo, updateSelectedDidiResponse.lastSyncTime)
                        }
                    } catch (ex: Exception) {
                        settingViewModel.onCatchError(ex, ApiType.BPC_UPDATE_DIDI_LIST_API)
                    }
                }
                savePATSummeryToServer(networkCallbackListener)
            } else {
                savePATSummeryToServer(networkCallbackListener)
            }
        }
    }

    private fun isBPCDidiNeedToBeReplaced() : Boolean{
        val villageList = villageListDao.getAllVillages(prefRepo.getAppLanguageId()?:2)
        for(village in villageList) {
            val villageId = village.id
            val oldDidiList = didiDao.getAllDidisForVillage(villageId)
            val updatedList = didiDao.getAllDidisForVillage(villageId)
            val oldBeneficiaryIdSelected = mutableListOf<Int>()
            oldDidiList.forEach {
                oldBeneficiaryIdSelected.add(it.serverId)
            }
            val newBeneficiaryIdSelected = mutableListOf<Int>()
            updatedList.forEach {
                newBeneficiaryIdSelected.add(it.serverId)
            }
            for (id in newBeneficiaryIdSelected) {
                if (oldBeneficiaryIdSelected.indexOf(id) == -1) {
                    return true
                } else
                    prefRepo.savePref(PREF_BPC_DIDI_LIST_SYNCED_FOR_VILLAGE_ + village.id, true)
            }
        }
        return false
    }

    private fun startSyncTimer(networkCallbackListener: NetworkCallbackListener){
        val timer = Timer()
        timer.schedule(object : TimerTask(){
            override fun run() {
                when (isPending) {
                    1 -> {
                        checkPendingPatStatus(networkCallbackListener)
                    }
                    2 -> {
                        checkUpdateBpcPatStatus(networkCallbackListener)
                    }
                }
            }
        },pendingTimerTime)
    }

    private fun checkPendingPatStatus(networkCallbackListener: NetworkCallbackListener) {
        NudgeLogger.d("SyncBPCDataOnServer", "checkPendingPatStatus -> called")
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiIDList= didiDao.fetchPendingPatStatusDidi(true,"")
            if(didiIDList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                didiIDList.forEach { didi ->
                    didi.transactionId?.let { ids.add(it) }
                }
                NudgeLogger.d("SyncBPCDataOnServer", "checkPendingPatStatus -> TransactionIdRequest = $ids")
                val response = apiService.getPendingStatus(TransactionIdRequest("",ids))
                if (response.status.equals(SUCCESS, true)) {
                    NudgeLogger.d("SyncBPCDataOnServer", "checkPendingPatStatus -> SUCCESS")
                    response.data?.forEach { transactionIdResponse ->
                        didiIDList.forEach { didiEntity ->
                            if (transactionIdResponse.transactionId == didiEntity.transactionId) {
                                didiDao.updateNeedToPostPAT(false,didiEntity.serverId)
                            }
                        }
                    }
                    updateBpcPatStatusToNetwork(networkCallbackListener)
                } else {
                    NudgeLogger.d("SyncBPCDataOnServer", "checkPendingPatStatus -> FAIL")
                    withContext(Dispatchers.Main) {
                        networkCallbackListener.onFailed()
                    }
                }
                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }

            } else {
                NudgeLogger.d("SyncBPCDataOnServer", "checkPendingPatStatus -> didiIDList is empty")
                updateBpcPatStatusToNetwork(networkCallbackListener)
            }
        }
    }

    private fun checkUpdateBpcPatStatus(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiIDList= didiDao.getAllPendingNeedToPostBPCProcessDidi(true,"")
            if(didiIDList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                didiIDList.forEach { didi ->
                    didi.transactionId?.let { ids.add(it) }
                }
                val response = apiService.getPendingStatus(TransactionIdRequest("",ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        didiIDList.forEach { didiEntity ->
                            if (transactionIdResponse.transactionId == didiEntity.transactionId) {
                                didiDao.updateNeedToPostPAT(false,didiEntity.serverId)
                            }
                        }
                    }
                    callWorkFlowAPIForBpc(networkCallbackListener)
                } else {
                    withContext(Dispatchers.Main) {
                        networkCallbackListener.onFailed()
                    }
                }
                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }

            } else {
                callWorkFlowAPIForBpc(networkCallbackListener)
            }
        }
    }

    // step 4
    fun callWorkFlowAPIForBpc( networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                withContext(Dispatchers.Main) {
                    delay(1000)
                    syncPercentage.value = 0.4f
                }
                val villageList = villageListDao.getAllVillages(prefRepo.getAppLanguageId()?:2)
                val addWorkFlowRequest = mutableListOf<AddWorkFlowRequest>()
                val editWorkFlowRequest = mutableListOf<EditWorkFlowRequest>()
                val needToEditStep = mutableListOf<StepListEntity>()
                val needToAddStep = mutableListOf<StepListEntity>()
                for(village in villageList) {
                    val villageId = village.id
                    val stepList =
                        stepsListDao.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }
                    val bpcStep = stepList.last()
                    if (bpcStep.workFlowId > 0) {
                        if (bpcStep.needToPost) {
                            editWorkFlowRequest.add(
                                (EditWorkFlowRequest(
                                    bpcStep.workFlowId,
                                    StepStatus.getStepFromOrdinal(bpcStep.isComplete)
                                ))
                            )
                            needToEditStep.add(bpcStep)
                        }
                    } else {
                        if (bpcStep.needToPost) {
                            needToAddStep.add(bpcStep)
                            addWorkFlowRequest.add(
                                (AddWorkFlowRequest(
                                    StepStatus.INPROGRESS.name, bpcStep.villageId,
                                    bpcStep.programId, bpcStep.id
                                ))
                            )
                        }
                    }
                }
                if (addWorkFlowRequest.size > 0) {

                    NudgeLogger.e("SyncHelper", "callWorkFlowAPI addWorkFlowRequest: $addWorkFlowRequest \n\n")

                    val addWorkFlowResponse = apiService.addWorkFlow(Collections.unmodifiableList(addWorkFlowRequest))
                    NudgeLogger.d("SyncBPCDataOnServer","addWorkFlow Request=> ${Gson().toJson(Collections.unmodifiableList(addWorkFlowRequest))}")
                    NudgeLogger.e("SyncHelper","callWorkFlowAPI response: status: ${addWorkFlowResponse.status}, message: ${addWorkFlowResponse.message}, data: ${addWorkFlowResponse.data} \n\n")

                    if (addWorkFlowResponse.status.equals(SUCCESS, true)) {
                        addWorkFlowResponse.data?.let {
                            if (addWorkFlowResponse.data[0].transactionId.isNullOrEmpty()) {
                                for (i in addWorkFlowResponse.data.indices) {
                                    val step = needToAddStep[i]
                                    stepsListDao.updateOnlyWorkFlowId(
                                        it[i].id,
                                        step.villageId,
                                        step.id
                                    )
                                    step.workFlowId = it[0].id
                                    NudgeLogger.e(
                                        "SyncBPCDataOnServer",
                                        "callWorkFlowAPI stepsListDao.updateOnlyWorkFlowId before stepId: $step.stepId, it[0].id: ${it[0].id}, villageId: $step.villageId"
                                    )
                                }
                                NudgeLogger.e(
                                    "SyncBPCDataOnServer",
                                    "callWorkFlowAPI stepsListDao.updateOnlyWorkFlowId after"
                                )
                                delay(100)
                                needToAddStep.addAll(needToEditStep)
                                updateBpcStepsToServer(needToAddStep, networkCallbackListener)
                            }
                        }
                    } else {
                        NudgeLogger.e("SyncBPCDataOnServer",
                            "callWorkFlowAPI ApiResponseFailException", ApiResponseFailException(addWorkFlowResponse.message))
                        networkCallbackListener.onFailed()
                    }

                } else if(needToEditStep.size>0){
                    updateBpcStepsToServer(needToEditStep, networkCallbackListener)
                }

            }catch (ex:Exception){
                settingViewModel.onCatchError(ex, ApiType.ADD_WORK_FLOW_API)
                withContext(Dispatchers.Main) {
                    networkCallbackListener.onFailed()
                }
            }
        }
    }

    private fun updateBpcStepsToServer(needToEditStep: MutableList<StepListEntity>, networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                if (needToEditStep.isNotEmpty()) {
                    val requestForStepUpdation = mutableListOf<EditWorkFlowRequest>()
                    for (step in needToEditStep) {
                        var stepCompletionDate = BLANK_STRING
                        stepCompletionDate =longToString(prefRepo.getPref(
                            PREF_BPC_PAT_COMPLETION_DATE_+step.villageId, System.currentTimeMillis()))
                        requestForStepUpdation.add(
                            EditWorkFlowRequest(
                                step.workFlowId,
                                StepStatus.getStepFromOrdinal(step.isComplete),
                                stepCompletionDate
                            )
                        )
                    }
                    val responseForStepUpdation =
                        apiService.editWorkFlow(requestForStepUpdation)
                    NudgeLogger.d("SyncBPCDataOnServer","editWorkFlow Request=> ${Gson().toJson(requestForStepUpdation)}")
                    NudgeLogger.e(
                        "SyncBPCDataOnServer",
                        "callWorkFlowAPI response: status: ${responseForStepUpdation.status}, message: ${responseForStepUpdation.message}, data: ${responseForStepUpdation.data} \n\n"
                    )


                    if (responseForStepUpdation.status.equals(SUCCESS, true)) {
                        responseForStepUpdation.data?.let {

                            for(i in responseForStepUpdation.data.indices) {
                                val step = needToEditStep[i]
                                stepsListDao.updateWorkflowId(
                                    step.stepId,
                                    step.workFlowId,
                                    step.villageId,
                                    step.status
                                )

                                NudgeLogger.e(
                                    "SyncBPCDataOnServer",
                                    "callWorkFlowAPI stepsListDao.updateWorkflowId after "
                                )
                                NudgeLogger.e(
                                    "SyncBPCDataOnServer",
                                    "callWorkFlowAPI stepsListDao.updateNeedToPost before stepId: $step.stepId"
                                )
                                stepsListDao.updateNeedToPost(step.id, step.villageId, false)
                                NudgeLogger.e(
                                    "SyncBPCDataOnServer",
                                    "callWorkFlowAPI stepsListDao.updateNeedToPost after stepId: $step.stepId"
                                )

                            }
                        }
                        sendBpcMatchScore(networkCallbackListener)
                    } else {
                        NudgeLogger.e("SyncBPCDataOnServer",
                            "callWorkFlowAPI ApiResponseFailException", ApiResponseFailException(responseForStepUpdation.message))
                        networkCallbackListener.onFailed()
                    }
                    if (!responseForStepUpdation.lastSyncTime.isNullOrEmpty()) {
                        updateLastSyncTime(
                            prefRepo,
                            responseForStepUpdation.lastSyncTime
                        )
                    }
                } else {
                    sendBpcMatchScore(networkCallbackListener)
                }
            } catch (ex: Exception) {
                settingViewModel.onCatchError(ex, ApiType.WORK_FLOW_API)
                withContext(Dispatchers.Main) {
                    networkCallbackListener.onFailed()
                }
            }

        }
    }

    // step 5
    fun sendBpcMatchScore(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.Main) {
                delay(1000)
                syncPercentage.value = 0.6f
            }
            if (!settingViewModel.isBPCScoreSaved()) {
                val didiList = didiDao.getAllDidisForVillage(prefRepo.getSelectedVillage().id)
                try {
                    val villageList  = villageListDao.getAllVillages(prefRepo.getAppLanguageId() ?: 2)
                    val passingScore = questionDao.getPassingScore()
                    val requestList = arrayListOf<SaveMatchSummaryRequest>()
                    villageList.forEach { village ->
                        val bpcStep =
                            stepsListDao.getAllStepsForVillage(village.id).sortedBy { it.orderNumber }
                                .last()
                        if (bpcStep.isComplete == StepStatus.COMPLETED.ordinal) {
                            val matchPercentage = calculateMatchPercentage(
                                didiList.filter { it.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal },
                                passingScore
                            )
                            val saveMatchSummaryRequest = SaveMatchSummaryRequest(
                                programId = bpcStep.programId,
                                score = matchPercentage,
                                villageId = village.id,
                                didiNotAvailableCountBPC = didiList.filter {
                                    it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal
                                            || it.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal
                                }.size
                            )
                            requestList.add(saveMatchSummaryRequest)
                        }
                        NudgeLogger.d("SyncBPCDataOnServer","sendBpcMatchScore saveMatchSummary Request=> ${requestList.json()}")
                        val saveMatchSummaryResponse = apiService.saveMatchSummary(requestList)
                        NudgeLogger.d("SyncBPCDataOnServer","sendBpcMatchScore saveMatchSummary saveMatchSummaryResponse=> ${saveMatchSummaryResponse.json()}")
                        if (saveMatchSummaryResponse.status.equals(SUCCESS, true)) {
                            withContext(Dispatchers.Main) {
                                networkCallbackListener.onSuccess()
                                prefRepo.savePref(PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ + prefRepo.getSelectedVillage().id, true)
                            }
                        } else {
                            prefRepo.savePref(PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ + prefRepo.getSelectedVillage().id, false)
                            withContext(Dispatchers.Main) {
                                networkCallbackListener.onFailed()
                            }
                        }
                        if(!saveMatchSummaryResponse.lastSyncTime.isNullOrEmpty()){
                            updateLastSyncTime(prefRepo,saveMatchSummaryResponse.lastSyncTime)
                        }
                    }
                } catch (ex: Exception) {
                    prefRepo.savePref(PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ + prefRepo.getSelectedVillage().id, false)
                    settingViewModel.onCatchError(ex, ApiType.BPC_SAVE_MATCH_PERCENTAGE_API)
                    withContext(Dispatchers.Main) {
                        networkCallbackListener.onFailed()
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    networkCallbackListener.onSuccess()
                }
            }
        }
    }

    fun calculateMatchPercentage(didiList: List<DidiEntity>, questionPassingScore: Int): Int {
        val matchedCount = didiList.filter {
            (it.score ?: 0.0) >= questionPassingScore.toDouble()
                    && (it.crpScore ?: 0.0) >= questionPassingScore.toDouble() }.size

        return if (didiList.isNotEmpty() && matchedCount != 0) ((matchedCount.toFloat()/didiList.size.toFloat()) * 100).toInt() else 0

    }

    // step 3 transaction id
    fun updateBpcPatStatusToNetwork(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.Main) {
                delay(1000)
                syncPercentage.value = 0.8f
            }
            val needToPostPatDidi =
                didiDao.getAllNeedToPostBPCProcessDidi(true)
            val passingScore = questionDao.getPassingScore()
            if (!needToPostPatDidi.isNullOrEmpty()) {
                val didiRequestList : ArrayList<EditDidiWealthRankingRequest> = arrayListOf()
                needToPostPatDidi.forEach { didi ->
                    var comment= BLANK_STRING
                    if(didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal ||  didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal)
                        comment= BLANK_STRING
                    else {
                        comment =if((didi.score ?: 0.0) < passingScore) LOW_SCORE else {
                            if(didi.patSurveyStatus==PatSurveyStatus.COMPLETED.ordinal && didi.section2Status==PatSurveyStatus.NOT_STARTED.ordinal){
                                TYPE_EXCLUSION
                            }else BLANK_STRING}
                    }
                    didiRequestList.add(
                        EditDidiWealthRankingRequest(
                            id = if (didi.serverId == 0) didi.id else didi.serverId,
                            score = didi.score,
                            comment =comment,
                            type = BPC_SURVEY_CONSTANT,
                            result = if(didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal ||  didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) DIDI_NOT_AVAILABLE
                            else {
                                if (didi.forVoEndorsement == 0) DIDI_REJECTED else {
                                    if (prefRepo.isUserBPC())
                                        VERIFIED_STRING
                                    else
                                        COMPLETED_STRING
                                }
                            }
                        )
                    )
                    try {
                        val updatedPatResponse = apiService.updateDidiRanking(didiRequestList)
                        NudgeLogger.d("SyncBPCDataOnServer","updateDidiRanking Request=> ${Gson().toJson(didiRequestList)}")
                        if (updatedPatResponse.status.equals(SUCCESS, true)) {
                            if (updatedPatResponse.data?.isNotEmpty() == true) {
                                if (updatedPatResponse.data?.get(0)?.transactionId.isNullOrEmpty()) {
                                    didiDao.updateNeedsToPostBPCProcessStatus(
                                        needsToPostBPCProcessStatus = false,
                                        didiId = didi.id
                                    )
                                    callWorkFlowAPIForBpc(networkCallbackListener)
                                } else {
                                    for (i in needToPostPatDidi.indices) {
                                        updatedPatResponse.data?.get(i)?.let {
                                            it.transactionId?.let { it1 ->
                                                didiDao.updateDidiTransactionId(
                                                    needToPostPatDidi[i].id,
                                                    it1
                                                )
                                            }
                                        }
                                    }
                                    isPending = 2
                                    startSyncTimer(networkCallbackListener)
                                }
                            } else {
                                didiDao.updateNeedsToPostBPCProcessStatus(
                                    needsToPostBPCProcessStatus = false,
                                    didiId = didi.id
                                )
                                callWorkFlowAPIForBpc(networkCallbackListener)
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                networkCallbackListener.onFailed()
                            }
                        }
                        if(!updatedPatResponse.lastSyncTime.isNullOrEmpty()){
                            updateLastSyncTime(prefRepo,updatedPatResponse.lastSyncTime)
                        }
                    } catch (ex: Exception) {
                        settingViewModel.onCatchError(ex, ApiType.DIDI_EDIT_API)
                    }
                }
            } else {
                callWorkFlowAPIForBpc(networkCallbackListener)
            }
        }

    }

    // setp 2 transaction id
    @SuppressLint("SuspiciousIndentation")
    fun savePATSummeryToServer(networkCallbackListener: NetworkCallbackListener){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val villageList = villageListDao.getAllVillages(prefRepo.getAppLanguageId()?:2)
                for(village in villageList) {
                    withContext(Dispatchers.IO) {
                        delay(1000)
                        syncPercentage.value = 0.2f
                    }
                    var optionList: List<OptionsItem>
                    val answeredDidiList: ArrayList<PATSummarySaveRequest> = arrayListOf()
                    val scoreDidiList: ArrayList<EditDidiWealthRankingRequest> = arrayListOf()
                    var surveyId =0

                    val didiIDList= answerDao.fetchPATSurveyDidiList()
                    if(didiIDList.isNotEmpty()){
                        didiIDList.forEach { didi->
                            NudgeLogger.d("SyncBPCDataOnServer", "savePATSummeryToServer Save: ${didi.id} :: ${didi.patSurveyStatus}")
                            val qList: ArrayList<AnswerDetailDTOListItem> = arrayListOf()
                            calculateDidiScore(didiId = didi.id)
                            delay(100)
                            didi.score = didiDao.getDidiScoreFromDb(didi.id)
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
                            val patSummarySaveRequest = PATSummarySaveRequest(
                                villageId = didi.villageId,
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
                                shgFlag = didi.shgFlag,
                                patExclusionStatus = didi.patExclusionStatus ?: 0
                            )
                            NudgeLogger.d("SyncBPCDataOnServer", "savePATSummeryToServer patSummarySaveRequest: $patSummarySaveRequest")
                            answeredDidiList.add(
                                patSummarySaveRequest
                            )
                        }
                        if(answeredDidiList.isNotEmpty()){
                            withContext(Dispatchers.IO){
                                NudgeLogger.d("SyncBPCDataOnServer", "savePATSummeryToServer answeredDidiList Request: ${answeredDidiList.json()}")
                                val saveAPIResponse= apiService.savePATSurveyToServer(answeredDidiList)
                                if(saveAPIResponse.status.equals(SUCCESS,true)){
                                    if(saveAPIResponse.data?.get(0)?.transactionId.isNullOrEmpty()) {
                                        didiIDList.forEach { didiItem ->
                                            didiDao.updateNeedToPostPAT(
                                                false,
                                                didiItem.id,
                                                didiItem.villageId
                                            )
                                        }
                                        NudgeLogger.d("SyncBPCDataOnServer", "savePATSummeryToServer -> saveAPIResponse.data?.get(0)?.transactionId.isNullOrEmpty()")
                                        updateBpcPatStatusToNetwork(networkCallbackListener)
                                    } else {
                                        for(i in didiIDList.indices) {
                                            saveAPIResponse.data?.get(i)?.let {
                                                didiDao.updateDidiTransactionId(
                                                    didiIDList[i].id,
                                                    it.transactionId
                                                )
                                            }
                                        }
                                        NudgeLogger.d("SyncBPCDataOnServer", "savePATSummeryToServer -> !saveAPIResponse.data?.get(0)?.transactionId.isNullOrEmpty()")

                                        isPending = 1
                                        startSyncTimer(networkCallbackListener)
                                    }
    //                                        checkDidiPatStatus()
                                } else {
                                    withContext(Dispatchers.Main) {
                                        networkCallbackListener.onFailed()
                                    }
                                }
                                if(!saveAPIResponse.lastSyncTime.isNullOrEmpty()){
                                    updateLastSyncTime(prefRepo,saveAPIResponse.lastSyncTime)
                                }
                                NudgeLogger.d("SyncBPCDataOnServer", "savePATSummeryToServer updateDidiScore Request=>: ${scoreDidiList.json()}")
                                apiService.updateDidiScore(scoreDidiList)
                            }
                        }
                    }
                }
                checkPendingPatStatus(networkCallbackListener)
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    networkCallbackListener.onFailed()
                }
                ex.printStackTrace()
                settingViewModel.onCatchError(ex, ApiType.BPC_PAT_SAVE_ANSWER_SUMMARY)
            }
        }
    }

    private fun calculateDidiScore(didiId: Int) {
        NudgeLogger.d("SyncBPCDataOnServer", "calculateDidiScore didiId: ${didiId}")
        var passingMark = 0
        var isDidiAccepted = false
        var comment = LOW_SCORE
        val _inclusiveQueList = answerDao.getAllInclusiveQues(didiId = didiId)
        if (_inclusiveQueList.isNotEmpty()) {
            var totalWightWithoutNumQue = answerDao.getTotalWeightWithoutNumQues(didiId)
            NudgeLogger.d(
                "SyncBPCDataOnServer",
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
                                "SyncBPCDataOnServer",
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
                            "SyncBPCDataOnServer",
                            "calculateDidiScore: for Flag FLAG_RATIO totalWightWithoutNumQue += newScore -> $totalWightWithoutNumQue"
                        )
                    }
                }
            }
            // TotalScore


            /*if (totalWightWithoutNumQue >= passingMark) {
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
            }*/
            NudgeLogger.d("SyncBPCDataOnServer", "calculateDidiScore totalWightWithoutNumQue: $totalWightWithoutNumQue")
            didiDao.updateDidiScore(
                score = totalWightWithoutNumQue,
                comment = comment,
                didiId = didiId,
                isDidiAccepted = isDidiAccepted
            )

        } else {
            didiDao.updateDidiScore(
                score = 0.0,
                comment = TYPE_EXCLUSION,
                didiId = didiId,
                isDidiAccepted = false
            )

        }
    }

}
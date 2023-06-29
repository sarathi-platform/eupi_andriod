package com.patsurvey.nudge

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.MutableState
import com.patsurvey.nudge.activities.settings.SettingViewModel
import com.patsurvey.nudge.activities.settings.TransactionIdRequest
import com.patsurvey.nudge.data.prefs.PrefRepo
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
                          val bpcSelectedDidiDao : BpcSelectedDidiDao,
                          val didiDao: DidiDao,
                          val stepsListDao: StepsListDao,
                          val questionDao: QuestionListDao,
                          var syncPercentage : MutableState<Float>,
                          val answerDao: AnswerDao,
                          val numericAnswerDao: NumericAnswerDao){

    private var isPending = 0
    private val pendingTimerTime:Long = 10000

    fun syncBPCDataToServer(networkCallbackListener: NetworkCallbackListener){
        sendBpcUpdatedDidiList(networkCallbackListener)
    }

    // step 1
    fun sendBpcUpdatedDidiList(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.Main) {
                syncPercentage.value = 0f
            }
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
                    savePATSummeryToServer(networkCallbackListener)
                } else {
                    Log.d("SurveySummaryViewModel", "sendBpcUpdatedDidiList: $FAIL")
                    networkCallbackListener.onFailed()
                }
            } catch (ex: Exception) {
                settingViewModel.onCatchError(ex, ApiType.BPC_UPDATE_DIDI_LIST_API)
            }
        }
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
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiIDList= didiDao.fetchPendingPatStatusDidi(true,"")
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
                    updateBpcPatStatusToNetwork(networkCallbackListener)
                } else
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
            } else {
                updateBpcPatStatusToNetwork(networkCallbackListener)
            }
        }
    }

    private fun checkUpdateBpcPatStatus(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiIDList= didiDao.getAllPendingNeedToPostBPCProcessDidi(true,prefRepo.getSelectedVillage().id,"")
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
                    updateBpcPatStatusToNetwork(networkCallbackListener)
                } else
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
            } else {
                updateBpcPatStatusToNetwork(networkCallbackListener)
            }
        }
    }

    // step 4
    fun callWorkFlowAPIForBpc( networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                withContext(Dispatchers.Main) {
                    syncPercentage.value = 0.4f
                }
                val villageId = prefRepo.getSelectedVillage().id
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
                            sendBpcMatchScore(networkCallbackListener)
                        }else{
                            networkCallbackListener.onFailed()
//                            settingViewModel.onError("ex", ApiType.BPC_UPDATE_DIDI_LIST_API)
                        }
                    }
                }
            }catch (ex:Exception){
                networkCallbackListener.onFailed()
                settingViewModel.onCatchError(ex, ApiType.BPC_UPDATE_DIDI_LIST_API)
            }
        }
    }

    // step 5
    fun sendBpcMatchScore(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.Main) {
                syncPercentage.value = 0.6f
            }
            val didiList = didiDao.getAllDidisForVillage(prefRepo.getSelectedVillage().id)
            try {
                val villageId = prefRepo.getSelectedVillage().id
                val passingScore = questionDao.getPassingScore()
                val bpcStep = stepsListDao.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }.last()
                val matchedCount = didiList.filter {
                    (it.score ?: 0.0) >= passingScore.toDouble()
                            && (it.crpScore ?: 0.0) >= passingScore.toDouble() }.size

                val matchPercentage = ((matchedCount.toFloat()/didiList.size.toFloat()) * 100).toInt()
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
                    networkCallbackListener.onFailed()
                }
            } catch (ex: Exception){
                settingViewModel.onCatchError(ex, ApiType.BPC_UPDATE_DIDI_LIST_API)
            }
        }
    }

    // step 3 transaction id
    fun updateBpcPatStatusToNetwork(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.Main) {
                syncPercentage.value = 0.8f
            }
            val needToPostPatDidi =
                didiDao.getAllNeedToPostBPCProcessDidi(true, prefRepo.getSelectedVillage().id)
            val passingScore = questionDao.getPassingScore()
            if (!needToPostPatDidi.isNullOrEmpty()) {
                val didiRequestList : ArrayList<EditDidiWealthRankingRequest> = arrayListOf()
                needToPostPatDidi.forEach { didi ->
                    if (didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && didi.section2Status == PatSurveyStatus.COMPLETED.ordinal) {
                        didiRequestList.add(EditDidiWealthRankingRequest(
                            id = didi.serverId,
                            type = BPC_SURVEY_CONSTANT,
                            result = PatSurveyStatus.COMPLETED.name,
                            score = didi.score,
                            comment = if ((didi.score
                                    ?: 0.0) < passingScore
                            ) LOW_SCORE else "",
                            localModifiedDate = System.currentTimeMillis()
                        ))
                    } else if (didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && didi.section2Status == PatSurveyStatus.NOT_STARTED.ordinal) {
                        didiRequestList.add(
                                EditDidiWealthRankingRequest(
                                    id = didi.serverId,
                                    type = BPC_SURVEY_CONSTANT,
                                    result = PatSurveyStatus.NOT_AVAILABLE.name,
                                    score = 0.0,
                                    comment = TYPE_EXCLUSION,
                                    localModifiedDate = System.currentTimeMillis()
                                )
                            )
                    }
                    try {
                        val updatedPatResponse = apiService.updateDidiRanking(didiRequestList)
                        if (updatedPatResponse.status.equals(SUCCESS, true)) {
                            if(updatedPatResponse.data?.get(0)?.transactionId.isNullOrEmpty()) {
                                didiDao.updateNeedsToPostBPCProcessStatus(
                                    needsToPostBPCProcessStatus = false,
                                    didiId = didi.id
                                )
                                callWorkFlowAPIForBpc(networkCallbackListener)
                            } else {
                                for(i in needToPostPatDidi.indices) {
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
                            networkCallbackListener.onFailed()
                        }
                    } catch (ex: Exception) {
                        settingViewModel.onCatchError(ex, ApiType.BPC_UPDATE_DIDI_LIST_API)
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
                withContext(Dispatchers.IO) {
                    syncPercentage.value = 0.2f
                }
                var optionList= emptyList<OptionsItem>()
                val answeredDidiList: ArrayList<PATSummarySaveRequest> = arrayListOf()
                val scoreDidiList: ArrayList<EditDidiWealthRankingRequest> = arrayListOf()
                var surveyId =0

                val didiIDList= answerDao.fetchPATSurveyDidiList(prefRepo.getSelectedVillage().id)
                if(didiIDList.isNotEmpty()){
                    didiIDList.forEach { didi->
                        Log.d(TAG, "savePATSummeryToServer Save: ${didi.id} :: ${didi.patSurveyStatus}")
                        val qList: ArrayList<AnswerDetailDTOListItem> = arrayListOf()
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
                                    isPending = 1
                                    startSyncTimer(networkCallbackListener)
                                }
//                                        checkDidiPatStatus()
                            } else {
                                networkCallbackListener.onFailed()
                            }
                            apiService.updateDidiScore(scoreDidiList)
                        }
                    }
                } else {
                    checkPendingPatStatus(networkCallbackListener)
                }
            } catch (ex: Exception) {
                networkCallbackListener.onFailed()
                ex.printStackTrace()
                settingViewModel.onCatchError(ex, ApiType.BPC_UPDATE_DIDI_LIST_API)
            }
        }
    }
    
}
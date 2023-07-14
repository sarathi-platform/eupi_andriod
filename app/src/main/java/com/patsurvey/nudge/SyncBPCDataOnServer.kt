package com.patsurvey.nudge

import android.annotation.SuppressLint
import androidx.compose.runtime.MutableState
import com.patsurvey.nudge.activities.settings.SettingViewModel
import com.patsurvey.nudge.activities.settings.TransactionIdRequest
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.dao.*
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.request.*
import com.patsurvey.nudge.model.response.OptionsItem
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.*
import kotlinx.coroutines.*
import java.util.*

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
                delay(1000)
                syncPercentage.value = 0f
            }
            if(isBPCDidiNeedToBeReplaced()) {
                val villagId = prefRepo.getSelectedVillage().id
                val oldDidiList = bpcSelectedDidiDao.fetchAllDidisForVillage(villageId = villagId)
                val updatedList = didiDao.getAllDidisForVillage(villageId = villagId)
                try {
                    val oldBeneficiaryIdSelected = mutableListOf<Int>()
                    oldDidiList.forEach {
                        oldBeneficiaryIdSelected.add(it.serverId)
                    }
                    NudgeLogger.d("SyncBPCDataOnServer", "sendBpcUpdatedDidiList: newBeneficiaryIdSelected -> $oldBeneficiaryIdSelected")
                    val newBeneficiaryIdSelected = mutableListOf<Int>()
                    updatedList.forEach {
                        newBeneficiaryIdSelected.add(it.serverId)
                    }
                    NudgeLogger.d("SyncBPCDataOnServer", "sendBpcUpdatedDidiList: newBeneficiaryIdSelected -> $newBeneficiaryIdSelected")
                    val updateSelectedDidiResponse = apiService.sendSelectedDidiList(
                        BpcUpdateSelectedDidiRequest(
                            oldBeneficiaryIdSelected = oldBeneficiaryIdSelected,
                            newBeneficiaryIdSelected = newBeneficiaryIdSelected,
                            villageId = villagId
                        )
                    )
                    if (updateSelectedDidiResponse.status.equals(SUCCESS, true)) {
                        NudgeLogger.d("SyncBPCDataOnServer", "sendBpcUpdatedDidiList: $SUCCESS")
                        prefRepo.savePref(PREF_BPC_DIDI_LIST_SYNCED_FOR_VILLAGE_ + villagId, true)
                        savePATSummeryToServer(networkCallbackListener)
                    } else {
                        NudgeLogger.d("SyncBPCDataOnServer", "sendBpcUpdatedDidiList: $FAIL")
                        prefRepo.savePref(PREF_BPC_DIDI_LIST_SYNCED_FOR_VILLAGE_ + villagId, false)
                        withContext(Dispatchers.Main) {
                            networkCallbackListener.onFailed()
                        }
                    }

                    if(!updateSelectedDidiResponse.lastSyncTime.isNullOrEmpty()){
                        updateLastSyncTime(prefRepo,updateSelectedDidiResponse.lastSyncTime)
                    }
                } catch (ex: Exception) {
                    settingViewModel.onCatchError(ex, ApiType.BPC_UPDATE_DIDI_LIST_API)
                }
            } else {
                prefRepo.savePref(PREF_BPC_DIDI_LIST_SYNCED_FOR_VILLAGE_ + prefRepo.getSelectedVillage().id, true)
                savePATSummeryToServer(networkCallbackListener)
            }
        }
    }

    fun isBPCDidiNeedToBeReplaced() : Boolean{
        val villageId = prefRepo.getSelectedVillage().id
        val oldDidiList = bpcSelectedDidiDao.fetchAllDidisForVillage(villageId)
        val updatedList = didiDao.getAllDidisForVillage(villageId)
        val oldBeneficiaryIdSelected = mutableListOf<Int>()
        oldDidiList.forEach {
            oldBeneficiaryIdSelected.add(it.serverId)
        }
        val newBeneficiaryIdSelected = mutableListOf<Int>()
        updatedList.forEach {
            newBeneficiaryIdSelected.add(it.serverId)
        }
        for(id in newBeneficiaryIdSelected){
            if(oldBeneficiaryIdSelected.indexOf(id) == -1){
                return true
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
                } else {
                    withContext(Dispatchers.Main) {
                        networkCallbackListener.onFailed()
                    }
                }
                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
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
                    delay(1000)
                    syncPercentage.value = 0.4f
                }
                val villageId = prefRepo.getSelectedVillage().id
                val stepList = stepsListDao.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }
                val bpcStep = stepList.last()
                if(bpcStep.workFlowId>0){

                    withContext(Dispatchers.IO){
                        val response = apiService.editWorkFlow(
                        listOf(
                            EditWorkFlowRequest(bpcStep.workFlowId, StepStatus.getStepFromOrdinal(bpcStep.isComplete))
                        ) )
                        if (response.status.equals(SUCCESS, true)) {
                            response.data?.let {
                                stepsListDao.updateWorkflowId(bpcStep.id, bpcStep.workFlowId,villageId,it[0].status)
                            }
                            stepsListDao.updateNeedToPost(bpcStep.id, villageId,false)
                            sendBpcMatchScore(networkCallbackListener)
                        }else{
                            withContext(Dispatchers.Main) {
                                networkCallbackListener.onFailed()
                            }
//                            settingViewModel.onError("ex", ApiType.BPC_UPDATE_DIDI_LIST_API)
                        }

                        if(!response.lastSyncTime.isNullOrEmpty()){
                            updateLastSyncTime(prefRepo,response.lastSyncTime)
                        }
                    }
                }
            }catch (ex:Exception){
                withContext(Dispatchers.Main) {
                    networkCallbackListener.onFailed()
                }
                settingViewModel.onCatchError(ex, ApiType.WORK_FLOW_API)
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
                    val villageId = prefRepo.getSelectedVillage().id
                    val passingScore = questionDao.getPassingScore()
                    val bpcStep =
                        stepsListDao.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }
                            .last()
                    val matchPercentage = calculateMatchPercentage(didiList.filter { it.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal }, passingScore)
                    val saveMatchSummaryRequest = SaveMatchSummaryRequest(
                        programId = bpcStep.programId,
                        score = matchPercentage,
                        villageId = villageId
                    )
                    val requestList = arrayListOf(saveMatchSummaryRequest)
                    val saveMatchSummaryResponse = apiService.saveMatchSummary(requestList)
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
                didiDao.getAllNeedToPostBPCProcessDidi(true, prefRepo.getSelectedVillage().id)
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
                                if (didi.forVoEndorsement == 0) DIDI_REJECTED else COMPLETED_STRING
                            }
                        )
                    )
                    try {
                        val updatedPatResponse = apiService.updateDidiRanking(didiRequestList)
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
                withContext(Dispatchers.IO) {
                    delay(1000)
                    syncPercentage.value = 0.2f
                }
                var optionList= emptyList<OptionsItem>()
                val answeredDidiList: ArrayList<PATSummarySaveRequest> = arrayListOf()
                val scoreDidiList: ArrayList<EditDidiWealthRankingRequest> = arrayListOf()
                var surveyId =0

                val didiIDList= answerDao.fetchPATSurveyDidiList(prefRepo.getSelectedVillage().id)
                if(didiIDList.isNotEmpty()){
                    didiIDList.forEach { didi->
                        NudgeLogger.d("SyncBPCDataOnServer", "savePATSummeryToServer Save: ${didi.id} :: ${didi.patSurveyStatus}")
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
                        val passingMark=questionDao.getPassingScore()
                        scoreDidiList.add(
                            EditDidiWealthRankingRequest(
                                id = if (didi.serverId == 0) didi.id else didi.serverId,
                                score = didi.score,
                                comment = if(didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal ||  didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal)
                                    BLANK_STRING
                                else {
                                    if(didi.patSurveyStatus==PatSurveyStatus.COMPLETED.ordinal && didi.section2Status==PatSurveyStatus.NOT_STARTED.ordinal){
                                        TYPE_EXCLUSION
                                    } else {
                                        if (didi.score < passingMark)
                                            LOW_SCORE
                                        else {
                                            BLANK_STRING
                                        }
                                    }
                                },
                                type = if (prefRepo.isUserBPC()) BPC_SURVEY_CONSTANT else PAT_SURVEY,
                                result = if (didi.forVoEndorsement == 0) DIDI_REJECTED else COMPLETED_STRING
                            )
                        )
                        val patSummarySaveRequest = PATSummarySaveRequest(
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
                            shgFlag = didi.shgFlag
                        )
                        NudgeLogger.d("SyncBPCDataOnServer", "savePATSummeryToServer patSummarySaveRequest: $patSummarySaveRequest")
                        answeredDidiList.add(
                            patSummarySaveRequest
                        )
                    }
                    if(answeredDidiList.isNotEmpty()){
                        withContext(Dispatchers.IO){
                            NudgeLogger.d("SyncBPCDataOnServer", "savePATSummeryToServer answeredDidiList: $answeredDidiList")
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
                            NudgeLogger.d("SyncBPCDataOnServer", "savePATSummeryToServer scoreDidiList: $scoreDidiList")
                            apiService.updateDidiScore(scoreDidiList)
                        }
                    }
                } else {
                    checkPendingPatStatus(networkCallbackListener)
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    networkCallbackListener.onFailed()
                }
                ex.printStackTrace()
                settingViewModel.onCatchError(ex, ApiType.BPC_PAT_SAVE_ANSWER_SUMMARY)
            }
        }
    }
    
}
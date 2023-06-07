package com.patsurvey.nudge

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import androidx.compose.runtime.MutableState
import com.google.gson.JsonArray
import com.patsurvey.nudge.activities.settings.TransactionIdRequest
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.dao.*
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.request.*
import com.patsurvey.nudge.model.response.OptionsItem
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.*
import kotlinx.coroutines.*
import java.util.Timer
import java.util.TimerTask

class SyncHelper (
    val prefRepo: PrefRepo,
    val apiService: ApiService,
    val tolaDao: TolaDao,
    val stepsListDao: StepsListDao,
    val exceptionHandler : CoroutineExceptionHandler,
    val villegeListDao: VillageListDao,
    val didiDao: DidiDao,
    var job: Job?,
    val showLoader : MutableState<Boolean>,
    var syncPercentage : MutableState<Float>,
    val answerDao: AnswerDao,
    val numericAnswerDao: NumericAnswerDao,
    val questionDao: QuestionListDao
){

    private val pendingTimerTime : Long= 10000
    private var isPending = 0
    fun syncDataToServer(networkCallbackListener: NetworkCallbackListener){
        showProgressBar(networkCallbackListener)
        Log.e("progress","started")
        addTolasToNetwork(networkCallbackListener)
//        addDidisToNetwork(networkCallbackListener)
//        updateWealthRankingToNetwork(networkCallbackListener)
//        checkTolaStatus()
    }

    private fun showProgressBar(networkCallbackListener: NetworkCallbackListener){
        val totalTimer : Long = 3000
        val interval : Long = 1000
        object: CountDownTimer(totalTimer, interval){
            override fun onTick(p0: Long) {
                val progress = ((((totalTimer-p0)*100)/totalTimer))
                syncPercentage.value = progress.toFloat()
                Log.e("progress","->$progress")
                Log.e("po","->$p0")
            }
            override fun onFinish() {
                networkCallbackListener.onSuccess()
                syncPercentage.value = 0f
            }
        }.start()
    }

    private fun startSyncTimer(networkCallbackListener: NetworkCallbackListener){
        val timer = Timer()
        timer.schedule(object : TimerTask(){
            override fun run() {
                when (isPending) {
                    1 -> {
                        checkTolaStatus(networkCallbackListener)
                    }
                    2 -> {
                        checkDidiStatus(networkCallbackListener)
                    }
                    3 -> {
                        checkDidiWealthStatus(networkCallbackListener)
                    }
                }
            }
        },pendingTimerTime)
    }

    private fun checkDidiWealthStatus(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.fetchPendingWealthStatusDidi(true,"")
            if(didiList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                didiList.forEach { didi ->
                    didi.transactionId?.let { ids.add(it) }
                }
                val response = apiService.getPendingStatus(TransactionIdRequest(ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        didiList.forEach { didi ->
                            if (transactionIdResponse.transactionId == didi.transactionId) {
                                didiDao.updateDidiNeedToPostWealthRank(didi.id,false)
                            }
                        }
                    }
                    savePATSummeryToServer(networkCallbackListener)
                } else
                    networkCallbackListener.onFailed()
            } else {
//                updateWealthRankingToNetwork(networkCallbackListener)
                savePATSummeryToServer(networkCallbackListener)
            }
        }
    }

    private fun checkDidiStatus(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.fetchPendingDidi(true,"")
            if(didiList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                didiList.forEach { tola ->
                    tola.transactionId?.let { ids.add(it) }
                }
                val response = apiService.getPendingStatus(TransactionIdRequest(ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        didiList.forEach { didi ->
                            if (transactionIdResponse.transactionId == didi.transactionId) {
                                didi.id = transactionIdResponse.referenceId
                            }
                        }
                    }
                    updateDidisNeedTOPostList(didiList,networkCallbackListener)
                } else
                    networkCallbackListener.onFailed()
            } else {
                updateWealthRankingToNetwork(networkCallbackListener)
            }
        }
    }

    private fun checkTolaStatus(networkCallbackListener :NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaList = tolaDao.fetchPendingTola(true,"")
            if(tolaList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                tolaList.forEach { tola ->
                    tola.transactionId?.let { ids.add(it) }
                }
                val response = apiService.getPendingStatus(TransactionIdRequest(ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        tolaList.forEach { tola ->
                            if (transactionIdResponse.transactionId == tola.transactionId) {
                                tola.id = transactionIdResponse.referenceId
                            }
                        }
                    }
                    updateTolaNeedTOPostList(tolaList,networkCallbackListener)
                } else
                    networkCallbackListener.onFailed()
            } else {
                addDidisToNetwork(networkCallbackListener)
            }
        }
    }

    fun addTolasToNetwork(networkCallbackListener: NetworkCallbackListener) {
        Log.e("add tola","called")
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaList = tolaDao.fetchTolaNeedToPost(true,"")
            val jsonTola = JsonArray()
            if (tolaList.isNotEmpty()) {
                for (tola in tolaList) {
                    jsonTola.add(AddCohortRequest.getRequestObjectForTola(tola).toJson())
                }
                Log.e("tola need to post","$tolaList.size")
                val response = apiService.addCohort(jsonTola)
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.let {
                        if((response.data[0].transactionId.isNullOrEmpty())) {
                            response.data.forEach { tolaDataFromNetwork ->
                                tolaList.forEach { tola ->
                                    if (TextUtils.equals(tolaDataFromNetwork.name, tola.name)) {
                                        tola.id = tolaDataFromNetwork.id
                                        tola.createdDate = tolaDataFromNetwork.createdDate
                                        tola.modifiedDate = tolaDataFromNetwork.modifiedDate
                                    }
                                    Log.e("tola after update", "$tolaList.size")
                                }
                                updateTolaNeedTOPostList(tolaList,networkCallbackListener)
                            }
                        } else {
                            for (i in 0..response.data.size){
                                tolaList[i].transactionId = response.data[i].transactionId
                                updateLocalTransactionIdToLocalTola(tolaList,networkCallbackListener)
                            }
                        }
                    }
//                    addDidisToNetwork(networkCallbackListener)
                }
                else {
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
                }
            } else {
                checkTolaStatus(networkCallbackListener)
            }
        }
    }

    private fun updateLocalTransactionIdToLocalTola(tolaList: List<TolaEntity>, networkCallbackListener: NetworkCallbackListener) {
        tolaList.forEach{tola->
            tola.transactionId?.let { tolaDao.updateTolaTransactionId(tola.id, it) }
        }
        isPending = 1
        startSyncTimer(networkCallbackListener)
    }

    private fun updateLocalTransactionIdToLocalDidi(didiList: List<DidiEntity>, networkCallbackListener: NetworkCallbackListener) {
        didiList.forEach{ didi->
            didi.transactionId?.let { didiDao.updateDidiTransactionId(didi.id, it) }
        }
        isPending = 2
        startSyncTimer(networkCallbackListener)
    }

    fun updateTolaNeedTOPostList(tolaList: List<TolaEntity>,networkCallbackListener: NetworkCallbackListener){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            updateTolaListWithIds(tolaList,networkCallbackListener)
        }
    }

    private fun updateTolaListWithIds(tolaList: List<TolaEntity>,networkCallbackListener: NetworkCallbackListener) {
        tolaDao.deleteTolaNeedToPost(true)
        Log.e("tola updated","$tolaList.size")
        val tolas = mutableListOf<TolaEntity>()
        tolaList.forEach {
            tolas.add(
                TolaEntity(
                    id = it.id,
                    name = it.name,
                    type = it.type,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    villageId = it.villageId,
                    needsToPost = false,
                    status = it.status,
                    createdDate = it.createdDate,
                    modifiedDate = it.modifiedDate,
                    transactionId = ""
                )
            )
        }
        tolaList.forEach{
            Log.e("tola need to post","${it.id}")
            Log.e("tola need to post","${it.needsToPost}")
        }
        tolaDao.insertAll(tolas)
        addDidisToNetwork(networkCallbackListener)
    }

    fun addDidisToNetwork(networkCallbackListener: NetworkCallbackListener) {
        Log.e("add didi","called")
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.fetchAllDidiNeedToPost(true,null)
            val jsonDidi = JsonArray()
            if (didiList.isNotEmpty()) {
                for (didi in didiList) {
                    jsonDidi.add(AddDidiRequest.getRequestObjectForDidi(didi).toJson())
                }
                val response = apiService.addDidis(jsonDidi)
                if (response.status.equals(SUCCESS, true)) {
                    if((response.data?.get(0)?.transactionId.isNullOrEmpty())) {
                        response.data?.let {
//                        networkCallbackListener.onSuccess()
                            response.data.forEach { didiFromNetwork ->
                                didiList.forEach { didi ->
                                    if (TextUtils.equals(didiFromNetwork.name, didi.name)) {
                                        didi.id = didiFromNetwork.id
                                        didi.createdDate = didiFromNetwork.createdDate
                                        didi.modifiedDate = didiFromNetwork.modifiedDate
                                    }
                                }
                            }
                        }
                        updateDidisNeedTOPostList(didiList,networkCallbackListener)
                    } else {
                        for (i in 0..(response.data?.size ?: 0)){
                            didiList[i].transactionId = response.data?.get(i)?.transactionId
                            updateLocalTransactionIdToLocalDidi(didiList,networkCallbackListener)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
                }
            } else {
                checkDidiStatus(networkCallbackListener)
            }
        }
    }

    fun updateDidisNeedTOPostList(didiList : List<DidiEntity>,networkCallbackListener: NetworkCallbackListener){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            updateDidiListWithServerIds(didiList,networkCallbackListener)
        }
    }

    private fun updateDidiListWithServerIds(oldDidiList: List<DidiEntity>,networkCallbackListener: NetworkCallbackListener) {
        didiDao.deleteDidiNeedToPost(true)
        oldDidiList.forEach(){ didiEntity ->
            didiEntity.needsToPost = false
        }
        didiDao.insertAll(oldDidiList)
        updateWealthRankingToNetwork(networkCallbackListener)
    }

    fun updateWealthRankingToNetwork(networkCallbackListener: NetworkCallbackListener){
        Log.e("add didi","called")
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                withContext(Dispatchers.IO){
                    val needToPostDidiList=didiDao.getAllNeedToPostDidiRanking(true)
                    if(needToPostDidiList.isNotEmpty()){
                        val didiRequestList = arrayListOf<EditDidiWealthRankingRequest>()
                        needToPostDidiList.forEach { didi->
                            launch {
                                didiRequestList.add(EditDidiWealthRankingRequest(didi.id, StepType.WEALTH_RANKING.name,didi.wealth_ranking))
//                                didiRequestList.add(EditDidiWealthRankingRequest(didi.id, StepType.SOCIAL_MAPPING.name, StepStatus.COMPLETED.name))
                            }
                        }
                        val updateWealthRankResponse=apiService.updateDidiRanking(
                            didiRequestList
                        )
                        if(updateWealthRankResponse.status.equals(SUCCESS,true)){
                            val didiListResponse = updateWealthRankResponse.data
                            if(!didiListResponse?.get(0)?.transactionId.isNullOrEmpty()){
                                for(i in needToPostDidiList.indices){
                                    needToPostDidiList[i].transactionId = didiListResponse?.get(i)?.transactionId
                                    needToPostDidiList[i].transactionId?.let {
                                        didiDao.updateDidiTransactionId(
                                            needToPostDidiList[i].id,
                                            it
                                        )
                                    }
                                }
                                isPending = 3
                                startSyncTimer(networkCallbackListener)
                            } else {
                                needToPostDidiList.forEach { didi ->
                                    didiDao.updateDidiNeedToPostWealthRank(didi.id,false)
                                }
                                savePATSummeryToServer(networkCallbackListener)
                            }
                        }
                    } else {
                        checkDidiWealthStatus(networkCallbackListener)
                    }

                }
            } catch (ex: Exception) {
                networkCallbackListener.onFailed()
//                onError("WealthRankingSurveyViewModel", "onError: ${ex.message}, \n${ex.stackTrace}")
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun savePATSummeryToServer(networkCallbackListener: NetworkCallbackListener){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                withContext(Dispatchers.IO){
                    var optionList= emptyList<OptionsItem>()
                    val answeredDidiList: java.util.ArrayList<PATSummarySaveRequest> = arrayListOf()
                    var surveyId =0

                    val didiIDList= answerDao.fetchPATSurveyDidiList(prefRepo.getSelectedVillage().id)
                    if(didiIDList.isNotEmpty()){
                        didiIDList.forEach { didi->
                            Log.d(TAG, "savePATSummeryToServer Save: ${didi.id} :: ${didi.patSurveyStatus}")
                            val qList: java.util.ArrayList<AnswerDetailDTOListItem> = arrayListOf()
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
                                                isSelected = false)
                                        )
                                    }else{
                                        val numOptionList=numericAnswerDao.getSingleQueOptions(it.questionId,it.didiId)
                                        val tList: java.util.ArrayList<OptionsItem> = arrayListOf()
                                        if(numOptionList.isNotEmpty()){
                                            numOptionList.forEach { numOption->
                                                tList.add(
                                                    OptionsItem(optionId = numOption.optionId,
                                                        optionValue = 0,
                                                        count = numOption.count,
                                                        summary = it.summary,
                                                        display = it.answerValue,
                                                        weight = numOption.weight,
                                                        isSelected = false)
                                                )
                                            }
                                            optionList=tList
                                        }

                                    }
                                    try {
                                        qList.add(
                                            AnswerDetailDTOListItem(
                                                questionId =it.questionId,
                                                section = it.actionType,
                                                options = optionList)
                                        )
                                    }catch (e:Exception){
                                        e.printStackTrace()
                                    }
                                }
                            }
                            answeredDidiList.add(
                                PATSummarySaveRequest(
                                    villageId= prefRepo.getSelectedVillage().id,
                                    surveyId=surveyId,
                                    beneficiaryId = didi.id,
                                    languageId = prefRepo.getAppLanguageId()?:0,
                                    stateId = prefRepo.getSelectedVillage().stateId,
                                    totalScore = 0,
                                    userType = USER_CRP,
                                    beneficiaryName= didi.name,
                                    answerDetailDTOList= qList,
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
//                                    networkCallbackListener.onSuccess()
                                        updateVoStatusToNetwork(networkCallbackListener)
                                    } else {
                                        /*for (didi in didiIDList){
                                            didi
                                        }*/
                                    }
                                } else {
                                    networkCallbackListener.onFailed()
                                }
                            }

                        }

                    }
                }

            }  catch (ex:Exception){
                networkCallbackListener.onFailed()
                ex.printStackTrace()
            }
        }
    }

    fun updateVoStatusToNetwork(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                withContext(Dispatchers.IO){
                    val needToPostDidiList=didiDao.getAllNeedToPostPATDidi(needsToPostPAT = true, villageId = prefRepo.getSelectedVillage().id)
                    if(needToPostDidiList.isNotEmpty()){
                        needToPostDidiList.forEach { didi->
                            launch {
                                didi.voEndorsementStatus.let {
                                    if (it == DidiEndorsementStatus.ENDORSED.ordinal) {
                                        val updateWealthRankResponse=apiService.updateDidiRanking(
                                            listOf(
                                                EditDidiWealthRankingRequest(didi.id,StepType.VO_ENDORSEMENT.name, ACCEPTED),
                                            )
                                        )
                                        if(updateWealthRankResponse.status.equals(SUCCESS,true)){
                                            didiDao.updateNeedToPostVO(false, didi.id, didi.villageId)
                                        } else {
                                            networkCallbackListener.onFailed()
                                        }
                                    } else if (it == DidiEndorsementStatus.REJECTED.ordinal) {
                                        val updateWealthRankResponse=apiService.updateDidiRanking(
                                            listOf(
                                                EditDidiWealthRankingRequest(didi.id,StepType.VO_ENDORSEMENT.name, DidiEndorsementStatus.REJECTED.name),
                                            )
                                        )
                                        if(updateWealthRankResponse.status.equals(SUCCESS,true)){
                                            didiDao.updateNeedToPostVO(false, didi.id, didi.villageId)
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
//                onCatchError(ex)
                networkCallbackListener.onFailed()
//                onError("SurveySummaryViewModel", "updateVoStatusToNetwork-> onError: ${ex.message}, \n${ex.stackTrace}")
            }
        }
    }

    fun markTransectWalkComplete(villageId: Int, stepId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val existingList = villegeListDao.getVillage(villageId).steps_completed
            val updatedCompletedStepsList = mutableListOf<Int>()
            if (!existingList.isNullOrEmpty()) {
                existingList.forEach {
                    updatedCompletedStepsList.add(it)
                }
            }
            updatedCompletedStepsList.add(stepId)
            villegeListDao.updateLastCompleteStep(villageId, updatedCompletedStepsList)
            stepsListDao.markStepAsCompleteOrInProgress(stepId, StepStatus.COMPLETED.ordinal,villageId)
            val stepDetails=stepsListDao.getStepForVillage(villageId, stepId)
            if(stepDetails.orderNumber<stepsListDao.getAllSteps().size){
                stepsListDao.markStepAsInProgress((stepDetails.orderNumber+1),
                    StepStatus.INPROGRESS.ordinal,villageId)
            }
        }
    }
}
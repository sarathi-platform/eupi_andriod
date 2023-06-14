package com.patsurvey.nudge

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import androidx.compose.runtime.MutableState
import com.google.gson.JsonArray
import com.patsurvey.nudge.activities.settings.SettingViewModel
import com.patsurvey.nudge.activities.settings.TransactionIdRequest
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.dao.*
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.dataModel.PATDidiStatusModel
import com.patsurvey.nudge.model.request.*
import com.patsurvey.nudge.model.response.OptionsItem
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.*
import kotlinx.coroutines.*
import java.util.Timer
import java.util.TimerTask

class SyncHelper (
    val settingViewModel: SettingViewModel,
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
        Log.e("progress","started")
        addTolasToNetwork(networkCallbackListener)
    }

/*    private fun showProgressBar(networkCallbackListener: NetworkCallbackListener){
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
    }*/

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
                    4 -> {
                        checkDidiPatStatus(networkCallbackListener)
                    }
                    5 -> {
                        checkVOStatus(networkCallbackListener)
                    }
                }
            }
        },pendingTimerTime)
    }

    private fun checkVOStatus(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.fetchPendingVOStatusStatusDidi(true,"")
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
                                didiDao.updateNeedToPostVO(false,didi.id)
                                didiDao.updateDidiTransactionId(didi.id,"")
                            }
                        }
                    }
                    syncPercentage.value = 100f
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onSuccess()
                    }
                } else {
                    syncPercentage.value = 100f
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
                }
            } else {
                syncPercentage.value = 100f
                withContext(Dispatchers.Main){
                    networkCallbackListener.onSuccess()
                }
            }
        }
    }

    private fun checkDidiPatStatus(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.fetchPendingPatStatusDidi(true,"")
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
                                didiDao.updateDidiNeedToPostPat(didi.id,false)
                                didiDao.updateDidiTransactionId(didi.id,"")
                            }
                        }
                    }
                    updateVoStatusToNetwork(networkCallbackListener)
                } else
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
            } else {
                updateVoStatusToNetwork(networkCallbackListener)
            }
        }
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
                                didiDao.updateDidiTransactionId(didi.id,"")
                            }
                        }
                    }
                    savePATSummeryToServer(networkCallbackListener)
                } else
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
            } else {
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
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
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
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
            } else {
                addDidisToNetwork(networkCallbackListener)
            }
        }
    }

    fun addTolasToNetwork(networkCallbackListener: NetworkCallbackListener) {
        settingViewModel.stepOneSyncStatus.value = 1
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
                            syncPercentage.value = 20f
                        } else {
                            for (i in 0..response.data.size){
                                tolaList[i].transactionId = response.data[i].transactionId
                                updateLocalTransactionIdToLocalTola(tolaList,networkCallbackListener)
                            }
                            syncPercentage.value = 10f
                        }
                    }
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
        callWorkFlowAPIForStep(1)
        settingViewModel.stepOneSyncStatus.value = 3
        settingViewModel.stepTwoSyncStatus.value = 1
        Log.e("add didi","called")
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.fetchAllDidiNeedToPost(true,"")
            val jsonDidi = JsonArray()
            if (didiList.isNotEmpty()) {
                for (didi in didiList) {
                    jsonDidi.add(AddDidiRequest.getRequestObjectForDidi(didi).toJson())
                }
                val response = apiService.addDidis(jsonDidi)
                if (response.status.equals(SUCCESS, true)) {
                    if(response.data?.get(0)?.transactionId.isNullOrEmpty()) {
                        response.data?.let {
//                        networkCallbackListener.onSuccess()
                            response.data.forEach { didiFromNetwork ->
                                didiList.forEach { didi ->
                                    if (TextUtils.equals(didiFromNetwork.name, didi.name)) {
                                        didi.serverId = didiFromNetwork.id
                                        didi.createdDate = didiFromNetwork.createdDate
                                        didi.modifiedDate = didiFromNetwork.modifiedDate
                                    }
                                }
                            }
                        }
                        updateDidisNeedTOPostList(didiList,networkCallbackListener)
                        syncPercentage.value = 40f
                    } else {
                        for (i in 0..(response.data?.size ?: 0)){
                            didiList[i].transactionId = response.data?.get(i)?.transactionId
                        }
                        updateLocalTransactionIdToLocalDidi(didiList,networkCallbackListener)
                        syncPercentage.value = 30f
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

    private fun callWorkFlowAPIForStep(step: Int) {
        Log.e("workflow api"," called")
        val villageId = prefRepo.getSelectedVillage().id
        val stepList = stepsListDao.getAllStepsForVillage(villageId)
        Log.e("workflow api called","$villageId -> $stepList -> $step")
        when(step){
            1->{
                if(stepList[0].isComplete ==  StepStatus.COMPLETED.ordinal){
                    callWorkFlowAPI(villageId,stepList[0].id)
                }
            }
            2->{
                if(stepList[1].isComplete ==  StepStatus.COMPLETED.ordinal){
                    callWorkFlowAPI(villageId,stepList[1].id)
                }
            }
            3->{
                if(stepList[2].isComplete ==  StepStatus.COMPLETED.ordinal){
                    callWorkFlowAPI(villageId,stepList[2].id)
                }
            }
            4->{
                if(stepList[3].isComplete ==  StepStatus.COMPLETED.ordinal){
                    callWorkFlowAPI(villageId,stepList[3].id)
                }
            }
            5->{
                if(stepList[4].isComplete ==  StepStatus.COMPLETED.ordinal){
                    callWorkFlowAPI(villageId,stepList[4].id)
                }
            }
        }
    }

    fun updateDidisNeedTOPostList(didiList : List<DidiEntity>,networkCallbackListener: NetworkCallbackListener){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            updateDidiListWithServerIds(didiList,networkCallbackListener)
        }
    }

    private fun updateDidiListWithServerIds(oldDidiList: List<DidiEntity>,networkCallbackListener: NetworkCallbackListener) {
        oldDidiList.forEach(){ didiEntity ->
            didiEntity.needsToPost = false
            didiEntity.transactionId = ""
            didiDao.updateDidi(didiEntity)
        }
        updateWealthRankingToNetwork(networkCallbackListener)
    }

    fun updateWealthRankingToNetwork(networkCallbackListener: NetworkCallbackListener){
        Log.e("add didi","called")
        callWorkFlowAPIForStep(2)
        settingViewModel.stepTwoSyncStatus.value = 3
        settingViewModel.stepThreeSyncStatus.value = 1
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                withContext(Dispatchers.IO){
                    val needToPostDidiList=didiDao.getAllNeedToPostDidiRanking(true)
                    if(needToPostDidiList.isNotEmpty()){
                        val didiRequestList = arrayListOf<EditDidiWealthRankingRequest>()
                        needToPostDidiList.forEach { didi->
                            launch {
                                didiRequestList.add(EditDidiWealthRankingRequest(didi.serverId, StepType.WEALTH_RANKING.name,didi.wealth_ranking))
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
                                syncPercentage.value = 50f
                            } else {
                                needToPostDidiList.forEach { didi ->
                                    didiDao.updateDidiNeedToPostWealthRank(didi.id,false)
                                }
                                syncPercentage.value = 60f
                                savePATSummeryToServer(networkCallbackListener)
                            }
                        } else
                            withContext(Dispatchers.Main){
                                networkCallbackListener.onFailed()
                            }
                    } else {
                        checkDidiWealthStatus(networkCallbackListener)
                    }

                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main){
                    networkCallbackListener.onFailed()
                }
            }
        }
    }

    fun fetchAnswerDidiList(didiIDList : List<PATDidiStatusModel>) : ArrayList<PATSummarySaveRequest>{
        var optionList= emptyList<OptionsItem>()
        val answeredDidiList: java.util.ArrayList<PATSummarySaveRequest> = arrayListOf()
        var surveyId =0
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
                    beneficiaryId = didi.serverId,
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
        return answeredDidiList
    }

    @SuppressLint("SuspiciousIndentation")
    fun savePATSummeryToServer(networkCallbackListener: NetworkCallbackListener){
        callWorkFlowAPIForStep(3)
        settingViewModel.stepThreeSyncStatus.value = 3
        settingViewModel.stepFourSyncStatus.value = 1
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                withContext(Dispatchers.IO){
                    val didiIDList= answerDao.fetchPATSurveyDidiList(prefRepo.getSelectedVillage().id)
                    if(didiIDList.isNotEmpty()){
                        val answeredDidiList = fetchAnswerDidiList(didiIDList)
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
                                        syncPercentage.value = 80f
                                        updateVoStatusToNetwork(networkCallbackListener)
                                    } else {
                                        for (i in didiIDList.indices){
                                            saveAPIResponse.data?.get(i)?.let {
                                                didiDao.updateDidiTransactionId(didiIDList[i].id,
                                                    it.transactionId)
                                            }
                                            didiDao.updateDidiNeedToPostPat(didiIDList[i].id,true)
                                        }
                                        isPending = 4
                                        startSyncTimer(networkCallbackListener)
                                        syncPercentage.value = 70f
                                    }
                                } else {
                                    withContext(Dispatchers.Main){
                                        networkCallbackListener.onFailed()
                                    }
                                }
                            }
                        } else
                            updateVoStatusToNetwork(networkCallbackListener)
                    } else {
                        updateVoStatusToNetwork(networkCallbackListener)
                    }
                }

            }  catch (ex:Exception){
                withContext(Dispatchers.Main){
                    networkCallbackListener.onFailed()
                }
                ex.printStackTrace()
            }
        }
    }

    fun updateVoStatusToNetwork(networkCallbackListener: NetworkCallbackListener) {
        settingViewModel.stepFifthSyncStatus.value = 1
        settingViewModel.stepFourSyncStatus.value = 3
        callWorkFlowAPIForStep(4)
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                withContext(Dispatchers.IO){
                    val needToPostDidiList=didiDao.fetchAllVONeedToPostStatusDidi(needsToPostVo = true, transactionId = "")
                    if(needToPostDidiList.isNotEmpty()){
                        val didiRequestList = arrayListOf<EditDidiWealthRankingRequest>()
                        needToPostDidiList.forEach { didi->
                            launch {
                                didi.voEndorsementStatus.let {
                                    if (it == DidiEndorsementStatus.ENDORSED.ordinal) {
                                        didiRequestList.add(EditDidiWealthRankingRequest(didi.serverId,StepType.VO_ENDORSEMENT.name, ACCEPTED))
                                    } else if (it == DidiEndorsementStatus.REJECTED.ordinal) {
                                        didiRequestList.add(EditDidiWealthRankingRequest(didi.serverId,StepType.VO_ENDORSEMENT.name, DidiEndorsementStatus.REJECTED.name))
                                    }
                                }
                            }
                        }
                        val updateWealthRankResponse=apiService.updateDidiRanking(didiRequestList)
                        if(updateWealthRankResponse.status.equals(SUCCESS,true)){
                            val didiListResponse = updateWealthRankResponse.data
                            if (didiListResponse?.get(0)?.transactionId != null) {
                                for (i in didiListResponse.indices) {
                                    val didiResponse = didiListResponse.get(i)
                                    val didi = didiRequestList[i]
                                    didiResponse.transactionId?.let {
                                        didiDao.updateDidiTransactionId(didi.id,
                                            it
                                        )
                                    }
                                }
                                isPending = 5
                                startSyncTimer(networkCallbackListener)
                            } else {
                                if (didiListResponse != null) {
                                    for (i in didiRequestList.indices) {
                                        val didi = didiRequestList[i]
                                        didiDao.updateNeedToPostVO(false,didi.id)
                                        didiDao.updateDidiTransactionId(didi.id,"")
                                    }
                                }
                                checkVOStatus(networkCallbackListener)
                            }
                        } else {
                            withContext(Dispatchers.Main){
                                networkCallbackListener.onFailed()
                            }
                        }
                    } else {
                        checkVOStatus(networkCallbackListener)
                        callWorkFlowAPIForStep(5)
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main){
                    networkCallbackListener.onFailed()
                }
            }
        }
    }

    fun getStepOneDataSizeInSync(stepOneMutableString : MutableState<String>){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            var sizeToBeShown = ""
            val tolaList = tolaDao.fetchTolaNeedToPost(true, "")
            if (tolaList.isNotEmpty()) {
                val jsonTola = JsonArray()
                for (tola in tolaList) {
                    jsonTola.add(AddCohortRequest.getRequestObjectForTola(tola).toJson())
                }
                sizeToBeShown = getSizeToBeShown(jsonTola.toString().toByteArray().size)
                Log.e("num of step 1", "$tolaList.size")
                Log.e("size of step 2", sizeToBeShown)
                stepOneMutableString.value = sizeToBeShown
            }
        }
    }

    fun getStepTwoDataSizeInSync(stepOneMutableString : MutableState<String>){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            var sizeToBeShown = ""
            val didiList = didiDao.fetchAllDidiNeedToPost(true, "")
            if (didiList.isNotEmpty()) {
                val didiJson = JsonArray()
                for (didi in didiList) {
                    didiJson.add(AddDidiRequest.getRequestObjectForDidi(didi).toJson())
                }
                sizeToBeShown = getSizeToBeShown(didiJson.toString().toByteArray().size)
                Log.e("num of step 2", "$didiList.size")
                Log.e("size of step 2", sizeToBeShown)
                stepOneMutableString.value = sizeToBeShown
            }
        }
    }

    fun getStepThreeDataSizeInSync(stepOneMutableString : MutableState<String>){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            var sizeToBeShown = ""
            val didiWealthList = didiDao.getAllNeedToPostDidiRanking(true)
            if (didiWealthList.isNotEmpty()) {
                val jsonDidi = JsonArray()
                for (didi in didiWealthList) {
                    jsonDidi.add(EditDidiWealthRankingRequest(didi.id, StepType.WEALTH_RANKING.name,didi.wealth_ranking).toJson())
                }
                sizeToBeShown = getSizeToBeShown(jsonDidi.toString().toByteArray().size)
                Log.e("num of step 3", "$didiWealthList.size")
                Log.e("size of step 3", sizeToBeShown)
                stepOneMutableString.value = sizeToBeShown
            }
        }
    }

    fun getStepFourDataSizeInSync(stepOneMutableString : MutableState<String>){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            var sizeToBeShown = ""
            val didiIDList= answerDao.fetchPATSurveyDidiList(prefRepo.getSelectedVillage().id)
            if(didiIDList.isNotEmpty()) {
                val answeredDidiList = fetchAnswerDidiList(didiIDList)
                if (answeredDidiList.isNotEmpty()) {
                    val jsonDidi = JsonArray()
                    for (didi in answeredDidiList) {
                        jsonDidi.add(didi.toJson())
                    }
                    sizeToBeShown = getSizeToBeShown(jsonDidi.toString().toByteArray().size)
                    Log.e("num of step 4", "$answeredDidiList.size")
                    Log.e("size of step 4", sizeToBeShown)
                    stepOneMutableString.value = sizeToBeShown
                }
            }
        }
    }

    fun getStepFiveDataSizeInSync(stepOneMutableString : MutableState<String>){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            var sizeToBeShown = ""
            val tolaList = didiDao.getAllNeedToPostPATDidi(needsToPostPAT = true, villageId = prefRepo.getSelectedVillage().id)
            if (tolaList.isNotEmpty()) {
                val jsonTola = JsonArray()
                for (didi in tolaList) {
                    jsonTola.add(AddDidiRequest.getRequestObjectForDidi(didi).toJson())
                }
                sizeToBeShown = getSizeToBeShown(jsonTola.toString().toByteArray().size)
                Log.e("num of step 5", "$tolaList.size")
                Log.e("size of step 5", sizeToBeShown)
                stepOneMutableString.value = sizeToBeShown
            }
        }
    }

    fun callWorkFlowAPI(villageId: Int,stepId: Int){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                Log.e("work flow","called")
                val dbResponse=stepsListDao.getStepForVillage(villageId, stepId)
                if(dbResponse.workFlowId>0){
                    val response = apiService.editWorkFlow(
                        listOf(
                            EditWorkFlowRequest(dbResponse.workFlowId,StepStatus.COMPLETED.name)
                        ) )
                    withContext(Dispatchers.IO){
                        if (response.status.equals(SUCCESS, true)) {
                            response.data?.let {
                                stepsListDao.updateWorkflowId(stepId,dbResponse.workFlowId,villageId,it[0].status)
                            }
                        }
                    }
                }

            }catch (ex:Exception){
//                onError(tag = "ProgressScreenViewModel", "Error : ${ex.localizedMessage}")
            }
        }
    }

    private fun getSizeToBeShown(size : Int) : String{
        var sizeToBeShown = ""
        if(size < 1024) {
            sizeToBeShown = "$size Bytes"
        } else if(size > 1024 && size < (1024*1024)) {
            val sizeInKB = size/1024
            sizeToBeShown = "$sizeInKB Bytes"
        } else if(size > 1024) {
            val sizeInMB = size/(1024*1024)
            sizeToBeShown = "$sizeInMB Bytes"
        }
        return sizeToBeShown
    }
}
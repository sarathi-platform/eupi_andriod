package com.patsurvey.nudge

import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.gson.JsonArray
import com.patsurvey.nudge.activities.settings.TransactionIdRequest
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.request.AddCohortRequest
import com.patsurvey.nudge.model.request.AddDidiRequest
import com.patsurvey.nudge.model.request.EditDidiWealthRankingRequest
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.StepType
import com.patsurvey.nudge.utils.WealthRank
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow
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
    var syncPercentage : MutableState<Float>
){

    private val pendingTimerTime : Long= 10000
    private var isPending = 0
    fun syncDataToServer(networkCallbackListener: NetworkCallbackListener){
        showProgressBar(networkCallbackListener)
        Log.e("progress","started")
        addTolasToNetwork(networkCallbackListener)
        addDidisToNetwork(networkCallbackListener)
        updateWealthRankingToNetwork(networkCallbackListener)
        checkTolaStatus()
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

    private fun startSyncTimer(){
        val timer = Timer()
        timer.schedule(object : TimerTask(){
            override fun run() {
                if(isPending == 1){
                    checkTolaStatus()
                }
            }
        },pendingTimerTime)
    }

    private fun checkTolaStatus() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaList = tolaDao.fetchPendingTola(true,"")
            val ids : ArrayList<String> = arrayListOf()
            tolaList.forEach{tola->
                tola.transactionId?.let { ids.add(it) }
            }
            val response = apiService.getPendingStatus(TransactionIdRequest(ids))
            if (response.status.equals(SUCCESS, true)) {
                response.data?.forEach{transactionIdResponse ->
                    tolaList.forEach{tola->
                        if(transactionIdResponse.transactionId == tola.transactionId){
                            tola.id = transactionIdResponse.referenceId
                        }
                    }
                }
                updateTolaNeedTOPostList(tolaList)
            }
        }
    }

    fun addTolasToNetwork(networkCallbackListener: NetworkCallbackListener) {
        Log.e("add tola","called")
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaList = tolaDao.fetchTolaNeedToPost(true)
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
                                updateTolaNeedTOPostList(tolaList)
                            }
                        } else {
                            for (i in 0..response.data.size){
                                tolaList[i].transactionId = response.data[i].transactionId
                                updateLocalTransactionIdToLocal(tolaList)
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
                addDidisToNetwork(networkCallbackListener)
            }
        }
    }

    private fun updateLocalTransactionIdToLocal(tolaList: List<TolaEntity>) {
        tolaList.forEach{tola->
            tola.transactionId?.let { tolaDao.updateTolaTransactionId(tola.id, it) }
        }
        isPending = 1
        startSyncTimer()
    }

    fun updateTolaNeedTOPostList(tolaList: List<TolaEntity>){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            updateTolaListWithIds(tolaList)
        }
    }

    private fun updateTolaListWithIds(tolaList: List<TolaEntity>) {
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
    }

    fun addDidisToNetwork(networkCallbackListener: NetworkCallbackListener) {
        Log.e("add didi","called")
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.getAllNeedToPostDidiRanking(true)
            val jsonDidi = JsonArray()
            if (didiList.isNotEmpty()) {
                for (didi in didiList) {
                    jsonDidi.add(AddDidiRequest.getRequestObjectForDidi(didi).toJson())
                }
                val response = apiService.addDidis(jsonDidi)
                if (response.status.equals(SUCCESS, true)) {
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
                    updateDidisNeedTOPostList(didiList)
                } else {
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
                }
            }
        }
    }

    fun updateDidisNeedTOPostList(didiList : List<DidiEntity>){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            updateDidiListWithServerIds(didiList)
        }
    }

    private fun updateDidiListWithServerIds(oldDidiList: List<DidiEntity>) {
        didiDao.deleteDidiNeedToPost(true)
        oldDidiList.forEach(){ didiEntity ->
            didiEntity.needsToPost = false
        }
        didiDao.insertAll(oldDidiList)
    }

    fun updateWealthRankingToNetwork(networkCallbackListener: NetworkCallbackListener){
        Log.e("add didi","called")
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                withContext(Dispatchers.IO){
                    val needToPostDidiList=didiDao.getAllNeedToPostDidiRanking(true)
                    if(needToPostDidiList.isNotEmpty()){
                        needToPostDidiList.forEach { didi->
                            launch {
                                didi.wealth_ranking.let {
                                    val updateWealthRankResponse=apiService.updateDidiRanking(
                                        listOf(
                                            EditDidiWealthRankingRequest(didi.id,
                                                StepType.WEALTH_RANKING.name,didi.wealth_ranking),
                                            EditDidiWealthRankingRequest(didi.id, StepType.SOCIAL_MAPPING.name, StepStatus.COMPLETED.name)
                                        )
                                    )
                                    if(updateWealthRankResponse.status.equals(SUCCESS,true)){
                                        didiDao.setNeedToPostRanking(didi.id,false)
                                    } else {
                                        networkCallbackListener.onFailed()
                                    }
                                }

                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                networkCallbackListener.onFailed()
//                onError("WealthRankingSurveyViewModel", "onError: ${ex.message}, \n${ex.stackTrace}")
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
package com.patsurvey.nudge

import android.text.TextUtils
import android.util.Log
import com.google.gson.JsonArray
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

class SyncHelper (
    val prefRepo: PrefRepo,
    val apiService: ApiService,
    val tolaDao: TolaDao,
    val stepsListDao: StepsListDao,
    val exceptionHandler : CoroutineExceptionHandler,
    val villegeListDao: VillageListDao,
    val didiDao: DidiDao,
    var job: Job?
){

    fun syncDataToServer(networkCallbackListener: NetworkCallbackListener){
        addTolasToNetwork(networkCallbackListener)
        addDidisToNetwork(networkCallbackListener)
        updateWealthRankingToNetwork(networkCallbackListener)
    }

    fun addTolasToNetwork(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaList = tolaDao.fetchTolaNeedToPost(true)
            val jsonTola = JsonArray()
            if (tolaList.isNotEmpty()) {
                for (tola in tolaList) {
                    jsonTola.add(AddCohortRequest.getRequestObjectForTola(tola).toJson())
                }
                val response = apiService.addCohort(jsonTola)
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.let {
                        networkCallbackListener.onSuccess()
                        response.data.forEach { tolaDataFromNetwork ->
                            tolaList.forEach { tola ->
                                if (TextUtils.equals(tolaDataFromNetwork.name, tola.name)) {
                                    tola.id = tolaDataFromNetwork.id
                                    tola.createdDate = tolaDataFromNetwork.createdDate
                                    tola.modifiedDate = tolaDataFromNetwork.modifiedDate
                                }
                            }
                            updateTolaNeedTOPostList(tolaList)
                        }
                    }
//                    addDidisToNetwork(networkCallbackListener)
                }
                else {
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
                }
            }
        }
    }
    fun updateTolaNeedTOPostList(tolaList: List<TolaEntity>){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            updateTolaListWithIds(tolaList)
        }
    }

    private fun updateTolaListWithIds(tolaList: List<TolaEntity>) {
        tolaDao.deleteTolaNeedToPost(true)
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
                    modifiedDate = it.modifiedDate
                )
            )
        }
        tolaDao.insertAll(tolas)
    }

    fun addDidisToNetwork(networkCallbackListener: NetworkCallbackListener) {
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
                        networkCallbackListener.onSuccess()
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
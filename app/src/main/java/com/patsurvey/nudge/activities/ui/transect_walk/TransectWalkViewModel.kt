package com.patsurvey.nudge.activities.ui.transect_walk

import android.text.TextUtils
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.request.*
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TransectWalkViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val apiInterface: ApiService,
    val tolaDao: TolaDao,
    val stepsListDao: StepsListDao,
    val didiDao: DidiDao,
    val villageListDao: VillageListDao
) : BaseViewModel() {

    private val _tolaList = MutableStateFlow(listOf<TolaEntity>())
    val tolaList: StateFlow<List<TolaEntity>> get() = _tolaList

    val villageEntity = mutableStateOf<VillageEntity?>(null)

    val isTransectWalkComplete = mutableStateOf(false)

    val showLoader = mutableStateOf(false)

    init {
//        fetchTolaList(villageId)

    }

    fun addTola(tola: Tola) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaItem = TolaEntity(
                id = 0,
                name = tola.name,
                type = CohortType.TOLA.type,
                latitude = tola.location.lat ?: 0.0,
                longitude = tola.location.long ?: 0.0,
                villageEntity.value?.id ?: 0,
                status = 1,
                createdDate = System.currentTimeMillis(),
                modifiedDate = System.currentTimeMillis()
            )
            tolaDao.insert(tolaItem)
            val updatedTolaList = tolaDao.getAllTolasForVillage(prefRepo.getSelectedVillage().id)
            withContext(Dispatchers.Main) {
                _tolaList.value = updatedTolaList
                prefRepo.savePref(TOLA_COUNT, _tolaList.value.size)
                if (isTransectWalkComplete.value) {
                    isTransectWalkComplete.value = false
                }
            }
        }
    }

    /*fun addEmptyTola() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaItem = TolaEntity.createEmptyTolaForVillageId(villageEntity.value?.id ?: 0)
            tolaDao.insert(tolaItem)
            withContext(Dispatchers.Main) {
                tolaList.add(tolaItem)
                prefRepo.savePref(TOLA_COUNT, tolaList.size)
                if (isTransectWalkComplete.value) {
                    isTransectWalkComplete.value = false
                }
            }
        }
    }*/

    fun addTolasToNetwork(villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val jsonTola = JsonArray()
            val filteredTolaList = tolaList.value.filter { it.needsToPost }
            if (filteredTolaList.isNotEmpty()) {
                for (tola in filteredTolaList) {
                    jsonTola.add(AddCohortRequest.getRequestObjectForTola(tola).toJson())
                }
                Log.d("TransectWalkViewModel", "$jsonTola")

//                val response = apiInterface.addCohort(jsonTola)
//                if (response.status.equals(SUCCESS, true)) {
//                    response.data?.let {
//                        response.data.forEach { tolaDataFromNetwork ->
//                            tolaList.value.forEach { tola ->
//                                if (TextUtils.equals(tolaDataFromNetwork.name, tola.name)) {
//                                    tola.id = tolaDataFromNetwork.id
//                                    tola.createdDate = tolaDataFromNetwork.createdDate
//                                    tola.modifiedDate = tolaDataFromNetwork.modifiedDate
//                                }
//                            }
//                        }
                        updateTolaListWithIds(tolaList.value, villageId)

                tolaList.value.forEach{
                    tolaDao.updateNeedToPost(it.id,false)
                }
//                tolaDao.setNeedToPost(
//                            tolaList.value.filter { it.needsToPost }.map { it.id },
//                            false
//                        )
//                    }
//                }
            }
        }
    }

    private fun updateTolaListWithIds(tolaList: List<TolaEntity>, villageId: Int) {
        tolaDao.deleteTolaTable(villageId)
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
                    needsToPost = true,
                    status = it.status,
                    createdDate = it.createdDate,
                    modifiedDate = it.modifiedDate
                )
            )
        }
        tolaDao.insertAll(tolas)
    }

    fun removeTola(tolaId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
//                val jsonArray = JsonArray()
//                jsonArray.add(DeleteTolaRequest(tolaId).toJson())
//                val response = apiInterface.deleteCohort(jsonArray)
//                if (response.status.equals(SUCCESS)) {
//                    tolaDao.removeTola(tolaId)
//                } else {
//                    tolaDao.setNeedToPost(listOf(tolaId), true)
//                }
                tolaDao.deleteTolaOffline(tolaId, TolaStatus.TOLA_DELETED.ordinal)
                val updatedTolaList = tolaDao.getAllTolasForVillage(prefRepo.getSelectedVillage().id)
                withContext(Dispatchers.Main) {
                    _tolaList.value = updatedTolaList
                    if (isTransectWalkComplete.value)
                        isTransectWalkComplete.value = false
                }
            } catch (ex: Exception) {
                onError("TransectWalkViewModel", "${ex.message}: \n${ex.stackTraceToString()}")
            }
        }
    }

    fun updateTola(id: Int, newName: String, newLocation: LocationCoordinates?) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val updatedTola = TolaEntity(
                id = id,
                name = newName,
                type = CohortType.TOLA.type,
                latitude = newLocation?.lat ?: 0.0,
                longitude = newLocation?.long ?: 0.0,
                villageId = tolaList.value[getIndexOfTola(id)].villageId,
                needsToPost = true,
                status = tolaList.value[getIndexOfTola(id)].status,
                createdDate = tolaList.value[getIndexOfTola(id)].createdDate,
                modifiedDate = System.currentTimeMillis()
            )
            tolaDao.insert(updatedTola)
//            val jsonTola = JsonArray()
//            jsonTola.add(EditCohortRequest.getRequestObjectForTola(updatedTola).toJson())
            val updatedTolaList = tolaDao.getAllTolasForVillage(prefRepo.getSelectedVillage().id)
            _tolaList.value = updatedTolaList
            if (isTransectWalkComplete.value)
                isTransectWalkComplete.value = false
//            apiInterface.editCohort(jsonTola)
            withContext(Dispatchers.Main) {
                _tolaList.value = updatedTolaList
            }
        }
    }

    fun fetchTolaList(villageId: Int){
        showLoader.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                _tolaList.emit(tolaDao.getAllTolasForVillage(villageId))
                showLoader.value = false
            }catch (ex:Exception){
                onError(tag = "TransectWalkViewModel", "Exception: ${ex.localizedMessage}")
                showLoader.value = false
            }
        }
    }

    fun setVillage(villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val village = villageListDao.getVillage(villageId)
            withContext(Dispatchers.Main) {
                villageEntity.value = village
            }
        }
    }

    private fun getIndexOfTola(id: Int): Int {
        return tolaList.value.map { it.id }.indexOf(id)
    }

    fun markTransectWalkComplete(villageId: Int, stepId: Int) {
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
            stepsListDao.markStepAsCompleteOrInProgress(stepId, StepStatus.COMPLETED.ordinal,villageId)
            val stepDetails=stepsListDao.getStepForVillage(villageId, stepId)
            if(stepDetails.orderNumber<stepsListDao.getAllSteps().size){
                stepsListDao.markStepAsInProgress((stepDetails.orderNumber+1),StepStatus.INPROGRESS.ordinal,villageId)
            }
        }
    }

    fun markTransectWalkIncomplete(stepId: Int,villageId:Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val step=stepsListDao.getStepForVillage(villageId, stepId)
            stepsListDao.markStepAsCompleteOrInProgress(stepId, StepStatus.INPROGRESS.ordinal,villageId)
            val completeStepList=stepsListDao.getAllCompleteStepsForVillage(villageId)
            completeStepList?.let {
                it.forEach { newStep->
                    if(newStep.orderNumber>step.orderNumber){
                        stepsListDao.markStepAsCompleteOrInProgress(newStep.id, StepStatus.INPROGRESS.ordinal,villageId)
                    }
                }
            }

        }
    }

    fun isTransectWalkComplete(stepId: Int,villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val isComplete = stepsListDao.isStepComplete(stepId, villageId = villageId) == StepStatus.COMPLETED.ordinal
            withContext(Dispatchers.Main) {
                isTransectWalkComplete.value = isComplete
            }
        }
    }

    fun callWorkFlowAPI(villageId: Int,stepId: Int){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val dbResponse=stepsListDao.getStepForVillage(villageId, stepId)
                if(dbResponse.workFlowId>0){
                    val response = apiInterface.editWorkFlow(
                        listOf(
                            EditWorkFlowRequest(dbResponse.workFlowId,StepStatus.COMPLETED.name)
                        ) )
                    withContext(Dispatchers.IO){
                        if (response.status.equals(SUCCESS, true)) {
                            response.data?.let {
                                stepsListDao.updateWorkflowId(stepId,dbResponse.workFlowId,villageId,it[0].status)
                            }
                        }else{
                            onError(tag = "ProgressScreenViewModel", "Error : ${response.message}")
                        }
                    }
                }

            }catch (ex:Exception){
                onError(tag = "ProgressScreenViewModel", "Error : ${ex.localizedMessage}")
            }
        }
    }

    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }


}
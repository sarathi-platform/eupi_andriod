package com.patsurvey.nudge.activities.ui.transect_walk

import android.text.TextUtils
import android.util.Log
import androidx.compose.runtime.mutableStateOf
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
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.request.AddCohortRequest
import com.patsurvey.nudge.model.request.DeleteTolaRequest
import com.patsurvey.nudge.model.request.EditCohortRequest
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.network.model.ErrorModelWithApi
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.CohortType
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.FORM_C
import com.patsurvey.nudge.utils.FORM_D
import com.patsurvey.nudge.utils.LocationCoordinates
import com.patsurvey.nudge.utils.PREF_FORM_PATH
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.TOLA_COUNT
import com.patsurvey.nudge.utils.Tola
import com.patsurvey.nudge.utils.TolaStatus
import com.patsurvey.nudge.utils.VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_
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
    val isVoEndorsementComplete = mutableStateOf(false)

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
                villageId = villageEntity.value?.id ?: 0,
                status = 1,
                localCreatedDate = System.currentTimeMillis(),
                localModifiedDate = System.currentTimeMillis(),
                transactionId = "",
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

    fun addTolasToNetwork(villageId: Int, networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val jsonTola = JsonArray()
            val filteredTolaList = tolaList.value.filter { it.needsToPost }
            if (filteredTolaList.isNotEmpty()) {
                for (tola in filteredTolaList) {
                    jsonTola.add(AddCohortRequest.getRequestObjectForTola(tola).toJson())
                }
                Log.d("TransectWalkViewModel", "$jsonTola")

                val response = apiInterface.addCohort(jsonTola)
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.let {
                        networkCallbackListener.onSuccess()
                        response.data.forEach { tolaDataFromNetwork ->
                            tolaList.value.forEach { tola ->
                                if (TextUtils.equals(tolaDataFromNetwork.name, tola.name)) {
                                    tola.id = tolaDataFromNetwork.id
                                    tola.createdDate = tolaDataFromNetwork.createdDate
                                    tola.modifiedDate = tolaDataFromNetwork.modifiedDate
                                }
                            }
                        }
                    }
                }
                else {
                    withContext(Dispatchers.Main){
                        networkCallbackListener.onFailed()
                    }
                }
            }
        }
    }
    fun updateTolaNeedTOPostList(villageId: Int){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            updateTolaListWithIds(tolaList.value, villageId)
        }
    }

    private fun updateTolaListWithIds(tolaList: List<TolaEntity>, villageId: Int) {
        tolaList.forEach{ tola ->
            tolaDao.updateTolaDetailAfterSync(
                id = tola.id,
                serverId = tola.serverId,
                needsToPost = false,
                transactionId = "",
                createdDate = tola.createdDate,
                modifiedDate = tola.modifiedDate,

            )
        }
    }

    fun removeTola(tolaId: Int, networkCallbackListener: NetworkCallbackListener, villageId: Int, stepId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                tolaDao.deleteTolaOffline(tolaId, TolaStatus.TOLA_DELETED.ordinal)
                val updatedTolaList = tolaDao.getAllTolasForVillage(prefRepo.getSelectedVillage().id)
                withContext(Dispatchers.Main) {
                    _tolaList.value = updatedTolaList
                    if (isTransectWalkComplete.value)
                        isTransectWalkComplete.value = false
                }
                deleteDidisForTola(tolaId)
                val stepDetails=stepsListDao.getStepForVillage(villageId, stepId)
                if (_tolaList.value.isEmpty()){
                    stepsListDao.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }.forEach {
                        if (stepDetails.orderNumber != it.orderNumber) {
                            if (it.orderNumber == 2) {
                                stepsListDao.markStepAsInProgress((it.orderNumber), StepStatus.INPROGRESS.ordinal, villageId)
                            } else {
                                stepsListDao.markStepAsInProgress((it.orderNumber), StepStatus.NOT_STARTED.ordinal, villageId)
                            }
                        }
                    }
                }
                withContext(Dispatchers.IO){
                    val jsonArray = JsonArray()
                    jsonArray.add(DeleteTolaRequest(tolaId, localModifiedDate = System.currentTimeMillis()).toJson())
                    val response = apiInterface.deleteCohort(jsonArray)
                    if (response.status.equals(SUCCESS)) {
                        tolaDao.removeTola(tolaId)
                    } else {
                        tolaDao.setNeedToPost(listOf(tolaId), true)
                        networkCallbackListener.onFailed()
                    }
                }
            } catch (ex: Exception) {
                onError("TransectWalkViewModel", "removeTola- ${ex.message}: \n${ex.stackTraceToString()}")
            }
        }
    }

    private fun deleteDidisForTola(tolaId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val didList = didiDao.getDidisForTola(tolaId)
                didiDao.deleteDidisForTola(tolaId, activeStatus = DidiStatus.DIID_DELETED.ordinal, needsToPostDeleteStatus = true)
                val jsonArray = JsonArray()
                didList.forEach {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("id", it.id)
                    jsonObject.addProperty("localModifiedDate", System.currentTimeMillis())
                    jsonArray.add(jsonObject)
                }
                val deleteDidiApiRespone = apiInterface.deleteDidi(jsonArray)
                if (deleteDidiApiRespone.status.equals(SUCCESS)) {
                    Log.d("TransectWalkViewModel", "Didids Deleted Successfully")
                } else {
                    Log.d("TransectWalkViewModel", "Didids not Deleted Successfully")
                }

            } catch (ex: Exception) {
                onError("TransectWalkViewModel", "deleteDidisForTola- ${ex.message}: \n${ex.stackTraceToString()}")
            }
        }
    }

    fun updateTola(id: Int, newName: String, newLocation: LocationCoordinates?, networkCallbackListener: NetworkCallbackListener) {
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
                modifiedDate = System.currentTimeMillis(),
                transactionId = "",
                serverId = tolaList.value[getIndexOfTola(id)].serverId
                localCreatedDate=tolaList.value[getIndexOfTola(id)].localCreatedDate,
                localModifiedDate=System.currentTimeMillis(),
                transactionId = ""
            )
            tolaDao.insert(updatedTola)
            didiDao.updateTolaName(id, newName)
            val updatedTolaList = tolaDao.getAllTolasForVillage(prefRepo.getSelectedVillage().id)
            _tolaList.value = updatedTolaList
            if (isTransectWalkComplete.value)
                isTransectWalkComplete.value = false

            withContext(Dispatchers.Main) {
                _tolaList.value = updatedTolaList
            }
            withContext(Dispatchers.IO){
                val jsonTola = JsonArray()
                jsonTola.add(EditCohortRequest.getRequestObjectForTola(updatedTola).toJson())
                val response = apiInterface.editCohort(jsonTola)
                if (response.status.equals(SUCCESS)){

                }else{
                    tolaDao.setNeedToPost(listOf(updatedTola.id), true)
                    Log.d("updateTola: ", "update tola request failed: ${response.message}")
                    networkCallbackListener.onFailed()
                }
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
            stepsListDao.markStepAsCompleteOrInProgress(
                stepId,
                StepStatus.COMPLETED.ordinal,
                villageId
            )
            val stepDetails = stepsListDao.getStepForVillage(villageId, stepId)
            if (stepDetails.orderNumber < stepsListDao.getAllSteps().size) {
                stepsListDao.markStepAsInProgress(
                    (stepDetails.orderNumber + 1),
                    StepStatus.INPROGRESS.ordinal,
                    villageId
                )
                prefRepo.savePref("$VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_${villageId}", false)
                for (i in 1..5) {
                    prefRepo.savePref(getFormPathKey(getFormSubPath(FORM_C, i)), "")
                    prefRepo.savePref(getFormPathKey(getFormSubPath(FORM_D, i)), "")
                }
            }
        }
    }

    fun markTransectWalkIncomplete(
        stepId: Int,
        villageId: Int,
        networkCallbackListener: NetworkCallbackListener
    ) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val step = stepsListDao.getStepForVillage(villageId, stepId)
            stepsListDao.markStepAsCompleteOrInProgress(
                stepId,
                StepStatus.INPROGRESS.ordinal,
                villageId
            )
            val completeStepList = stepsListDao.getAllCompleteStepsForVillage(villageId)
            completeStepList?.let {
                it.forEach { newStep ->
                    if (newStep.orderNumber > step.orderNumber) {
                        stepsListDao.markStepAsCompleteOrInProgress(
                            newStep.id,
                            StepStatus.INPROGRESS.ordinal,
                            villageId
                        )
                    }
                }
            }
            completeStepList?.let {
                val apiRequest = mutableListOf<EditWorkFlowRequest>()
                it.forEach { newStep ->
                    if (newStep.orderNumber > step.orderNumber) {
                        if (newStep.workFlowId > 0) {
                            apiRequest.add(
                                EditWorkFlowRequest(
                                    newStep.workFlowId,
                                    StepStatus.INPROGRESS.name
                                )
                            )
                        }
                    }
                }
                if (apiRequest.isNotEmpty()) {
                    launch {
                        val response = apiInterface.editWorkFlow(apiRequest)
                        if (response.status.equals(SUCCESS)) {
                            response.data?.let { response ->
                                response.forEach { it ->
                                    stepsListDao.updateWorkflowId(
                                        stepId,
                                        it.id,
                                        villageId,
                                        it.status
                                    )
                                }
                            }
                        } else {
                            networkCallbackListener.onFailed()
                        }
                    }
                }
            }
        }
    }

    fun isTransectWalkComplete(stepId: Int, villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val isComplete = stepsListDao.isStepComplete(
                stepId,
                villageId = villageId
            ) == StepStatus.COMPLETED.ordinal
            withContext(Dispatchers.Main) {
                isTransectWalkComplete.value = isComplete
            }
        }
    }

    fun isVoEndorsementCompleteForVillage(villageId: Int) {
        val isComplete =
            prefRepo.getPref("$VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_${villageId}", false)
        isVoEndorsementComplete.value = isComplete
    }

    fun callWorkFlowAPI(
        villageId: Int,
        stepId: Int,
        networkCallbackListener: NetworkCallbackListener
    ) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val dbResponse = stepsListDao.getStepForVillage(villageId, stepId)
                val stepList = stepsListDao.getAllStepsForVillage(villageId)
                if (dbResponse.workFlowId > 0) {
                    val response = apiInterface.editWorkFlow(
                        listOf(
                            EditWorkFlowRequest(dbResponse.workFlowId, StepStatus.COMPLETED.name)
                        )
                    )
                    withContext(Dispatchers.IO) {
                        if (response.status.equals(com.patsurvey.nudge.utils.SUCCESS, true)) {
                            response.data?.let {
                                stepsListDao.updateWorkflowId(
                                    stepId,
                                    dbResponse.workFlowId,
                                    villageId,
                                    it[0].status
                                )
                            }
                        } else {
                            networkCallbackListener.onFailed()
                            onError(tag = "ProgressScreenViewModel", "Error : ${response.message}")
                        }
                    }
                }
                launch {
                    try {
                        stepList.forEach { step ->
                            if (step.id != stepId && step.orderNumber > dbResponse.orderNumber && step.workFlowId > 0) {
                                val inProgressStepResponse = apiInterface.editWorkFlow(
                                    listOf(
                                        EditWorkFlowRequest(
                                            step.workFlowId,
                                            StepStatus.INPROGRESS.name
                                        )
                                    )
                                )
                                if (inProgressStepResponse.status.equals(SUCCESS, true)) {
                                    inProgressStepResponse.data?.let {
                                        stepsListDao.updateWorkflowId(
                                            step.id,
                                            step.workFlowId,
                                            villageId,
                                            it[0].status
                                        )
                                    }
                                }
                            }
                        }
                    } catch (ex: Exception) {
                        onCatchError(ex, ApiType.WORK_FLOW_API)
                    }
                }
            } catch (ex: Exception) {
                networkCallbackListener.onFailed()
                onError(tag = "ProgressScreenViewModel", "Error : ${ex.localizedMessage}")
            }
        }
    }

    override fun onServerError(error: ErrorModel?) {
        showLoader.value = false
        networkErrorMessage.value = error?.message.toString()
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        TODO("Not yet implemented")
    }

    fun getFormPathKey(subPath: String): String {
        //val subPath formPictureScreenViewModel.pageItemClicked.value
        //"${PREF_FORM_PATH}_${formPictureScreenViewModel.prefRepo.getSelectedVillage().name}_${subPath}"
        return "${PREF_FORM_PATH}_${prefRepo.getSelectedVillage().name}_${subPath}"
    }

    fun getFormSubPath(formName: String, pageNumber: Int): String {
        return "${formName}_page_$pageNumber"
    }

}
package com.patsurvey.nudge.activities.ui.transect_walk

import androidx.compose.runtime.mutableStateOf
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.patsurvey.nudge.activities.settings.TransactionIdRequest
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.intefaces.LocalDbListener
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.request.AddCohortRequest
import com.patsurvey.nudge.model.request.DeleteTolaRequest
import com.patsurvey.nudge.model.request.EditCohortRequest
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.CohortType
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.FORM_C
import com.patsurvey.nudge.utils.FORM_D
import com.patsurvey.nudge.utils.LocationCoordinates
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_FORM_PATH
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.TOLA_COUNT
import com.patsurvey.nudge.utils.Tola
import com.patsurvey.nudge.utils.TolaStatus
import com.patsurvey.nudge.utils.VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_
import com.patsurvey.nudge.utils.updateLastSyncTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Timer
import java.util.TimerTask
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
    private var isPending = 0
    init {
//        fetchTolaList(villageId)

    }

    fun addTola(tola: Tola, dbListener: LocalDbListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val isTolaExist = tolaDao.getTolaExist(tola.name, prefRepo.getSelectedVillage().id) > 0
            if (!isTolaExist) {
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
                    needsToPost = true,
                )
                tolaDao.insert(tolaItem)
                val updatedTolaList =
                    tolaDao.getAllTolasForVillage(prefRepo.getSelectedVillage().id)
                withContext(Dispatchers.Main) {
                    _tolaList.value = updatedTolaList
                    prefRepo.savePref(TOLA_COUNT, _tolaList.value.size)
                    if (isTransectWalkComplete.value) {
                        isTransectWalkComplete.value = false
                    }
                    dbListener.onInsertionSuccess()
                }
            } else {
                withContext(Dispatchers.Main) {
                    dbListener.onInsertionFailed()
                }
            }
        }
    }

    fun isTolaExist(name : String) : Boolean{
        return tolaDao.getTolaExist(name,prefRepo.getSelectedVillage().id) > 0
    }


    fun addEmptyTola() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaItem = TolaEntity.createEmptyTolaForVillageId(villageEntity.value?.id ?: 0)
            tolaDao.insert(tolaItem)
            val updatedTolaList =
                tolaDao.getAllTolasForVillage(prefRepo.getSelectedVillage().id)
            withContext(Dispatchers.Main) {
//                prefRepo.savePref(TOLA_COUNT, tolaList.size)
                _tolaList.value = updatedTolaList
                if (isTransectWalkComplete.value) {
                    isTransectWalkComplete.value = false
                }
            }
        }
    }

    fun addTolasToNetwork() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val jsonTola = JsonArray()
            val tolaList = tolaDao.fetchTolaNeedToPost(true,"",0)
//            val filteredTolaList = tolaList
            if (tolaList.isNotEmpty()) {
                for (tola in tolaList) {
                    jsonTola.add(AddCohortRequest.getRequestObjectForTola(tola).toJson())
                }
                NudgeLogger.d("TransectWalkViewModel", "$jsonTola")
                val response = apiInterface.addCohort(jsonTola)
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.let {
                        if(response.data[0].transactionId.isNullOrEmpty()) {
                            for(i in response.data.indices){
                                val tola = tolaList[i]
                                val tolaDataFromNetwork = response.data[i]
                                val createdTime = tolaDataFromNetwork.createdDate
                                val modifiedDate = tolaDataFromNetwork.modifiedDate
                                tolaDao.updateTolaDetailAfterSync(tola.id,tolaDataFromNetwork.id,
                                    false,
                                    "",
                                    createdTime,
                                    modifiedDate)
                                tola.serverId = tolaDataFromNetwork.id
                                tola.createdDate = tolaDataFromNetwork.createdDate
                                tola.modifiedDate = tolaDataFromNetwork.modifiedDate
                            }
                            checkTolaAddStatus()
                        } else {
                            response.data.forEach { tola ->
                                for(i in response.data.indices){
                                    response.data[i].transactionId.let { it1 ->
                                        tolaDao.updateTolaTransactionId(tolaList[i].id,
                                            it1
                                        )
                                    }
                                }
                            }
                            isPending = 1
                            startSyncTimerForStatus()
                        }
                    }
                } else {
                    checkTolaAddStatus()
                }
                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }
            } else {
                checkTolaAddStatus()
            }
        }
    }

    private fun startSyncTimerForStatus(){
        val timer = Timer()
        timer.schedule(object : TimerTask(){
            override fun run() {
                when(isPending){
                    1 -> {
                        checkTolaAddStatus()
                    }
                    2 -> {
                        checkTolaDeleteStatus()
                    }
                    3 -> {
                        checkTolaUpdateStatus()
                    }
                }
            }
        },10000)
    }

    private fun checkTolaAddStatus(){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaList = tolaDao.fetchPendingTola(true,"")
            if(tolaList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                tolaList.forEach { tola ->
                    tola.transactionId?.let { ids.add(it) }
                }
                val response = apiInterface.getPendingStatus(TransactionIdRequest("",ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        tolaList.forEach { tola ->
                            if (transactionIdResponse.transactionId == tola.transactionId) {
                                tola.serverId = transactionIdResponse.referenceId
                            }
                        }
                    }
                    for(tola in tolaList) {
                        tolaDao.updateTolaDetailAfterSync(
                            id = tola.id,
                            serverId = tola.serverId,
                            needsToPost = false,
                            transactionId = "",
                            createdDate = tola.createdDate?:0L,
                            modifiedDate = tola.modifiedDate?:0L
                        )
                    }
                    deleteTolaToNetwork()
                } else {
                    deleteTolaToNetwork()
                }
                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }

            } else {
                deleteTolaToNetwork()
            }
        }
    }

    private fun deleteTolaToNetwork() {
        NudgeLogger.e("delete tola","called")
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaList = tolaDao.fetchAllTolaNeedToDelete(TolaStatus.TOLA_DELETED.ordinal)
            val jsonTola = JsonArray()
            if (tolaList.isNotEmpty()) {
                for (tola in tolaList) {
                    jsonTola.add(DeleteTolaRequest(tola.serverId, localModifiedDate = System.currentTimeMillis()).toJson())
                }
                NudgeLogger.e("tola need to post","$tolaList.size")
                val response = apiInterface.deleteCohort(jsonTola)
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.let {
                        if((response.data[0]?.transactionId.isNullOrEmpty())) {
                            tolaList.forEach { tola ->
                                tolaDao.deleteTola(tola.id)
                            }
                            checkTolaDeleteStatus()
                        } else {
                            for (i in 0 until response.data.size){
                                tolaList[i].transactionId = response.data[i]?.transactionId
                                tolaList[i].transactionId?.let { it1 ->
                                    tolaDao.updateTolaTransactionId(tolaList[i].id,
                                        it1
                                    )
                                }
                            }
                            isPending = 2
                            startSyncTimerForStatus()
                        }
                    }
                } else {
                    checkTolaDeleteStatus()
                }

                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }

            } else {
                checkTolaDeleteStatus()
            }
        }
    }

    private fun updateTolasToNetwork() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaList = tolaDao.fetchAllTolaNeedToUpdate(true,"",0)
            val jsonTola = JsonArray()
            if (tolaList.isNotEmpty()) {
                for (tola in tolaList) {
                    jsonTola.add(EditCohortRequest.getRequestObjectForTola(tola).toJson())
                }
                NudgeLogger.e("tola need to post","$tolaList.size")
                val response = apiInterface.editCohort(jsonTola)
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.let {
                        if((response.data[0].transactionId.isNullOrEmpty())) {
                            tolaList.forEach { tola ->
                                tolaDao.updateNeedToPost(tola.id,false)
                            }
                            checkTolaUpdateStatus()
                        } else {
                            for (i in 0 until response.data.size){
                                tolaList[i].transactionId = response.data[i].transactionId
                                tolaList[i].transactionId?.let { it1 ->
                                    tolaDao.updateTolaTransactionId(tolaList[i].id,
                                        it1
                                    )
                                }
                            }
                            isPending = 3
                            startSyncTimerForStatus()
                        }
                    }
                } else {
                    checkTolaUpdateStatus()
                }
                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }
            } else {
                checkTolaUpdateStatus()
            }
        }
    }

    fun checkTolaUpdateStatus(){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaList = tolaDao.fetchAllPendingTolaNeedToUpdate(true,"")
            if(tolaList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                tolaList.forEach { tola ->
                    tola.transactionId?.let { ids.add(it) }
                }
                val response = apiInterface.getPendingStatus(TransactionIdRequest("",ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        tolaList.forEach { tola ->
                            if (transactionIdResponse.transactionId == tola.transactionId) {
                                tolaDao.updateNeedToPost(tola.id,false)
                                tolaDao.updateTolaTransactionId(tola.id,"")
                            }
                        }
                    }
                }

                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }

            }
        }
    }

    fun checkTolaDeleteStatus(){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaList = tolaDao.fetchAllPendingTolaNeedToDelete(TolaStatus.TOLA_DELETED.ordinal,"")
            if(tolaList.isNotEmpty()) {
                val ids: ArrayList<String> = arrayListOf()
                tolaList.forEach { tola ->
                    tola.transactionId?.let { ids.add(it) }
                }
                val response = apiInterface.getPendingStatus(TransactionIdRequest("",ids))
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.forEach { transactionIdResponse ->
                        tolaList.forEach { tola ->
                            if (transactionIdResponse.transactionId == tola.transactionId) {
                                tolaDao.deleteTola(tola.id)
                            }
                        }
                    }
                    updateTolasToNetwork()
                }
                if(!response.lastSyncTime.isNullOrEmpty()){
                    updateLastSyncTime(prefRepo,response.lastSyncTime)
                }

            } else {
                updateTolasToNetwork()
            }
        }
    }


    fun removeTola(tolaId: Int, isOnline: Boolean, networkCallbackListener: NetworkCallbackListener, villageId: Int, stepId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                tolaDao.deleteTolaOffline(tolaId, TolaStatus.TOLA_DELETED.ordinal)
                val updatedTolaList = tolaDao.getAllTolasForVillage(prefRepo.getSelectedVillage().id)
                withContext(Dispatchers.Main) {
                    _tolaList.value = updatedTolaList
                }
                deleteDidisForTola(tolaId,isOnline)
                val stepDetails=stepsListDao.getStepForVillage(villageId, stepId)
                if (updatedTolaList.isEmpty()){
                    stepsListDao.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }.forEach { newStep ->
                        if (newStep.orderNumber == stepDetails.orderNumber) {
                            stepsListDao.markStepAsInProgress((stepDetails.orderNumber), StepStatus.INPROGRESS.ordinal, villageId)
                            stepsListDao.updateNeedToPost(stepDetails.id, villageId, true)
                        }
                        if (newStep.orderNumber > stepDetails.orderNumber) {
                            stepsListDao.markStepAsInProgress(
                                (newStep.orderNumber),
                                StepStatus.NOT_STARTED.ordinal,
                                villageId
                            )
                            stepsListDao.updateNeedToPost(newStep.id, villageId, true)
                        }
                    }
                }
                if (isOnline) {
                    val tolaToBeDeleted = tolaDao.fetchSingleTola(tolaId)
                    if (tolaToBeDeleted?.serverId != 0) {
                        val jsonArray = JsonArray()
                        jsonArray.add(
                            DeleteTolaRequest(
                                tolaId,
                                localModifiedDate = System.currentTimeMillis()
                            ).toJson()
                        )
                        val response = apiInterface.deleteCohort(jsonArray)
                        if (response.status.equals(SUCCESS)) {
                            tolaDao.removeTola(tolaId)
                        } else {
                            tolaDao.setNeedToPost(listOf(tolaId), true)
                            networkCallbackListener.onFailed()
                        }

                        if(!response.lastSyncTime.isNullOrEmpty()){
                            updateLastSyncTime(prefRepo,response.lastSyncTime)
                        }


                    }
                }
            } catch (ex: Exception) {
                onError("TransectWalkViewModel", "removeTola- ${ex.message}: \n${ex.stackTraceToString()}")
            }
        }
    }

    private fun deleteDidisForTola(tolaId: Int , isOnline: Boolean) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val didList = didiDao.getDidisForTola(tolaId)
                didiDao.deleteDidisForTola(
                    tolaId,
                    activeStatus = DidiStatus.DIID_DELETED.ordinal,
                    needsToPostDeleteStatus = true
                )
                if (isOnline) {
                    val jsonArray = JsonArray()
                    didList.forEach {
                        val jsonObject = JsonObject()
                        jsonObject.addProperty("id", it.id)
                        jsonObject.addProperty("localModifiedDate", System.currentTimeMillis())
                        jsonArray.add(jsonObject)
                    }
                    val deleteDidiApiRespone = apiInterface.deleteDidi(jsonArray)
                    if (deleteDidiApiRespone.status.equals(SUCCESS)) {
                        NudgeLogger.d("TransectWalkViewModel", "Didids Deleted Successfully")
                    } else {
                        NudgeLogger.d("TransectWalkViewModel", "Didids not Deleted Successfully")
                    }
                    if(!deleteDidiApiRespone.lastSyncTime.isNullOrEmpty()){
                        updateLastSyncTime(prefRepo,deleteDidiApiRespone.lastSyncTime)
                    }
                }
            }
            catch (ex: Exception) {
                onError(
                    "TransectWalkViewModel",
                    "deleteDidisForTola- ${ex.message}: \n${ex.stackTraceToString()}"
                )
            }
        }
    }

    fun updateTola(id: Int, newName: String, newLocation: LocationCoordinates?, isOnline: Boolean, networkCallbackListener: NetworkCallbackListener) {
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
                serverId = tolaList.value[getIndexOfTola(id)].serverId,
                localCreatedDate=tolaList.value[getIndexOfTola(id)].localCreatedDate,
                localModifiedDate=System.currentTimeMillis()
            )
            tolaDao.insert(updatedTola)
            didiDao.updateTolaName(id, newName)
            val updatedTolaList = tolaDao.getAllTolasForVillage(prefRepo.getSelectedVillage().id)
            withContext(Dispatchers.Main) {
                _tolaList.value = updatedTolaList
            }
            if (isOnline && updatedTola.serverId != 0) {
                val jsonTola = JsonArray()
                jsonTola.add(EditCohortRequest.getRequestObjectForTola(updatedTola).toJson())
                val response = apiInterface.editCohort(jsonTola)
                if (response.status.equals(SUCCESS)) {
                    tolaDao.updateNeedToPost(updatedTola.id, false)
                } else {
                    tolaDao.setNeedToPost(listOf(updatedTola.id), true)
                    NudgeLogger.d("updateTola: ", "update tola request failed: ${response.message}")
                    networkCallbackListener.onFailed()
                }
                if (!response.lastSyncTime.isNullOrEmpty()) {
                    updateLastSyncTime(prefRepo, response.lastSyncTime)
                }
            } else {
                tolaDao.updateNeedToPost(updatedTola.id, true)
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
            NudgeLogger.d("TransectWalkViewModel", "markTransectWalkComplete -> called")
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
            NudgeLogger.d("TransectWalkViewModel", "markStepAsCompleteOrInProgress -> markStepAsCompleteOrInProgress($stepId, StepStatus.COMPLETED.ordinal, $villageId)")

            stepsListDao.updateNeedToPost(stepId, villageId, true)
            val stepDetails = stepsListDao.getStepForVillage(villageId, stepId)
            if (stepDetails.orderNumber < stepsListDao.getAllSteps().size) {
                NudgeLogger.d("TransectWalkViewModel", "markStepAsCompleteOrInProgress -> stepDetails.orderNumber: ${stepDetails.orderNumber}")
                stepsListDao.markStepAsInProgress(
                    (stepDetails.orderNumber + 1),
                    StepStatus.INPROGRESS.ordinal,
                    villageId
                )
                NudgeLogger.d("TransectWalkViewModel", "markStepAsCompleteOrInProgress -> stepsListDao.markStepAsInProgress(${stepDetails.orderNumber + 1}, StepStatus.INPROGRESS.ordinal, $villageId)")
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
        isOnline: Boolean,
        networkCallbackListener: NetworkCallbackListener
    ) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            NudgeLogger.d("TransectWalkViewModel", "markTransectWalkIncomplete -> called")
            val step = stepsListDao.getStepForVillage(villageId, stepId)
            stepsListDao.markStepAsCompleteOrInProgress(
                stepId,
                StepStatus.INPROGRESS.ordinal,
                villageId
            )
            NudgeLogger.d("TransectWalkViewModel", "markTransectWalkIncomplete -> stepsListDao.markStepAsCompleteOrInProgress($stepId, StepStatus.INPROGRESS.ordinal, $villageId)")
            stepsListDao.updateNeedToPost(stepId, villageId, true)
            val completeStepList = stepsListDao.getAllCompleteStepsForVillage(villageId)
            completeStepList.let {
                it.forEach { newStep ->
                    if (newStep.orderNumber > step.orderNumber) {
                        stepsListDao.markStepAsCompleteOrInProgress(
                            newStep.id,
                            StepStatus.INPROGRESS.ordinal,
                            villageId
                        )
                        NudgeLogger.d("TransectWalkViewModel", "markTransectWalkIncomplete -> stepsListDao.markStepAsCompleteOrInProgress(${newStep.id}, StepStatus.INPROGRESS.ordinal, $villageId)")
                        stepsListDao.updateNeedToPost(newStep.id, villageId, true)
                    }
                }
            }
            if (isOnline) {
                val apiRequest = mutableListOf<EditWorkFlowRequest>()
                apiRequest.add(
                    EditWorkFlowRequest(
                        step.workFlowId,
                        StepStatus.INPROGRESS.name
                    )
                )
                completeStepList.let {
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
                        NudgeLogger.d("TransectWalkViewModel", "markTransectWalkIncomplete -> apiRequest: $apiRequest")
                        val response = apiInterface.editWorkFlow(apiRequest)
                        if (response.status.equals(SUCCESS)) {
                            response.data?.let { response ->
                                response.forEach {
                                    stepsListDao.updateWorkflowId(
                                        stepId,
                                        it.id,
                                        villageId,
                                        it.status
                                    )

                                }
                            }
                            stepsListDao.updateNeedToPost(stepId, villageId, false)
                        } else {
                            networkCallbackListener.onFailed()
                        }

                        if (!response.lastSyncTime.isNullOrEmpty()) {
                            updateLastSyncTime(prefRepo, response.lastSyncTime)
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
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList = stepsListDao.getAllStepsForVillage(villageId).sortedBy { it.orderNumber}
            val isComplete = stepList[stepList.map { it.orderNumber }.indexOf(5)].isComplete
            isVoEndorsementComplete.value = isComplete == StepStatus.COMPLETED.ordinal
        }
    }

    fun callWorkFlowAPI(
        villageId: Int,
        stepId: Int,
        networkCallbackListener: NetworkCallbackListener
    ) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                NudgeLogger.d("TransectWalkViewModel", "callWorkFlowAPI -> called")
                val dbResponse = stepsListDao.getStepForVillage(villageId, stepId)
                val stepList = stepsListDao.getAllStepsForVillage(villageId)
                if (dbResponse.workFlowId > 0) {
                    val response = apiInterface.editWorkFlow(
                        listOf(
                            EditWorkFlowRequest(dbResponse.workFlowId, StepStatus.COMPLETED.name)
                        )
                    )
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.let {
                            NudgeLogger.d("TransectWalkViewModel", "callWorkFlowAPI -> response = SUCCESS, data:  ${response.data}")
                            stepsListDao.updateWorkflowId(
                                stepId,
                                dbResponse.workFlowId,
                                villageId,
                                it[0].status
                            )
                        }
                        stepsListDao.updateNeedToPost(stepId, villageId, false)
                    } else {
                        NudgeLogger.d("TransectWalkViewModel", "callWorkFlowAPI -> response = FAIL")
                        networkCallbackListener.onFailed()
                        onError(tag = "ProgressScreenViewModel", "Error : ${response.message}")
                    }

                    if (!response.lastSyncTime.isNullOrEmpty()) {
                        updateLastSyncTime(prefRepo, response.lastSyncTime)
                    }
                }
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
                            NudgeLogger.d("TransectWalkViewModel", "callWorkFlowAPI -> inProgressStepResponse = FAIL")
                            if (inProgressStepResponse.status.equals(SUCCESS, true)) {
                                inProgressStepResponse.data?.let {
                                    NudgeLogger.d("TransectWalkViewModel", "callWorkFlowAPI -> inProgressStepResponse = SUCCESS, inProgressStepResponse.data = ${inProgressStepResponse.data}")
                                    stepsListDao.updateWorkflowId(
                                        step.id,
                                        step.workFlowId,
                                        villageId,
                                        it[0].status
                                    )
                                }
                                stepsListDao.updateNeedToPost(step.id, villageId, false)
                            }

                            if (!inProgressStepResponse.lastSyncTime.isNullOrEmpty()) {
                                updateLastSyncTime(prefRepo, inProgressStepResponse.lastSyncTime)
                            }
                        }
                    }
                } catch (ex: Exception) {
                    onCatchError(ex, ApiType.WORK_FLOW_API)
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
        NudgeLogger.e("TransectWalkViewModel", "onServerError -> $errorModel")
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
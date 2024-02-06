package com.patsurvey.nudge.activities.ui.transect_walk

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.patsurvey.nudge.MyApplication
import com.patsurvey.nudge.MyApplication.Companion.appScopeLaunch
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.settings.TransactionIdRequest
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.intefaces.LocalDbListener
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.request.AddCohortRequest
import com.patsurvey.nudge.model.request.DeleteTolaRequest
import com.patsurvey.nudge.model.request.EditCohortRequest
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.BPC_VERIFICATION_STEP_ORDER
import com.patsurvey.nudge.utils.CohortType
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.FORM_C
import com.patsurvey.nudge.utils.FORM_D
import com.patsurvey.nudge.utils.LocationCoordinates
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_FORM_PATH
import com.patsurvey.nudge.utils.PREF_TRANSECT_WALK_COMPLETION_DATE_
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.TOLA_COUNT
import com.patsurvey.nudge.utils.Tola
import com.patsurvey.nudge.utils.TolaStatus
import com.patsurvey.nudge.utils.VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_
import com.patsurvey.nudge.utils.getUniqueIdForEntity
import com.patsurvey.nudge.utils.longToString
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
    private val transectWalkRepository: TransectWalkRepository

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
            val isTolaExist = transectWalkRepository.getTolaExist(tola.name, transectWalkRepository.getSelectedVillage().id) > 0
            if (!isTolaExist) {
                val tolaItem = TolaEntity(
                    id = 0,
                    name = tola.name.trim(),
                    type = CohortType.TOLA.type,
                    latitude = tola.location.lat ?: 0.0,
                    longitude = tola.location.long ?: 0.0,
                    villageId = villageEntity.value?.id ?: 0,
                    status = 1,
                    localCreatedDate = System.currentTimeMillis(),
                    localModifiedDate = System.currentTimeMillis(),
                    transactionId = "",
                    needsToPost = true,
                    localUniqueId = getUniqueIdForEntity(MyApplication.applicationContext())
                )
                transectWalkRepository.tolaInsert(tolaItem)
                val addTolaEvent = transectWalkRepository.createEvent(tolaItem, EventName.ADD_TOLA, EventType.STATEFUL)
                addTolaEvent?.let { NudgeCore.getEventObserver()?.addEvent(it) }
                val updatedTolaList =
                    transectWalkRepository.getAllTolasForVillage(transectWalkRepository.getSelectedVillage().id)
                withContext(Dispatchers.Main) {
                    _tolaList.value = updatedTolaList
                    transectWalkRepository.savePref(TOLA_COUNT, _tolaList.value.size)
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
        return transectWalkRepository.getTolaExist(name,transectWalkRepository.getSelectedVillage().id) > 0
    }


    fun addEmptyTola() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaItem = TolaEntity.createEmptyTolaForVillageId(villageEntity.value?.id ?: 0)
            transectWalkRepository.tolaInsert(tolaItem)
            val addTolaEvent = transectWalkRepository.createEvent(tolaItem, EventName.ADD_TOLA, EventType.STATEFUL)
            addTolaEvent?.let { NudgeCore.getEventObserver()?.addEvent(it) }
            val updatedTolaList =
                transectWalkRepository.getAllTolasForVillage(transectWalkRepository.getSelectedVillage().id)
            withContext(Dispatchers.Main) {
//                prefRepo.savePref(TOLA_COUNT, tolaList.size)
                _tolaList.value = updatedTolaList
                if (isTransectWalkComplete.value) {
                    isTransectWalkComplete.value = false
                }
            }
        }
    }

    fun addTolasToNetwork(networkCallbackListener: NetworkCallbackListener) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            NudgeLogger.d("TransectWalkViewModel", "addTolasToNetwork: called")
            try {
                val jsonTola = JsonArray()
                val tolaList =  transectWalkRepository.fetchTolaNeedToPost(true,"",0)
//            val filteredTolaList = tolaList
                if (tolaList.isNotEmpty()) {
                    for (tola in tolaList) {
                        jsonTola.add(AddCohortRequest.getRequestObjectForTola(tola).toJson())
                    }
                    NudgeLogger.d("TransectWalkViewModel", "addTolasToNetwork: tolaList: $jsonTola")
                    val response = transectWalkRepository.addCohort(jsonTola)
                    NudgeLogger.d("TransectWalkViewModel", "addTolasToNetwork:  response: status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}")
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.let {
                            if(response.data[0].transactionId.isNullOrEmpty()) {
                                NudgeLogger.d("TransectWalkViewModel", "addTolasToNetwork: transactionId empty")
                                for(i in response.data.indices){
                                    val tola = tolaList[i]
                                    val tolaDataFromNetwork = response.data[i]
                                    val createdTime = tolaDataFromNetwork.createdDate
                                    val modifiedDate = tolaDataFromNetwork.modifiedDate
                                    NudgeLogger.d("TransectWalkViewModel", "addTolasToNetwork: updateTolaDetailAfterSync before for tola = $tola")
                                    transectWalkRepository.updateTolaDetailAfterSync(tola.id,tolaDataFromNetwork.id,
                                        false,
                                        "",
                                        createdTime,
                                        modifiedDate)
                                    NudgeLogger.d("TransectWalkViewModel", "addTolasToNetwork: updateTolaDetailAfterSync after for tola = $tola")
                                    tola.serverId = tolaDataFromNetwork.id
                                    tola.createdDate = tolaDataFromNetwork.createdDate
                                    tola.modifiedDate = tolaDataFromNetwork.modifiedDate
                                }
                                checkTolaAddStatus(networkCallbackListener)
                            } else {
                                NudgeLogger.d("TransectWalkViewModel", "addTolasToNetwork: transactionId not empty")
                                response.data.forEach { tola ->
                                    for(i in response.data.indices){
                                        response.data[i].transactionId.let { it1 ->
                                            NudgeLogger.d("TransectWalkViewModel", "addTolasToNetwork: updateTolaTransactionId before for tola: ${tolaList[i]}, transactionId: $it1")
                                            transectWalkRepository.updateTolaTransactionId(tolaList[i].id,
                                                it1
                                            )
                                            NudgeLogger.d("TransectWalkViewModel", "addTolasToNetwork: updateTolaTransactionId after for tola: ${tolaList[i]}, transactionId: $it1")

                                        }
                                    }
                                }
                                isPending = 1
                                startSyncTimerForStatus(networkCallbackListener)
                            }
                        }
                    } else {
                        NudgeLogger.d("TransectWalkScreen", "addTolasToNetwork -> onFailed")
                        networkCallbackListener.onFailed()
                    }
                    if(!response.lastSyncTime.isNullOrEmpty()){
                       transectWalkRepository.updateLastSyncTime(response.lastSyncTime)
                    }
                } else {
                    checkTolaAddStatus(networkCallbackListener)
                }
            }  catch (ex: Exception) {
                networkCallbackListener.onFailed()
                NudgeLogger.d("TransectWalkScreen", "addTolasToNetwork -> onFailed")
                onError(tag = "TransectWalkViewModel", "addTolasToNetwork -> Error : ${ex.localizedMessage}")
                onCatchError(ex, ApiType.TOLA_ADD_API)
            }

        }
    }

    private fun startSyncTimerForStatus(networkCallbackListener: NetworkCallbackListener) {
        NudgeLogger.d("TransectWalkViewModel","startSyncTimerForStatus: called")
        val timer = Timer()
        timer.schedule(object : TimerTask(){
            override fun run() {

                when(isPending){
                    1 -> {
                        NudgeLogger.d("TransectWalkViewModel","startSyncTimerForStatus: isPending: $isPending")
                        checkTolaAddStatus(networkCallbackListener)
                    }
                    2 -> {
                        NudgeLogger.d("TransectWalkViewModel","startSyncTimerForStatus: isPending: $isPending")
                        checkTolaDeleteStatus(networkCallbackListener)
                    }
                    3 -> {
                        NudgeLogger.d("TransectWalkViewModel","startSyncTimerForStatus: isPending: $isPending")
                        checkTolaUpdateStatus(networkCallbackListener)
                    }
                }
            }
        },10000)
    }

    private fun checkTolaAddStatus(networkCallbackListener: NetworkCallbackListener) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            NudgeLogger.d("TransectWalkScreen", "checkTolaAddStatus called")
            try {
                val tolaList = transectWalkRepository.fetchPendingTola(true,"")
                if(tolaList.isNotEmpty()) {
                    val ids: ArrayList<String> = arrayListOf()
                    tolaList.forEach { tola ->
                        tola.transactionId?.let { ids.add(it) }
                    }
                    NudgeLogger.d("TransectWalkScreen", "checkTolaAddStatus tolaList: $tolaList, size: ${tolaList.size}")
                    val response = transectWalkRepository.getPendingStatus(TransactionIdRequest("",ids))
                    NudgeLogger.d("TransectWalkScreen", "checkTolaAddStatus  response: status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}")
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.forEach { transactionIdResponse ->
                            tolaList.forEach { tola ->
                                if (transactionIdResponse.transactionId == tola.transactionId) {
                                    tola.serverId = transactionIdResponse.referenceId
                                }
                            }
                        }
                        for(tola in tolaList) {
                            NudgeLogger.d("TransectWalkScreen", "checkTolaAddStatus ->  updateTolaDetailAfterSync: before for tola: $tola")
                            transectWalkRepository.updateTolaDetailAfterSync(
                                id = tola.id,
                                serverId = tola.serverId,
                                needsToPost = false,
                                transactionId = "",
                                createdDate = tola.createdDate?:0L,
                                modifiedDate = tola.modifiedDate?:0L
                            )
                            NudgeLogger.d("TransectWalkScreen", "checkTolaAddStatus ->  updateTolaDetailAfterSync: after for tola: $tola")
                        }
                        deleteTolaToNetwork(networkCallbackListener)
                    } else {
                        NudgeLogger.d("TransectWalkScreen", "checkTolaAddStatus onFailed")
                        networkCallbackListener.onFailed()
                    }
                    if(!response.lastSyncTime.isNullOrEmpty()){
                        transectWalkRepository.updateLastSyncTime(response.lastSyncTime)
                    }

                } else {
                    deleteTolaToNetwork(networkCallbackListener)
                }
            } catch (ex: Exception) {
                networkCallbackListener.onFailed()
                NudgeLogger.d("TransectWalkScreen", "checkTolaAddStatus -> onFailed")
                onError(tag = "TransectWalkViewModel", "checkTolaAddStatus -> Error : ${ex.localizedMessage}")
                onCatchError(ex, ApiType.STATUS_CALL_BACK_API)
            }
        }
    }

    private fun deleteTolaToNetwork(networkCallbackListener: NetworkCallbackListener) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            NudgeLogger.d("TransectWalkViewModel","deleteTolaToNetwork called")
            try {
                val tolaList = transectWalkRepository.fetchAllTolaNeedToDelete(TolaStatus.TOLA_DELETED.ordinal)
                val jsonTola = JsonArray()
                if (tolaList.isNotEmpty()) {
                    for (tola in tolaList) {
                        jsonTola.add(DeleteTolaRequest(tola.serverId, localModifiedDate = System.currentTimeMillis()).toJson())
                    }
                    NudgeLogger.d("TransectWalkViewModel","deleteTolaToNetwork: tola need to post: $tolaList, size: ${tolaList.size}")
                    val response = transectWalkRepository.deleteCohort(jsonTola)
                    NudgeLogger.d("TransectWalkViewModel","deleteTolaToNetwork:  response: status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}")
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.let {
                            if((response.data[0]?.transactionId.isNullOrEmpty())) {
                                NudgeLogger.d("TransectWalkViewModel","deleteTolaToNetwork:  transactionId is empty")
                                tolaList.forEach { tola ->
                                    NudgeLogger.d("TransectWalkViewModel","deleteTolaToNetwork:  tolaDao.deleteTola before for tola: $tola")
                                    transectWalkRepository.deleteTola(tola.id)
                                    NudgeLogger.d("TransectWalkViewModel","deleteTolaToNetwork:  tolaDao.deleteTola after for tola: $tola")
                                }

                                checkTolaDeleteStatus(networkCallbackListener)
                            } else {
                                NudgeLogger.d("TransectWalkViewModel","deleteTolaToNetwork:  transactionId is not empty")
                                for (i in 0 until response.data.size){
                                    tolaList[i].transactionId = response.data[i]?.transactionId
                                    tolaList[i].transactionId?.let { it1 ->
                                        NudgeLogger.d("TransectWalkViewModel","deleteTolaToNetwork:  tolaDao.updateTolaTransactionId before for tola: ${tolaList[i]}, transactionId: $it1")
                                        transectWalkRepository.updateTolaTransactionId(tolaList[i].id,
                                            it1
                                        )
                                        NudgeLogger.d("TransectWalkViewModel","deleteTolaToNetwork:  tolaDao.updateTolaTransactionId after for tola: ${tolaList[i]}, transactionId: $it1")
                                    }
                                }
                                isPending = 2
                                startSyncTimerForStatus(networkCallbackListener)
                            }
                        }
                    } else {
                        NudgeLogger.d("TransectWalkViewModel","deleteTolaToNetwork: onFailed")
                        networkCallbackListener.onFailed()
                    }

                    if(!response.lastSyncTime.isNullOrEmpty()){
                        transectWalkRepository.updateLastSyncTime(response.lastSyncTime)
                    }

                } else {
                    checkTolaDeleteStatus(networkCallbackListener)
                }
            } catch (ex: Exception) {
                NudgeLogger.d("TransectWalkViewModel","deleteTolaToNetwork: onFailed")
                networkCallbackListener.onFailed()
                onError(tag = "TransectWalkViewModel", "deleteTolaToNetwork -> Error : ${ex.localizedMessage}")
                onCatchError(ex, ApiType.TOLA_DELETE_API)
            }
        }
    }

    private fun updateTolasToNetwork(networkCallbackListener: NetworkCallbackListener) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            NudgeLogger.d("TransectWalkViewModel","updateTolasToNetwork: called")
            try {
                val tolaList = transectWalkRepository.fetchAllTolaNeedToUpdate(true,"",0)
                val jsonTola = JsonArray()
                if (tolaList.isNotEmpty()) {
                    for (tola in tolaList) {
                        jsonTola.add(EditCohortRequest.getRequestObjectForTola(tola).toJson())
                    }
                    NudgeLogger.d("TransectWalkViewModel","updateTolasToNetwork: tolaList: $tolaList, size: ${tolaList.size}")
                    val response = transectWalkRepository.editCohort(jsonTola)
                    NudgeLogger.d("TransectWalkViewModel","updateTolasToNetwork:  response: status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}")
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.let {
                            if((response.data[0].transactionId.isNullOrEmpty())) {
                                NudgeLogger.d("TransectWalkViewModel","updateTolasToNetwork: transactionId empty")
                                tolaList.forEach { tola ->
                                    NudgeLogger.d("TransectWalkViewModel","updateTolasToNetwork: tolaDao.updateNeedToPost before for tola: $tola")
                                    transectWalkRepository.updateNeedToPost(tola.id,false)
                                    NudgeLogger.d("TransectWalkViewModel","updateTolasToNetwork: tolaDao.updateNeedToPost after for tola: $tola")

                                }
                                checkTolaUpdateStatus(networkCallbackListener)
                            } else {
                                NudgeLogger.d("TransectWalkViewModel","updateTolasToNetwork: transactionId not empty")
                                for (i in 0 until response.data.size){
                                    tolaList[i].transactionId = response.data[i].transactionId
                                    tolaList[i].transactionId?.let { it1 ->
                                        NudgeLogger.d("TransectWalkViewModel","updateTolasToNetwork:  tolaDao.updateTolaTransactionId before for tola: ${tolaList[i]}, transactionId: $it1")
                                        transectWalkRepository.updateTolaTransactionId(tolaList[i].id,
                                            it1
                                        )
                                        NudgeLogger.d("TransectWalkViewModel","updateTolasToNetwork:  tolaDao.updateTolaTransactionId after for tola: ${tolaList[i]}, transactionId: $it1")
                                    }
                                }
                                isPending = 3
                                startSyncTimerForStatus(networkCallbackListener)
                            }
                        }
                    } else {
                        networkCallbackListener.onFailed()
                    }
                    if(!response.lastSyncTime.isNullOrEmpty()){
                        transectWalkRepository.updateLastSyncTime(response.lastSyncTime)
                    }
                } else {
                    checkTolaUpdateStatus(networkCallbackListener)
                }
            } catch (ex: Exception) {
                NudgeLogger.d("TransectWalkViewModel","updateTolasToNetwork: onFailed")
                networkCallbackListener.onFailed()
                onError(tag = "TransectWalkViewModel", "updateTolasToNetwork -> Error : ${ex.localizedMessage}")
                onCatchError(ex, ApiType.TOLA_EDIT_API)
            }
        }
    }

    fun checkTolaUpdateStatus(networkCallbackListener: NetworkCallbackListener){
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            NudgeLogger.d("TransectWalkViewModel","checkTolaUpdateStatus: called")
            try {
                val tolaList = transectWalkRepository.fetchAllPendingTolaNeedToUpdate(true,"")
                if(tolaList.isNotEmpty()) {
                    val ids: ArrayList<String> = arrayListOf()
                    tolaList.forEach { tola ->
                        tola.transactionId?.let { ids.add(it) }
                    }
                    NudgeLogger.d("TransectWalkViewModel","checkTolaUpdateStatus: tolaList: $tolaList, size: ${tolaList.size}")
                    val response = transectWalkRepository.getPendingStatus(TransactionIdRequest("",ids))
                    NudgeLogger.d("TransectWalkViewModel","checkTolaUpdateStatus:  response: status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}")
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.forEach { transactionIdResponse ->
                            tolaList.forEach { tola ->
                                if (transactionIdResponse.transactionId == tola.transactionId) {
                                    NudgeLogger.d("TransectWalkViewModel","checkTolaUpdateStatus: tolaDao.updateNeedToPost before for tola: $tola")
                                    transectWalkRepository.updateNeedToPost(tola.id,false)
                                    NudgeLogger.d("TransectWalkViewModel","checkTolaUpdateStatus: tolaDao.updateNeedToPost after for tola: $tola")
                                    NudgeLogger.d("TransectWalkViewModel","checkTolaUpdateStatus: tolaDao.updateTolaTransactionId before for tola: $tola")
                                    transectWalkRepository.updateTolaTransactionId(tola.id,"")
                                    NudgeLogger.d("TransectWalkViewModel","checkTolaUpdateStatus: tolaDao.updateTolaTransactionId after for tola: $tola")
                                }
                            }
                        }
                        NudgeLogger.d("TransectWalkViewModel","checkTolaUpdateStatus: onSuccess")
                        networkCallbackListener.onSuccess()
                    } else {
                        NudgeLogger.d("TransectWalkViewModel","checkTolaUpdateStatus: onFailed")
                        networkCallbackListener.onFailed()
                    }
                    if(!response.lastSyncTime.isNullOrEmpty()){
                        transectWalkRepository.updateLastSyncTime(response.lastSyncTime)
                    }
                } else {
                    NudgeLogger.d("TransectWalkViewModel","checkTolaUpdateStatus: onSuccess")
                    networkCallbackListener.onSuccess()
                }
            } catch (ex: Exception) {
                NudgeLogger.d("TransectWalkViewModel","checkTolaUpdateStatus: onFailed")
                networkCallbackListener.onFailed()
                onError(tag = "TransectWalkViewModel", "checkTolaUpdateStatus -> Error : ${ex.localizedMessage}")
                onCatchError(ex, ApiType.STATUS_CALL_BACK_API)
            }
        }
    }

    fun checkTolaDeleteStatus(networkCallbackListener: NetworkCallbackListener) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            NudgeLogger.d("TransectWalkViewModel","checkTolaDeleteStatus: called")
            try {
                val tolaList = transectWalkRepository.fetchAllPendingTolaNeedToDelete(TolaStatus.TOLA_DELETED.ordinal,"")
                if(tolaList.isNotEmpty()) {
                    val ids: ArrayList<String> = arrayListOf()
                    tolaList.forEach { tola ->
                        tola.transactionId?.let { ids.add(it) }
                    }
                    NudgeLogger.d("TransectWalkViewModel","checkTolaDeleteStatus: tolaList: $tolaList, size: ${tolaList.size}")
                    val response = transectWalkRepository.getPendingStatus(TransactionIdRequest("",ids))
                    NudgeLogger.d("TransectWalkViewModel","checkTolaDeleteStatus:  response: status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}")
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.forEach { transactionIdResponse ->
                            tolaList.forEach { tola ->
                                if (transactionIdResponse.transactionId == tola.transactionId) {
                                    NudgeLogger.d("TransectWalkViewModel","checkTolaDeleteStatus: tolaDao.deleteTola before for tola: $tola")
                                    transectWalkRepository.deleteTola(tola.id)
                                    NudgeLogger.d("TransectWalkViewModel","checkTolaDeleteStatus: tolaDao.deleteTola after for tola: $tola")

                                }
                            }
                        }
                        updateTolasToNetwork(networkCallbackListener)
                    } else {
                        NudgeLogger.d("TransectWalkViewModel","checkTolaDeleteStatus: onFailed")
                        networkCallbackListener.onFailed()
                    }
                    if(!response.lastSyncTime.isNullOrEmpty()){
                        transectWalkRepository.updateLastSyncTime(response.lastSyncTime)
                    }

                } else {
                    updateTolasToNetwork(networkCallbackListener)
                }
            } catch (ex: Exception) {
                NudgeLogger.d("TransectWalkViewModel","checkTolaDeleteStatus: onFailed")
                networkCallbackListener.onFailed()
                onError(tag = "TransectWalkViewModel", "checkTolaDeleteStatus -> Error : ${ex.localizedMessage}")
                onCatchError(ex, ApiType.TOLA_DELETE_API)
            }
        }
    }


    fun removeTola(tolaId: Int, context: Context, isOnline: Boolean, networkCallbackListener: NetworkCallbackListener, villageId: Int, stepId: Int) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            try {
                val localTola = transectWalkRepository.getTola(tolaId)
                val didiListForTola = transectWalkRepository.getDidisForTola(if (localTola.serverId == 0) localTola.id else localTola.serverId)
                if (didiListForTola.isEmpty()) {
                    transectWalkRepository.deleteTolaOffline(
                        tolaId,
                        TolaStatus.TOLA_DELETED.ordinal
                    )

                    val deleteTolaEvent = transectWalkRepository.createEvent(
                        localTola,
                        EventName.DELETE_TOLA,
                        EventType.STATEFUL
                    )
                    deleteTolaEvent?.let { NudgeCore.getEventObserver()?.addEvent(it) }

                    val updatedTolaList = transectWalkRepository.getAllTolasForVillage(transectWalkRepository.getSelectedVillage().id)
                    withContext(Dispatchers.Main) {
                        _tolaList.value = updatedTolaList
                    }
                    deleteDidisForTola(if (localTola.serverId == 0) localTola.id else localTola.serverId, isOnline)
                    val stepDetails=transectWalkRepository.getStepForVillage(villageId, stepId)
                    if (updatedTolaList.isEmpty()){
                        transectWalkRepository.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }.forEach { newStep ->
                            if (newStep.orderNumber == stepDetails.orderNumber) {
                                transectWalkRepository.markStepAsInProgress((stepDetails.orderNumber), StepStatus.INPROGRESS.ordinal, villageId)
                                transectWalkRepository.updateNeedToPost(stepDetails.id, villageId, true)
                            }
                            if (newStep.orderNumber > stepDetails.orderNumber) {
                                transectWalkRepository.markStepAsInProgress(
                                    (newStep.orderNumber),
                                    StepStatus.NOT_STARTED.ordinal,
                                    villageId
                                )
                                transectWalkRepository.updateNeedToPost(newStep.id, villageId, true)
                            }
                        }
                    }
                    if (isOnline) {
                        val tolaToBeDeleted = transectWalkRepository.fetchSingleTola(tolaId)
                        if (tolaToBeDeleted?.serverId != 0) {
                            val jsonArray = JsonArray()
                            jsonArray.add(
                                DeleteTolaRequest(
                                    tolaId,
                                    localModifiedDate = System.currentTimeMillis()
                                ).toJson()
                            )
                            val response = transectWalkRepository.deleteCohort(jsonArray)
                            if (response.status.equals(SUCCESS)) {
                                transectWalkRepository.removeTola(tolaId)
                            } else {
                                transectWalkRepository.setNeedToPost(listOf(tolaId), true)
                                networkCallbackListener.onFailed()
                            }

                            if(!response.lastSyncTime.isNullOrEmpty()){
                                transectWalkRepository.updateLastSyncTime(response.lastSyncTime)
                            }


                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.cannot_delete_tola_message_text),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (ex: Exception) {
                onError("TransectWalkViewModel", "removeTola- ${ex.message}: \n${ex.stackTraceToString()}")
            }
        }
    }

    private fun deleteDidisForTola(tolaId: Int , isOnline: Boolean) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            try {
                val didList = transectWalkRepository.getDidisForTola(tolaId)
                transectWalkRepository.deleteDidisForTola(
                    tolaId,
                    activeStatus = DidiStatus.DIID_DELETED.ordinal,
                    needsToPostDeleteStatus = true
                )
                didList.forEach {
                    // TODO Handle tola deletion for beneficiaries assigned to that tola.
//                    transectWalkRepository.createEvent()
                }
                if (isOnline) {
                    val jsonArray = JsonArray()
                    didList.forEach {
                        val jsonObject = JsonObject()
                        jsonObject.addProperty("id", it.id)
                        jsonObject.addProperty("localModifiedDate", System.currentTimeMillis())
                        jsonArray.add(jsonObject)
                    }
                    val deleteDidiApiRespone = transectWalkRepository.deleteDidi(jsonArray)
                    if (deleteDidiApiRespone.status.equals(SUCCESS)) {
                        NudgeLogger.d("TransectWalkViewModel", "Didids Deleted Successfully")
                    } else {
                        NudgeLogger.d("TransectWalkViewModel", "Didids not Deleted Successfully")
                    }
                    if(!deleteDidiApiRespone.lastSyncTime.isNullOrEmpty()){
                        transectWalkRepository.updateLastSyncTime(deleteDidiApiRespone.lastSyncTime)
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
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            val updatedTola = TolaEntity(
                id = id,
                name = newName.trim(),
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
                localModifiedDate=System.currentTimeMillis(),
                localUniqueId = getUniqueIdForEntity(MyApplication.applicationContext())
            )
//            transectWalkRepository.tolaInsert(updatedTola)
            transectWalkRepository.updateTolaName(id, newName)
            // TODO Move to repository
            val updatedTolaEvent = transectWalkRepository.createEvent(
                updatedTola,
                EventName.UPDATE_TOLA,
                EventType.STATEFUL
            )
            // TODO handle empty event case.

            updatedTolaEvent?.let { NudgeCore.getEventObserver()?.addEvent(it) }
//            NudgeCore.getEventObserver()?.syncPendingEvent(NudgeCore.getAppContext())
            val updatedTolaList = transectWalkRepository.getAllTolasForVillage(transectWalkRepository.getSelectedVillage().id)
            withContext(Dispatchers.Main) {
                _tolaList.value = updatedTolaList
            }
            if (isOnline && updatedTola.serverId != 0) {
                val jsonTola = JsonArray()
                jsonTola.add(EditCohortRequest.getRequestObjectForTola(updatedTola).toJson())
                val response = transectWalkRepository.editCohort(jsonTola)
                NudgeLogger.d("TransectWalkViewModel", "updateTola -> response: status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}")
                if (response.status.equals(SUCCESS)) {
                    transectWalkRepository.updateNeedToPost(updatedTola.id, false)
                } else {
                    transectWalkRepository.setNeedToPost(listOf(updatedTola.id), true)
                    NudgeLogger.d("updateTola: ", "update tola request failed: ${response.message}")
                    networkCallbackListener.onFailed()
                }
                if (!response.lastSyncTime.isNullOrEmpty()) {
                    transectWalkRepository.updateLastSyncTime( response.lastSyncTime)
                }
            } else {
                transectWalkRepository.updateNeedToPost(updatedTola.id, true)
            }

        }
    }

    fun fetchTolaList(villageId: Int){
        showLoader.value = true
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            try {
                _tolaList.emit(transectWalkRepository.getAllTolasForVillage(villageId))
                showLoader.value = false
            }catch (ex:Exception){
                onError(tag = "TransectWalkViewModel", "Exception: ${ex.localizedMessage}")
                showLoader.value = false
            }
        }
    }

    fun setVillage(villageId: Int) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            var village = transectWalkRepository.fetchVillageDetailsForLanguage(villageId, transectWalkRepository.getAppLanguageId() ?: 2) ?: transectWalkRepository.getVillage(villageId)
            withContext(Dispatchers.Main) {
                villageEntity.value = village
            }
        }
    }

    private fun getIndexOfTola(id: Int): Int {
        return tolaList.value.map { it.id }.indexOf(id)
    }

    fun markTransectWalkComplete(villageId: Int, stepId: Int) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            NudgeLogger.d("TransectWalkViewModel", "markTransectWalkComplete -> called")
            val existingList = transectWalkRepository.getVillage(villageId).steps_completed
            val updatedCompletedStepsList = mutableListOf<Int>()
            if (!existingList.isNullOrEmpty()) {
                existingList.forEach {
                    updatedCompletedStepsList.add(it)
                }
            }
            updatedCompletedStepsList.add(stepId)
            transectWalkRepository.updateLastCompleteStep(villageId, updatedCompletedStepsList)

            transectWalkRepository.markStepAsCompleteOrInProgress(
                stepId,
                StepStatus.COMPLETED.ordinal,
                villageId
            )
            NudgeLogger.d("TransectWalkViewModel", "markStepAsCompleteOrInProgress -> stepsListDao.markStepAsCompleteOrInProgress($stepId, StepStatus.COMPLETED.ordinal, $villageId)")
            transectWalkRepository.updateNeedToPost(stepId, villageId, true)
            val stepList = transectWalkRepository.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }
            val currentStep = stepList[stepList.map { it.orderNumber }.indexOf(1)]
            if (currentStep.orderNumber < stepList.size && currentStep.orderNumber > 1) {
                NudgeLogger.d("TransectWalkViewModel", "markStepAsCompleteOrInProgress ->currentStep: $currentStep")
                val nextStepId = (stepList[stepList.map { it.orderNumber }.indexOf(2)].id)
                transectWalkRepository.markStepAsInProgress(
                    nextStepId,
                    StepStatus.INPROGRESS.ordinal,
                    villageId
                )
                NudgeLogger.d("TransectWalkViewModel", "markStepAsCompleteOrInProgress -> stepsListDao.markStepAsInProgress($nextStepId, StepStatus.INPROGRESS.ordinal, $villageId)")
                transectWalkRepository.savePref("$VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_${villageId}", false)
                for (i in 1..5) {
                    transectWalkRepository.savePref(getFormPathKey(getFormSubPath(FORM_C, i)), "")
                    transectWalkRepository.savePref(getFormPathKey(getFormSubPath(FORM_D, i)), "")
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
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            NudgeLogger.d("TransectWalkViewModel", "markTransectWalkIncomplete -> called")
            val stepList = transectWalkRepository.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }
            val transectWalkStep = stepList[stepList.map { it.orderNumber }.indexOf(1)]
            transectWalkRepository.markStepAsCompleteOrInProgress(
                stepId = transectWalkStep.id,
                isComplete = StepStatus.INPROGRESS.ordinal,
                villageId = villageId
            )
            NudgeLogger.d("TransectWalkViewModel", "markTransectWalkIncomplete -> stepsListDao.markStepAsCompleteOrInProgress($stepId, StepStatus.INPROGRESS.ordinal, $villageId)")

            if (transectWalkStep.isComplete == StepStatus.COMPLETED.ordinal)
                saveWorkflowEventIntoDb(stepStatus = StepStatus.INPROGRESS, villageId = villageId, stepId = stepId)

            transectWalkRepository.updateNeedToPost(stepId, villageId, true)
            val completeStepList = transectWalkRepository.getAllCompleteStepsForVillage(villageId)
            completeStepList.let {
                it.forEach { newStep ->
                    if (newStep.orderNumber > stepList[stepList.map { it.orderNumber }
                            .indexOf(1)].orderNumber && newStep.orderNumber < BPC_VERIFICATION_STEP_ORDER) {
                        transectWalkRepository.markStepAsCompleteOrInProgress(
                            newStep.id,
                            StepStatus.INPROGRESS.ordinal,
                            villageId
                        )
                        NudgeLogger.d(
                            "TransectWalkViewModel",
                            "markTransectWalkIncomplete -> stepsListDao.markStepAsCompleteOrInProgress(${newStep.id}, StepStatus.INPROGRESS.ordinal, $villageId)"
                        )
                        saveWorkflowEventIntoDb(stepStatus = StepStatus.INPROGRESS, villageId = villageId, stepId = newStep.id)
                        transectWalkRepository.updateNeedToPost(newStep.id, villageId, true)
                    }
                }
            }
            try {
                if (isOnline) {
                    val apiRequest = mutableListOf<EditWorkFlowRequest>()
                    apiRequest.add(
                        EditWorkFlowRequest(
                            stepList[stepList.map { it.orderNumber }.indexOf(1)].workFlowId,
                            StepStatus.INPROGRESS.name
                        )
                    )
                    completeStepList.let { it ->
                        it.forEach { newStep ->
                            if (newStep.orderNumber > stepList[stepList.map { it.orderNumber }
                                    .indexOf(1)].orderNumber && newStep.orderNumber < BPC_VERIFICATION_STEP_ORDER) {
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
                            val response = transectWalkRepository.editWorkFlow(apiRequest)
                            NudgeLogger.d("TransectWalkViewModel", "markTransectWalkIncomplete -> response: status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}")
                            if (response.status.equals(SUCCESS)) {
                                response.data?.let { response ->
                                    response.forEach { it ->
                                        transectWalkRepository.updateWorkflowId(
                                            stepId = it.programsProcessId,
                                            workflowId = it.id,
                                            villageId = villageId,
                                            status = it.status
                                        )
                                        transectWalkRepository.updateNeedToPost(it.programsProcessId, villageId, false)
                                    }
                                    NudgeLogger.d("TransectWalkViewModel", "markTransectWalkIncomplete -> onSuccess")
                                    networkCallbackListener.onSuccess()
                                }
                            } else {
                                NudgeLogger.d("TransectWalkViewModel", "markTransectWalkIncomplete -> onFailed")
                                networkCallbackListener.onFailed()
                            }

                            if (!response.lastSyncTime.isNullOrEmpty()) {
                               transectWalkRepository.updateLastSyncTime( response.lastSyncTime)
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                NudgeLogger.d("TransectWalkViewModel", "markTransectWalkIncomplete -> onFailed")
                networkCallbackListener.onFailed()
                onCatchError(ex, ApiType.WORK_FLOW_API)
            }
        }
    }

    fun isTransectWalkComplete(stepId: Int, villageId: Int) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            val isComplete = transectWalkRepository.isStepComplete(
                stepId,
                villageId = villageId
            ) == StepStatus.COMPLETED.ordinal
            withContext(Dispatchers.Main) {
                isTransectWalkComplete.value = isComplete
            }
        }
    }

    fun isVoEndorsementCompleteForVillage(villageId: Int) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            val stepList = transectWalkRepository.getAllStepsForVillage(villageId).sortedBy { it.orderNumber}
            val isComplete = stepList[stepList.map { it.orderNumber }.indexOf(5)].isComplete
            isVoEndorsementComplete.value = isComplete == StepStatus.COMPLETED.ordinal
        }
    }

    fun saveTransectWalkCompletionDate() {
        val currentTime = System.currentTimeMillis()
        transectWalkRepository.savePref(PREF_TRANSECT_WALK_COMPLETION_DATE_ +transectWalkRepository.getSelectedVillage().id, currentTime)
    }
    fun callWorkFlowAPI(
        villageId: Int,
        stepId: Int,
        networkCallbackListener: NetworkCallbackListener
    ) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            NudgeLogger.d("TransectWalkViewModel", "callWorkFlowAPI -> called")
            try {
                val dbResponse = transectWalkRepository.getStepForVillage(villageId, stepId)
                NudgeLogger.d("TransectWalkViewModel", "callWorkFlowAPI -> dbResponse = $dbResponse")
                val stepList = transectWalkRepository.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }
                NudgeLogger.d("TransectWalkViewModel", "callWorkFlowAPI -> stepList = $stepList")
                if (dbResponse.workFlowId > 0) {
                    val primaryWorkFlowRequest = listOf(EditWorkFlowRequest(stepList[stepList.map { it.orderNumber }.indexOf(1)].workFlowId
                        , StepStatus.COMPLETED.name, longToString(transectWalkRepository.getPref(
                            PREF_TRANSECT_WALK_COMPLETION_DATE_+transectWalkRepository.getSelectedVillage().id,System.currentTimeMillis()))
                    ))
                    NudgeLogger.d("TransectWalkViewModel", "callWorkFlowAPI -> primaryWorkFlowRequest = $primaryWorkFlowRequest")
                    val response = transectWalkRepository.editWorkFlow(primaryWorkFlowRequest)
                    NudgeLogger.d("TransectWalkViewModel", "callWorkFlowAPI -> response: status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}")
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.let {
                            transectWalkRepository.updateWorkflowId(
                                stepId = stepList[stepList.map { it.orderNumber }.indexOf(1)].id,
                                workflowId = stepList[stepList.map { it.orderNumber }.indexOf(1)].workFlowId,
                                villageId = villageId,
                                status = it[0].status
                            )
                        }
                        transectWalkRepository.updateNeedToPost(stepList[stepList.map { it.orderNumber }.indexOf(1)].id, villageId, false)
                    } else {
                        networkCallbackListener.onFailed()
                        onError(tag = "TransectWalkViewModel", "Error : ${response.message}")
                    }
                    if (!response.lastSyncTime.isNullOrEmpty()) {
                        transectWalkRepository.updateLastSyncTime(response.lastSyncTime)
                    }
                }
                try {
                    NudgeLogger.d("TransectWalkViewModel", "callWorkFlowAPI -> second try = called")
                    stepList.forEach { step ->
                        NudgeLogger.d("TransectWalkViewModel", "callWorkFlowAPI -> step = $step" +
                                "step.orderNumber > 1 && step.workFlowId > 0: " +
                                "${step.orderNumber > 1} && ${step.workFlowId > 0}")
                        if (step.orderNumber > 1 &&  step.workFlowId > 0) {
                            val inProgressStepRequest = listOf(EditWorkFlowRequest(step.workFlowId, StepStatus.INPROGRESS.name))
                            NudgeLogger.d("TransectWalkViewModel", "callWorkFlowAPI -> inProgressStepRequest = $inProgressStepRequest")
                            val inProgressStepResponse = transectWalkRepository.editWorkFlow(inProgressStepRequest)
                            NudgeLogger.d("TransectWalkViewModel", "callWorkFlowAPI -> inProgressStepResponse: status = ${inProgressStepResponse.status}, message = ${inProgressStepResponse.message}, data = ${inProgressStepResponse.data.toString()}")
                            if (inProgressStepResponse.status.equals(SUCCESS, true)) {
                                inProgressStepResponse.data?.let {
                                    transectWalkRepository.updateWorkflowId(
                                        stepId = it[0].programsProcessId,
                                        workflowId = it[0].id,
                                        villageId = villageId,
                                        status = it[0].status
                                    )
                                }
                                transectWalkRepository.updateNeedToPost(step.id, villageId, false)
                            } else {
                                NudgeLogger.d("TransectWalkViewModel", "callWorkFlowAPI -> inProgressStepResponse = FAIL")
                                networkCallbackListener.onFailed()
                            }

                            if (!inProgressStepResponse.lastSyncTime.isNullOrEmpty()) {
                                transectWalkRepository.updateLastSyncTime( inProgressStepResponse.lastSyncTime)
                            }
                        }
                    }
                } catch (ex: Exception) {
                    NudgeLogger.d("TransectWalkViewModel", "callWorkFlowAPI -> second try- onFailed()")
                    networkCallbackListener.onFailed()
                    onCatchError(ex, ApiType.WORK_FLOW_API)
                }
            } catch (ex: Exception) {
                NudgeLogger.d("TransectWalkViewModel", "callWorkFlowAPI -> onFailed()")
                networkCallbackListener.onFailed()
                onError(tag = "TransectWalkViewModel", "callWorkFlowAPI -> Error : ${ex.localizedMessage}")
                onCatchError(ex, ApiType.WORK_FLOW_API)
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
        return "${PREF_FORM_PATH}_${transectWalkRepository.getSelectedVillage().id}_${subPath}"
    }

    fun getFormSubPath(formName: String, pageNumber: Int): String {
        return "${formName}_page_$pageNumber"
    }
    fun getSelectedVillage(): VillageEntity {
        return transectWalkRepository.getSelectedVillage()
    }

    fun saveWorkflowEventIntoDb(stepStatus: StepStatus, villageId: Int, stepId: Int) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepEntity =
                transectWalkRepository.getStepForVillage(villageId = villageId, stepId = stepId)
            val updateWorkflowEvent = transectWalkRepository.createWorkflowEvent(
                eventItem = stepEntity,
                stepStatus = stepStatus,
                eventName = EventName.WORKFLOW_STATUS_UPDATE,
                eventType = EventType.STATEFUL,
                prefRepo = transectWalkRepository.prefRepo
            )
            updateWorkflowEvent?.let { event ->
                transectWalkRepository.insertEventIntoDb(event, emptyList())
            }
        }
    }

}
package com.patsurvey.nudge.activities.ui.transect_walk

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import com.google.gson.JsonArray
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.patsurvey.nudge.MyApplication.Companion.appScopeLaunch
import com.patsurvey.nudge.R
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.domain.usecases.tola.AddTolaUseCase
import com.patsurvey.nudge.domain.usecases.tola.DeleteTolaUseCase
import com.patsurvey.nudge.domain.usecases.tola.GetSelectedVillageUseCase
import com.patsurvey.nudge.domain.usecases.tola.GetTolaUseCase
import com.patsurvey.nudge.domain.usecases.tola.UpdateTolaUseCase
import com.patsurvey.nudge.intefaces.LocalDbListener
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.request.DeleteTolaRequest
import com.patsurvey.nudge.model.request.EditCohortRequest
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.BPC_VERIFICATION_STEP_ORDER
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.FORM_C
import com.patsurvey.nudge.utils.FORM_D
import com.patsurvey.nudge.utils.LocationCoordinates
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_FORM_PATH
import com.patsurvey.nudge.utils.PREF_TRANSECT_WALK_COMPLETION_DATE_
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.Tola
import com.patsurvey.nudge.utils.VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_
import com.patsurvey.nudge.utils.longToString
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
    private val addTolaUseCase: AddTolaUseCase,
    private val deleteTolaUseCase: DeleteTolaUseCase,
    private val getTolaUseCase: GetTolaUseCase,
    private val updateTolaUseCase: UpdateTolaUseCase,
    private val getSelectedVillageUseCase: GetSelectedVillageUseCase
) : BaseViewModel() {

    private val _tolaList = MutableStateFlow(listOf<TolaEntity>())
    val tolaList: StateFlow<List<TolaEntity>> get() = _tolaList

    val villageEntity = mutableStateOf<VillageEntity?>(null)

    val isTransectWalkComplete = mutableStateOf(false)
    val isVoEndorsementComplete = mutableStateOf(false)

    val showLoader = mutableStateOf(false)
    private var isPending = 0


    fun addTola(tola: Tola, dbListener: LocalDbListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val isSucss = addTolaUseCase.invoke(
                tola = tola,
                villageId = getSelectedVillageUseCase.getSelectedVillage().id
            )

            if (isSucss) {
                withContext(Dispatchers.Main) {
                    dbListener.onInsertionSuccess()
                }
                transectWalkRepository.saveEvent(
                    eventItem = tolaItem,
                    eventName = EventName.ADD_TOLA,
                    eventType = EventType.STATEFUL
                )
            } else {
                withContext(Dispatchers.Main) {
                    dbListener.onInsertionFailed()
                }


            }
        }
    }

//    fun addEmptyTola() {
//        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
//            val tolaItem = TolaEntity.createEmptyTolaForVillageId(villageEntity.value?.id ?: 0)
//            transectWalkRepository.tolaInsert(tolaItem)
//            transectWalkRepository.saveEvent(
//                eventItem = tolaItem,
//                eventName = EventName.ADD_TOLA,
//                eventType = EventType.STATEFUL
//            )
//            val updatedTolaList =
//                transectWalkRepository.getAllTolasForVillage(transectWalkRepository.getSelectedVillage().id)
//            withContext(Dispatchers.Main) {
////                prefRepo.savePref(TOLA_COUNT, tolaList.size)
//                _tolaList.value = updatedTolaList
//                if (isTransectWalkComplete.value) {
//                    isTransectWalkComplete.value = false
//                }
//            }
//        }
//    }









    fun removeTola(
        tolaId: Int,
        context: Context,
        isOnline: Boolean,
        networkCallbackListener: NetworkCallbackListener,
        villageId: Int,
        stepId: Int
    ) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            try {
                deleteTolaUseCase.invoke(tolaId = tolaId)

                    transectWalkRepository.saveEvent(
                        eventItem = localTola,
                        eventName = EventName.DELETE_TOLA,
                        eventType = EventType.STATEFUL
                    )

                val updatedTolaList =
                    getTolaUseCase.getAllTolasForVillage(getSelectedVillageUseCase.getSelectedVillage().id)
                    withContext(Dispatchers.Main) {
                        _tolaList.value = updatedTolaList
                    }
                    deleteDidisForTola(
                        if (localTola.serverId == 0) localTola.id else localTola.serverId,
                        isOnline
                    )
                    val stepDetails = transectWalkRepository.getStepForVillage(villageId, stepId)
                    if (updatedTolaList.isEmpty()) {
                        transectWalkRepository.getAllStepsForVillage(villageId)
                            .sortedBy { it.orderNumber }.forEach { newStep ->
                                if (newStep.orderNumber == stepDetails.orderNumber) {
                                    transectWalkRepository.markStepAsInProgress(
                                        (stepDetails.orderNumber),
                                        StepStatus.INPROGRESS.ordinal,
                                        villageId
                                    )
                                    transectWalkRepository.updateNeedToPost(
                                        stepDetails.id,
                                        villageId,
                                        true
                                    )
                                }
                                if (newStep.orderNumber > stepDetails.orderNumber) {
                                    transectWalkRepository.markStepAsInProgress(
                                        (newStep.orderNumber),
                                        StepStatus.NOT_STARTED.ordinal,
                                        villageId
                                    )
                                    transectWalkRepository.updateNeedToPost(
                                        newStep.id,
                                        villageId,
                                        true
                                    )
                                }
                            }
                    }
                    if (isOnline && isSyncEnabled(transectWalkRepository.prefRepo)) {
                        val tolaToBeDeleted = transectWalkRepository.fetchSingleTola(tolaId)
                        if (tolaToBeDeleted?.serverId != 0) {
                            val jsonArray = JsonArray()
                            jsonArray.add(
                                DeleteTolaRequest.getRequestObjectForDeleteTola(localTola).toJson()
                            )
                            val response = transectWalkRepository.deleteCohort(jsonArray)
                            if (response.status.equals(SUCCESS)) {
                                transectWalkRepository.removeTola(tolaId)
                            } else {
                                transectWalkRepository.setNeedToPost(listOf(tolaId), true)
                                networkCallbackListener.onFailed()
                            }

                            if (!response.lastSyncTime.isNullOrEmpty()) {
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
                onError(
                    "TransectWalkViewModel",
                    "removeTola- ${ex.message}: \n${ex.stackTraceToString()}"
                )
            }
        }


    private suspend fun deleteDidisForTola(tolaId: Int, isOnline: Boolean) {
            try {
                val didiList = getTolaUseCase.getDidisForTola(tolaId)
                transectWalkRepository.deleteDidisForTola(
                    tolaId,
                    activeStatus = DidiStatus.DIID_DELETED.ordinal,
                    needsToPostDeleteStatus = true
                )

                didiList.forEach { didi ->
                    transectWalkRepository.saveEvent(
                        didi,
                        EventName.DELETE_DIDI,
                        EventType.STATEFUL
                    )
                }


            } catch (ex: Exception) {
                onError(
                    "TransectWalkViewModel",
                    "deleteDidisForTola- ${ex.message}: \n${ex.stackTraceToString()}"
                )
            }

    }

    fun updateTola(
        id: Int,
        newName: String,
        newLocation: LocationCoordinates?,
        isOnline: Boolean,
        networkCallbackListener: NetworkCallbackListener
    ) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            updateTolaUseCase.invoke(
                id = id,
                name = newName.trim(),
                lat = newLocation?.lat ?: 0.0,
                longitude = newLocation?.long ?: 0.0,
                villageId = tolaList.value[getIndexOfTola(id)].villageId
            )

            transectWalkRepository.saveEvent(
                eventItem = localTola,
                eventName = EventName.UPDATE_TOLA,
                eventType = EventType.STATEFUL
            )


            val updatedTolaList =
                transectWalkRepository.getAllTolasForVillage(transectWalkRepository.getSelectedVillage().id)
            withContext(Dispatchers.Main) {
                _tolaList.value = updatedTolaList
            }
            if (isOnline && updatedTola.serverId != 0 && isSyncEnabled(transectWalkRepository.prefRepo)) {
                val jsonTola = JsonArray()
                jsonTola.add(EditCohortRequest.getRequestObjectForTola(updatedTola).toJson())
                val response = transectWalkRepository.editCohort(jsonTola)
                NudgeLogger.d(
                    "TransectWalkViewModel",
                    "updateTola -> response: status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}"
                )
                if (response.status.equals(SUCCESS)) {
                    transectWalkRepository.updateNeedToPost(updatedTola.id, false)
                } else {
                    transectWalkRepository.setNeedToPost(listOf(updatedTola.id), true)
                    NudgeLogger.d("updateTola: ", "update tola request failed: ${response.message}")
                    networkCallbackListener.onFailed()
                }
                if (!response.lastSyncTime.isNullOrEmpty()) {
                    transectWalkRepository.updateLastSyncTime(response.lastSyncTime)
                }
            } else {
                transectWalkRepository.updateNeedToPost(updatedTola.id, true)
            }

        }
    }

    fun fetchTolaList(villageId: Int) {
        showLoader.value = true
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            try {
                _tolaList.emit(getTolaUseCase.getAllTolasForVillage(villageId))
                showLoader.value = false
            } catch (ex: Exception) {
                onError(tag = "TransectWalkViewModel", "Exception: ${ex.localizedMessage}")
                showLoader.value = false
            }
        }
    }

    fun setVillage(villageId: Int) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            var village = transectWalkRepository.fetchVillageDetailsForLanguage(
                villageId,
                transectWalkRepository.getAppLanguageId() ?: 2
            ) ?: transectWalkRepository.getVillage(villageId)
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
            NudgeLogger.d(
                "TransectWalkViewModel",
                "markStepAsCompleteOrInProgress -> stepsListDao.markStepAsCompleteOrInProgress($stepId, StepStatus.COMPLETED.ordinal, $villageId)"
            )
            transectWalkRepository.updateNeedToPost(stepId, villageId, true)
            val stepList =
                transectWalkRepository.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }
            val currentStep = stepList[stepList.map { it.orderNumber }.indexOf(1)]
            if (currentStep.orderNumber < stepList.size && currentStep.orderNumber > 1) {
                NudgeLogger.d(
                    "TransectWalkViewModel",
                    "markStepAsCompleteOrInProgress ->currentStep: $currentStep"
                )
                val nextStepId = (stepList[stepList.map { it.orderNumber }.indexOf(2)].id)
                transectWalkRepository.markStepAsInProgress(
                    nextStepId,
                    StepStatus.INPROGRESS.ordinal,
                    villageId
                )
                NudgeLogger.d(
                    "TransectWalkViewModel",
                    "markStepAsCompleteOrInProgress -> stepsListDao.markStepAsInProgress($nextStepId, StepStatus.INPROGRESS.ordinal, $villageId)"
                )
                transectWalkRepository.savePref(
                    "$VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_${villageId}",
                    false
                )
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
            val stepList =
                transectWalkRepository.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }
            val transectWalkStep = stepList[stepList.map { it.orderNumber }.indexOf(1)]
            transectWalkRepository.markStepAsCompleteOrInProgress(
                stepId = transectWalkStep.id,
                isComplete = StepStatus.INPROGRESS.ordinal,
                villageId = villageId
            )
            NudgeLogger.d(
                "TransectWalkViewModel",
                "markTransectWalkIncomplete -> stepsListDao.markStepAsCompleteOrInProgress($stepId, StepStatus.INPROGRESS.ordinal, $villageId)"
            )
            if (transectWalkStep.isComplete == StepStatus.COMPLETED.ordinal)
                updateWorkflowStatus(
                    stepStatus = StepStatus.INPROGRESS,
                    villageId = villageId,
                    stepId = stepId,
                )


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
                        updateWorkflowStatus(
                            stepStatus = StepStatus.INPROGRESS,
                            villageId = villageId,
                            stepId = newStep.id
                        )
                        NudgeLogger.d(
                            "TransectWalkViewModel",
                            "markTransectWalkIncomplete -> stepsListDao.markStepAsCompleteOrInProgress(${newStep.id}, StepStatus.INPROGRESS.ordinal, $villageId)"
                        )
                        transectWalkRepository.updateNeedToPost(newStep.id, villageId, true)
                    }
                }
            }
            try {
                if (isOnline && isSyncEnabled(prefRepo = transectWalkRepository.prefRepo)) {
                    val apiRequest = mutableListOf<EditWorkFlowRequest>()
                    apiRequest.add(
                        EditWorkFlowRequest(
                            stepList[stepList.map { it.orderNumber }.indexOf(1)].workFlowId,
                            StepStatus.INPROGRESS.name,

                            villageId = villageId,
                            programsProcessId = stepList[stepList.map { it.orderNumber }
                                .indexOf(1)].id
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
                                            StepStatus.INPROGRESS.name,
                                            villageId = villageId,
                                            programsProcessId = newStep.id

                                        )
                                    )
                                }
                            }
                        }
                        if (apiRequest.isNotEmpty()) {
                            NudgeLogger.d(
                                "TransectWalkViewModel",
                                "markTransectWalkIncomplete -> apiRequest: $apiRequest"
                            )
                            val response = transectWalkRepository.editWorkFlow(apiRequest)
                            NudgeLogger.d(
                                "TransectWalkViewModel",
                                "markTransectWalkIncomplete -> response: status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}"
                            )
                            if (response.status.equals(SUCCESS)) {
                                response.data?.let { response ->
                                    response.forEach { it ->
                                        transectWalkRepository.updateWorkflowId(
                                            stepId = it.programsProcessId,
                                            workflowId = it.id,
                                            villageId = villageId,
                                            status = it.status
                                        )
                                        transectWalkRepository.updateNeedToPost(
                                            it.programsProcessId,
                                            villageId,
                                            false
                                        )
                                    }
                                    NudgeLogger.d(
                                        "TransectWalkViewModel",
                                        "markTransectWalkIncomplete -> onSuccess"
                                    )
                                    networkCallbackListener.onSuccess()
                                }
                            } else {
                                NudgeLogger.d(
                                    "TransectWalkViewModel",
                                    "markTransectWalkIncomplete -> onFailed"
                                )
                                networkCallbackListener.onFailed()
                            }

                            if (!response.lastSyncTime.isNullOrEmpty()) {
                                transectWalkRepository.updateLastSyncTime(response.lastSyncTime)
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
            val stepList =
                transectWalkRepository.getAllStepsForVillage(villageId).sortedBy { it.orderNumber }
            val isComplete = stepList[stepList.map { it.orderNumber }.indexOf(5)].isComplete
            isVoEndorsementComplete.value = isComplete == StepStatus.COMPLETED.ordinal
        }
    }

    fun saveTransectWalkCompletionDate() {
        val currentTime = System.currentTimeMillis()
        transectWalkRepository.savePref(
            PREF_TRANSECT_WALK_COMPLETION_DATE_ + transectWalkRepository.getSelectedVillage().id,
            currentTime
        )
    }

    fun callWorkFlowAPI(
        villageId: Int,
        stepId: Int,
        networkCallbackListener: NetworkCallbackListener
    ) {
        if (!isSyncEnabled(prefRepo = transectWalkRepository.prefRepo)) {
            return
        }
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            NudgeLogger.d("TransectWalkViewModel", "callWorkFlowAPI -> called")
            try {
                val dbResponse = transectWalkRepository.getStepForVillage(villageId, stepId)
                NudgeLogger.d(
                    "TransectWalkViewModel",
                    "callWorkFlowAPI -> dbResponse = $dbResponse"
                )
                val stepList = transectWalkRepository.getAllStepsForVillage(villageId)
                    .sortedBy { it.orderNumber }
                NudgeLogger.d("TransectWalkViewModel", "callWorkFlowAPI -> stepList = $stepList")
                if (dbResponse.workFlowId > 0) {
                    val primaryWorkFlowRequest = listOf(EditWorkFlowRequest(stepList[stepList.map { it.orderNumber }.indexOf(1)].workFlowId
                        , StepStatus.COMPLETED.name, longToString(transectWalkRepository.getPref(
                            PREF_TRANSECT_WALK_COMPLETION_DATE_ + transectWalkRepository.getSelectedVillage().id,
                            System.currentTimeMillis()
                        )
                        ),
                        villageId,
                        programsProcessId = stepList[stepList.map { it.orderNumber }
                            .indexOf(1)].workFlowId
                    ))
                    NudgeLogger.d("TransectWalkViewModel", "callWorkFlowAPI -> primaryWorkFlowRequest = $primaryWorkFlowRequest")
                    val response = transectWalkRepository.editWorkFlow(primaryWorkFlowRequest)
                    NudgeLogger.d(
                        "TransectWalkViewModel",
                        "callWorkFlowAPI -> response: status = ${response.status}, message = ${response.message}, data = ${response.data.toString()}"
                    )
                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.let {
                            transectWalkRepository.updateWorkflowId(
                                stepId = stepList[stepList.map { it.orderNumber }.indexOf(1)].id,
                                workflowId = stepList[stepList.map { it.orderNumber }
                                    .indexOf(1)].workFlowId,
                                villageId = villageId,
                                status = it[0].status
                            )
                        }
                        transectWalkRepository.updateNeedToPost(stepList[stepList.map { it.orderNumber }
                            .indexOf(1)].id, villageId, false)
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
                            val inProgressStepRequest = listOf(
                                EditWorkFlowRequest(
                                    step.workFlowId, StepStatus.INPROGRESS.name,
                                    villageId = villageId, programsProcessId = step.id
                                )
                            )
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
                                NudgeLogger.d(
                                    "TransectWalkViewModel",
                                    "callWorkFlowAPI -> inProgressStepResponse = FAIL"
                                )
                                networkCallbackListener.onFailed()
                            }

                            if (!inProgressStepResponse.lastSyncTime.isNullOrEmpty()) {
                                transectWalkRepository.updateLastSyncTime(inProgressStepResponse.lastSyncTime)
                            }
                        }
                    }
                } catch (ex: Exception) {
                    NudgeLogger.d(
                        "TransectWalkViewModel",
                        "callWorkFlowAPI -> second try- onFailed()"
                    )
                    networkCallbackListener.onFailed()
                    onCatchError(ex, ApiType.WORK_FLOW_API)
                }
            } catch (ex: Exception) {
                NudgeLogger.d("TransectWalkViewModel", "callWorkFlowAPI -> onFailed()")
                networkCallbackListener.onFailed()
                onError(
                    tag = "TransectWalkViewModel",
                    "callWorkFlowAPI -> Error : ${ex.localizedMessage}"
                )
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

    fun updateWorkflowStatusInEvent(stepStatus: StepStatus, villageId: Int, stepId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            updateWorkflowStatus(
                stepStatus = StepStatus.COMPLETED,
                villageId = villageId,
                stepId = stepId
            )
        }
    }

    override suspend fun updateWorkflowStatus(stepStatus: StepStatus, villageId: Int, stepId: Int) {
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

        updateWorkflowEvent?.let {
            transectWalkRepository.saveEventToMultipleSources(
                it,
                listOf()
            )
        }

    }


    fun getStateId(): Int {
        return transectWalkRepository.prefRepo.getStateId()
    }

}
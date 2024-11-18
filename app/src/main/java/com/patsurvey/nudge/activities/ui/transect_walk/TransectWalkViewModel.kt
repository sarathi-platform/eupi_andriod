package com.patsurvey.nudge.activities.ui.transect_walk

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.patsurvey.nudge.MyApplication.Companion.appScopeLaunch
import com.patsurvey.nudge.R
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.getTolaId
import com.patsurvey.nudge.domain.usecases.didiDetails.DeleteDidiUseCase
import com.patsurvey.nudge.domain.usecases.didiDetails.GetDidiUseCase
import com.patsurvey.nudge.domain.usecases.steps.GetStepListUseCase
import com.patsurvey.nudge.domain.usecases.steps.UpdateStepListUseCase
import com.patsurvey.nudge.domain.usecases.tola.AddTolaUseCase
import com.patsurvey.nudge.domain.usecases.tola.DeleteTolaUseCase
import com.patsurvey.nudge.domain.usecases.tola.GetSelectedVillageUseCase
import com.patsurvey.nudge.domain.usecases.tola.GetTolaUseCase
import com.patsurvey.nudge.domain.usecases.tola.TolaEventWriterUseCase
import com.patsurvey.nudge.domain.usecases.tola.UpdateTolaUseCase
import com.patsurvey.nudge.intefaces.LocalDbListener
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.utils.BPC_VERIFICATION_STEP_ORDER
import com.patsurvey.nudge.utils.FORM_C
import com.patsurvey.nudge.utils.FORM_D
import com.patsurvey.nudge.utils.LocationCoordinates
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_FORM_PATH
import com.patsurvey.nudge.utils.PREF_TRANSECT_WALK_COMPLETION_DATE_
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.TOLA_COUNT
import com.patsurvey.nudge.utils.Tola
import com.patsurvey.nudge.utils.VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

//This class is responsible for add tola/ edit tola/ delete tola
@HiltViewModel
class TransectWalkViewModel @Inject constructor(
    private val addTolaUseCase: AddTolaUseCase,
    private val deleteTolaUseCase: DeleteTolaUseCase,
    private val getTolaUseCase: GetTolaUseCase,
    private val updateTolaUseCase: UpdateTolaUseCase,
    private val getSelectedVillageUseCase: GetSelectedVillageUseCase,
    private val deleteDidiUseCase: DeleteDidiUseCase,
    private val getDidiUseCase: GetDidiUseCase,
    private val tolaEventWriterUseCase: TolaEventWriterUseCase,
    private val getStepListUseCase: GetStepListUseCase,
    private val transectWalkRepository: TransectWalkRepository,
    private val updateStepListUseCase: UpdateStepListUseCase,
    private val prefRepo: PrefRepo
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
            val tolaEntity = addTolaUseCase.invoke(
                tola = tola,
                villageId = getSelectedVillageUseCase.getSelectedVillage().id
            )
            if (tolaEntity != null) {
                tolaEventWriterUseCase.invoke(
                    tolaItem = tolaEntity,
                    eventName = EventName.ADD_TOLA,
                    eventType = EventType.STATEFUL
                )
                withContext(Dispatchers.Main) {
                    dbListener.onInsertionSuccess()
                }
            } else {

                withContext(Dispatchers.Main) {
                    dbListener.onInsertionFailed()
                }

            }
        }
    }


    fun addEmptyTola() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val tolaItem = TolaEntity.createEmptyTolaForVillageId(villageEntity.value?.id ?: 0)
            transectWalkRepository.tolaInsert(tolaItem)
            tolaEventWriterUseCase.invoke(
                tolaItem = tolaItem,
                eventName = EventName.ADD_TOLA,
                eventType = EventType.STATEFUL
            )
            val updatedTolaList =
                transectWalkRepository.getAllTolasForVillage(transectWalkRepository.getSelectedVillage().id)
            withContext(Dispatchers.Main) {
                prefRepo.savePref(TOLA_COUNT, tolaList.value.size)
                _tolaList.value = updatedTolaList
                if (isTransectWalkComplete.value) {
                    isTransectWalkComplete.value = false
                }
            }
        }
    }









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

                tolaEventWriterUseCase.invoke(
                    tolaItem = tolaList.value.find { it.getTolaId() == tolaId },
                    eventName = EventName.DELETE_TOLA,
                    eventType = EventType.STATEFUL
                )

                val updatedTolaList =
                    getTolaUseCase.getAllTolasForVillage(getSelectedVillageUseCase.getSelectedVillage().id)
                withContext(Dispatchers.Main) {
                    _tolaList.value = updatedTolaList
                }
                deleteDidisForTola(tolaId)
                val stepDetails = getStepListUseCase.invoke(villageId = villageId, stepId = stepId)
                if (updatedTolaList.isEmpty()) {
                    getStepListUseCase.invoke(villageId = villageId)
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
    }


    private suspend fun deleteDidisForTola(tolaId: Int) {
            try {

                val didiList = getDidiUseCase.invoke(tolaId)
                deleteDidiUseCase.invoke(tolaId)
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
            val updatedTolaList =
                getTolaUseCase.getAllTolasForVillage(getSelectedVillageUseCase.getSelectedVillage().id)
            withContext(Dispatchers.Main) {
                _tolaList.value = updatedTolaList
            }
            tolaEventWriterUseCase.invoke(
                tolaItem = tolaList.value.find { it.getTolaId() == id },
                eventName = EventName.UPDATE_TOLA,
                eventType = EventType.STATEFUL
            )



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
            )
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

            updateStepListUseCase.invoke(
                villageId = villageId, stepId = stepId, stepStatus = StepStatus.COMPLETED.ordinal,
            )

            NudgeLogger.d(
                "TransectWalkViewModel",
                "markStepAsCompleteOrInProgress -> stepsListDao.markStepAsCompleteOrInProgress($stepId, StepStatus.COMPLETED.ordinal, $villageId)"
            )
            val stepList =
                getStepListUseCase.invoke(villageId).sortedBy { it.orderNumber }
            val currentStep = stepList[stepList.map { it.orderNumber }.indexOf(1)]
            if (currentStep.orderNumber < stepList.size && currentStep.orderNumber > 1) {
                NudgeLogger.d(
                    "TransectWalkViewModel",
                    "markStepAsCompleteOrInProgress ->currentStep: $currentStep"
                )
                val nextStepId = (stepList[stepList.map { it.orderNumber }.indexOf(2)].id)
                updateStepListUseCase.invoke(
                    stepId = nextStepId,
                    stepStatus = StepStatus.INPROGRESS.ordinal,
                    villageId = villageId
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
            updateStepListUseCase.invoke(
                stepId = transectWalkStep.id,
                stepStatus = StepStatus.INPROGRESS.ordinal,
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
                    }
                }
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

    fun saveTransectWalkCompletionDate() {
        val currentTime = System.currentTimeMillis()
        transectWalkRepository.savePref(
            PREF_TRANSECT_WALK_COMPLETION_DATE_ + transectWalkRepository.getSelectedVillage().id,
            currentTime
        )
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
        return prefRepo.getStateId()
    }

}
package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.viewModel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nudge.core.Core
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.enums.EventType
import com.nudge.core.showCustomToast
import com.nudge.core.ui.events.DialogEvents
import com.nudge.core.utils.state.DialogState
import com.nudge.core.value
import com.nudge.syncmanager.EventWriterEvents
import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupSubTabUiModel
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import com.sarathi.smallgroupmodule.data.domain.EventWriterHelperImpl
import com.sarathi.smallgroupmodule.data.model.SubjectAttendanceState
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.useCase.SmallGroupAttendanceUserCase
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.presentation.SmallGroupAttendanceEntityState
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.presentation.event.SmallGroupAttendanceEvent
import com.sarathi.smallgroupmodule.utils.getDate
import com.sarathi.smallgroupmodule.utils.getDateInMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SmallGroupAttendanceScreenViewModel @Inject constructor(
    private val smallGroupAttendanceUserCase: SmallGroupAttendanceUserCase,
    private val eventWriterHelperImpl: EventWriterHelperImpl,
) : BaseViewModel() {

    private val _smallGroupDetails: MutableState<SmallGroupSubTabUiModel> =
        mutableStateOf(SmallGroupSubTabUiModel.getEmptyModel())
    val smallGroupDetails: State<SmallGroupSubTabUiModel> get() = _smallGroupDetails

    private val _subjectList: MutableState<MutableList<SubjectEntity>> =
        mutableStateOf(mutableListOf())
    val subjectList: State<List<SubjectEntity>> get() = _subjectList

    val selectedDate =
        mutableStateOf(System.currentTimeMillis().getDate().getDateInMillis())

    private val _smallGroupAttendanceEntityState: MutableState<MutableList<SmallGroupAttendanceEntityState>> =
        mutableStateOf(
            mutableListOf()
        )
    val smallGroupAttendanceEntityState: State<List<SmallGroupAttendanceEntityState>> get() = _smallGroupAttendanceEntityState

    val selectedItems =
        mutableStateOf(mapOf<Int, Boolean>())


    val allSelected = mutableStateOf(false)

    val alertDialogState: MutableState<DialogState> = mutableStateOf(DialogState())

    var markedDatesList: List<Long> = emptyList()

    override fun <T> onEvent(event: T) {

        when (event) {

            is DialogEvents.ShowDialogEvent -> {
                alertDialogState.value = alertDialogState.value.copy(event.showDialog)
            }

            is SmallGroupAttendanceEvent.LoadSmallGroupDetailsForSmallGroupIdEvent -> {

                viewModelScope.launch(Dispatchers.IO) {
                    _smallGroupDetails.value =
                        smallGroupAttendanceUserCase.fetchSmallGroupDetailsFromDbUseCase.invoke(
                            event.smallGroupId
                        )
                    _subjectList.value.addAll(
                        smallGroupAttendanceUserCase.fetchDidiListForSmallGroupFromDbUseCase.invoke(
                            event.smallGroupId
                        )
                    )
                    subjectList.value.forEach { subject ->
                        _smallGroupAttendanceEntityState.value.add(
                            SmallGroupAttendanceEntityState(
                                subjectId = subject.subjectId ?: 0,
                                subjectEntity = subject,
                                false
                            )
                        )
                    }
                    selectedItems.value =
                        smallGroupAttendanceEntityState.value.map { it.subjectId to false }.toMap()
                    allSelected.value = selectedItems.value.values.all {
                        it
                    } && selectedItems.value.isNotEmpty()

                    markedDatesList = smallGroupAttendanceUserCase.fetchMarkedDatesUseCase.invoke()
                }
            }

            is SmallGroupAttendanceEvent.MarkAttendanceForAllEvent -> {

                val newSelection = selectedItems.value.mapValues { event.checked }
                selectedItems.value = newSelection
                allSelected.value =
                    selectedItems.value.values.filter { it == true }.size == selectedItems.value.size && selectedItems.value.isNotEmpty()

            }

            is SmallGroupAttendanceEvent.MarkAttendanceForSubjectEvent -> {

                if (event.subjectId == 0)
                    return

                selectedItems.value = selectedItems.value.toMutableMap().apply {
                    this[event.subjectId] = event.checked
                }
                allSelected.value =
                    selectedItems.value.values.filter { it == true }.size == selectedItems.value.size && selectedItems.value.isNotEmpty()

            }

            is EventWriterEvents.SaveAttendanceEvent -> {
                viewModelScope.launch(Dispatchers.IO) {
                    eventWriterHelperImpl.saveEventToMultipleSources(
                        event = event.events,
                        eventType = EventType.STATEFUL,
                        eventDependencies = event.dependencyEntityList
                    )
                }
            }

            is SmallGroupAttendanceEvent.SubmitAttendanceForDateEvent -> {

                viewModelScope.launch(Dispatchers.IO) {

                    val isAttendanceAllowedForDate = checkIsAttendanceAllowedForDate()

                    if (isAttendanceAllowedForDate) {
                        smallGroupAttendanceEntityState.value.forEach { subjectState ->
                            addSaveAttendanceEvent(
                                smallGroupAttendanceEntityState = subjectState,
                                smallGroupDetails = smallGroupDetails.value,
                                checked = selectedItems.value[subjectState.subjectId] ?: false
                            )
                        }

                        onEvent(SmallGroupAttendanceEvent.SaveAttendanceForDateToDbEvent)
                    } else {
                        withContext(mainDispatcher) {
                            showCustomToast(
                                Core.getContext(),
                                "Attendance already marked for the date: ${selectedDate.value.getDate()}"
                            )
                        }
                    }
                }

            }

            is SmallGroupAttendanceEvent.SaveAttendanceForDateToDbEvent -> {

                val finalAttendanceStateList = ArrayList<SubjectAttendanceState>()
                smallGroupAttendanceEntityState.value.forEach { state ->
                    finalAttendanceStateList.add(
                        SubjectAttendanceState(
                            state.subjectId.value(),
                            selectedItems.value[state.subjectId].value(),
                            date = selectedDate.value
                        )
                    )
                }

                ioViewModelScope {
                    smallGroupAttendanceUserCase.saveAttendanceToDbUseCase.invoke(
                        finalAttendanceStateList
                    )
                }

            }
        }

    }

    private suspend fun checkIsAttendanceAllowedForDate(): Boolean {
        val updatedMarkedAttendanceList =
            smallGroupAttendanceUserCase.fetchMarkedDatesUseCase.invoke()
        val markedListInString = updatedMarkedAttendanceList.map { it.getDate() }
        return !markedListInString.contains(selectedDate.value.getDate())
    }

    private suspend fun addSaveAttendanceEvent(
        smallGroupAttendanceEntityState: SmallGroupAttendanceEntityState?,
        smallGroupDetails: SmallGroupSubTabUiModel,
        checked: Boolean
    ) {
        val event = eventWriterHelperImpl.createAttendanceEvent(
            subjectEntity = smallGroupAttendanceEntityState?.subjectEntity!!,
            smallGroupSubTabUiModel = smallGroupDetails,
            attendance = checked,
            date = selectedDate.value
        )
        onEvent(EventWriterEvents.SaveAttendanceEvent(event, listOf<EventDependencyEntity>()))
    }

    private fun updateAttendanceForAll(checked: Boolean) {
        val tempList = smallGroupAttendanceEntityState.value
        val updatedList = ArrayList<SmallGroupAttendanceEntityState>()
        tempList.forEach { subjectState ->
            updatedList.add(subjectState.copy(attendance = checked))
        }
        _smallGroupAttendanceEntityState.value.clear()
        _smallGroupAttendanceEntityState.value.addAll(updatedList)

    }

    private fun updateAttendanceForSubject(checked: Boolean, subjectId: Int) {
        val tempList = smallGroupAttendanceEntityState.value
        val subjectToUpdate = tempList.find { it.subjectId == subjectId }
        val index = tempList.map { it.subjectId }.indexOf(subjectId)
        _smallGroupAttendanceEntityState.value.removeAt(index)
        subjectToUpdate?.copy(attendance = checked)
            ?.let { state -> _smallGroupAttendanceEntityState.value.add(index, state) }
        Log.d(
            "TAG",
            "SmallGroupAttendanceScreen updateAttendanceForSubject: ${smallGroupAttendanceEntityState.value}"
        )

    }

    fun dateValidator(selectedDate: Long): Boolean {
        return selectedDate < System.currentTimeMillis()
                && !markedDatesList.contains(selectedDate)
    }

}

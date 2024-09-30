package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.enums.EventType
import com.nudge.core.getCurrentTimeInMillis
import com.nudge.core.ui.events.DialogEvents
import com.nudge.core.utils.state.DialogState
import com.nudge.core.value
import com.nudge.syncmanager.EventWriterEvents
import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupSubTabUiModel
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import com.sarathi.smallgroupmodule.data.domain.EventWriterHelperImpl
import com.sarathi.smallgroupmodule.data.model.SubjectAttendanceHistoryState
import com.sarathi.smallgroupmodule.data.model.SubjectAttendanceState
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.useCase.SmallGroupAttendanceEditUserCase
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.presentation.SmallGroupAttendanceEntityState
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.presentation.event.SmallGroupAttendanceEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SmallGroupAttendanceEditScreenViewModel @Inject constructor(
    private val smallGroupAttendanceEditUserCase: SmallGroupAttendanceEditUserCase,
    private val eventWriterHelperImpl: EventWriterHelperImpl
) : BaseViewModel() {

    private val _smallGroupDetails: MutableState<SmallGroupSubTabUiModel> =
        mutableStateOf(SmallGroupSubTabUiModel.getEmptyModel())
    val smallGroupDetails: State<SmallGroupSubTabUiModel> get() = _smallGroupDetails

    private val _subjectList: MutableState<MutableList<SubjectEntity>> =
        mutableStateOf(mutableListOf())
    val subjectList: State<List<SubjectEntity>> get() = _subjectList

    val selectedDate =
        mutableStateOf(getCurrentTimeInMillis())

    private val _smallGroupAttendanceEntityState: MutableState<MutableList<SmallGroupAttendanceEntityState>> =
        mutableStateOf(
            mutableListOf()
        )
    val smallGroupAttendanceEntityState: State<List<SmallGroupAttendanceEntityState>> get() = _smallGroupAttendanceEntityState

    val selectedItems =
        mutableStateOf(mapOf<Int, Boolean>())


    val allSelected = mutableStateOf(false)

    val alertDialogState: MutableState<DialogState> = mutableStateOf(DialogState())

    private val _smallGroupAttendanceHistoryForDate: MutableState<List<SubjectAttendanceHistoryState>> =
        mutableStateOf(
            mutableListOf()
        )
    val smallGroupAttendanceHistoryForDate: State<List<SubjectAttendanceHistoryState>> get() = _smallGroupAttendanceHistoryForDate

    override fun <T> onEvent(event: T) {

        when (event) {

            is DialogEvents.ShowDialogEvent -> {
                alertDialogState.value = alertDialogState.value.copy(event.showDialog)
            }

            is SmallGroupAttendanceEvent.LoadSmallGroupAttendanceForGroupForDateEvent -> {

                ioViewModelScope {

                    selectedDate.value = event.selectedDate

                    fetchSmallGroupDetails(event.smallGroupId)

                    fetchDidiListForSmallGroup(event.smallGroupId)

                    fetchSmallGroupAttendanceHistoryForDate(event.smallGroupId, event.selectedDate)

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
                ioViewModelScope {
                    eventWriterHelperImpl.saveEventToMultipleSources(
                        event = event.events,
                        eventType = EventType.STATEFUL,
                        eventDependencies = event.dependencyEntityList
                    )
                }
            }

            is SmallGroupAttendanceEvent.UpdateAttendanceForDateEvent -> {
                ioViewModelScope {
                    smallGroupAttendanceEntityState.value.forEach { subjectState ->
                        addSaveAttendanceEvent(
                            smallGroupAttendanceEntityState = subjectState,
                            smallGroupDetails = smallGroupDetails.value,
                            checked = selectedItems.value[subjectState.subjectId] ?: false
                        )
                    }
                    onEvent(SmallGroupAttendanceEvent.SaveAttendanceForDateToDbEvent)
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
                    smallGroupAttendanceEditUserCase.updateAttendanceToDbUseCase.invoke(
                        finalAttendanceStateList,
                        selectedDate.value
                    )
                }

            }
        }

    }

    private suspend fun fetchSmallGroupAttendanceHistoryForDate(
        smallGroupId: Int,
        selectedDate: Long
    ) {
        _smallGroupAttendanceHistoryForDate.value =
            smallGroupAttendanceEditUserCase.fetchAttendanceHistoryForDateFromDbUseCase.invoke(
                smallGroupId,
                selectedDate
            )
        updateSmallGroupAttendanceEntityState()
    }

    private fun updateSelectedItemsList() {
        selectedItems.value =
            smallGroupAttendanceEntityState.value.map { it.subjectId to it.attendance }.toMap()
        allSelected.value = selectedItems.value.values.all {
            it
        } && selectedItems.value.isNotEmpty()
    }

    private fun updateSmallGroupAttendanceEntityState() {
        subjectList.value.forEach { subject ->
            _smallGroupAttendanceEntityState.value.add(
                SmallGroupAttendanceEntityState(
                    subjectId = subject.subjectId ?: 0,
                    subjectEntity = subject,
                    attendance = smallGroupAttendanceHistoryForDate.value.find { it.subjectId == subject.subjectId }?.attendance.value()
                )
            )
        }
        updateSelectedItemsList()
    }

    private suspend fun fetchDidiListForSmallGroup(smallGroupId: Int) {
        _subjectList.value.addAll(
            smallGroupAttendanceEditUserCase.fetchDidiListForSmallGroupFromDbUseCase.invoke(
                smallGroupId
            )
        )
    }

    private suspend fun fetchSmallGroupDetails(smallGroupId: Int) {
        _smallGroupDetails.value =
            smallGroupAttendanceEditUserCase.fetchSmallGroupDetailsFromDbUseCase.invoke(
                smallGroupId
            )
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

    fun dateValidator(selectedDate: Long): Boolean {
        return selectedDate < System.currentTimeMillis()
    }


}
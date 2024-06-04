package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.enums.EventType
import com.nudge.syncmanager.EventWriterEvents
import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupSubTabUiModel
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import com.sarathi.smallgroupmodule.data.domain.EventWriterHelperImpl
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.useCase.SmallGroupAttendanceUserCase
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.presentation.SmallGroupAttendanceEntityState
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.presentation.event.SmallGroupAttendanceEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmallGroupAttendanceScreenViewModel @Inject constructor(
    private val smallGroupAttendanceUserCase: SmallGroupAttendanceUserCase,
    private val eventWriterHelperImpl: EventWriterHelperImpl
) : BaseViewModel() {

    private val _smallGroupDetails: MutableState<SmallGroupSubTabUiModel> =
        mutableStateOf(SmallGroupSubTabUiModel.getEmptyModel())
    val smallGroupDetails: State<SmallGroupSubTabUiModel> get() = _smallGroupDetails

    private val _subjectList: MutableState<MutableList<SubjectEntity>> =
        mutableStateOf(mutableListOf())
    val subjectList: State<List<SubjectEntity>> get() = _subjectList

    val isAllSelected = mutableStateOf(false)

    val markedAttendanceList = mutableStateOf(mutableSetOf<Int?>())

    private val _smallGroupAttendanceEntityState: MutableState<MutableList<SmallGroupAttendanceEntityState>> =
        mutableStateOf(
            mutableListOf()
        )
    val smallGroupAttendanceEntityState: State<List<SmallGroupAttendanceEntityState>> get() = _smallGroupAttendanceEntityState

    override fun <T> onEvent(event: T) {

        when (event) {
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
                }
            }

            is SmallGroupAttendanceEvent.MarkAttendanceForAll -> {
                isAllSelected.value = event.checked
                if (event.checked) {
                    markedAttendanceList.value.addAll(subjectList.value.map { it.subjectId })
                } else {
                    markedAttendanceList.value.clear()
                }
                updateAttendanceForAll(event.checked)
                viewModelScope.launch(Dispatchers.IO) {
                    smallGroupAttendanceEntityState.value.forEach { smallGroupAttendanceEntityState ->
                        addSaveAttendanceEvent(
                            smallGroupAttendanceEntityState,
                            smallGroupDetails.value,
                            event.checked
                        )
                    }
                }
            }

            is SmallGroupAttendanceEvent.MarkAttendanceForSubject -> {

                if (event.subjectId == 0)
                    return

                if (event.checked) {
                    markedAttendanceList.value.add(event.subjectId)
                } else {
                    markedAttendanceList.value.remove(event.subjectId)
                }
                updateAttendanceForSubject(event.checked, event.subjectId)

                viewModelScope.launch(Dispatchers.IO) {
                    addSaveAttendanceEvent(
                        smallGroupAttendanceEntityState.value.find { it.subjectId == event.subjectId },
                        smallGroupDetails.value,
                        event.checked
                    )
                }
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
        }

    }

    private suspend fun addSaveAttendanceEvent(
        smallGroupAttendanceEntityState: SmallGroupAttendanceEntityState?,
        smallGroupDetails: SmallGroupSubTabUiModel,
        checked: Boolean
    ) {
        val event = eventWriterHelperImpl.createAttendanceEvent(
            subjectEntity = smallGroupAttendanceEntityState?.subjectEntity!!,
            smallGroupSubTabUiModel = smallGroupDetails,
            attendance = checked
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

    }

}

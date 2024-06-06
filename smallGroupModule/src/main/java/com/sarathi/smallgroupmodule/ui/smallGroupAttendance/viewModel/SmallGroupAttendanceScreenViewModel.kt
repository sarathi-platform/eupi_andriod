package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.viewModel

import android.util.Log
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

    val selectedDate =
        mutableStateOf(System.currentTimeMillis())

    private val _smallGroupAttendanceEntityState: MutableState<MutableList<SmallGroupAttendanceEntityState>> =
        mutableStateOf(
            mutableListOf()
        )
    val smallGroupAttendanceEntityState: State<List<SmallGroupAttendanceEntityState>> get() = _smallGroupAttendanceEntityState

    val selectedItems =
        mutableStateOf(mapOf<Int, Boolean>())


    val allSelected = mutableStateOf(false)

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
                    selectedItems.value =
                        smallGroupAttendanceEntityState.value.map { it.subjectId to false }.toMap()
                    allSelected.value = selectedItems.value.values.all {
                        it
                    } && selectedItems.value.isNotEmpty()
                }
            }

            is SmallGroupAttendanceEvent.MarkAttendanceForAll -> {

                val newSelection = selectedItems.value.mapValues { event.checked }
                selectedItems.value = newSelection
                allSelected.value =
                    selectedItems.value.values.filter { it == true }.size == selectedItems.value.size && selectedItems.value.isNotEmpty()

                /*viewModelScope.launch(Dispatchers.IO) {
                    smallGroupAttendanceEntityState.value.forEach { smallGroupAttendanceEntityState ->
                        addSaveAttendanceEvent(
                            smallGroupAttendanceEntityState,
                            smallGroupDetails.value,
                            event.checked
                        )
                    }
                }*/
            }

            is SmallGroupAttendanceEvent.MarkAttendanceForSubject -> {

                if (event.subjectId == 0)
                    return

                selectedItems.value = selectedItems.value.toMutableMap().apply {
                    this[event.subjectId] = event.checked
                }
                allSelected.value =
                    selectedItems.value.values.filter { it == true }.size == selectedItems.value.size && selectedItems.value.isNotEmpty()

                /*viewModelScope.launch(Dispatchers.IO) {

                }*/
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

            is SmallGroupAttendanceEvent.SubmitAttendanceForDate -> {

                viewModelScope.launch(Dispatchers.IO) {
                    smallGroupAttendanceEntityState.value.forEach { subjectState ->
                        addSaveAttendanceEvent(
                            smallGroupAttendanceEntityState = subjectState,
                            smallGroupDetails = smallGroupDetails.value,
                            checked = selectedItems.value[subjectState.subjectId] ?: false
                        )
                    }
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

}

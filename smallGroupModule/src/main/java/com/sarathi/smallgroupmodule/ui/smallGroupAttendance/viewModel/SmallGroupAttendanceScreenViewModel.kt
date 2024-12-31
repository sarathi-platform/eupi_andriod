package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.enums.EventType
import com.nudge.core.getCurrentTimeInMillis
import com.nudge.core.helper.TranslationEnum
import com.nudge.core.ui.events.CommonEvents
import com.nudge.core.ui.events.DialogEvents
import com.nudge.core.utils.state.DialogState
import com.nudge.core.value
import com.nudge.syncmanager.EventWriterEvents
import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupSubTabUiModel
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import com.sarathi.smallgroupmodule.data.domain.EventWriterHelperImpl
import com.sarathi.smallgroupmodule.data.model.SubjectAttendanceState
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.useCase.SmallGroupAttendanceUserCase
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.presentation.SmallGroupAttendanceEntityState
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.presentation.event.SmallGroupAttendanceEvent
import com.sarathi.smallgroupmodule.utils.getDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
        mutableStateOf(getCurrentTimeInMillis())

    private val _smallGroupAttendanceEntityState: MutableState<MutableList<SmallGroupAttendanceEntityState>> =
        mutableStateOf(
            mutableListOf()
        )
    val smallGroupAttendanceEntityState: State<List<SmallGroupAttendanceEntityState>> get() = _smallGroupAttendanceEntityState

    private val _filteredSmallGroupAttendanceEntityState: MutableState<List<SmallGroupAttendanceEntityState>> =
        mutableStateOf(
            mutableListOf()
        )
    val filteredSmallGroupAttendanceEntityState: State<List<SmallGroupAttendanceEntityState>> get() = _filteredSmallGroupAttendanceEntityState

    val selectedItems =
        mutableStateOf(mapOf<Int, Boolean>())


    val allSelected = mutableStateOf(false)

    val alertDialogState: MutableState<DialogState> = mutableStateOf(DialogState())

    var markedDatesList: List<Long> = emptyList()

    override fun <T> onEvent(event: T) {

        when (event) {
            is InitDataEvent.InitDataState -> {
                setTranslationConfig()
            }

            is DialogEvents.ShowDialogEvent -> {
                alertDialogState.value = alertDialogState.value.copy(event.showDialog)
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(event.showLoader)
            }

            is SmallGroupAttendanceEvent.LoadSmallGroupDetailsForSmallGroupIdEvent -> {

                ioViewModelScope {

                    fetchSmallGroupDetails(event.smallGroupId)

                    fetchDidiListForSmallGroup(event.smallGroupId)

                    updateSmallGroupAttendanceEntityState()

                    updateSelectedItemsList()

                    val subjectIds = smallGroupAttendanceEntityState.value.map { it.subjectId }
                    fetchMarkedDates(subjectIds)

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

            is SmallGroupAttendanceEvent.SubmitAttendanceForDateEvent -> {

                ioViewModelScope {

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
                        delay(200)
                        withContext(mainDispatcher) {
                            event.result(true)
                        }

                    } else {
                        delay(200)
                        withContext(mainDispatcher) {
                            event.result(false)
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

            is CommonEvents.SearchValueChangedEvent -> {
                searchByDidiName(event.searchQuery)
            }
        }

    }

    private fun searchByDidiName(searchQuery: String) {
        _filteredSmallGroupAttendanceEntityState.value = if (searchQuery.isNotEmpty()) {
            val filteredList = ArrayList<SmallGroupAttendanceEntityState>()
            smallGroupAttendanceEntityState.value.forEach { state ->
                if (state.subjectEntity.subjectName.trim().lowercase()
                        .contains(searchQuery.trim().lowercase())
                ) {
                    filteredList.add(state)
                }
            }
            filteredList
        } else {
            smallGroupAttendanceEntityState.value
        }
    }

    private suspend fun fetchMarkedDates(subjectIds: List<Int>) {
        markedDatesList = smallGroupAttendanceUserCase.fetchMarkedDatesUseCase.invoke(subjectIds)
    }

    private suspend fun updateSelectedItemsList() {
        selectedItems.value =
            smallGroupAttendanceEntityState.value.map { it.subjectId to false }.toMap()
        allSelected.value = selectedItems.value.values.all {
            it
        } && selectedItems.value.isNotEmpty()
    }

    private suspend fun updateSmallGroupAttendanceEntityState() {
        subjectList.value.forEach { subject ->
            _smallGroupAttendanceEntityState.value.add(
                SmallGroupAttendanceEntityState(
                    subjectId = subject.subjectId ?: 0,
                    subjectEntity = subject,
                    false
                )
            )
        }
        _filteredSmallGroupAttendanceEntityState.value = smallGroupAttendanceEntityState.value
    }

    private suspend fun fetchDidiListForSmallGroup(smallGroupId: Int) {
        _subjectList.value.addAll(
            smallGroupAttendanceUserCase.fetchDidiListForSmallGroupFromDbUseCase.invoke(
                smallGroupId
            )
        )
    }

    private suspend fun fetchSmallGroupDetails(smallGroupId: Int) {
        _smallGroupDetails.value =
            smallGroupAttendanceUserCase.fetchSmallGroupDetailsFromDbUseCase.invoke(
                smallGroupId
            )
    }

    private suspend fun checkIsAttendanceAllowedForDate(): Boolean {
        val subjectIds = smallGroupAttendanceEntityState.value.map { it.subjectId }
        val updatedMarkedAttendanceList =
            smallGroupAttendanceUserCase.fetchMarkedDatesUseCase.invoke(subjectIds)
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


    fun dateValidator(selectedDate: Long): Boolean {
        return selectedDate < getCurrentTimeInMillis()
                && !markedDatesList.contains(selectedDate)
    }
    override fun getScreenName(): TranslationEnum {
        return TranslationEnum.SmallGroupAttendanceScreen
    }
}

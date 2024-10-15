package com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nudge.core.ONE_YEAR_RANGE_DURATION
import com.nudge.core.enums.EventType
import com.nudge.core.getCurrentTimeInMillis
import com.nudge.core.getDayPriorCurrentTimeMillis
import com.nudge.core.ui.events.CommonEvents
import com.nudge.core.ui.events.DialogEvents
import com.nudge.core.utils.state.DialogState
import com.nudge.syncmanager.EventWriterEvents
import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupSubTabUiModel
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import com.sarathi.smallgroupmodule.data.domain.EventWriterHelperImpl
import com.sarathi.smallgroupmodule.data.model.SubjectAttendanceHistoryState
import com.sarathi.smallgroupmodule.data.model.convertToSubjectAttendanceStateList
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.domain.useCase.SmallGroupAttendanceHistoryUseCase
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.presentation.event.SmallGroupAttendanceEvent
import com.sarathi.smallgroupmodule.utils.getDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SmallGroupAttendanceHistoryViewModel @Inject constructor(
    private val smallGroupAttendanceHistoryUseCase: SmallGroupAttendanceHistoryUseCase,
    private val eventWriterHelperImpl: EventWriterHelperImpl
) : BaseViewModel() {

    private val _smallGroupDetails: MutableState<SmallGroupSubTabUiModel> =
        mutableStateOf(SmallGroupSubTabUiModel.getEmptyModel())
    val smallGroupDetails: State<SmallGroupSubTabUiModel> get() = _smallGroupDetails

    val isAttendanceAvailable: MutableState<Boolean> = mutableStateOf(false)

    private val _dateRangeFilter: MutableState<Pair<Long, Long>> = mutableStateOf(
        Pair(
            getDayPriorCurrentTimeMillis(ONE_YEAR_RANGE_DURATION),
            System.currentTimeMillis()
        )
    )
    val dateRangeFilter: State<Pair<Long, Long>> get() = _dateRangeFilter

    private val _subjectAttendanceHistoryStateList: MutableState<List<SubjectAttendanceHistoryState>> =
        mutableStateOf(
            mutableListOf()
        )
    val subjectAttendanceHistoryStateList: State<List<SubjectAttendanceHistoryState>> get() = _subjectAttendanceHistoryStateList

    private val _subjectAttendanceHistoryStateMappingByDate: MutableState<MutableMap<Long, List<SubjectAttendanceHistoryState>>> =
        mutableStateOf(
            mutableMapOf()
        )
    val subjectAttendanceHistoryStateMappingByDate: State<Map<Long, List<SubjectAttendanceHistoryState>>> get() = _subjectAttendanceHistoryStateMappingByDate

    private val _alertDialogState: MutableState<DialogState> = mutableStateOf(DialogState())
    val alertDialogState: State<DialogState> get() = _alertDialogState

    private var deleteHistoryForDateState: Pair<Int, Long>? = null

    override fun <T> onEvent(event: T) {
        when (event) {

            is DialogEvents.ShowDialogEvent -> {
                _alertDialogState.value =
                    _alertDialogState.value.copy(isDialogVisible = event.showDialog)
            }

            is SmallGroupAttendanceEvent.LoadSmallGroupDetailsForSmallGroupIdEvent -> {

                ioViewModelScope {

                    val details =
                        smallGroupAttendanceHistoryUseCase.fetchSmallGroupDetailsFromDbUseCase.invoke(
                            event.smallGroupId
                        )

                    setDateRangeToOldest(event.smallGroupId)

                    _subjectAttendanceHistoryStateList.value =
                        smallGroupAttendanceHistoryUseCase.fetchSmallGroupAttendanceHistoryFromDbUseCase.invoke(
                            event.smallGroupId,
                            dateRangeFilter.value
                        ).sortedByDescending { it.date }
                    _subjectAttendanceHistoryStateMappingByDate.value =
                        subjectAttendanceHistoryStateList.value.groupBy { it.date }.toMutableMap()

                    isAttendanceAvailable.value =
                        !_subjectAttendanceHistoryStateMappingByDate.value.isEmpty()

                    withContext(Dispatchers.Main) {
                        _smallGroupDetails.value = details
                    }

                }
            }

            is SmallGroupAttendanceEvent.LoadSmallGroupAttendanceHistoryOnDateRangeUpdateEvent -> {
                ioViewModelScope {
                    _subjectAttendanceHistoryStateList.value =
                        smallGroupAttendanceHistoryUseCase
                            .fetchSmallGroupAttendanceHistoryFromDbUseCase
                            .invoke(smallGroupDetails.value.smallGroupId, getFinalDateRangeFilter())
                            .sortedByDescending { it.date }
                    _subjectAttendanceHistoryStateMappingByDate.value =
                        subjectAttendanceHistoryStateList.value.groupBy { it.date }.toMutableMap()
                }

            }

            is CommonEvents.UpdateDateRange -> {
                if (event.startDate != null && event.endDate != null) {
                    _dateRangeFilter.value =
                        _dateRangeFilter.value.copy(event.startDate!!, event.endDate!!)
                }
            }

            is SmallGroupAttendanceEvent.DeleteAttendanceForDateEvent -> {
                ioViewModelScope {
                    deleteHistoryForDateState?.let { deleteHistoryForDate ->
                        val subjectAttendanceHistoryStateList =
                            subjectAttendanceHistoryStateMappingByDate.value.get(
                                deleteHistoryForDate.second
                            )
                        val subjectAttendanceStateList =
                            subjectAttendanceHistoryStateList.convertToSubjectAttendanceStateList()

                        removeAttendanceForDateFromUi(deleteHistoryForDate.second)

                        subjectAttendanceHistoryStateList?.forEach { subjectAttendanceHistoryState ->
                            addDeleteAttendanceEvent(
                                subjectAttendanceHistoryState,
                                deleteHistoryForDate.second
                            )
                        }

                        smallGroupAttendanceHistoryUseCase.deleteAttendanceToDbUseCase.invoke(
                            deleteHistoryForDate.first,
                            deleteHistoryForDate.second,
                            subjectAttendanceStateList
                        ) {
                            event.onSuccess()
                        }
                    }
                }
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

            is SmallGroupAttendanceEvent.InitiateDeleteForDateEvent -> {
                deleteHistoryForDateState = event.deleteHistoryForDateState
            }

            is SmallGroupAttendanceEvent.TerminateDeleteForDateEvent -> {
                deleteHistoryForDateState = null
            }
        }
    }

    private suspend fun setDateRangeToOldest(smallGroupId: Int) {
        val subjectIds =
            smallGroupAttendanceHistoryUseCase.fetchSmallGroupAttendanceHistoryFromDbUseCase.fetchSubjectIdsForSmallGroup(
                smallGroupId
            )
        val oldestDate =
            smallGroupAttendanceHistoryUseCase.fetchMarkedDatesUseCase.invoke(subjectIds)
                .minOrNull()
        oldestDate?.let { startDate ->
            onEvent(CommonEvents.UpdateDateRange(startDate, getCurrentTimeInMillis()))
        }
    }

    private fun removeAttendanceForDateFromUi(date: Long) {
        val tempList = _subjectAttendanceHistoryStateMappingByDate.value
        tempList.remove(date)
        _subjectAttendanceHistoryStateMappingByDate.value = tempList

    }

    private fun getFinalDateRangeFilter(): Pair<Long, Long> {
        val currentDateInMillis = System.currentTimeMillis()
        return if (dateRangeFilter.value.second.getDate() == currentDateInMillis.getDate())
            dateRangeFilter.value.copy(second = currentDateInMillis)
        else
            dateRangeFilter.value

    }

    private suspend fun addDeleteAttendanceEvent(
        subjectAttendanceHistoryState: SubjectAttendanceHistoryState,
        date: Long
    ) {
        val event = eventWriterHelperImpl.createDeleteAttendanceEvent(
            subjectEntity = subjectAttendanceHistoryState.subjectEntity,
            smallGroupSubTabUiModel = smallGroupDetails.value,
            date = date
        )
        onEvent(EventWriterEvents.SaveAttendanceEvent(event, listOf()))
    }

}

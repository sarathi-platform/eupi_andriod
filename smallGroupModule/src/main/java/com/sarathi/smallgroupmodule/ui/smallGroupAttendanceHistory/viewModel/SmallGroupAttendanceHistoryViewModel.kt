package com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nudge.core.DEFAULT_DATE_RANGE_DURATION
import com.nudge.core.getDayPriorCurrentTimeMillis
import com.nudge.core.ui.events.CommonEvents
import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupSubTabUiModel
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import com.sarathi.smallgroupmodule.data.model.SubjectAttendanceHistoryState
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.domain.useCase.SmallGroupAttendanceHistoryUseCase
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.presentation.event.SmallGroupAttendanceEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SmallGroupAttendanceHistoryViewModel @Inject constructor(
    private val smallGroupAttendanceHistoryUseCase: SmallGroupAttendanceHistoryUseCase
) : BaseViewModel() {

    private val _smallGroupDetails: MutableState<SmallGroupSubTabUiModel> =
        mutableStateOf(SmallGroupSubTabUiModel.getEmptyModel())
    val smallGroupDetails: State<SmallGroupSubTabUiModel> get() = _smallGroupDetails

    val isAttendanceAvailable: MutableState<Boolean> = mutableStateOf(false)

    private val _dateRangeFilter: MutableState<Pair<Long, Long>> = mutableStateOf(
        Pair(
            getDayPriorCurrentTimeMillis(DEFAULT_DATE_RANGE_DURATION), System.currentTimeMillis()
        )
    )
    val dateRangeFilter: State<Pair<Long, Long>> get() = _dateRangeFilter

    private val _subjectAttendanceHistoryStateList: MutableState<List<SubjectAttendanceHistoryState>> =
        mutableStateOf(
            mutableListOf()
        )
    val subjectAttendanceHistoryStateList: State<List<SubjectAttendanceHistoryState>> get() = _subjectAttendanceHistoryStateList

    private val _subjectAttendanceHistoryStateMappingByDate: MutableState<Map<Long, List<SubjectAttendanceHistoryState>>> =
        mutableStateOf(
            mutableMapOf()
        )
    val subjectAttendanceHistoryStateMappingByDate: State<Map<Long, List<SubjectAttendanceHistoryState>>> get() = _subjectAttendanceHistoryStateMappingByDate

    override fun <T> onEvent(event: T) {
        when (event) {
            is SmallGroupAttendanceEvent.LoadSmallGroupDetailsForSmallGroupIdEvent -> {

                viewModelScope.launch(Dispatchers.IO) {

                    val details =
                        smallGroupAttendanceHistoryUseCase.fetchSmallGroupDetailsFromDbUseCase.invoke(
                            event.smallGroupId
                        )

//                    onEvent(SmallGroupAttendanceEvent.LoadSmallGroupAttendanceHistoryOnDateRangeUpdateEvent)
                    _subjectAttendanceHistoryStateList.value =
                        smallGroupAttendanceHistoryUseCase.fetchSmallGroupAttendanceHistoryFromDbUseCase.invoke(
                            event.smallGroupId,
                            dateRangeFilter.value
                        ).sortedByDescending { it.date }
                    _subjectAttendanceHistoryStateMappingByDate.value =
                        subjectAttendanceHistoryStateList.value.groupBy { it.date }

                    if (_subjectAttendanceHistoryStateMappingByDate.value.isEmpty())
                        isAttendanceAvailable.value = false
                    else
                        isAttendanceAvailable.value = true

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
                            .invoke(smallGroupDetails.value.smallGroupId, dateRangeFilter.value)
                    _subjectAttendanceHistoryStateMappingByDate.value =
                        subjectAttendanceHistoryStateList.value.groupBy { it.date }
                }

            }

            is CommonEvents.UpdateDateRange -> {
                if (event.startDate != null && event.endDate != null) {
                    _dateRangeFilter.value =
                        _dateRangeFilter.value.copy(event.startDate!!, event.endDate!!)
//                    Pair(event.startDate, event.endDate)
                }
            }
        }
    }

}

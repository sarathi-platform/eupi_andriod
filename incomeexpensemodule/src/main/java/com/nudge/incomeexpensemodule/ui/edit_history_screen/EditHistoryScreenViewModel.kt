package com.nudge.incomeexpensemodule.ui.edit_history_screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nudge.core.DEFAULT_DATE_RANGE_DURATION
import com.nudge.core.getDayPriorCurrentTimeMillis
import com.nudge.core.helper.TranslationEnum
import com.nudge.core.ui.events.CommonEvents
import com.nudge.core.utils.CoreLogger
import com.nudge.core.value
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodEventMappingEntity
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchSubjectLivelihoodEventMappingUseCase
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditHistoryScreenViewModel @Inject constructor(private val fetchSubjectLivelihoodEventMappingUseCase: FetchSubjectLivelihoodEventMappingUseCase) :
    BaseViewModel() {
    private val tag = EditHistoryScreenViewModel::class.java.simpleName
    private val _subjectLivelihoodEventSummaryUiModelList =
        mutableListOf<SubjectLivelihoodEventMappingEntity>()
    private val subjectLivelihoodEventSummaryUiModelList: List<SubjectLivelihoodEventMappingEntity> get() = _subjectLivelihoodEventSummaryUiModelList
    private val _filterSubjectLivelihoodEventSummaryUiModelList =
        mutableListOf<SubjectLivelihoodEventMappingEntity>()
    val filterSubjectLivelihoodEventSummaryUiModelList: List<SubjectLivelihoodEventMappingEntity> get() = _filterSubjectLivelihoodEventSummaryUiModelList

    val showCustomDatePicker = mutableStateOf(false)
    private val _dateRangeFilter: MutableState<Pair<Long, Long>> = mutableStateOf(
        Pair(
            getDayPriorCurrentTimeMillis(DEFAULT_DATE_RANGE_DURATION), System.currentTimeMillis()
        )
    )

    val dateRangeFilter: State<Pair<Long, Long>> get() = _dateRangeFilter

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitEditHistoryState -> {
                setTranslationConfig()
                loadEditHistoryData(transactionId = event.transactionId)
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(event.showLoader)
            }

            is CommonEvents.UpdateDateRange -> {
                if (event.startDate != null && event.endDate != null) {
                    _dateRangeFilter.value =
                        _dateRangeFilter.value.copy(event.startDate!!, event.endDate!!)
                    updateEventsHistoryList()
                }
            }
        }
    }

    private fun loadEditHistoryData(transactionId: String) {
        ioViewModelScope {
            try {
                _subjectLivelihoodEventSummaryUiModelList.clear()
                fetchSubjectLivelihoodEventMappingUseCase.getSubjectLivelihoodEventMappingListForTransactionIdFromDb(
                    transactionId = transactionId
                )?.let {
                    _subjectLivelihoodEventSummaryUiModelList.addAll(
                        it
                    )
                }
                _filterSubjectLivelihoodEventSummaryUiModelList.clear()
                _filterSubjectLivelihoodEventSummaryUiModelList.addAll(
                    _subjectLivelihoodEventSummaryUiModelList
                )
            } catch (ex: Exception) {
                CoreLogger.e(
                    tag = tag,
                    msg = "loadEditHistoryData: Exception -> ${ex.message}",
                    ex = ex
                )
            } finally {
                withContext(mainDispatcher) {
                    onEvent(LoaderEvent.UpdateLoaderState(false))
                }
            }
        }
    }

    private fun updateEventsHistoryList() {
        val result =
            _subjectLivelihoodEventSummaryUiModelList
        _filterSubjectLivelihoodEventSummaryUiModelList.clear()
        _filterSubjectLivelihoodEventSummaryUiModelList.addAll(result.filter {
            (it.date.value() >= dateRangeFilter.value.first) && (it.date.value() <= dateRangeFilter.value.second)
        })
    }

    override fun getScreenName(): TranslationEnum {
        return TranslationEnum.EditHistoryScreen
    }
}
package com.nudge.incomeexpensemodule.ui.data_summary_screen.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.nudge.core.enums.SubTabs
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodEventMappingEntity
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchSubjectIncomeExpenseSummaryUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchSubjectLivelihoodEventMappingUseCase
import com.sarathi.dataloadingmangement.repository.liveihood.GetLivelihoodMappingForSubjectFromDbRepository
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DataSummaryScreenViewModel @Inject constructor(
    private val getLivelihoodMappingForSubjectFromDbRepository: GetLivelihoodMappingForSubjectFromDbRepository,
    private val fetchSubjectLivelihoodEventMappingUseCase: FetchSubjectLivelihoodEventMappingUseCase,
    private val fetchSubjectIncomeExpenseSummaryUseCase: FetchSubjectIncomeExpenseSummaryUseCase
) : BaseViewModel() {
    private val _livelihoodEvent = mutableStateListOf<List<SubjectLivelihoodEventMappingEntity>>()
    val livelihoodEvent: SnapshotStateList<List<SubjectLivelihoodEventMappingEntity>> get() = _livelihoodEvent
    val countMap: MutableMap<SubTabs, Int> = mutableMapOf()

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataSummaryScreenState -> {
                loadAddDataSummaryData(subjectId = event.subjectId)
            }
        }
    }

    private fun loadAddDataSummaryData(subjectId: Int) {
        ioViewModelScope {
            fetchSubjectLivelihoodEventMappingUseCase.getSubjectLivelihoodEventMappingListFromDb(
                subjectId = subjectId
            )?.let {
                if (it.isNotEmpty() && it.size != 0) {
                    countMap.put(SubTabs.All, it.size)
                    _livelihoodEvent.add(it)
                }
            }

        }
    }
}
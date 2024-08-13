package com.nudge.incomeexpensemodule.ui.screens.dataTab.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.nudge.core.enums.SubTabs
import com.nudge.core.model.uiModel.LivelihoodModel
import com.nudge.incomeexpensemodule.events.DataTabEvents
import com.nudge.incomeexpensemodule.ui.screens.dataTab.domain.useCase.DataTabUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.GetLivelihoodListFromDbUseCase
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.IncomeExpenseSummaryUiModel
import com.sarathi.dataloadingmangement.model.uiModel.livelihood.SubjectEntityWithLivelihoodMappingUiModel
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DataTabScreenViewModel @Inject constructor(
    private val dataTabUseCase: DataTabUseCase,
    private val getLivelihoodListFromDbUseCase: GetLivelihoodListFromDbUseCase,
) : BaseViewModel() {

    private val _filters = mutableStateListOf<LivelihoodModel>()
    val filters: SnapshotStateList<LivelihoodModel> get() = _filters

    val selectedFilterValue = mutableStateOf(DEFAULT_FILTER_INDEX)

    val countMap: MutableMap<SubTabs, Int> = mutableMapOf()

    val isFilterApplied = mutableStateOf(false)

    val _subjectList: MutableState<List<SubjectEntityWithLivelihoodMappingUiModel>> =
        mutableStateOf(mutableListOf())
    val subjectList: State<List<SubjectEntityWithLivelihoodMappingUiModel>> get() = _subjectList

    private val _filteredSubjectList: MutableState<List<SubjectEntityWithLivelihoodMappingUiModel>> =
        mutableStateOf(mutableListOf())
    val filteredSubjectList: State<List<SubjectEntityWithLivelihoodMappingUiModel>> get() = _filteredSubjectList

    private val _incomeExpenseSummaryUiModel =
        mutableStateMapOf<Int, IncomeExpenseSummaryUiModel?>()
    val incomeExpenseSummaryUiModel: SnapshotStateMap<Int, IncomeExpenseSummaryUiModel?> get() = _incomeExpenseSummaryUiModel

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                loadAddDataForDataTab(isRefresh = false)
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }

            is DataTabEvents.FilterApplied -> {
                applyFilter(event.livelihoodModel)
            }
        }
    }

    private fun applyFilter(livelihoodModel: LivelihoodModel) {
        selectedFilterValue.value = livelihoodModel.livelihoodId
        isFilterApplied.value = selectedFilterValue.value != DEFAULT_FILTER_INDEX
    }

    private fun loadAddDataForDataTab(isRefresh: Boolean) {
        onEvent(LoaderEvent.UpdateLoaderState(true))
        ioViewModelScope {
            dataTabUseCase.invoke(isRefresh) { isSuccess, successMsg ->
                if (isSuccess)
                    initDataTab()
                else
                    onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private fun initDataTab() {
        ioViewModelScope {
            _subjectList.value =
                dataTabUseCase.fetchDidiDetailsWithLivelihoodMappingUseCase.invoke()
            _filteredSubjectList.value = subjectList.value

            _incomeExpenseSummaryUiModel.clear()
            _incomeExpenseSummaryUiModel.putAll(
                dataTabUseCase.fetchSubjectIncomeExpenseSummaryUseCase.getSummaryForSubjects(
                    subjectList.value
                )
            )

            createFilterBottomSheetList()

            countMap.put(SubTabs.All, filteredSubjectList.value.size)
            withContext(mainDispatcher) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private suspend fun createFilterBottomSheetList() {
        _filters.clear()
        _filters.add(LivelihoodModel.getAllFilter())
        _filters.addAll(getLivelihoodListFromDbUseCase.invoke().distinctBy { it.livelihoodId })
    }

}

const val DEFAULT_FILTER_INDEX = 0

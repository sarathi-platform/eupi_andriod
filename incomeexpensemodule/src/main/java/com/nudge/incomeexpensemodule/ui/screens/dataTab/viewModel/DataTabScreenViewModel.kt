package com.nudge.incomeexpensemodule.ui.screens.dataTab.viewModel

import android.text.TextUtils
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.nudge.core.enums.SubTabs
import com.nudge.core.getCurrentTimeInMillis
import com.nudge.core.getDayPriorCurrentTimeMillis
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

    var lastEventDateMapForSubject: Map<Int, Long> = hashMapOf()

    var livelihoodModelList: List<LivelihoodModel> = listOf()

    val showAssetDialog: MutableState<Triple<Boolean, Int, List<Int>>> =
        mutableStateOf(Triple(false, -1, listOf()))

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

            is DataTabEvents.ShowAssetDialogForSubject -> {
                showAssetDialog.value =
                    Triple(event.showDialog, event.subjectId, event.livelihoodIds)
            }

            is DataTabEvents.LivelihoodFilterApplied -> {
                applyFilter(event.livelihoodId)
            }

            is DataTabEvents.OnSearchQueryChanged -> {
                onSearchQueryChanged(event.searchQuery)
            }
        }
    }

    private fun onSearchQueryChanged(searchQuery: String) {
        val filteredList = if (isFilterApplied.value) {
            getFilteredList(LIVELIHOOD_FILTER, selectedFilterValue.value)
        } else {
            subjectList.value
        }

        _filteredSubjectList.value = if (!TextUtils.isEmpty(searchQuery)) {
            filteredList.filter { it.subjectName.toLowerCase().contains(searchQuery.toLowerCase()) }
        } else {
            filteredList
        }

    }

    private fun applyFilter(livelihoodId: Int) {
        selectedFilterValue.value = livelihoodId
        isFilterApplied.value = selectedFilterValue.value != DEFAULT_FILTER_INDEX
        _filteredSubjectList.value = if (isFilterApplied.value) {
            getFilteredList(LIVELIHOOD_FILTER, selectedFilterValue.value)
        } else {
            subjectList.value
        }

    }

    private fun <T> getFilteredList(
        filterType: String,
        filterValues: T
    ): List<SubjectEntityWithLivelihoodMappingUiModel> {
        return when (filterType) {
            LIVELIHOOD_FILTER -> {
                val livelihoodId = filterValues as Int
                subjectList.value.filter { it.primaryLivelihoodId == livelihoodId || it.secondaryLivelihoodId == livelihoodId }
            }

            else -> {
                subjectList.value
            }
        }
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
            val currentTime = getCurrentTimeInMillis()
            _incomeExpenseSummaryUiModel.putAll(
                dataTabUseCase.fetchSubjectIncomeExpenseSummaryUseCase.getSummaryForSubjectForDuration(
                    subjectLivelihoodMappingEntityList = subjectList.value,
                    durationStart = getDayPriorCurrentTimeMillis(currentTime),
                    durationEnd = currentTime
                )
            )

            lastEventDateMapForSubject =
                dataTabUseCase.fetchSubjectLivelihoodEventHistoryUseCase.invoke(subjectList.value.map { it.subjectId })

            livelihoodModelList =
                getLivelihoodListFromDbUseCase.invoke().distinctBy { it.livelihoodId }

            createFilterBottomSheetList(livelihoodModelList)

            countMap.put(SubTabs.All, filteredSubjectList.value.size)
            withContext(mainDispatcher) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private suspend fun createFilterBottomSheetList(livelihoodModelsList: List<LivelihoodModel>) {
        _filters.clear()
        _filters.add(LivelihoodModel.getAllFilter())
        _filters.addAll(livelihoodModelsList)
    }

}

const val LIVELIHOOD_FILTER = "livelihood_filter"

const val DEFAULT_FILTER_INDEX = 0

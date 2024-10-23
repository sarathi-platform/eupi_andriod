package com.nudge.incomeexpensemodule.ui.screens.dataTab.viewModel

import android.text.TextUtils
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.nudge.core.DEFAULT_DATE_RANGE_DURATION
import com.nudge.core.TabsCore
import com.nudge.core.WEEK_DURATION_RANGE
import com.nudge.core.enums.SubTabs
import com.nudge.core.enums.TabsEnum
import com.nudge.core.getCurrentTimeInMillis
import com.nudge.core.getDayPriorCurrentTimeMillis
import com.nudge.core.helper.TranslationEnum
import com.nudge.core.model.uiModel.LivelihoodModel
import com.nudge.incomeexpensemodule.events.DataTabEvents
import com.nudge.incomeexpensemodule.ui.screens.dataTab.domain.useCase.DataTabUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.GetLivelihoodListFromDbUseCase
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.IncomeExpenseSummaryUiModel
import com.sarathi.dataloadingmangement.model.uiModel.livelihood.DataTabScreenUiModel
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

    val tabs = listOf<SubTabs>(SubTabs.All, SubTabs.NoEntryMonthTab, SubTabs.NoEntryWeekTab)

    private val _filters = mutableStateListOf<LivelihoodModel>()
    val filters: SnapshotStateList<LivelihoodModel> get() = _filters

    val selectedFilterValue = mutableStateOf(DEFAULT_FILTER_INDEX)

    val countMap: MutableMap<SubTabs, Int> = mutableMapOf()

    val isFilterApplied = mutableStateOf(false)
    val isSortApplied = mutableStateOf(false)

    val _subjectList: MutableState<List<SubjectEntityWithLivelihoodMappingUiModel>> =
        mutableStateOf(mutableListOf())
    val subjectList: State<List<SubjectEntityWithLivelihoodMappingUiModel>> get() = _subjectList

    private val _filteredSubjectList: MutableState<List<SubjectEntityWithLivelihoodMappingUiModel>> =
        mutableStateOf(mutableListOf())
    private val filteredSubjectList: State<List<SubjectEntityWithLivelihoodMappingUiModel>> get() = _filteredSubjectList

    private val _filteredDataTabScreenUiEntityList: MutableState<List<DataTabScreenUiModel>> =
        mutableStateOf(mutableListOf())
    val filteredDataTabScreenUiEntityList: State<List<DataTabScreenUiModel>> get() = _filteredDataTabScreenUiEntityList

    private val _incomeExpenseSummaryUiModel =
        mutableStateMapOf<Int, IncomeExpenseSummaryUiModel?>()
    val incomeExpenseSummaryUiModel: SnapshotStateMap<Int, IncomeExpenseSummaryUiModel?> get() = _incomeExpenseSummaryUiModel

    private var lastEventDateMapForSubject: Map<Int, Long> = hashMapOf()

    var livelihoodModelList: List<LivelihoodModel> = listOf()

    val showAssetDialog: MutableState<Triple<Boolean, Int, List<Int>>> =
        mutableStateOf(Triple(false, -1, listOf()))

    override fun <T> onEvent(event: T) {
        super.onEvent(event)
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

            is DataTabEvents.LivelihoodSortApplied -> {
                if (isSortApplied.value) {
                    _filteredDataTabScreenUiEntityList.value =
                        _filteredDataTabScreenUiEntityList.value.sortedByDescending { it.lastUpdated }
                } else {
                    _filteredDataTabScreenUiEntityList.value =
                        _filteredDataTabScreenUiEntityList.value.sortedBy { it.lastUpdated }

                }
            }

            is DataTabEvents.OnSubTabChanged -> {
                updateDataTabScreenUiEntityListForSubTab(tabs[TabsCore.getSubTabForTabIndex(TabsEnum.DataTab.tabIndex)])
            }
        }
    }

    private fun onSearchQueryChanged(searchQuery: String) {
        var filteredList = if (isFilterApplied.value) {
            getFilteredList(LIVELIHOOD_FILTER, selectedFilterValue.value)
        } else {
            subjectList.value
        }

        _filteredSubjectList.value = if (!TextUtils.isEmpty(searchQuery)) {
            filteredList.filter { it.subjectName.toLowerCase().contains(searchQuery.toLowerCase()) }
        } else {
            filteredList
        }
        updateDataTabScreenUiEntityList()
    }

    private fun applyFilter(livelihoodId: Int) {
        selectedFilterValue.value = livelihoodId
        isFilterApplied.value = selectedFilterValue.value != DEFAULT_FILTER_INDEX
        _filteredSubjectList.value = if (isFilterApplied.value) {
            getFilteredList(LIVELIHOOD_FILTER, selectedFilterValue.value)
        } else {
            subjectList.value
        }
        updateDataTabScreenUiEntityList()

    }

    private fun <T> getFilteredList(
        filterType: String,
        filterValues: T
    ): List<SubjectEntityWithLivelihoodMappingUiModel> {
        return when (filterType) {
            LIVELIHOOD_FILTER -> {
                val livelihoodId = filterValues as Int
                subjectList.value.filter { it.livelihoodId == livelihoodId }
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

            updateDataTabScreenUiEntityList()

            livelihoodModelList =
                getLivelihoodListFromDbUseCase.getLivelihoodListForFilterUi()
                    .distinctBy { it.livelihoodId }

            createFilterBottomSheetList(livelihoodModelList)

            updateCountMap()

            withContext(mainDispatcher) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private fun updateDataTabScreenUiEntityList(): List<DataTabScreenUiModel> {
        _filteredDataTabScreenUiEntityList.value = DataTabScreenUiModel.getDataTabUiEntityList(
            filteredSubjectList.value,
            lastEventDateMapForSubject
        )
        onEvent(DataTabEvents.LivelihoodSortApplied)
        return filteredDataTabScreenUiEntityList.value
    }

    private fun updateDataTabScreenUiEntityListForSubTab(subTabs: SubTabs) {
        val currentTime = getCurrentTimeInMillis()
        when (subTabs) {
            SubTabs.All -> {
                _filteredSubjectList.value = subjectList.value
                updateDataTabScreenUiEntityList()
            }

            SubTabs.NoEntryWeekTab -> {
                _filteredDataTabScreenUiEntityList.value =
                    DataTabScreenUiModel.getDataTabUiEntityList(
                        filteredSubjectList.value,
                        lastEventDateMapForSubject
                    ).filterNot {
                        it.lastUpdated <= currentTime && it.lastUpdated >= getDayPriorCurrentTimeMillis(
                            WEEK_DURATION_RANGE
                        )
                    }
            }

            SubTabs.NoEntryMonthTab -> {
                _filteredDataTabScreenUiEntityList.value =
                    DataTabScreenUiModel.getDataTabUiEntityList(
                        filteredSubjectList.value,
                        lastEventDateMapForSubject
                    ).filterNot {
                        it.lastUpdated <= currentTime && it.lastUpdated >= getDayPriorCurrentTimeMillis(
                            DEFAULT_DATE_RANGE_DURATION
                        )
                    }
            }

            else -> {
                _filteredSubjectList.value = subjectList.value
            }
        }
    }

    private fun updateCountMap() {

        countMap.put(SubTabs.All, filteredDataTabScreenUiEntityList.value.size)
        countMap.put(
            SubTabs.NoEntryWeekTab,
            getLastEventMapListForSubTab(SubTabs.NoEntryWeekTab)
        )
        countMap.put(
            SubTabs.NoEntryMonthTab,
            getLastEventMapListForSubTab(SubTabs.NoEntryMonthTab)
        )
    }

    private fun getLastEventMapListForSubTab(subTabs: SubTabs): Int {
        val currentTime = getCurrentTimeInMillis()
        return when (subTabs) {
            SubTabs.All -> {
                lastEventDateMapForSubject.size
            }

            SubTabs.NoEntryWeekTab -> {
                filteredDataTabScreenUiEntityList.value.filterNot {
                    it.lastUpdated <= currentTime && it.lastUpdated >= getDayPriorCurrentTimeMillis(
                        WEEK_DURATION_RANGE
                    )
                }.size
            }

            SubTabs.NoEntryMonthTab -> {
                filteredDataTabScreenUiEntityList.value.filterNot {
                    it.lastUpdated <= currentTime && it.lastUpdated >= getDayPriorCurrentTimeMillis(
                        DEFAULT_DATE_RANGE_DURATION
                    )
                }.size
            }

            else -> {
                lastEventDateMapForSubject.size
            }
        }
    }

    private suspend fun createFilterBottomSheetList(livelihoodModelsList: List<LivelihoodModel>) {
        _filters.clear()
        _filters.add(LivelihoodModel.getAllFilter())
        _filters.addAll(livelihoodModelsList)
    }

    override fun refreshData() {
        super.refreshData()
        loadAddDataForDataTab(isRefresh = true)


    }

    override fun getScreenName(): TranslationEnum {
        return TranslationEnum.DataTabScreen
    }
}


const val LIVELIHOOD_FILTER = "livelihood_filter"

const val DEFAULT_FILTER_INDEX = 0

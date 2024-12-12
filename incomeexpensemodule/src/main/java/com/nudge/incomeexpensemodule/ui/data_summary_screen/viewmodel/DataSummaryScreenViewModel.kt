package com.nudge.incomeexpensemodule.ui.data_summary_screen.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.example.incomeexpensemodule.R
import com.nudge.core.DEFAULT_DATE_RANGE_DURATION
import com.nudge.core.NOT_DECIDED_LIVELIHOOD_ID
import com.nudge.core.TabsCore
import com.nudge.core.enums.SubTabs
import com.nudge.core.enums.TabsEnum
import com.nudge.core.getCurrentTimeInMillis
import com.nudge.core.getDayPriorCurrentTimeMillis
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.model.uiModel.LivelihoodModel
import com.nudge.core.ui.events.CommonEvents
import com.nudge.core.ui.events.DialogEvents
import com.nudge.core.utils.CoreLogger
import com.nudge.core.value
import com.nudge.incomeexpensemodule.events.DataSummaryScreenEvents
import com.nudge.incomeexpensemodule.utils.IncomeExpenseConstants
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchLivelihoodEventUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchSubjectIncomeExpenseSummaryUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchSubjectLivelihoodEventMappingUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.GetLivelihoodListFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.GetSubjectLivelihoodMappingFromUseCase
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.IncomeExpenseSummaryUiModel
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventUiModel
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.SubjectLivelihoodEventSummaryUiModel
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DataSummaryScreenViewModel @Inject constructor(
    private val getSubjectLivelihoodMappingFromUseCase: GetSubjectLivelihoodMappingFromUseCase,
    private val fetchSubjectLivelihoodEventMappingUseCase: FetchSubjectLivelihoodEventMappingUseCase,
    private val fetchLivelihoodEventUseCase: FetchLivelihoodEventUseCase,
    private val fetchSubjectIncomeExpenseSummaryUseCase: FetchSubjectIncomeExpenseSummaryUseCase,
    private val getLivelihoodListFromDbUseCase: GetLivelihoodListFromDbUseCase,
) : BaseViewModel() {

    private val tag = DataSummaryScreenViewModel::class.java.simpleName
    private val ALL_DATA = -1

    val tabs = listOf<SubTabs>(
        SubTabs.LastWeekTab,
        SubTabs.LastMonthTab,
        SubTabs.Last3MonthsTab,
        SubTabs.CustomDateRange
    )

    var subjectId: Int = -1

    val areEventsNotAvailableForSubject: MutableState<Boolean> = mutableStateOf(true)

    val livelihoodEventMap = HashMap<Int, List<LivelihoodEventUiModel>>()

    private val _subjectLivelihoodEventSummaryUiModelList =
        mutableListOf<SubjectLivelihoodEventSummaryUiModel>()
    private val subjectLivelihoodEventSummaryUiModelList: List<SubjectLivelihoodEventSummaryUiModel> get() = _subjectLivelihoodEventSummaryUiModelList

    private val _filteredSubjectLivelihoodEventSummaryUiModelList =
        mutableStateListOf<SubjectLivelihoodEventSummaryUiModel>()
    val filteredSubjectLivelihoodEventSummaryUiModelList: SnapshotStateList<SubjectLivelihoodEventSummaryUiModel> get() = _filteredSubjectLivelihoodEventSummaryUiModelList

    private val _livelihoodModel = mutableListOf<LivelihoodModel>()
    val livelihoodModel: List<LivelihoodModel> get() = _livelihoodModel

    private val _incomeExpenseSummaryUiModel: SnapshotStateMap<Int, IncomeExpenseSummaryUiModel?> =
        mutableStateMapOf()
    val incomeExpenseSummaryUiModel: SnapshotStateMap<Int, IncomeExpenseSummaryUiModel?> get() = _incomeExpenseSummaryUiModel

    private val _livelihoodDropdownList = mutableStateListOf<ValuesDto>()
    val livelihoodDropdownList: SnapshotStateList<ValuesDto> get() = _livelihoodDropdownList

    val eventsSubFilterList: List<ValuesDto> =
        listOf(ValuesDto(1, "All"), ValuesDto(2, "Assets"), ValuesDto(3, "Income/Expense"))

    val selectedLivelihood: MutableState<Int> = mutableStateOf(0)

    val selectedEventsSubFilter: MutableState<Int> = mutableStateOf(0)

    private val _showAssetDialog: MutableState<Boolean> = mutableStateOf(false)
    val showAssetDialog: State<Boolean> get() = _showAssetDialog

    var filtersTuple: Triple<Int, Int, Int> = Triple(0, 0, 0)

    val showCustomDatePicker = mutableStateOf(false)

    private val _dateRangeFilter: MutableState<Pair<Long, Long>> = mutableStateOf(
        Pair(
            getDayPriorCurrentTimeMillis(DEFAULT_DATE_RANGE_DURATION), System.currentTimeMillis()
        )
    )

    val dateRangeFilter: State<Pair<Long, Long>> get() = _dateRangeFilter

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataSummaryScreenState -> {
                loadAddDataSummaryData(subjectId = event.subjectId)
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(event.showLoader)
            }

            is DialogEvents.ShowDialogEvent -> {
                _showAssetDialog.value = event.showDialog
            }

            is CommonEvents.UpdateDateRange -> {
                if (event.startDate != null && event.endDate != null) {
                    _dateRangeFilter.value =
                        _dateRangeFilter.value.copy(event.startDate!!, event.endDate!!)
                }
            }

            is DataSummaryScreenEvents.CustomDateRangeFilterSelected -> {

                filtersTuple = Triple(
                    event.selectedTabIndex,
                    selectedLivelihood.value,
                    selectedEventsSubFilter.value
                )

                updateEventsList()

            }

            is DataSummaryScreenEvents.FilterDataForLivelihood -> {
                selectedLivelihood.value = event.livelihoodId

                filtersTuple = Triple(
                    TabsCore.getSubTabForTabIndex(TabsEnum.DataSummaryTab.tabIndex),
                    selectedLivelihood.value,
                    selectedEventsSubFilter.value
                )

                updateEventsList()

            }

            is DataSummaryScreenEvents.EventsSubFilterSelected -> {
                selectedEventsSubFilter.value = event.selectedValue

                filtersTuple = Triple(
                    TabsCore.getSubTabForTabIndex(TabsEnum.DataSummaryTab.tabIndex),
                    selectedLivelihood.value,
                    selectedEventsSubFilter.value
                )

                updateEventsList()

            }

            is DataSummaryScreenEvents.TabFilterSelected -> {

                filtersTuple = Triple(
                    event.selectedTabIndex,
                    selectedLivelihood.value,
                    selectedEventsSubFilter.value
                )

                updateEventsList()


            }
        }
    }

    private fun updateEventsList() {
        _filteredSubjectLivelihoodEventSummaryUiModelList.clear()
        _filteredSubjectLivelihoodEventSummaryUiModelList.addAll(
            getListForFilters(
                filtersTuple.first,
                filtersTuple.second,
                filtersTuple.third
            )
        )
    }

    private fun getListForFilters(
        selectedTabFilter: Int,
        livelihoodFilter: Int,
        eventsSubFilter: Int
    ): List<SubjectLivelihoodEventSummaryUiModel> {
        var result = if (livelihoodFilter == ALL_DATA) {
            livelihoodModel.mapNotNull { _livelihoodModel ->
                subjectLivelihoodEventSummaryUiModelList
                    .filter { it.livelihoodId == _livelihoodModel.livelihoodId }
                    .takeIf { it.isNotEmpty() }
            }.flatten()
        } else {
            subjectLivelihoodEventSummaryUiModelList.filter { it.livelihoodId == livelihoodFilter }
        }

        result =
            filterListForSelectedTab(if (selectedTabFilter == -1) 0 else selectedTabFilter, result)

        result = filterListForSubFilter(eventsSubFilter, result)


        return result

    }

    private fun filterListForSubFilter(
        eventsSubFilter: Int,
        resultAfterTabFilter: List<SubjectLivelihoodEventSummaryUiModel>
    ): List<SubjectLivelihoodEventSummaryUiModel> {
        val resultAfterSubFilter = when (eventsSubFilter) {
            1 -> {
                resultAfterTabFilter
            }

            2 -> {
                resultAfterTabFilter.filter { it.assetId != null }
            }

            3 -> {
                resultAfterTabFilter.filter { it.transactionAmount != null }
            }

            else -> {
                resultAfterTabFilter
            }
        }
        return resultAfterSubFilter
    }

    private fun filterListForSelectedTab(
        selectedTabFilter: Int,
        result: List<SubjectLivelihoodEventSummaryUiModel>
    ): List<SubjectLivelihoodEventSummaryUiModel> {
        val resultAfterTabFilter = when (tabs[selectedTabFilter].id) {
            SubTabs.LastWeekTab.id -> {
                result.filter {
                    (it.date.value() <= getCurrentTimeInMillis())
                            && (it.date.value() >= getDayPriorCurrentTimeMillis(
                        IncomeExpenseConstants.WEEK_DURATION
                    ))
                }

            }

            SubTabs.LastMonthTab.id -> {
                result.filter {
                    (it.date.value() <= getCurrentTimeInMillis())
                            && (it.date.value() >= getDayPriorCurrentTimeMillis(
                        IncomeExpenseConstants.MONTH_DURATION
                    ))
                }
            }

            SubTabs.Last3MonthsTab.id -> {
                result.filter {
                    (it.date.value() <= getCurrentTimeInMillis())
                            && (it.date.value() >= getDayPriorCurrentTimeMillis(
                        IncomeExpenseConstants.LAST_3_MONTH_DURATION
                    ))
                }
            }

            SubTabs.CustomDateRange.id -> {
                result.filter {
                    (it.date.value() >= dateRangeFilter.value.first) && (it.date.value() <= dateRangeFilter.value.second)
                }
            }

            else -> {
                result
            }
        }
        return resultAfterTabFilter
    }

    fun setPreviousScreenData(mSubjectId: Int) {
        subjectId = mSubjectId
    }
    //Todo UNCOMENT After livelihood Mapping Refactor

    private fun loadAddDataSummaryData(subjectId: Int) {

        ioViewModelScope {
            try {
                fetchSubjectLivelihoodEventMappingUseCase.getSubjectLivelihoodEventMappingListFromDb(
                    subjectId = subjectId
                )?.let {
                    areEventsNotAvailableForSubject.value = it.isNotEmpty()
                }
                val subjectLivelihoodMapping =
                    getSubjectLivelihoodMappingFromUseCase.invoke(subjectId)

                subjectLivelihoodMapping.let {

                    val livelihoodIds = listOf(
                        it.first()?.livelihoodId.value(),
                        it.last()?.livelihoodId.value()
                    ).filter { it != NOT_DECIDED_LIVELIHOOD_ID }//Filter not Decided

                    val livelihoodEventList = fetchLivelihoodEventUseCase.invoke(livelihoodIds)

                    livelihoodEventMap.putAll(livelihoodEventList.groupBy { it.livelihoodId })

                    _subjectLivelihoodEventSummaryUiModelList.clear()
                    _subjectLivelihoodEventSummaryUiModelList.addAll(
                        fetchSubjectLivelihoodEventMappingUseCase.invoke(subjectId)
                            .sortedByDescending { it.createdDate }
                    )
                    _subjectLivelihoodEventSummaryUiModelList.addAll(
                        fetchSubjectLivelihoodEventMappingUseCase.getLivelihoodEventsWithAssetAndMoneyEntryForDeletedSubject(
                            subjectId
                        ).sortedByDescending { it.createdDate }
                    )

                    _livelihoodModel.clear()
                    _livelihoodModel.addAll(
                        getLivelihoodListFromDbUseCase.invoke(livelihoodIds)
                    )

                    _incomeExpenseSummaryUiModel.clear()
                    _incomeExpenseSummaryUiModel.putAll(
                        fetchSubjectIncomeExpenseSummaryUseCase.getLivelihoodIncomeExpenseSummaryMap(
                            subjectId = subjectId,
                            subjectLivelihoodMappingEntity = it
                        )
                    )

                    createLivelihoodDropDownList()

                    with(TabsCore.getSubTabForTabIndex(TabsEnum.DataSummaryTab.tabIndex)) {
                        if (this.equals(-1))
                            TabsCore.setSubTabIndex(TabsEnum.DataSummaryTab.tabIndex, 0)
                        else
                            TabsCore.setSubTabIndex(TabsEnum.DataSummaryTab.tabIndex, this)

                    }

                    updateEventsList()

                }


            } catch (ex: Exception) {
                CoreLogger.e(
                    tag = tag,
                    msg = "loadAddDataSummaryData: Exception -> ${ex.message}",
                    ex = ex
                )
            } finally {
                withContext(mainDispatcher) {
                    onEvent(LoaderEvent.UpdateLoaderState(false))
                }
            }
        }
    }

    private suspend fun createLivelihoodDropDownList() {
        _livelihoodDropdownList.clear()
        if (livelihoodModel.size > 1) {
            CoreAppDetails.getContext()?.getString(R.string.all)?.let {
                _livelihoodDropdownList.add(ValuesDto(-1, it, false))
            }
        }
        livelihoodModel.forEach {
            _livelihoodDropdownList.add(ValuesDto(it.livelihoodId, it.name, false))
        }
        selectedLivelihood.value = livelihoodDropdownList.first().id
        selectedEventsSubFilter.value = eventsSubFilterList.first().id
        filtersTuple = Triple(
            TabsCore.getSubTabForTabIndex(TabsEnum.DataSummaryTab.tabIndex),
            selectedLivelihood.value,
            selectedEventsSubFilter.value
        )
    }

    fun getLivelihood(): IncomeExpenseSummaryUiModel? {
        if (selectedLivelihood.value == ALL_DATA) {
            val totalIncome = incomeExpenseSummaryUiModel.values.sumOf { it?.totalIncome ?: 0.0 }
            val totalExpense = incomeExpenseSummaryUiModel.values.sumOf { it?.totalExpense ?: 0.0 }

            val livelihoodAssetMap = incomeExpenseSummaryUiModel.values
                .mapNotNull { it?.livelihoodAssetMap }
                .flatMap { it.entries }
                .map { it.toPair() }
                .toMap()

            val totalAssetCountForLivelihood = incomeExpenseSummaryUiModel.values
                .mapNotNull { it?.totalAssetCountForLivelihood }
                .flatMap { it.entries }
                .map { it.toPair() }
                .toMap()

            val assetsCountWithValue = incomeExpenseSummaryUiModel.values
                .mapNotNull { it?.assetsCountWithValue }
                .flatten()

            val imageUriForLivelihood = incomeExpenseSummaryUiModel.values
                .mapNotNull { it?.imageUriForLivelihood }
                .flatMap { it.entries }
                .map { it.toPair() }
                .toMap()
            return IncomeExpenseSummaryUiModel(
                subjectId = subjectId,
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                livelihoodAssetMap = livelihoodAssetMap,
                totalAssetCountForLivelihood = totalAssetCountForLivelihood,
                assetsCountWithValue = assetsCountWithValue,
                imageUriForLivelihood = imageUriForLivelihood
            )
        }

        return selectedLivelihood.value?.let { incomeExpenseSummaryUiModel[it] }
    }

    fun getEventsList(): List<LivelihoodEventUiModel>? {
        return if (selectedLivelihood.value == ALL_DATA) {
            livelihoodModel.flatMap { _livelihoodModel ->
                livelihoodEventMap[_livelihoodModel.livelihoodId] ?: emptyList()
            }
        } else {
            livelihoodEventMap[selectedLivelihood.value]
        }
    }
}
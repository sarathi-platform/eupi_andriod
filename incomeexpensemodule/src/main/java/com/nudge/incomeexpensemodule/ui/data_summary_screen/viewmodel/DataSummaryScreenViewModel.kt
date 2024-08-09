package com.nudge.incomeexpensemodule.ui.data_summary_screen.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.nudge.core.enums.SubTabs
import com.nudge.core.model.uiModel.LivelihoodModel
import com.nudge.core.ui.events.DialogEvents
import com.nudge.core.utils.CoreLogger
import com.nudge.incomeexpensemodule.events.DataSummaryScreenEvents
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

    var subjectId: Int = -1

    val areEventsNotAvailableForSubject: MutableState<Boolean> = mutableStateOf(true)

    val livelihoodEventMap = HashMap<Int, List<LivelihoodEventUiModel>>()

    private val _subjectLivelihoodEventSummaryUiModelList =
        mutableListOf<SubjectLivelihoodEventSummaryUiModel>()
    private val subjectLivelihoodEventSummaryUiModelList: List<SubjectLivelihoodEventSummaryUiModel> get() = _subjectLivelihoodEventSummaryUiModelList

    private val _filteredSubjectLivelihoodEventSummaryUiModelList =
        mutableStateListOf<SubjectLivelihoodEventSummaryUiModel>()
    val filteredSubjectLivelihoodEventSummaryUiModelList: SnapshotStateList<SubjectLivelihoodEventSummaryUiModel> get() = _filteredSubjectLivelihoodEventSummaryUiModelList

    val countMap: MutableMap<SubTabs, Int> = mutableMapOf()

    private val _livelihoodModel = mutableListOf<LivelihoodModel>()
    val livelihoodModel: List<LivelihoodModel> get() = _livelihoodModel

    private val _incomeExpenseSummaryUiModel: MutableState<IncomeExpenseSummaryUiModel?> =
        mutableStateOf(IncomeExpenseSummaryUiModel.getDefaultIncomeExpenseSummaryUiModel(subjectId))
    val incomeExpenseSummaryUiModel: State<IncomeExpenseSummaryUiModel?> get() = _incomeExpenseSummaryUiModel

    private val _livelihoodDropdownList = mutableStateListOf<ValuesDto>()
    val livelihoodDropdownList: SnapshotStateList<ValuesDto> get() = _livelihoodDropdownList

    val selectedLivelihood: MutableState<Int> = mutableStateOf(0)

    private val _showAssetDialog: MutableState<Boolean> = mutableStateOf(false)
    val showAssetDialog: State<Boolean> get() = _showAssetDialog

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

            is DataSummaryScreenEvents.FilterDataForLivelihood -> {
                selectedLivelihood.value = event.livelihoodId
                _filteredSubjectLivelihoodEventSummaryUiModelList.clear()
                _filteredSubjectLivelihoodEventSummaryUiModelList.addAll(
                    subjectLivelihoodEventSummaryUiModelList.filter { it.livelihoodId == selectedLivelihood.value })
            }
        }
    }

    fun setPreviousScreenData(mSubjectId: Int) {
        subjectId = mSubjectId
    }

    private fun loadAddDataSummaryData(subjectId: Int) {
        ioViewModelScope {
            try {
                fetchSubjectLivelihoodEventMappingUseCase.getSubjectLivelihoodEventMappingListFromDb(
                    subjectId = subjectId
                )?.let {
                    if (it.isNotEmpty()) {
                        countMap.put(SubTabs.All, it.size)
                        areEventsNotAvailableForSubject.value = false
                    }
                }

                val subjectLivelihoodMapping =
                    getSubjectLivelihoodMappingFromUseCase.invoke(subjectId)

                subjectLivelihoodMapping?.let {

                    val livelihoodIds = listOf(
                        it.primaryLivelihoodId,
                        it.secondaryLivelihoodId
                    )

                    val livelihoodEventList = fetchLivelihoodEventUseCase.invoke(livelihoodIds)

                    livelihoodEventMap.putAll(livelihoodEventList.groupBy { it.livelihoodId })

                    _subjectLivelihoodEventSummaryUiModelList.clear()
                    _subjectLivelihoodEventSummaryUiModelList.addAll(
                        fetchSubjectLivelihoodEventMappingUseCase.invoke(subjectId)
                    )

                    _livelihoodModel.clear()
                    _livelihoodModel.addAll(
                        getLivelihoodListFromDbUseCase.invoke(livelihoodIds)
                    )

                    _incomeExpenseSummaryUiModel.value =
                        fetchSubjectIncomeExpenseSummaryUseCase.invoke(
                            subjectId = subjectId,
                            subjectLivelihoodMappingEntity = it
                        )

                    createLivelihoodDropDownList()

                    _filteredSubjectLivelihoodEventSummaryUiModelList.clear()
                    _filteredSubjectLivelihoodEventSummaryUiModelList.addAll(
                        subjectLivelihoodEventSummaryUiModelList.filter { it.livelihoodId == selectedLivelihood.value }
                    )
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
        livelihoodModel.forEach {
            _livelihoodDropdownList.add(ValuesDto(it.livelihoodId, it.name, false))
        }
        selectedLivelihood.value = livelihoodDropdownList.first().id
    }
}
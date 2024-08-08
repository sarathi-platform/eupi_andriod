package com.nudge.incomeexpensemodule.ui.data_summary_screen.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.nudge.core.enums.SubTabs
import com.nudge.core.model.uiModel.LivelihoodModel
import com.nudge.core.ui.events.DialogEvents
import com.nudge.incomeexpensemodule.events.DataSummaryScreenEvents
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodEventMappingEntity
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchSubjectIncomeExpenseSummaryUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchSubjectLivelihoodEventMappingUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.GetLivelihoodListFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.GetSubjectLivelihoodMappingFromUseCase
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.IncomeExpenseSummaryUiModel
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
    private val fetchSubjectIncomeExpenseSummaryUseCase: FetchSubjectIncomeExpenseSummaryUseCase,
    private val getLivelihoodListFromDbUseCase: GetLivelihoodListFromDbUseCase,
) : BaseViewModel() {

    private val _livelihoodEvent = mutableStateListOf<List<SubjectLivelihoodEventMappingEntity>>()
    val livelihoodEvent: SnapshotStateList<List<SubjectLivelihoodEventMappingEntity>> get() = _livelihoodEvent
    val countMap: MutableMap<SubTabs, Int> = mutableMapOf()

    private val _livelihoodModel = mutableListOf<LivelihoodModel>()
    val livelihoodModel: List<LivelihoodModel> get() = _livelihoodModel

    private val _incomeExpenseSummaryUiModel: MutableState<IncomeExpenseSummaryUiModel?> =
        mutableStateOf(null)
    val incomeExpenseSummaryUiModel: State<IncomeExpenseSummaryUiModel?> get() = _incomeExpenseSummaryUiModel

    private val _livelihoodDropdownList = mutableStateListOf<ValuesDto>()
    val livelihoodDropdownList: SnapshotStateList<ValuesDto> get() = _livelihoodDropdownList


    private val _showAssetDialog: MutableState<Boolean> = mutableStateOf(false)
    val showAssetDialog: State<Boolean> get() = _showAssetDialog

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataSummaryScreenState -> {
                onEvent(LoaderEvent.UpdateLoaderState(true))
                loadAddDataSummaryData(subjectId = event.subjectId)
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(event.showLoader)
            }

            is DialogEvents.ShowDialogEvent -> {
                _showAssetDialog.value = event.showDialog
            }

            is DataSummaryScreenEvents.FilterDataForLivelihood -> {

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

            val subjectLivelihoodMapping = getSubjectLivelihoodMappingFromUseCase.invoke(subjectId)

            subjectLivelihoodMapping?.let {
                _livelihoodModel.addAll(
                    getLivelihoodListFromDbUseCase(
                        livelihoodIds = listOf(
                            it.primaryLivelihoodId,
                            it.secondaryLivelihoodId
                        )
                    )
                )

                createLivelihoodDropDownList()

                _incomeExpenseSummaryUiModel.value = fetchSubjectIncomeExpenseSummaryUseCase(
                    subjectId = subjectId,
                    subjectLivelihoodMappingEntity = it
                )
            }

            withContext(mainDispatcher) {
                LoaderEvent.UpdateLoaderState(false)
            }
        }
    }

    private fun createLivelihoodDropDownList() {
        livelihoodModel.forEach {
            _livelihoodDropdownList.add(ValuesDto(it.livelihoodId, it.name, false))
        }
    }
}
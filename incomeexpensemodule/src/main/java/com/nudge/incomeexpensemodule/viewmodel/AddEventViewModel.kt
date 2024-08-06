package com.nudge.incomeexpensemodule.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.nudge.core.model.uiModel.LivelihoodModel
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchAssetUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchLivelihoodEventUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchProductUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.GetLivelihoodListFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.GetSubjectLivelihoodMappingFromUseCase
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddEventViewModel @Inject constructor(
    private val getSubjectLivelihoodMappingFromUseCase: GetSubjectLivelihoodMappingFromUseCase,
    private val getLivelihoodListFromDbUseCase: GetLivelihoodListFromDbUseCase,
    private val fetchLivelihoodEventUseCase: FetchLivelihoodEventUseCase,
    private val fetchAssetUseCase: FetchAssetUseCase,
    private val fetchProductUseCase: FetchProductUseCase
) : BaseViewModel() {

    private val _livelihoodDropdownValue = mutableStateListOf<ValuesDto>()
    val livelihoodDropdownValue: SnapshotStateList<ValuesDto> get() = _livelihoodDropdownValue


    private val _livelihoodEventDropdownValue = mutableStateListOf<ValuesDto>()
    val livelihoodEventDropdownValue: SnapshotStateList<ValuesDto> get() = _livelihoodEventDropdownValue

    private val _livelihoodAssetDropdownValue = mutableStateListOf<ValuesDto>()
    val livelihoodAssetDropdownValue: SnapshotStateList<ValuesDto> get() = _livelihoodAssetDropdownValue

    private val _livelihoodProductDropdownValue = mutableStateListOf<ValuesDto>()
    val livelihoodProductDropdownValue: SnapshotStateList<ValuesDto> get() = _livelihoodProductDropdownValue

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitAddEventState -> {
                fetchEventData(event.subjectId)
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }

        }
    }

    private fun fetchEventData(subjectId: Int) {
        ioViewModelScope {

            val livelihoodForDidi =
                getSubjectLivelihoodMappingFromUseCase.invoke(subjectId = subjectId)
            if (livelihoodForDidi != null) {
                val livelihoodDropDown = getLivelihoodListFromDbUseCase.invoke(
                    listOf(
                        livelihoodForDidi.primaryLivelihoodId,
                        livelihoodForDidi.secondaryLivelihoodId
                    )
                )
                _livelihoodDropdownValue.clear()
                _livelihoodDropdownValue.addAll(getLivelihooldDropValue(livelihoodDropDown))

            }
        }
    }

    fun onLivelihoodSelect(livelihoodId: Int) {
        ioViewModelScope {
            _livelihoodEventDropdownValue.clear()
            _livelihoodAssetDropdownValue.clear()
            _livelihoodProductDropdownValue.clear()

            _livelihoodEventDropdownValue.addAll(fetchLivelihoodEventUseCase.invoke(livelihoodId))
            _livelihoodAssetDropdownValue.addAll(fetchAssetUseCase.invoke(livelihoodId))
            _livelihoodProductDropdownValue.addAll(fetchProductUseCase.invoke(livelihoodId))

        }
    }


    private fun getLivelihooldDropValue(livelihoodForDidi: List<LivelihoodModel>): List<ValuesDto> {
        return livelihoodForDidi.map {
            ValuesDto(id = it.livelihoodId, value = it.name, isSelected = false)
        }
    }
}
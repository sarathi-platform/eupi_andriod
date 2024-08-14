package com.nudge.incomeexpensemodule.viewmodel

import android.text.TextUtils
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.nudge.core.BLANK_STRING
import com.nudge.core.getDate
import com.nudge.core.model.uiModel.LivelihoodModel
import com.nudge.core.value
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchAssetUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchLivelihoodEventUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchProductUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchSavedEventUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.SaveLivelihoodEventUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.WriteLivelihoodEventUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.GetLivelihoodListFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.GetSubjectLivelihoodMappingFromUseCase
import com.sarathi.dataloadingmangement.enums.LivelihoodEventDataCaptureTypeEnum
import com.sarathi.dataloadingmangement.enums.LivelihoodEventTypeDataCaptureMapping.Companion.getLivelihoodEventFromName
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventScreenData
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventUiModel
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddEventViewModel @Inject constructor(
    private val getSubjectLivelihoodMappingFromUseCase: GetSubjectLivelihoodMappingFromUseCase,
    private val getLivelihoodListFromDbUseCase: GetLivelihoodListFromDbUseCase,
    private val fetchLivelihoodEventUseCase: FetchLivelihoodEventUseCase,
    private val fetchAssetUseCase: FetchAssetUseCase,
    private val fetchProductUseCase: FetchProductUseCase,
    private val saveLivelihoodEventUseCase: SaveLivelihoodEventUseCase,
    private val fetchSavedEventUseCase: FetchSavedEventUseCase,
    private val writeLivelihoodEventUseCase: WriteLivelihoodEventUseCase
) : BaseViewModel() {

    val showDeleteDialog = mutableStateOf(false)
    private val _livelihoodDropdownValue = mutableStateListOf<ValuesDto>()
    val livelihoodDropdownValue: SnapshotStateList<ValuesDto> get() = _livelihoodDropdownValue


    private val _livelihoodEventDropdownValue = mutableStateListOf<ValuesDto>()
    val livelihoodEventDropdownValue: SnapshotStateList<ValuesDto> get() = _livelihoodEventDropdownValue

    private val _livelihoodAssetDropdownValue = mutableStateListOf<ValuesDto>()
    val livelihoodAssetDropdownValue: SnapshotStateList<ValuesDto> get() = _livelihoodAssetDropdownValue

    private val _livelihoodProductDropdownValue = mutableStateListOf<ValuesDto>()
    val livelihoodProductDropdownValue: SnapshotStateList<ValuesDto> get() = _livelihoodProductDropdownValue

    private var eventList: List<LivelihoodEventUiModel> = ArrayList<LivelihoodEventUiModel>()
    var questionVisibilityMap = mutableStateMapOf<LivelihoodEventDataCaptureTypeEnum, Boolean>()

    var eventType: String = ""
    var selectedLivelihoodId = mutableStateOf(-1)
    var selectedAssetTypeId = mutableStateOf(-1)
    var selectedProductId = mutableStateOf(-1)
    var selectedEventId = mutableStateOf(-1)
    var assetCount = mutableStateOf("")
    var amount = mutableStateOf("")
    var selectedDate = mutableStateOf("")
    var isSubmitButtonEnable = mutableStateOf(false)
    var selectedDateInLong: Long = 0

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitAddEventState -> {
                fetchEventData(event.subjectId, event.transactionId)
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }

        }
    }

    private fun fetchEventData(subjectId: Int, transactionId: String) {
        ioViewModelScope {
            val savedEvent = fetchSavedEventUseCase.fetchEvent(
                subjectId = subjectId,
                transactionId = transactionId
            )
            LivelihoodEventDataCaptureTypeEnum.values().forEach {
                questionVisibilityMap[it] = false
            }
            if (savedEvent != null) {

                selectedDate.value = savedEvent.date.getDate()
                selectedDateInLong = savedEvent.date
                selectedLivelihoodId.value = savedEvent.livelihoodId
                selectedEventId.value = savedEvent.eventId
                selectedProductId.value = savedEvent.productId
                eventType = savedEvent.selectedEvent.name
                amount.value = savedEvent.amount.toString()
                selectedAssetTypeId.value = savedEvent.assetType
                assetCount.value = savedEvent.assetCount.toString()
                getLivelihoodEventFromName(eventType).livelihoodEventDataCaptureTypes.forEach {
                    questionVisibilityMap[it] = questionVisibilityMap.containsKey(it)
                }

                fetchAssestEventProductValues()
            }
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

            validateForm()
        }
    }

    fun onLivelihoodSelect(livelihoodId: Int) {
        resetForm()
        ioViewModelScope {
            selectedLivelihoodId.value = livelihoodId
            _livelihoodEventDropdownValue.clear()
            _livelihoodAssetDropdownValue.clear()
            _livelihoodProductDropdownValue.clear()

            fetchAssestEventProductValues()
            validateForm()
        }
    }

    private suspend fun fetchAssestEventProductValues() {
        eventList = fetchLivelihoodEventUseCase.invoke(selectedLivelihoodId.value)
        _livelihoodEventDropdownValue.addAll(eventList.map {
            ValuesDto(
                id = it.id,
                value = it.name,
                isSelected = it.id == selectedEventId.value,
                originalName = it.originalName
            )
        })
        _livelihoodAssetDropdownValue.addAll(
            fetchAssetUseCase.invoke(
                selectedLivelihoodId.value,
                selectedAssetTypeId.value
            )
        )
        _livelihoodProductDropdownValue.addAll(
            fetchProductUseCase.invoke(
                selectedLivelihoodId.value,
                selectedProductId.value
            )
        )
    }

    private fun resetForm() {
        selectedEventId.value = -1
        getLivelihoodEventFromName(eventType).livelihoodEventDataCaptureTypes.forEach {
            questionVisibilityMap[it] = false

        }
    }


    private fun getLivelihooldDropValue(livelihoodForDidi: List<LivelihoodModel>): List<ValuesDto> {
        return livelihoodForDidi.map {
            ValuesDto(
                id = it.livelihoodId,
                value = it.name,
                originalName = it.originalName,
                isSelected = if (selectedLivelihoodId.value == it.livelihoodId) true else false
            )
        }
    }

    fun onEventSelected(selectedValue: ValuesDto) {
        resetForm()
        eventType = eventList.find { it.id == selectedValue.id }?.eventType ?: BLANK_STRING

        selectedEventId.value = selectedValue.id
        selectedProductId.value = -1
        selectedAssetTypeId.value = -1
        assetCount.value = BLANK_STRING
        amount.value = BLANK_STRING


        getLivelihoodEventFromName(eventType).livelihoodEventDataCaptureTypes.forEach {
            if (questionVisibilityMap.containsKey(it)) {
                questionVisibilityMap[it] = true
            } else {
                questionVisibilityMap[it] = false
            }
        }
        validateForm()

    }

    fun onSubmitButtonClick(subjectId: Int, transactionId: String) {

        ioViewModelScope {
            val event = getLivelihoodEventFromName(eventType)

            val mTransactionId =
                if (transactionId != BLANK_STRING) transactionId else UUID.randomUUID()
                    .toString()
            val livelihoodScreenData = LivelihoodEventScreenData(
                subjectId = subjectId,
                amount = amount.value.toIntOrNull() ?: 0,
                assetCount = assetCount.value.toIntOrNull() ?: 0,
                assetType = selectedAssetTypeId.value,
                date = selectedDateInLong,
                livelihoodId = selectedLivelihoodId.value,
                eventId = selectedEventId.value,
                productId = selectedProductId.value,
                selectedEvent = event,
                transactionId = mTransactionId,
                eventValue = livelihoodEventDropdownValue.find { it.id == selectedEventId.value }?.originalName
                    ?: BLANK_STRING,
                livelihoodValue = livelihoodDropdownValue.find { it.id == selectedLivelihoodId.value }?.originalName
                    ?: BLANK_STRING,
                assetTypeValue = livelihoodAssetDropdownValue.find { it.id == selectedAssetTypeId.value }?.originalName
                    ?: BLANK_STRING,
                productValue = livelihoodProductDropdownValue.find { it.id == selectedProductId.value }?.originalName
                    ?: BLANK_STRING
            )
            saveLivelihoodEventUseCase.addOrEditEvent(
                particular = getParticulars(),
                eventData = livelihoodScreenData
            )
            writeLivelihoodEventUseCase.writeLivelihoodEvent(
                particular = getParticulars(),
                eventData = livelihoodScreenData
            )

        }
    }

    private fun getParticulars(): String {
        var particulars = ""

        particulars =
            "Livelihood=${livelihoodDropdownValue.find { it.id == selectedLivelihoodId.value }?.originalName}|"
        particulars +=
            "Event=${_livelihoodEventDropdownValue.find { it.id == selectedEventId.value }?.originalName}|"
        if (selectedAssetTypeId.value != -1) {
            particulars += "AssetType=${_livelihoodAssetDropdownValue.find { it.id == selectedAssetTypeId.value }?.originalName}|"
        }
        if (selectedProductId.value != -1) {
            particulars += "Product=${_livelihoodProductDropdownValue.find { it.id == selectedProductId.value }?.originalName}|"
        }

        return particulars
    }

    fun validateForm() {

        isSubmitButtonEnable.value = checkValidData()


    }

    private fun checkValidData(): Boolean {
        if (selectedEventId.value != -1 && selectedLivelihoodId.value != -1 &&
            !TextUtils.isEmpty(selectedDate.value)
        ) {
            if (questionVisibilityMap[LivelihoodEventDataCaptureTypeEnum.TYPE_OF_ASSET].value()) {
                if (selectedAssetTypeId.value == -1) {
                    return false

                }
            }

            if (questionVisibilityMap[LivelihoodEventDataCaptureTypeEnum.COUNT_OF_ASSET].value()) {
                if (TextUtils.isEmpty(assetCount.value)) {
                    return false
                }
            }
            if (questionVisibilityMap[LivelihoodEventDataCaptureTypeEnum.AMOUNT].value()) {
                if (TextUtils.isEmpty(amount.value)) {
                    return false
                }
            }
            if (questionVisibilityMap[LivelihoodEventDataCaptureTypeEnum.TYPE_OF_PRODUCT].value()) {
                if (selectedProductId.value == -1) {
                    return false

                }
            }
            return true


        } else {
            return false
        }
    }

    fun onDeleteClick(transactionId: String, subjectId: Int) {
        ioViewModelScope {
            saveLivelihoodEventUseCase.deleteLivelihoodEvent(
                transactionId = transactionId,
                subjectId = subjectId,
                getLivelihoodEventFromName(eventType)
            )
            writeLivelihoodEventUseCase.writeDeleteLivelihoodEvent(
                transactionId = transactionId,
                subjectId = subjectId
            )
        }
    }


}





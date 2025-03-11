package com.nudge.incomeexpensemodule.viewmodel

import android.text.TextUtils
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.nudge.core.BLANK_STRING
import com.nudge.core.DEFAULT_LANGUAGE_CODE
import com.nudge.core.NOT_DECIDED_LIVELIHOOD_ID
import com.nudge.core.getCurrentTimeInMillis
import com.nudge.core.getDate
import com.nudge.core.helper.TranslationEnum
import com.nudge.core.json
import com.nudge.core.model.response.Validation
import com.nudge.core.model.uiModel.LivelihoodModel
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.ui.commonUi.MAXIMUM_RANGE
import com.nudge.core.value
import com.sarathi.dataloadingmangement.INFLOW
import com.sarathi.dataloadingmangement.OUTFLOW
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchAssetUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchLivelihoodEventUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchProductUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchSavedEventUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.LivelihoodEventValidationUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.SaveLivelihoodEventUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.WriteLivelihoodEventUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.GetLivelihoodListFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.GetSubjectLivelihoodMappingFromUseCase
import com.sarathi.dataloadingmangement.enums.AddEventFieldEnum
import com.sarathi.dataloadingmangement.enums.EntryFlowTypeEnum
import com.sarathi.dataloadingmangement.enums.LivelihoodEventDataCaptureTypeEnum
import com.sarathi.dataloadingmangement.enums.LivelihoodEventTypeDataCaptureMapping
import com.sarathi.dataloadingmangement.enums.LivelihoodEventTypeDataCaptureMapping.Companion.getLivelihoodEventFromName
import com.sarathi.dataloadingmangement.enums.LivelihoodTypeEnum
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventScreenData
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventUiModel
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.ProductAssetUiModel
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import getLivelihoodIdsWithOrderForSubject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
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
    private val writeLivelihoodEventUseCase: WriteLivelihoodEventUseCase,
    private val validationUseCase: LivelihoodEventValidationUseCase,
    private val coreSharedPrefs: CoreSharedPrefs,
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
    private var livelihoodList: List<LivelihoodModel> = ArrayList<LivelihoodModel>()
    private var assetTypeList: List<ProductAssetUiModel> = ArrayList<ProductAssetUiModel>()
    private var producTypeList: List<ProductAssetUiModel> = ArrayList<ProductAssetUiModel>()
    var questionVisibilityMap = mutableStateMapOf<LivelihoodEventDataCaptureTypeEnum, Boolean>()

    var eventType: String = ""
    var selectedLivelihoodId = mutableStateOf(-1)
    var selectedAssetTypeId = mutableStateOf(-1)
    var selectedChildAssetTypeId = mutableStateOf(-1)
    var selectedProductId = mutableStateOf(-1)
    var selectedEventId = mutableStateOf(-1)
    var assetCount = mutableStateOf("")
    var amount = mutableStateOf("")
    var selectedDate = mutableStateOf("")
    var isSubmitButtonEnable = mutableStateOf(false)
    var isDateOfEventVisible = mutableStateOf(true)
    var selectedDateInLong: Long = 0
    val maxAssetValue = mutableIntStateOf(MAXIMUM_RANGE)
    private val _fieldValidationAndMessageMap =
        MutableStateFlow<Map<String, Pair<Boolean, String>>>(emptyMap())
    val fieldValidationAndMessageMap: StateFlow<Map<String, Pair<Boolean, String>>> =
        _fieldValidationAndMessageMap

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitAddEventState -> {
                setTranslationConfig()
                fetchEventData(event.subjectId, event.transactionId)
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
        }
    }

    override fun getScreenName(): TranslationEnum {
        return TranslationEnum.AddEventScreen
    }


    private fun fetchEventData(subjectId: Int, transactionId: String) {
        ioViewModelScope {
            val savedEvent = fetchSavedEventUseCase.fetchEvent(
                subjectId = subjectId,
                transactionId = transactionId
            )
            LivelihoodEventDataCaptureTypeEnum.values().forEach {
                if (!questionVisibilityMap.containsKey(it)) {
                    questionVisibilityMap[it] = false
                }
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

                if (savedEvent.selectedEvent.name == LivelihoodEventTypeDataCaptureMapping.AssetTransition.name) {
                    val assetList = saveLivelihoodEventUseCase.fetchAssetJournalList(
                        subjectId = subjectId,
                        transactionId = transactionId
                    )
                    selectedAssetTypeId.value =
                        assetList?.find { it.transactionFlow == INFLOW }?.assetId ?: -1
                    selectedChildAssetTypeId.value =
                        assetList?.find { it.transactionFlow == OUTFLOW }?.assetId ?: -1
                }

                getLivelihoodEventFromName(eventType).livelihoodEventDataCaptureTypes.forEach {
                    questionVisibilityMap[it] = questionVisibilityMap.containsKey(it)
                }
                fetEventValues()
                fetchAssestProductValues()
            }
            val livelihoodForDidi =
                getSubjectLivelihoodMappingFromUseCase.invoke(subjectId = subjectId)
            if (livelihoodForDidi != null) {
                livelihoodList = getLivelihoodListFromDbUseCase.invoke(
                    listOf(
                        livelihoodForDidi.find { it.type == LivelihoodTypeEnum.PRIMARY.typeId }?.livelihoodId.value(),
                        livelihoodForDidi.find { it.type == LivelihoodTypeEnum.SECONDARY.typeId }?.livelihoodId.value()
                    ).filter { it != NOT_DECIDED_LIVELIHOOD_ID }//Filter Not decided events
                )

                val livelihoodIdsWithOrder = getLivelihoodIdsWithOrderForSubject(livelihoodForDidi)

                _livelihoodDropdownValue.clear()
                livelihoodIdsWithOrder.sortedBy { it?.second }.forEach { idsWithOrder ->
                    livelihoodList.find { it.programLivelihoodId == idsWithOrder?.first }
                        ?.let { livelihoodModel ->
                            _livelihoodDropdownValue.add(getLivelihooldDropValue(livelihoodModel))
                        }
                }
                revalidateAllFieldsInEdit(subjectId, transactionId)
            }
        }
    }



    private fun revalidateAllFieldsInEdit(subjectId: Int, transactionId: String) {
        AddEventFieldEnum.values().forEach {
            validateForm(
                subjectId,
                fieldName = it.name,
                transactionId = transactionId,
                onValidationComplete = { evalutorResult, message ->
                    updateFieldValidationMessageAndMap(key = it.name, Pair(evalutorResult, message))

                }
            )
    }
    }

    fun onLivelihoodSelect(livelihoodId: Int, subjectId: Int, transactionId: String) {
        resetForm()
        ioViewModelScope {
            selectedLivelihoodId.value = livelihoodId
            _livelihoodEventDropdownValue.clear()
            _livelihoodAssetDropdownValue.clear()
            _livelihoodProductDropdownValue.clear()
            fetEventValues()
            fetchAssestProductValues()
            validateForm(
                subjectId,
                fieldName = AddEventFieldEnum.LIVELIHOOD_TYPE.name,

                transactionId = transactionId,
                onValidationComplete = { _, _ ->
// No need to handle here
                })
        }
    }

    private suspend fun fetchAssestProductValues() {
        assetTypeList = fetchAssetUseCase.invoke(
            selectedLivelihoodId.value,
            selectedAssetTypeId.value
        )
        _livelihoodAssetDropdownValue.clear()
        _livelihoodAssetDropdownValue.addAll(
            assetTypeList.map {
                ValuesDto(
                    it.id,
                    it.name,
                    it.id == selectedAssetTypeId.value,
                    originalName = it.originalName
                )
            }
        )
        producTypeList = fetchProductUseCase.invoke(selectedLivelihoodId.value)
        _livelihoodProductDropdownValue.addAll(producTypeList.map {
            ValuesDto(
                it.id,
                it.name,
                it.id == selectedProductId.value,
                originalName = it.originalName
            )
        }

        )
    }

    private suspend fun fetEventValues() {
        eventList = fetchLivelihoodEventUseCase.invoke(selectedLivelihoodId.value)
        _livelihoodEventDropdownValue.addAll(eventList.map {
            ValuesDto(
                id = it.id,
                value = it.name,
                isSelected = it.id == selectedEventId.value,
                originalName = it.originalName
            )
        }.sortedBy { it.originalName?.lowercase() })
    }

    private fun resetForm() {
        selectedEventId.value = -1
        getLivelihoodEventFromName(eventType).livelihoodEventDataCaptureTypes.forEach {
            questionVisibilityMap[it] = false

        }
        AddEventFieldEnum.values().forEach { eventFieldName ->
            _fieldValidationAndMessageMap.value =
                _fieldValidationAndMessageMap.value.toMutableMap().apply {
                    this[eventFieldName.name] = Pair(true, BLANK_STRING)
            }
        }
    }


    private fun getLivelihooldDropValue(livelihoodForDidi: List<LivelihoodModel>): List<ValuesDto> {
        return livelihoodForDidi.map {
            ValuesDto(
                id = it.programLivelihoodId,
                value = it.name,
                originalName = it.originalName,
                isSelected = selectedLivelihoodId.value == it.programLivelihoodId
            )
        }
    }

    private fun getLivelihooldDropValue(livelihoodForDidi: LivelihoodModel): ValuesDto {
        return livelihoodForDidi.let {
            ValuesDto(
                id = it.programLivelihoodId,
                value = it.name,
                originalName = it.originalName,
                isSelected = selectedLivelihoodId.value == it.programLivelihoodId
            )
        }
    }

    fun onEventSelected(selectedValue: ValuesDto, subjectId: Int) {
        resetForm()
        eventType = eventList.find { it.id == selectedValue.id }?.eventType ?: BLANK_STRING

        selectedEventId.value = selectedValue.id
        selectedProductId.value = -1
        selectedAssetTypeId.value = -1

        assetCount.value = BLANK_STRING
        amount.value = BLANK_STRING

        _livelihoodAssetDropdownValue.clear()
        _livelihoodProductDropdownValue.clear()

    }

    fun loadAssetAndProduct() {
        val livelihoodDataCaptureTypes =
            getLivelihoodEventFromName(eventType).livelihoodEventDataCaptureTypes
        livelihoodDataCaptureTypes.forEach { captureType ->
            questionVisibilityMap[captureType] = questionVisibilityMap.containsKey(captureType)
        }
        ioViewModelScope {
            fetchAssestProductValues()
        }
    }

    fun updateAssetVisibility(isValid: Boolean) {
        val livelihoodDataCaptureTypes = getLivelihoodEventFromName(eventType)
            .livelihoodEventDataCaptureTypes
            .filterNot { it == LivelihoodEventDataCaptureTypeEnum.TYPE_OF_ASSET }
        isDateOfEventVisible.value = isValid
        livelihoodDataCaptureTypes.forEach { captureType ->
            questionVisibilityMap[captureType] = if (isValid) {
                questionVisibilityMap.containsKey(captureType)
            } else {
                !questionVisibilityMap.containsKey(captureType)
            }
        }
    }


    fun onSubmitButtonClick(subjectId: Int, transactionId: String, onComplete: () -> Unit) {

        ioViewModelScope {
            val event = getLivelihoodEventFromName(eventType)

            createAndAddEvent(
                transactionId = transactionId,
                subjectId = subjectId,
                event = event,
                selectedAssetId = selectedAssetTypeId.value,

                )
            if (eventType == LivelihoodEventTypeDataCaptureMapping.AssetTransition.name) {
                createAndAddEvent(
                    transactionId = transactionId,
                    subjectId = subjectId,
                    event = event,
                    selectedAssetId = selectedChildAssetTypeId.value,
                    isChildEvent = true
                )
            }

            withContext(mainDispatcher)
            {
                onComplete()
            }

        }
    }

    private suspend fun createAndAddEvent(
        transactionId: String,
        subjectId: Int,
        event: LivelihoodEventTypeDataCaptureMapping,
        selectedAssetId: Int,
        isChildEvent: Boolean = false
    ) {
        if (eventType == LivelihoodEventTypeDataCaptureMapping.AssetTransition.name) {
            event.assetJournalEntryFlowType =
                if (isChildEvent) EntryFlowTypeEnum.OUTFLOW else EntryFlowTypeEnum.INFLOW
        }
        val createdDateTime = getCurrentTimeInMillis()
        val modifiedDate = getCurrentTimeInMillis()
        val mTransactionId =
            if (transactionId != BLANK_STRING) transactionId else UUID.randomUUID()
                .toString()
        val livelihoodScreenData = LivelihoodEventScreenData(
            subjectId = subjectId,
            amount = amount.value.toIntOrNull() ?: 0,
            assetCount = assetCount.value.toIntOrNull() ?: 0,
            assetType = selectedAssetId,
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
            assetTypeValue = livelihoodAssetDropdownValue.find { it.id == selectedAssetId }?.originalName
                ?: BLANK_STRING,
            productValue = livelihoodProductDropdownValue.find { it.id == selectedProductId.value }?.originalName
                ?: BLANK_STRING
        )
        Log.d("TAG", "createAndAddEvent Details 1: ${livelihoodScreenData.json()} ")

        saveLivelihoodEventUseCase.addOrEditEvent(
            particular = getParticulars(),
            createdDate = createdDateTime,
            eventData = livelihoodScreenData,
            modifiedDate = modifiedDate,
            isEventNeedToSaveInSubjectEventMapping = !isChildEvent
        )
        writeLivelihoodEventUseCase.writeLivelihoodEvent(
            particular = getParticulars(),
            eventData = livelihoodScreenData,
            createdDateTime = createdDateTime,
            modifiedDate = modifiedDate
        )
    }

    private fun getParticulars(): String {

        var particulars =
            "Livelihood=${livelihoodDropdownValue.find { it.id == selectedLivelihoodId.value }?.originalName}|"
        particulars +=
            "Event=${_livelihoodEventDropdownValue.find { it.id == selectedEventId.value }?.originalName}|"
        if (eventType == LivelihoodEventTypeDataCaptureMapping.AssetTransition.name) {
            if (selectedAssetTypeId.value != -1 && selectedChildAssetTypeId.value != -1) {
                particulars += "ChildAssetType=${_livelihoodAssetDropdownValue.find { it.id == selectedChildAssetTypeId.value }?.originalName}|" +
                        "AdultAssetType=${_livelihoodAssetDropdownValue.find { it.id == selectedAssetTypeId.value }?.originalName}|"
            }
        } else {
            if (selectedAssetTypeId.value != -1) {
                particulars += "AssetType=${_livelihoodAssetDropdownValue.find { it.id == selectedAssetTypeId.value }?.originalName}|"
            }

        }

        if (selectedProductId.value != -1) {
            particulars += "Product=${_livelihoodProductDropdownValue.find { it.id == selectedProductId.value }?.originalName}|"
        }

        return particulars
    }

    fun validateForm(
        subjectId: Int,
        fieldName: String,
        transactionId: String,
        onValidationComplete: (Boolean, String) -> Unit
    ) {
        validationExpressionEvalutor(
            subjectId,
            fieldName,
            transactionId = transactionId
        ) { isValid, message ->
            // Pass the result back through the callback
            // Update submit button state based on validation result
            var fieldValidationFromConfig = true
            onValidationComplete(isValid, message)

            fieldValidationAndMessageMap.value.forEach {

                if (!it.value.first) {
                    fieldValidationFromConfig = false
                }
            }
            isSubmitButtonEnable.value = fieldValidationFromConfig && checkValidData()
        }

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
            val currentDateTime = System.currentTimeMillis()
            saveLivelihoodEventUseCase.deleteLivelihoodEvent(
                transactionId = transactionId,
                subjectId = subjectId,
                getLivelihoodEventFromName(eventType),
                modifiedDate = currentDateTime
            )
            writeLivelihoodEventUseCase.writeDeleteLivelihoodEvent(
                transactionId = transactionId,
                subjectId = subjectId,
                modifiedDate = currentDateTime
            )
        }
    }

    /*
    This method is responsible to handle the validation that is coming through backend using livelihood config

     */
    fun validationExpressionEvalutor(
        subjectId: Int,
        fieldName: String,
        transactionId: String,
        onValidationResult: (Boolean, String) -> Unit
    ) {
        ioViewModelScope {
            val selectedLivelihood =
                livelihoodList.find { it.programLivelihoodId == selectedLivelihoodId.value }
            val selectedEvent = eventList.find { it.id == selectedEventId.value }
            val selectedAssetType = assetTypeList.find { it.id == selectedAssetTypeId.value }
            val selectedProduct = producTypeList.find { it.id == selectedProductId.value }
            val selectedValidations =
                selectedLivelihood?.validations?.filter { it.eventName == selectedEvent?.originalName }

            if (selectedValidations != null && selectedValidations.isNotEmpty()) {
                var validation =
                    selectedValidations.first().validation.find { it.field == fieldName && it.languageCode == coreSharedPrefs.getAppLanguage() }
                        ?: selectedValidations.first().validation.find { it.field == fieldName && it.languageCode == DEFAULT_LANGUAGE_CODE }

                if (selectedAssetTypeId.value != -1 && selectedValidations.find { it.assetType == selectedAssetType?.type } != null) {
                    validation =
                        selectedValidations.find { it.assetType == selectedAssetType?.type }?.validation?.find { it.field == fieldName && it.languageCode == coreSharedPrefs.getAppLanguage() }
                    validateExpression(
                        validation,
                        subjectId,
                        selectedAssetType,
                        selectedLivelihood,
                        transactionId,
                        onValidationResult
                    )

                } else if (selectedProductId.value != -1 && selectedValidations.find { it.productType == selectedProduct?.type } != null) {
                    validation =
                        selectedValidations.find { it.productType == selectedProduct?.type }?.validation?.find { it.field == fieldName && it.languageCode == coreSharedPrefs.getAppLanguage() }
                            ?: selectedValidations.find { it.productType == selectedProduct?.type }?.validation?.find { it.field == fieldName && it.languageCode == DEFAULT_LANGUAGE_CODE }

                    validateExpression(
                        validation,
                        subjectId,
                        selectedAssetType,
                        selectedLivelihood,
                        transactionId,
                        onValidationResult
                    )
                } else {
                    validateExpression(
                        validation,
                        subjectId,
                        selectedAssetType,
                        selectedLivelihood,
                        transactionId,
                        onValidationResult
                    )
                }

            } else {
                onValidationResult(true, BLANK_STRING)
            }


        }
    }

    private suspend fun validateExpression(
        validation: Validation?,
        subjectId: Int,
        selectedAssetType: ProductAssetUiModel?,
        selectedLivelihood: LivelihoodModel,
        transactionId: String,
        onValidationResult: (Boolean, String) -> Unit
    ) {
        if (validation == null) {
            onValidationResult(true, BLANK_STRING)
            return
        }
        val expressionResult = validationUseCase.invoke(
            validationExpression = validation.condition,
            validationRegex = validation.regex,
            subjectId = subjectId,
            selectedAsset = selectedAssetType,
            selectedLivelihood = selectedLivelihood,
            assetCount = assetCount.value,
            amount = amount.value,
            message = validation.message,
            transactionId = transactionId
        )
        validation.conditionalMessage?.let { conditionalMessages ->
            conditionalMessages.forEach { conditionalMessage ->
                val conditionalMessageExpressionResult = validationUseCase.invoke(
                    validationExpression = conditionalMessage.condition,
                    validationRegex = validation.regex,
                    subjectId = subjectId,
                    selectedAsset = selectedAssetType,
                    selectedLivelihood = selectedLivelihood,
                    assetCount = assetCount.value,
                    amount = amount.value,
                    message = conditionalMessage.languageList?.find { it.languageCode == coreSharedPrefs.getAppLanguage() }?.message
                        ?: BLANK_STRING,
                    transactionId = transactionId
                )
                if (conditionalMessageExpressionResult.first) {
                    onValidationResult(
                        expressionResult.first,
                        conditionalMessageExpressionResult.second
                    )
                    return
                }
            }

        } ?: onValidationResult(expressionResult.first, expressionResult.second)
    }

    fun updateFieldValidationMessageAndMap(key: String, value: Pair<Boolean, String>) {
        _fieldValidationAndMessageMap.value =
            _fieldValidationAndMessageMap.value.toMutableMap().apply {
                this[key] = value
            }
}
}









package com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component

import android.util.Log
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DELIMITER_MULTISELECT_OPTIONS
import com.nrlm.baselinesurvey.LIVELIHOOD_SOURCE_TAG
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.database.entity.FormQuestionResponseEntity
import com.nrlm.baselinesurvey.model.response.ContentList
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nrlm.baselinesurvey.ui.common_components.CalculationResultComponent
import com.nrlm.baselinesurvey.ui.common_components.EditTextWithTitleComponent
import com.nrlm.baselinesurvey.ui.common_components.RadioOptionTypeComponent
import com.nrlm.baselinesurvey.ui.common_components.RangePickerComponent
import com.nrlm.baselinesurvey.ui.common_components.TypeMultiSelectedDropDownComponent
import com.nrlm.baselinesurvey.ui.question_screen.presentation.questionComponent.IncrementDecrementView
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.QuestionTypeEvent
import com.nrlm.baselinesurvey.ui.question_type_screen.viewmodel.QuestionTypeScreenViewModel
import com.nrlm.baselinesurvey.ui.theme.dimen_100_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_14_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_24_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_30_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_64_dp
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.findTagForId
import com.nrlm.baselinesurvey.utils.getResponseForOptionId
import com.nrlm.baselinesurvey.utils.saveFormQuestionResponseEntity
import com.nrlm.baselinesurvey.utils.tagList
import kotlinx.coroutines.launch

@Composable
fun NestedLazyListForFormQuestions(
    modifier: Modifier = Modifier,
    outerState: LazyListState = rememberLazyListState(),
    innerState: LazyListState = rememberLazyListState(),
    viewModel: BaseViewModel,
    onSaveFormTypeOption: (questionTypeEvent: QuestionTypeEvent) -> Unit,
    saveCacheFormData: (formQuestionResponseEntity: FormQuestionResponseEntity) -> Unit,
    answeredQuestionCountIncreased: () -> Unit,
    sectionInfoButtonClicked: (contents: List<ContentList>) -> Unit,
    ) {
    val scope = rememberCoroutineScope()
    val questionTypeScreenViewModel = (viewModel as QuestionTypeScreenViewModel)

    val formQuestionResponseEntity = questionTypeScreenViewModel.formQuestionResponseEntity

    val answeredQuestionCount = remember {
        mutableIntStateOf(questionTypeScreenViewModel.formTypeOption?.options?.size ?: 0)
    }

    val answeredQuestionIndices = remember {
        mutableStateOf(mutableListOf<Int>())
    }


    SideEffect {
        if (outerState.layoutInfo.visibleItemsInfo.size == 2 && innerState.layoutInfo.totalItemsCount == 0)
            scope.launch { outerState.scrollToItem(outerState.layoutInfo.totalItemsCount) }
    }

    val innerFirstVisibleItemIndex by remember {
        derivedStateOf {
            innerState.firstVisibleItemIndex
        }
    }

    val editTextValued = remember {
        mutableStateOf(mutableMapOf<Int, String>())
    }

    BoxWithConstraints(
        modifier = modifier
            .scrollable(
                state = rememberScrollableState {
                    scope.launch {
                        val toDown = it <= 0
                        if (toDown) {
                            if (outerState.run { firstVisibleItemIndex == layoutInfo.totalItemsCount - 1 }) {
                                innerState.scrollBy(-it)
                            } else {
                                outerState.scrollBy(-it)
                            }
                        } else {
                            if (innerFirstVisibleItemIndex == 0 && innerState.firstVisibleItemScrollOffset == 0) {
                                outerState.scrollBy(-it)
                            } else {
                                innerState.scrollBy(-it)
                            }
                        }
                    }
                    it
                },
                Orientation.Vertical,
            )
            .fillMaxHeight()
    ) {
        LazyColumn(
            userScrollEnabled = false,
            state = outerState,
            modifier = Modifier
                .heightIn(dimen_100_dp, maxHeight)
                .padding(horizontal = 16.dp)
        ) {
            item {
                LazyColumn(
                    state = innerState,
                    userScrollEnabled = false,
                    modifier = Modifier
                        .height(maxHeight), verticalArrangement = Arrangement.spacedBy(dimen_14_dp),

                ) {
                    item {
                        Spacer(modifier = Modifier.width(dimen_24_dp))
                    }
                    itemsIndexed(
                        items = /*formTypeOption?.options*/questionTypeScreenViewModel.updatedOptionList.distinctBy { it.optionId }
                            .filter {
                                it
                                    .optionItemEntity?.optionType != QuestionType.Form.name && it.optionItemEntity?.optionType != QuestionType.FormWithNone.name
                            }.filter { it.showQuestion }.sortedBy { it.optionItemEntity?.order }
                            ?: emptyList()
                    ) { index, option ->
                        when (option.optionItemEntity?.optionType) {
                            QuestionType.SingleSelectDropdown.name,
                            QuestionType.SingleSelectDropDown.name -> {
                                val isEditAllowed =
                                    if (tagList.findTagForId(option.optionItemEntity.optionTag)
                                            .contains(LIVELIHOOD_SOURCE_TAG, true)
                                    ) {
                                        if (questionTypeScreenViewModel.tempRefId.value == BLANK_STRING) {
                                            true
                                        } else {
                                            (questionTypeScreenViewModel.updatedOptionList.distinctBy { it.optionId }
                                                .map { it.optionItemEntity?.optionType }
                                                .contains(QuestionType.FormWithNone.name)
                                                    && BaselineCore.isEditAllowedForNoneMarkedQuestion())
                                        }
                                    } else {
                                        true
                                    }
                                TypeDropDownComponent(
                                    option.optionItemEntity?.display,
                                    option.optionItemEntity.selectedValue ?: "Select",
                                    showQuestionState = option,
                                    isContent = option.optionItemEntity.contentEntities.isNotEmpty(),
                                    sources = option.optionItemEntity.values,
                                    isEditAllowed = isEditAllowed,
                                    selectOptionText = if (viewModel.tempRefId.value != BLANK_STRING) {

                                        option.optionItemEntity.values?.find { valueDto ->
                                            valueDto.id == (formQuestionResponseEntity.value.getResponseForOptionId(
                                                option.optionId ?: -1
                                            )?.selectedValueId)?.first()
                                        }?.id
                                            ?: 0 //TODO change from checking text to check only for id
                                    }

                                    else {
                                        option.optionItemEntity.values?.find { valueDto ->
                                            valueDto.id == (formQuestionResponseEntity.value.getResponseForOptionId(
                                                option.optionId ?: -1
                                            )?.selectedValueId)?.first()
                                        }?.id
                                            ?: 0 //TODO change from checking text to check only for id
                                    }

                                    /*viewModel.storeCacheForResponse.getResponseForOptionId(
                                        optionId = option.optionId ?: -1
                                    )?.selectedValue ?: BLANK_STRING*/,
                                    onInfoButtonClicked = {
                                        sectionInfoButtonClicked(option.optionItemEntity.contentEntities)
                                    }
                                ) { value ->
                                    questionTypeScreenViewModel.onEvent(
                                        QuestionTypeEvent.UpdateConditionalOptionState(
                                            option,
                                            option.optionItemEntity.values?.find { it.id == value }?.value
                                                ?: BLANK_STRING //TODO change from checking text to check only for id
                                        )
                                    )
                                    questionTypeScreenViewModel.formTypeOption?.let { formTypeOption ->
                                        saveCacheFormData(
                                            saveFormQuestionResponseEntity(
                                                formTypeOption,
                                                option.optionId ?: 0,
                                                option.optionItemEntity.values?.find { it.id == value }?.value
                                                    ?: BLANK_STRING,
                                                viewModel.referenceId,
                                                selectedIds = listOf(value)
                                            )
                                        )
                                    }
                                    questionTypeScreenViewModel.onEvent(QuestionTypeEvent.UpdateCalculationTypeQuestionValue)
                                    answeredQuestionCountIncreased()
                                }
                            }


                            QuestionType.MultiSelectDropDown.name,
                            QuestionType.MultiSelectDropdown.name -> {

                                val mOption = if (viewModel.tempRefId.value != BLANK_STRING)
                                    option.optionItemEntity?.values?.filter {
                                        formQuestionResponseEntity.value.getResponseForOptionId(
                                            option.optionId ?: -1
                                        )?.selectedValueId?.contains(it.id) == true
                                    }?.map { it.value }?.joinToString(DELIMITER_MULTISELECT_OPTIONS)
                                        ?: BLANK_STRING
                                else
                                    option.optionItemEntity?.values?.filter {
                                        viewModel.storeCacheForResponse.getResponseForOptionId(
                                            optionId = option.optionId ?: -1
                                        )?.selectedValueId
                                            ?.contains(it.id) == true
                                    }?.map { it.value }?.joinToString(DELIMITER_MULTISELECT_OPTIONS)
                                        ?: BLANK_STRING


                                TypeMultiSelectedDropDownComponent(
                                    title = option.optionItemEntity.display,
                                    sources = option.optionItemEntity.values,
                                    showQuestionState = option,
                                    isContent = option.optionItemEntity.contentEntities.isNotEmpty(),
                                    selectOptionText = mOption,
                                    onInfoButtonClicked = {
                                        sectionInfoButtonClicked(option.optionItemEntity.contentEntities)
                                    }
                                ) { value ->
                                    val valueIds = option.optionItemEntity.values?.filter {
                                        value.split(DELIMITER_MULTISELECT_OPTIONS)
                                            .contains(it.value)
                                    }?.map { it.id } ?: listOf()
                                    questionTypeScreenViewModel.onEvent(
                                        QuestionTypeEvent.UpdateConditionalOptionState(
                                            option,
                                            value
                                        )
                                    )
                                    questionTypeScreenViewModel.onEvent(QuestionTypeEvent.UpdateCalculationTypeQuestionValue)
                                    questionTypeScreenViewModel.formTypeOption?.let { formTypeOption ->
                                        saveCacheFormData(
                                            saveFormQuestionResponseEntity(
                                                formTypeOption,
                                                option.optionId ?: 0,
                                                selectedValue = value,
                                                referenceId = viewModel.referenceId,
                                                selectedIds = valueIds
                                            )
                                        )
                                    }
                                    answeredQuestionCountIncreased()
                                }
                            }

                            QuestionType.Input.name,
                            QuestionType.InputText.name,
                            QuestionType.InputNumberEditText.name -> {
                                Log.d(
                                    "TAG",
                                    "EditTextWithTitleComponent: id: ${option.optionId}, questionId: ${option.optionItemEntity.questionId}, ${option?.optionItemEntity?.display}, type: ${option.optionItemEntity.optionType}, showQuestion: ${option.showQuestion}"
                                )
                                Log.d(
                                    "TAG",
                                    "EditTextWithTitleComponent response: ${
                                        if (viewModel.tempRefId.value != BLANK_STRING)
                                            formQuestionResponseEntity.value.getResponseForOptionId(
                                                option.optionId ?: -1
                                            )?.selectedValue
                                                ?: BLANK_STRING
                                        else
                                            viewModel.storeCacheForResponse.getResponseForOptionId(
                                                optionId = option.optionId ?: -1
                                            )?.selectedValue ?: BLANK_STRING
                                    }"
                                )

                                val responseValue = if (viewModel.tempRefId.value != BLANK_STRING)
                                    formQuestionResponseEntity.value.getResponseForOptionId(
                                        option.optionId ?: -1
                                    )?.selectedValue
                                        ?: BLANK_STRING
                                else
                                    viewModel.storeCacheForResponse.getResponseForOptionId(
                                        optionId = option.optionId ?: -1
                                    )?.selectedValue ?: BLANK_STRING

                                EditTextWithTitleComponent(
                                    option.optionItemEntity.display,
                                    showQuestion = option,
                                    isContent = option.optionItemEntity.contentEntities.isNotEmpty(),
                                    resetResponse = responseValue == BLANK_STRING,
                                    defaultValue = responseValue,
                                    isOnlyNumber = option.optionItemEntity.optionType == QuestionType.InputNumber.name || option.optionItemEntity.optionType == QuestionType.InputNumberEditText.name,
                                    onInfoButtonClicked = {
                                        sectionInfoButtonClicked(option.optionItemEntity.contentEntities)
                                    }
                                ) { value ->
                                    questionTypeScreenViewModel.formTypeOption.let { it1 ->
                                        if (!option.optionItemEntity.conditions.isNullOrEmpty()) {
                                            questionTypeScreenViewModel.onEvent(
                                                QuestionTypeEvent.UpdateConditionalOptionState(
                                                    optionItemEntityState = option,
                                                    value
                                                )
                                            )
                                        }
                                        saveCacheFormData(
                                            saveFormQuestionResponseEntity(
                                                questionTypeScreenViewModel.formTypeOption,
                                                option.optionId ?: 0,
                                                value,
                                                viewModel.referenceId
                                            )
                                        )
                                        questionTypeScreenViewModel.onEvent(QuestionTypeEvent.UpdateCalculationTypeQuestionValue)

                                    }
                                    answeredQuestionCountIncreased()
                                }
                            }

                            QuestionType.InputNumber.name -> {
                                IncrementDecrementView(
                                    title = option.optionItemEntity.display ?: BLANK_STRING,
                                    currentValue = if (viewModel.tempRefId.value != BLANK_STRING)
                                        formQuestionResponseEntity.value.getResponseForOptionId(
                                            option.optionId ?: -1
                                        )?.selectedValue
                                            ?: BLANK_STRING
                                    else
                                        viewModel.storeCacheForResponse.getResponseForOptionId(
                                            optionId = option.optionId ?: -1
                                        )?.selectedValue ?: BLANK_STRING,
                                    isContent = option.optionItemEntity.contentEntities.isNotEmpty(),
                                    onInfoButtonClicked = {
                                        sectionInfoButtonClicked(option.optionItemEntity.contentEntities)
                                    },
                                    onAnswerSelection = { selectedValue ->
                                        questionTypeScreenViewModel.formTypeOption.let { formTypeOption ->
                                            saveCacheFormData(
                                                saveFormQuestionResponseEntity(
                                                    formTypeOption,
                                                    option.optionId ?: 0,
                                                    selectedValue,
                                                    viewModel.referenceId
                                                )
                                            )
                                        }
                                        answeredQuestionCountIncreased()
                                    }
                                )
                            }

                            QuestionType.RadioButton.name,
                            QuestionType.Toggle.name -> {
                                RadioOptionTypeComponent(
                                    optionItemEntityState = option,
                                    isContent = option.optionItemEntity.contentEntities.isNotEmpty(),
                                    selectedValue = if (viewModel.tempRefId.value != BLANK_STRING)
                                        formQuestionResponseEntity.value.getResponseForOptionId(
                                            option.optionId ?: -1
                                        )?.selectedValue
                                            ?: BLANK_STRING
                                    else
                                        viewModel.storeCacheForResponse.getResponseForOptionId(
                                            optionId = option.optionId ?: -1
                                        )?.selectedValue ?: BLANK_STRING,
                                    onInfoButtonClicked = {
                                        sectionInfoButtonClicked(option.optionItemEntity.contentEntities)
                                    },
                                    onOptionSelected = { selectedValue, selectedOptionId ->
                                        questionTypeScreenViewModel.onEvent(
                                            QuestionTypeEvent.UpdateConditionalOptionState(
                                                option,
                                                selectedValue
                                            )
                                        )
                                        questionTypeScreenViewModel.onEvent(QuestionTypeEvent.UpdateCalculationTypeQuestionValue)
                                        questionTypeScreenViewModel.formTypeOption?.let { formTypeOption ->
                                            saveCacheFormData(
                                                saveFormQuestionResponseEntity(
                                                    formTypeOption,
                                                    option.optionId ?: 0,
                                                    selectedValue,
                                                    viewModel.referenceId,
                                                    selectedIds = listOf(selectedOptionId)

                                                )
                                            )
                                        }
                                        answeredQuestionCountIncreased()
                                    }
                                )
                            }

                            QuestionType.MultiSelect.name,
                            QuestionType.Grid.name -> {
                            }

                            QuestionType.Calculation.name -> {

                                CalculationResultComponent(
                                    title = option.optionItemEntity.display,
                                    showQuestion = option,
                                    defaultValue = questionTypeScreenViewModel.calculatedResult.value
                                )
                            }

                            QuestionType.HrsMinPicker.name,
                            QuestionType.YrsMonthPicker.name -> {
                                RangePickerComponent(
                                    title = option.optionItemEntity.display
                                    ?: BLANK_STRING,
                                    typePicker = option.optionItemEntity?.optionType
                                        ?: BLANK_STRING,
                                    defaultValue = if (viewModel.tempRefId.value != BLANK_STRING)
                                        formQuestionResponseEntity.value.getResponseForOptionId(
                                            option.optionId ?: -1
                                        )?.selectedValue
                                            ?: BLANK_STRING
                                    else
                                        viewModel.storeCacheForResponse.getResponseForOptionId(
                                            optionId = option.optionId ?: -1
                                        )?.selectedValue ?: BLANK_STRING,
                                    showQuestionState = option,
                                    onInfoButtonClicked = {}) { value, id ->
                                    questionTypeScreenViewModel.onEvent(
                                        QuestionTypeEvent.UpdateConditionalOptionState(
                                            option,
                                            value
                                        )
                                    )
                                    questionTypeScreenViewModel.onEvent(QuestionTypeEvent.UpdateCalculationTypeQuestionValue)
                                    questionTypeScreenViewModel.formTypeOption?.let { formTypeOption ->
                                        saveCacheFormData(
                                            saveFormQuestionResponseEntity(
                                                formTypeOption,
                                                option.optionId ?: 0,
                                                value,
                                                viewModel.referenceId
                                            )
                                        )
                                    }
                                    answeredQuestionCountIncreased()
                                }
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .height(dimen_64_dp))
                    }
                    item {
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .height(dimen_30_dp))
                    }
                }
            }
        }
    }

}




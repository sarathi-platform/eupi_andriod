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
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.database.entity.FormQuestionResponseEntity
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nrlm.baselinesurvey.ui.common_components.EditTextWithTitleComponent
import com.nrlm.baselinesurvey.ui.question_screen.presentation.questionComponent.IncrementDecrementView
import com.nrlm.baselinesurvey.ui.common_components.RadioOptionTypeComponent
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.QuestionTypeEvent
import com.nrlm.baselinesurvey.ui.question_type_screen.viewmodel.QuestionTypeScreenViewModel
import com.nrlm.baselinesurvey.ui.theme.dimen_100_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_14_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_24_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_30_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_64_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.utils.getResponseForOptionId
import com.nrlm.baselinesurvey.utils.saveFormQuestionResponseEntity
import kotlinx.coroutines.launch

@Composable
fun NestedLazyListForFormQuestions(
    modifier: Modifier = Modifier,
    outerState: LazyListState = rememberLazyListState(),
    innerState: LazyListState = rememberLazyListState(),
    viewModel: BaseViewModel,
    onSaveFormTypeOption: (questionTypeEvent: QuestionTypeEvent) -> Unit,
    saveCacheFormData: (formQuestionResponseEntity: FormQuestionResponseEntity) -> Unit,
    answeredQuestionCountIncreased: (count: Int) -> Unit,
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
                        items = /*formTypeOption?.options*/questionTypeScreenViewModel.updatedOptionList.distinctBy { it.optionId }.filter { it
                            .optionItemEntity?.optionType != QuestionType.Form.name}.filter { it.showQuestion } ?: emptyList()
                    ) { index, option ->
                        when (option.optionItemEntity?.optionType) {
                            QuestionType.SingleSelectDropdown.name,
                            QuestionType.SingleSelectDropDown.name-> {
                                TypeDropDownComponent(
                                    option.optionItemEntity?.display,
                                    option.optionItemEntity.selectedValue ?: "Select",
                                    showQuestionState = option,
                                    sources = option.optionItemEntity.values,
                                    selectOptionText = formQuestionResponseEntity.value.getResponseForOptionId(option.optionId ?: -1)?.selectedValue ?: BLANK_STRING
                                ) { value ->
                                    questionTypeScreenViewModel.onEvent(QuestionTypeEvent.UpdateConditionalOptionState(option, value))
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
                                }
                            }
                            QuestionType.Input.name,
                            QuestionType.InputText.name,
                            QuestionType.InputNumber.name-> {
                                EditTextWithTitleComponent(
                                    option.optionItemEntity.display,
                                    showQuestion = option,
                                    defaultValue = formQuestionResponseEntity.value.getResponseForOptionId(option.optionId ?: -1)?.selectedValue ?: BLANK_STRING,
                                    isOnlyNumber = option.optionItemEntity.optionType == QuestionType.InputNumber.name
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
                                    }

                                }
                            }

                            QuestionType.InputNumber.name -> {
                                IncrementDecrementView(
                                    title = option.optionItemEntity.display ?: BLANK_STRING,
                                    currentValue = formQuestionResponseEntity.value.getResponseForOptionId(
                                        option.optionId ?: -1
                                    )?.selectedValue ?: BLANK_STRING,
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
                                    }
                                )
                            }

                            QuestionType.RadioButton.name,
                            QuestionType.Toggle.name-> {
                                RadioOptionTypeComponent(
                                    optionItemEntityState = option,
                                    selectedValue = formQuestionResponseEntity.value.getResponseForOptionId(
                                        option.optionId ?: -1
                                    )?.selectedValue ?: BLANK_STRING,
                                    onOptionSelected = { selectedValue ->
                                        questionTypeScreenViewModel.onEvent(QuestionTypeEvent.UpdateConditionalOptionState(option, selectedValue))
                                        questionTypeScreenViewModel.formTypeOption?.let { formTypeOption ->
                                            saveCacheFormData(
                                                saveFormQuestionResponseEntity(
                                                    formTypeOption,
                                                    option.optionId ?: 0,
                                                    selectedValue,
                                                    viewModel.referenceId
                                                )
                                            )
                                        }
                                    }
                                )
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




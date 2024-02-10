package com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nrlm.baselinesurvey.ui.common_components.EditTextWithTitleComponent
import com.nrlm.baselinesurvey.ui.common_components.SwitchComponent
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.entity.FormTypeOption
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.QuestionTypeEvent
import com.nrlm.baselinesurvey.ui.question_type_screen.viewmodel.QuestionTypeScreenViewModel
import com.nrlm.baselinesurvey.ui.theme.dimen_24_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.utils.storeGivenAnswered
import kotlinx.coroutines.launch

@Composable
fun NestedLazyList(
    modifier: Modifier = Modifier,
    outerState: LazyListState = rememberLazyListState(),
    innerState: LazyListState = rememberLazyListState(),
    formTypeOption: FormTypeOption?,
    viewModel: BaseViewModel,
    onSaveFormTypeOption: (questionTypeEvent: QuestionTypeEvent) -> Unit
) {
    val scope = rememberCoroutineScope()
    val questionTypeScreenViewModel = (viewModel as QuestionTypeScreenViewModel)

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
    ) {
        LazyColumn(
            userScrollEnabled = false,
            state = outerState,
            modifier = Modifier
                .heightIn(maxHeight)
                .padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(dimen_8_dp)
        ) {
            item {
                LazyColumn(
                    state = innerState,
                    userScrollEnabled = false,
                    modifier = Modifier
                        .height(maxHeight), verticalArrangement = Arrangement.spacedBy(dimen_8_dp)

                ) {
                    item {
                        Spacer(modifier = Modifier.width(dimen_24_dp))
                    }
                    itemsIndexed(
                        items = formTypeOption?.options ?: emptyList()
                    ) { index, option ->
                        when (option.optionType) {
                            QuestionType.SingleSelectDropdown.name -> {
                                TypeDropDownComponent(
                                    option.display,
                                    option.selectedValue ?: "Select",
                                    option.values
                                ) { value ->
                                    formTypeOption?.let { it1 ->
                                        onSaveFormTypeOption(storeGivenAnswered(
                                            it1, option.optionId ?: 0, value,
                                            referenceId = viewModel.referenceId
                                        ))
                                    }
                                }
                            }

                            QuestionType.Input.name -> {
                                EditTextWithTitleComponent(
                                    option.display,
                                    option.selectedValue ?: ""
                                ) { value ->
                                    formTypeOption?.let { it1 ->
                                        onSaveFormTypeOption(
                                        storeGivenAnswered(
                                            it1, option.optionId ?: 0, value,
                                            referenceId = viewModel.referenceId
                                        ))
                                    }
                                }
                            }

                            QuestionType.Toggle.name -> {
                                SwitchComponent(option.display, option.selectedValue ?: "No") { value ->
                                    formTypeOption?.let { it1 ->
                                        onSaveFormTypeOption(
                                            storeGivenAnswered(
                                                it1, option.optionId ?: 0, value,
                                                referenceId = viewModel.referenceId
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}




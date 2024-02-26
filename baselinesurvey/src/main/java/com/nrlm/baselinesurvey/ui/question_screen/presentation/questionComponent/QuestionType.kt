package com.nrlm.baselinesurvey.ui.question_screen.presentation.questionComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.model.datamodel.QuestionList
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nrlm.baselinesurvey.ui.common_components.CTAButtonComponent
import com.nrlm.baselinesurvey.ui.common_components.EditTextWithTitleComponent
import com.nrlm.baselinesurvey.ui.common_components.GridOptionCard
import com.nrlm.baselinesurvey.ui.common_components.OptionCard
import com.nrlm.baselinesurvey.ui.common_components.RadioButtonOptionComponent
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.TypeDropDownComponent
import com.nrlm.baselinesurvey.ui.theme.defaultTextStyle
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_18_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.DescriptionContentType
import com.patsurvey.nudge.customviews.htmltext.HtmlText
import kotlinx.coroutines.launch


@Composable
fun QuestionType(
    modifier: Modifier = Modifier,
    parentIndex: Int,
    questionIndex: Int,
    question: QuestionEntity,
    optionItemEntityList: List<OptionItemEntity>?,
    selectedOptionIndex: Int = -1,
    maxCustomHeight: Dp,
    isLazyVerticalGrid: Boolean = false,
    onAnswerSelection: (questionIndex: Int, optionItem: OptionItemEntity) -> Unit,
    questionDetailExpanded: (index: Int) -> Unit,
    onMediaTypeDescriptionAction: (descriptionContentType: DescriptionContentType, contentLink: String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var selectedIndex by remember { mutableIntStateOf(selectedOptionIndex) }
    val outerState: LazyListState = rememberLazyListState()

    val innerState: LazyGridState = rememberLazyGridState()
    val innerListState: LazyListState = rememberLazyListState()
    val questionList = remember {
        mutableStateOf(mutableListOf<QuestionList?>())
    }
    val optionDetailVisibilityState = remember {
        mutableStateOf(false)
    }
    val selectedOptionsItem = remember { mutableListOf<OptionItemEntity>() }


    SideEffect {
        val totalItemsCount =
            if (isLazyVerticalGrid) innerState.layoutInfo.totalItemsCount else innerListState.layoutInfo.totalItemsCount
        if (outerState.layoutInfo.visibleItemsInfo.size == 2 && totalItemsCount == 0) scope.launch {
            outerState.scrollToItem(
                outerState.layoutInfo.totalItemsCount
            )
        }
        println("outer ${outerState.layoutInfo.visibleItemsInfo.map { it.index }}")
        println("inner ${innerState.layoutInfo.visibleItemsInfo.map { it.index }}")
    }

    BoxWithConstraints(
        modifier = modifier
            .scrollable(
                state = outerState,
                Orientation.Vertical,
            )
            .heightIn(min = 100.dp, maxCustomHeight)
    ) {
        Card(elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight, max = maxHeight)
                .background(white)
                .clickable {

                }
                .then(modifier)) {
            Column(modifier = Modifier.background(white)) {
                Column(
                    Modifier.padding(top = dimen_16_dp),
                    verticalArrangement = Arrangement.spacedBy(dimen_18_dp)
                ) {
                    LazyColumn(
                        state = outerState,
                        modifier = Modifier.heightIn(min = 110.dp, max = maxCustomHeight)
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = dimen_16_dp)
                            ) {
                                Text(
                                    text = "${parentIndex}.${questionIndex + 1}. ",
                                    style = defaultTextStyle,
                                    color = textColorDark
                                )
                                HtmlText(
                                    text = "${question.questionDisplay}",
                                    style = defaultTextStyle,
                                    color = textColorDark,
                                    overflow = TextOverflow.Ellipsis,
                                    softWrap = true
                                )
                            }
                        }
                        item {
                            when (question.type) {
                                QuestionType.RadioButton.name,
                                QuestionType.MultiSelect.name, QuestionType.Grid.name
                                -> {
                                    LazyVerticalGridList(
                                        innerState = innerState,
                                        maxCustomHeight = maxCustomHeight,
                                        optionItemEntityList = optionItemEntityList,
                                        selectedIndex = selectedIndex,
                                        onAnswerSelection = onAnswerSelection,
                                        questionIndex = questionIndex,
                                        optionDetailVisibilityState = optionDetailVisibilityState.value,
                                        questionList = questionList.value,
                                        questionType = question.type ?: "",
                                    )
                                }

                                QuestionType.SingleSelect.name, QuestionType.List.name,
                                QuestionType.Form.name, QuestionType.Input.name,
                                QuestionType.SingleSelectDropdown.name -> {
                                    CreateLazyHorizontalGrid(
                                        innerState = innerListState,
                                        maxCustomHeight = maxCustomHeight,
                                        optionItemEntityList = optionItemEntityList,
                                        selectedOptionIndex = selectedOptionIndex,
                                        questionType = question.type ?: "",
                                        questionIndex = 1
                                    )

                                }


                            }
                        }
                        item {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(dimen_10_dp)
                            )
//                            Divider(
//                                thickness = dimen_1_dp,
//                                color = lightGray2,
//                                modifier = Modifier.fillMaxWidth()
//                            )
//                            ExpandableDescriptionContentComponent(questionDetailExpanded,
//                                questionIndex,
//                                question,
//                                imageClickListener = { imageTypeDescriptionContent ->
//                                    onMediaTypeDescriptionAction(
//                                        DescriptionContentType.IMAGE_TYPE_DESCRIPTION_CONTENT,
//                                        imageTypeDescriptionContent
//                                    )
//                                },
//                                videoLinkClicked = { videoTypeDescriptionContent ->
//                                    onMediaTypeDescriptionAction(
//                                        DescriptionContentType.VIDEO_TYPE_DESCRIPTION_CONTENT,
//                                        videoTypeDescriptionContent
//                                    )
//                                })
                        }
                    }
                }
            }

        }
    }
}

@Composable
private fun LazyVerticalGridList(
    innerState: LazyGridState,
    maxCustomHeight: Dp,
    optionItemEntityList: List<OptionItemEntity>?,
    selectedIndex: Int,
    onAnswerSelection: (questionIndex: Int, optionItem: OptionItemEntity) -> Unit,
    questionIndex: Int,
    questionType: String,
    optionDetailVisibilityState: Boolean,
    questionList: MutableList<QuestionList?>,
    selectedOptionIndex: Int = -1,
    selectedOptionIndices: List<Int> = listOf(),
) {

    var selectedIndex1 = selectedIndex
    val selectedIndex = remember { mutableIntStateOf(selectedOptionIndex) }
    val selectedIndices = remember { mutableStateListOf<Int>() }
    selectedIndices.addAll(selectedOptionIndices)

    val selectedOptionsItem = remember { mutableListOf<OptionItemEntity>() }

    LazyVerticalGrid(
        userScrollEnabled = false,
        state = innerState,
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .wrapContentWidth()
            .padding(horizontal = dimen_16_dp)
            .heightIn(min = 110.dp, max = maxCustomHeight)
    ) {
        itemsIndexed(
            optionItemEntityList ?: emptyList()
        ) { _index: Int, optionItem: OptionItemEntity ->
            when (questionType) {
                QuestionType.RadioButton.name -> {
                    RadioButtonOptionComponent(
                        index = _index, optionsItem = optionItem, selectedIndex = selectedIndex1
                    ) {
                        selectedIndex1 = _index
//                        onAnswerSelection(questionIndex, optionItem)
                    }
                }

                QuestionType.Grid.name, QuestionType.MultiSelect.name -> {
//                    GridType(optionItem, _index, selectedIndices, selectedOptionsItem)
                    Spacer(modifier = Modifier.height(4.dp))
                }

                else -> {}
            }

        }
    }
}

/*@Composable
private fun GridType(
    optionItem: OptionItemEntity,
    _index: Int,
    selectedIndices: SnapshotStateList<Int>,
    selectedOptionsItem: MutableList<OptionItemEntity>
) {
    GridOptionCard(
        optionItem = optionItem,
        index = _index,
        selectedIndex = selectedIndices
    ) {
        if (!selectedIndice.contains(it)) {
            optionItem.isSelected = true
            selectedIndices.add(it)
        } else {
            optionItem.isSelected = false
            selectedIndices.remove(it)
        }
        if (!selectedOptionsItem.contains(optionItem)) {
            selectedOptionsItem.add(optionItem)
        } else {
            selectedOptionsItem.remove(optionItem)
        }
        //onAnswerSelection(questionIndex, selectedOptionsItem, selectedIndices)
    }
}*/

@Composable
fun CreateLazyHorizontalGrid(
    innerState: LazyListState,
    maxCustomHeight: Dp,
    optionItemEntityList: List<OptionItemEntity>?,
    selectedOptionIndex: Int = -1,
    questionIndex: Int,
    questionType: String,
    questionTitle: String = ""
) {
    val selectedIndex = remember { mutableIntStateOf(selectedOptionIndex) }

    LazyColumn(
        userScrollEnabled = false,
        state = innerState,
        modifier = Modifier
            .wrapContentWidth()
            .padding(horizontal = dimen_16_dp)
            .heightIn(min = 110.dp, max = maxCustomHeight)
    ) {
        itemsIndexed(
            optionItemEntityList ?: listOf()
        ) { _index: Int, optionsItem: OptionItemEntity ->
            when (questionType) {
                QuestionType.List.name, QuestionType.SingleSelect.name -> {
                    ListType(optionsItem, _index, selectedIndex, questionIndex)
                    Spacer(modifier = Modifier.height(dimen_8_dp))
                }

                QuestionType.Form.name -> {
                    FormType(questionTitle)
                }

                QuestionType.Input.name -> {
                    InputType(optionsItem = optionsItem)
                }

                QuestionType.SingleSelectDropdown.name -> {
                    DropDownType(optionsItem = optionsItem)
                }

                else -> {}
            }
        }
    }

}

@Composable
private fun DropDownType(optionsItem: OptionItemEntity) {
    TypeDropDownComponent(
        optionsItem.display,
        optionsItem.selectedValue ?: "Select",
        optionsItem.values
    ) {
        //onAnswerSelection(optionsItem.optionId?:0,it)
    }
}

@Composable
private fun InputType(optionsItem: OptionItemEntity) {
    EditTextWithTitleComponent(
        optionsItem.display,
        optionsItem.selectedValue ?: ""
    ) {
        //  onAnswerSelection(optionsItem.optionId?:0,it)
    }
    Spacer(modifier = Modifier.height(dimen_8_dp))
}

@Composable
private fun FormType(questionTitle: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        CTAButtonComponent(
            tittle = questionTitle,
            Modifier
                .fillMaxWidth()
        ) {
            //onAnswerSelection()
        }
    }
}

@Composable
private fun ListType(
    optionsItem: OptionItemEntity, _index: Int, selectedIndex: MutableIntState, questionIndex: Int
) {
    OptionCard(
        optionItem = optionsItem,
        index = _index,
        selectedIndex = selectedIndex.value,
    ) {
        selectedIndex.value = questionIndex
    }
}

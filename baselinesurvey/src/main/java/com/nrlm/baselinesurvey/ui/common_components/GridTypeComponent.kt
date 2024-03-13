package com.nrlm.baselinesurvey.ui.common_components

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.database.entity.ContentEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.model.datamodel.OptionsItem
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nrlm.baselinesurvey.ui.question_screen.presentation.QuestionEntityState
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.defaultCardElevation
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_18_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_1_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_20_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_5_dp
import com.nrlm.baselinesurvey.ui.theme.languageItemActiveBg
import com.nrlm.baselinesurvey.ui.theme.lightGray2
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.DescriptionContentType
import com.nrlm.baselinesurvey.utils.getIndexById
import com.patsurvey.nudge.customviews.htmltext.HtmlText
import kotlinx.coroutines.launch

@Composable
fun GridTypeComponent(
    modifier: Modifier = Modifier,
    question: QuestionEntity,
    showQuestionState: QuestionEntityState = QuestionEntityState.getEmptyStateObject(),
    optionItemEntityList: List<OptionItemEntity>,
    questionIndex: Int,
    contests: List<ContentEntity?>? = listOf(),
    selectedOptionIndices: List<Int>,
    maxCustomHeight: Dp,
    onAnswerSelection: (questionIndex: Int, optionItems: List<OptionItemEntity>, selectedIndeciesCount: List<Int>) -> Unit,
    onMediaTypeDescriptionAction: (descriptionContentType: DescriptionContentType, contentLink: String) -> Unit,
    questionDetailExpanded: (index: Int) -> Unit
) {

    val scope = rememberCoroutineScope()
    val outerState: LazyListState = rememberLazyListState()
    val innerState: LazyGridState = rememberLazyGridState()
    val innerFirstVisibleItemIndex by remember {
        derivedStateOf {
            innerState.firstVisibleItemIndex
        }
    }

    val selectedIndices = remember { mutableStateOf(mutableSetOf<Int>()) }
    selectedIndices.value.clear()
    selectedIndices.value.addAll(selectedOptionIndices)

    val selectedOptionsItem = remember { mutableSetOf<OptionItemEntity>() }

    SideEffect {
        if (outerState.layoutInfo.visibleItemsInfo.size == 2 && innerState.layoutInfo.totalItemsCount == 0)
            scope.launch { outerState.scrollToItem(outerState.layoutInfo.totalItemsCount) }
        println("outer ${outerState.layoutInfo.visibleItemsInfo.map { it.index }}")
        println("inner ${innerState.layoutInfo.visibleItemsInfo.map { it.index }}")
    }
    val manualMaxHeight = (showQuestionState.optionItemEntityState.size * 100).dp

    BoxWithConstraints(
        modifier = modifier
            .scrollable(
                state = outerState,
                Orientation.Vertical,
            )
            .heightIn(min = 100.dp, maxCustomHeight + manualMaxHeight)
    ) {

        VerticalAnimatedVisibilityComponent(visible = showQuestionState.showQuestion) {
            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = defaultCardElevation
                ),
                shape = RoundedCornerShape(roundedCornerRadiusDefault),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(white)
                    .clickable {
                    }
                    .then(modifier)
            ) {
                Column(modifier = Modifier.background(white)) {
                    Column(
                        Modifier.padding(top = dimen_16_dp),
                        verticalArrangement = Arrangement.spacedBy(
                            dimen_18_dp
                        )
                    ) {
                        LazyColumn(
                            state = outerState,
                            modifier = Modifier
                                .heightIn(min = 110.dp, max = maxCustomHeight + manualMaxHeight)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier.padding(horizontal = dimen_16_dp)
                                ) {
                                    HtmlText(
                                        text = "${questionIndex + 1}. ",
                                        style = TextStyle(
                                            fontFamily = NotoSans,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = textColorDark
                                        ),
                                    )

                                    HtmlText(
                                        text = "${question.questionDisplay}",
                                        style = TextStyle(
                                            fontFamily = NotoSans,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = textColorDark
                                        ),
                                    )
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(dimen_10_dp))
                            }

                            item {
                                if (optionItemEntityList?.isNotEmpty() == true) {
                                    LazyVerticalGrid(
                                        userScrollEnabled = false,
                                        state = innerState,
                                        columns = GridCells.Fixed(2),
                                        modifier = Modifier
                                            .wrapContentWidth()
                                            .padding(horizontal = dimen_16_dp)
                                            .heightIn(
                                                min = 110.dp,
                                                max = maxCustomHeight + manualMaxHeight
                                            ),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        itemsIndexed(optionItemEntityList.sortedBy { it.optionId }
                                            ?: emptyList()) { _index, optionItem ->
                                            if (optionItem.optionType?.equals(QuestionType.Grid.name, true) == true
                                                || optionItem.optionType?.equals(QuestionType.MultiSelect.name, true) == true) {
                                                GridOptionCard(
                                                    optionItem = optionItem,
                                                    index = _index,
                                                    selectedIndex = selectedIndices.value.toList()
                                                ) { selectedOptionId ->

                                                    try {
                                                        if (!selectedIndices.value.contains(
                                                                selectedOptionId
                                                            )
                                                        ) {
                                                            selectedIndices.value.add(
                                                                selectedOptionId
                                                            )
                                                            selectedOptionsItem.add(
                                                                optionItemEntityList[optionItemEntityList.getIndexById(
                                                                    selectedOptionId
                                                                )]
                                                            )
                                                        } else {
                                                            selectedIndices.value.remove(
                                                                selectedOptionId
                                                            )
                                                            selectedOptionsItem.remove(
                                                                optionItemEntityList[optionItemEntityList.getIndexById(
                                                                    selectedOptionId
                                                                )]
                                                            )
                                                        }

                                                        optionItemEntityList.forEach { optionItemEntity ->
                                                            if (selectedIndices.value.contains(
                                                                    optionItemEntity.optionId
                                                                )
                                                            ) {
                                                                selectedOptionsItem.add(
                                                                    optionItemEntityList[
                                                                        optionItemEntityList.getIndexById(
                                                                            optionItemEntity.optionId!!
                                                                        )
                                                                    ]
                                                                )
                                                            }
                                                        }

                                                        onAnswerSelection(
                                                            questionIndex,
                                                            selectedOptionsItem.toList(),
                                                            selectedOptionIndices
                                                        )
                                                    } catch (ex: Exception) {
                                                        BaselineLogger.e(
                                                            "GridTypeComponent",
                                                            "GridOptionCard onOptionSelected exception -> ${ex.localizedMessage}",
                                                            ex
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                            }
                                        }

                                    }
                                }
                            }

                            (optionItemEntityList.sortedBy { it.optionId }.filter { it.conditional }
                                ?: emptyList<OptionItemEntity>()).forEachIndexed() { index, optionItem ->
                                    item {

                                        if (optionItem.optionType?.equals(QuestionType.InputNumber.name, true) == true
                                            || optionItem.optionType?.equals(QuestionType.InputText.name, true) == true
                                            || optionItem.optionType?.equals(QuestionType.Input.name, true) == true) {
                                            val optionItemEntityState = showQuestionState.optionItemEntityState.find { it.optionId == optionItem.optionId }
                                            Box(modifier = Modifier.padding(horizontal = dimen_20_dp)) {
                                                EditTextWithTitleComponent(
                                                    optionItem.display,
                                                    showQuestion = optionItemEntityState,
                                                    defaultValue = optionItemEntityState?.optionItemEntity?.selectedValue
                                                        ?: BLANK_STRING,
                                                    isOnlyNumber = optionItemEntityState?.optionItemEntity?.optionType == QuestionType.InputNumber.name
                                                ) { value ->
                                                    val updatedOptionItem =
                                                        optionItem.copy(selectedValue = value)
                                                    try {
                                                        if (!selectedIndices.value.contains(
                                                                updatedOptionItem.optionId
                                                            )
                                                        ) {
                                                            selectedIndices.value.add(
                                                                updatedOptionItem.optionId!!
                                                            )
                                                            selectedOptionsItem.add(
                                                                optionItemEntityList[optionItemEntityList.getIndexById(
                                                                    updatedOptionItem.optionId!!
                                                                )]
                                                            )
                                                        } else {
                                                            selectedIndices.value.remove(
                                                                updatedOptionItem.optionId
                                                            )
                                                            selectedOptionsItem.remove(
                                                                optionItemEntityList[optionItemEntityList.getIndexById(
                                                                    updatedOptionItem.optionId!!
                                                                )]
                                                            )
                                                        }

                                                        optionItemEntityList.forEach { optionItemEntity ->
                                                            if (selectedIndices.value.contains(
                                                                    optionItemEntity.optionId
                                                                )
                                                            ) {
                                                                selectedOptionsItem.add(
                                                                    optionItemEntityList[
                                                                        optionItemEntityList.getIndexById(
                                                                            optionItemEntity.optionId!!
                                                                        )
                                                                    ]
                                                                )
                                                            }
                                                        }

                                                        onAnswerSelection(
                                                            questionIndex,
                                                            selectedOptionsItem.toList(),
                                                            selectedOptionIndices
                                                        )
                                                    } catch (ex: Exception) {
                                                        BaselineLogger.e(
                                                            "GridTypeComponent",
                                                            "GridOptionCard onOptionSelected exception -> ${ex.localizedMessage}",
                                                            ex
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            item {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 10.dp)
                                )
                                if (contests?.isNotEmpty() == true) {
                                    Divider(
                                        thickness = dimen_1_dp,
                                        color = lightGray2,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    ExpandableDescriptionContentComponent(
                                        questionDetailExpanded,
                                        questionIndex,
                                        contents = contests,
                                        subTitle = BLANK_STRING,
                                        imageClickListener = { imageTypeDescriptionContent ->
                                            onMediaTypeDescriptionAction(
                                                DescriptionContentType.IMAGE_TYPE_DESCRIPTION_CONTENT,
                                                imageTypeDescriptionContent
                                            )
                                        },
                                        videoLinkClicked = { videoTypeDescriptionContent ->
                                            onMediaTypeDescriptionAction(
                                                DescriptionContentType.VIDEO_TYPE_DESCRIPTION_CONTENT,
                                                videoTypeDescriptionContent
                                            )
                                        }
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

@Composable
fun GridOptionCard(
    modifier: Modifier = Modifier,
    optionItem: OptionItemEntity,
    index: Int,
    selectedIndex: List<Int>,
    onOptionSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimen_5_dp, vertical = dimen_5_dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (selectedIndex.contains(optionItem.optionId)) blueDark else languageItemActiveBg)
            .clickable {
                onOptionSelected(optionItem.optionId ?: -1)
            }
            .then(modifier)) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HtmlText(
                    text = optionItem.display ?: BLANK_STRING,
                    style = TextStyle(
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    ),
                    color = if (selectedIndex.contains(optionItem.optionId)) white else textColorDark
                )
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
        )
    }

}

@Preview(showBackground = true)
@Composable
fun GridTypeQuestionPreview() {
    val optionList = mutableListOf<OptionsItem>()
    for (i in 1..5) {
        optionList.add(OptionsItem("Option Value $i", i + 1, i, 1, "Summery"))
    }

    val question = QuestionEntity(
        id = 3,
        questionId = 12,
        questionDisplay = "How much is your current savings? (Select all that apply)",
        questionSummary = "How much is your current savings? (Select all that apply)",
        order = 12,
        type = "Grid",
        gotoQuestionId = 13,
        questionImageUrl = "Section1_ColourTV.webp",
        surveyId = 1
    )
    val option1 = OptionItemEntity(
        optionId = 1,
        display = "YES",
        weight = 1,
        summary = "YES",
        optionValue = 1,
        // optionImage = R.drawable.icon_check,
        optionImage = "",
        optionType = "",
        surveyId = 1,
        questionId = 1,
        id = 1
    )

    val option2 = OptionItemEntity(
        optionId = 2,
        display = "NO",
        weight = 0,
        summary = "NO",
        optionValue = 0,
        // optionImage = R.drawable.icon_close,
        optionImage = "",
        optionType = "",
        surveyId = 1,
        questionId = 1,
        id = 1
    )

    val option3 = OptionItemEntity(
        optionId = 1,
        display = "Milk and milk products",
        weight = 1,
        summary = "Milk and milk products",
        optionValue = 1,
        // optionImage = R.drawable.icon_check,
        optionImage = "",
        optionType = "",
        surveyId = 1,
        questionId = 1,
        id = 1
    )
    val optionItemEntity = listOf(option1, option2, option3, option1)

    BoxWithConstraints() {
        GridTypeComponent(
            modifier = Modifier.padding(16.dp),
            question = question,
            optionItemEntityList = optionItemEntity,
            onAnswerSelection = { questionIndex, optionsItem, selectedIndeciesCount ->

            },
            questionDetailExpanded = {},
            questionIndex = 1,
            maxCustomHeight = maxHeight,
            selectedOptionIndices = listOf(),
            onMediaTypeDescriptionAction = { descriptionContentType, contentLink ->
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GridOptionCardPreview() {
    val selectedIndex = remember {
        mutableStateOf(mutableListOf<Int>(1))
    }
    val option = OptionItemEntity(
        optionId = 1,
        display = "Milk and milk products",
        weight = 1,
        summary = "Milk and milk products",
        optionValue = 1,
        // optionImage = R.drawable.icon_check,
        optionImage = "",
        optionType = "",
        surveyId = 1,
        questionId = 1,
        id = 1
    )
    GridOptionCard(modifier = Modifier, option, index = 0, onOptionSelected = {}, selectedIndex = selectedIndex.value)
}
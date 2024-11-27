package com.sarathi.surveymanager.ui.component

import android.annotation.SuppressLint
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nudge.core.BLANK_STRING
import com.nudge.core.activityCompleteOrDidiReassignedToast
import com.nudge.core.getQuestionNumber
import com.nudge.core.model.QuestionStatusModel
import com.nudge.core.ui.theme.GreyLight
import com.nudge.core.ui.theme.NotoSans
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.defaultCardElevation
import com.nudge.core.ui.theme.dimen_0_dp
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_18_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.languageItemActiveBg
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.white
import com.nudge.core.value
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel
import com.sarathi.surveymanager.ui.htmltext.HtmlText
import kotlinx.coroutines.launch

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun GridTypeComponent(
    contests: List<ContentList?>? = listOf(),
    modifier: Modifier = Modifier,
    questionDisplay: String,
    optionUiModelList: List<OptionsUiModel>,
    areOptionsEnabled: Boolean = true,
    isRequiredField: Boolean = true,
    questionIndex: Int,
    maxCustomHeight: Dp,
    showCardView: Boolean = false,
    isTaskMarkedNotAvailable: MutableState<Boolean> = mutableStateOf(false),
    questionStatusModel: QuestionStatusModel,
    isQuestionDisplay: Boolean = true,
    optionStateMap: SnapshotStateMap<Pair<Int, Int>, Boolean> = mutableStateMapOf(),
    onAnswerSelection: (optionIndex: Int, isSelected: Boolean) -> Unit,
    isFromTypeQuestion: Boolean = false,
    onDetailIconClicked: () -> Unit = {},
    navigateToMediaPlayerScreen: (ContentList) -> Unit = {}, // Default empty lambda
    questionDetailExpanded: (index: Int) -> Unit,
) {

    val scope = rememberCoroutineScope()
    val outerState: LazyListState = rememberLazyListState()
    val innerState: LazyGridState = rememberLazyGridState()

    val context = LocalContext.current

    SideEffect {
        if (outerState.layoutInfo.visibleItemsInfo.size == 2 && innerState.layoutInfo.totalItemsCount == 0)
            scope.launch { outerState.scrollToItem(outerState.layoutInfo.totalItemsCount) }
        println("outer ${outerState.layoutInfo.visibleItemsInfo.map { it.index }}")
        println("inner ${innerState.layoutInfo.visibleItemsInfo.map { it.index }}")
    }
    val manualMaxHeight = 100.dp

    BoxWithConstraints(
        modifier = modifier
            .scrollable(
                state = outerState,
                Orientation.Vertical,
            )
            .heightIn(min = 100.dp, maxCustomHeight + manualMaxHeight)
    ) {

        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (showCardView) defaultCardElevation else dimen_0_dp
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
                        if (isQuestionDisplay) {
                            item {
                                Row(
                                    modifier = Modifier.padding(horizontal = dimen_16_dp)
                                ) {
                                    QuestionComponent(
                                        isFromTypeQuestionInfoIconVisible = isFromTypeQuestion && contests?.isNotEmpty() == true,
                                        onDetailIconClicked = { onDetailIconClicked() },
                                        title = questionDisplay,
                                        questionNumber = if (showCardView) getQuestionNumber(
                                            questionIndex
                                        ) else BLANK_STRING,
                                        isRequiredField = isRequiredField
                                    )
                                }
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(dimen_10_dp))
                        }

                        item {
                            if (optionUiModelList?.isNotEmpty() == true) {
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
                                    itemsIndexed(
                                        optionUiModelList
                                            ?: emptyList()
                                    ) { _index, optionItem ->
                                        GridOptionCard(
                                            optionItem = optionItem,
                                            isEnabled = optionStateMap.filter {
                                                it.key == Pair(
                                                    optionItem.questionId,
                                                    optionItem.optionId
                                                )
                                            }.entries.firstOrNull(),
                                            isOptionSelected = optionItem.isSelected.value(),
                                            isTaskMarkedNotAvailable = isTaskMarkedNotAvailable,
                                            questionStatusModel = questionStatusModel
                                        ) { selectedOptionId, isSelected ->

                                            onAnswerSelection(_index, isSelected)

                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }

                                }
                            }
                        }
                        item {
                            if (showCardView && contests?.isNotEmpty() == true)
                            ContentBottomViewComponent(
                                contents = contests,
                                questionIndex = questionIndex,
                                showCardView = showCardView,
                                questionDetailExpanded = {},
                                navigateToMediaPlayerScreen = { contentList ->
                                    navigateToMediaPlayerScreen(contentList)
                                }
                            )
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
    optionItem: OptionsUiModel,
    isTaskMarkedNotAvailable: MutableState<Boolean>,
    isEnabled: Map.Entry<Pair<Int, Int>, Boolean>?,
    isOptionSelected: Boolean = false,
    questionStatusModel: QuestionStatusModel,
    onOptionSelected: (Int, isSelected: Boolean) -> Unit
) {
    val context = LocalContext.current
    val isSelected = remember(optionItem.description, isEnabled?.key, isEnabled?.value) {
        mutableStateOf(isOptionSelected)
    }

    val isOptionEnabled = remember(isEnabled?.key, isEnabled?.value) {
        mutableStateOf(isEnabled?.value.value(defaultValue = true))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimen_5_dp, vertical = dimen_5_dp)
            .clip(RoundedCornerShape(6.dp))
            .background(
                selectBackgroundColor(
                    isSelected.value,
                    isTaskMarkedNotAvailable
                )
            )
            .clickable {
                if (isOptionEnabled.value
                    && !questionStatusModel.isDidiReassigned
                    && questionStatusModel.isEditAllowed
                ) {
                    isSelected.value = !isSelected.value
                    onOptionSelected(optionItem.optionId ?: -1, isSelected.value)
                } else {
                    context.activityCompleteOrDidiReassignedToast(questionStatusModel)
                }
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
                    text = optionItem.description.value(),
                    style = TextStyle(
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    ),
                    color = selectTextColor(
                        selectedValueState = isSelected.value,
                        isTaskMarkedNotAvailable = isTaskMarkedNotAvailable,
                        isEnabled = isOptionEnabled.value
                    )
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

@Composable
fun selectBackgroundColor(
    selectedValueState: Boolean,
    isTaskMarkedNotAvailable: MutableState<Boolean>
): Color {
    return if (isTaskMarkedNotAvailable.value)
        GreyLight
    else {
        if (selectedValueState) {
            blueDark
        } else {
            languageItemActiveBg
        }
    }
}

@Composable
fun selectTextColor(
    selectedValueState: Boolean,
    isTaskMarkedNotAvailable: MutableState<Boolean>,
    isEnabled: Boolean
): Color {
    return if (isTaskMarkedNotAvailable.value)
        textColorDark.copy(
            alpha = if (isEnabled) 1f else 0.5f
        )
    else {
        if (selectedValueState) {
            white
        } else {
            textColorDark.copy(
                alpha = if (isEnabled) 1f else 0.5f
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GridOptionCardPreview() {
    /*val selectedIndex = remember {
        mutableStateOf(mutableListOf<Int>(1))
    }
    val option = OptionItemEntity(
        optionId = 1,
//        display = "Milk and milk products",
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
    GridOptionCard(
        modifier = Modifier,
        option,
        onOptionSelected = {},
        selectedIndex = selectedIndex.value
    )*/
}
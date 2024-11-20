package com.sarathi.surveymanager.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nudge.core.BLANK_STRING
import com.nudge.core.activityCompleteOrDidiReassignedToast
import com.nudge.core.getQuestionNumber
import com.nudge.core.model.QuestionStatusModel
import com.nudge.core.ui.theme.defaultCardElevation
import com.nudge.core.ui.theme.dimen_0_dp
import com.nudge.core.ui.theme.dimen_100_dp
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_18_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_64_dp
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@Composable
fun RadioQuestionBoxComponent(
    modifier: Modifier = Modifier,
    questionIndex: Int,
    questionDisplay: String,
    isRequiredField: Boolean = true,
    optionUiModelList: List<OptionsUiModel>,
    selectedOptionIndex: Int = -1,
    maxCustomHeight: Dp,
    showCardView: Boolean = false,
    questionStatusModel: QuestionStatusModel,
    isQuestionTypeToggle: Boolean = false,
    onAnswerSelection: (questionIndex: Int, optionItemIndex: Int) -> Unit,
) {

    val scope = rememberCoroutineScope()
    var selectedIndex by remember(questionIndex) { mutableIntStateOf(selectedOptionIndex) }
    val outerState: LazyListState = rememberLazyListState()
    val innerState: LazyGridState = rememberLazyGridState()
    SideEffect {
        if (outerState.layoutInfo.visibleItemsInfo.size == 2 && innerState.layoutInfo.totalItemsCount == 0)
            scope.launch { outerState.scrollToItem(outerState.layoutInfo.totalItemsCount) }
        println("outer ${outerState.layoutInfo.visibleItemsInfo.map { it.index }}")
        println("inner ${innerState.layoutInfo.visibleItemsInfo.map { it.index }}")
    }
    val context = LocalContext.current

    BoxWithConstraints(
        modifier = modifier
            .scrollable(
                state = outerState,
                Orientation.Vertical,
            )
            .heightIn(
                min = if (isQuestionTypeToggle) dimen_64_dp else dimen_100_dp,
                maxCustomHeight
            )
    ) {
        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (showCardView) defaultCardElevation else dimen_0_dp
            ),
            shape = RoundedCornerShape(roundedCornerRadiusDefault),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight, max = maxHeight)
                .background(white)
                .clickable {

                }
                .then(modifier)
        ) {
            Column(modifier = Modifier.background(white)) {

                Column(
                    Modifier.padding(top = if (isQuestionTypeToggle) dimen_5_dp else dimen_16_dp),
                    verticalArrangement = Arrangement.spacedBy(if (isQuestionTypeToggle) dimen_5_dp else dimen_18_dp)
                ) {
                    LazyColumn(
                        state = outerState,
                        modifier = Modifier
                            .heightIn(
                                min = if (isQuestionTypeToggle) 60.dp else 100.dp,
                                max = maxCustomHeight
                            )
                    ) {

                        item {

                            Row(
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
                                    .padding(horizontal = dimen_16_dp)
                            ) {
                                QuestionComponent(
                                    title = questionDisplay,
                                    questionNumber = if (showCardView) getQuestionNumber(
                                        questionIndex
                                    ) else BLANK_STRING,
                                    isRequiredField = isRequiredField
                                )
                            }
                        }
                        item {
                            if (optionUiModelList.isNotEmpty()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    optionUiModelList.forEachIndexed { _index, option ->
                                        RadioButtonOptionComponent(
                                            modifier = Modifier.weight(1f),
                                            index = _index,
                                            optionsItem = option,
                                            isIconRequired = !isQuestionTypeToggle,
                                            selectedIndex = selectedIndex
                                        ) {
                                            if (questionStatusModel.isEditAllowed && !questionStatusModel.isDidiReassigned) {
                                                selectedIndex = _index
                                                onAnswerSelection(
                                                    questionIndex,
                                                    selectedIndex
                                                )
                                            } else {
                                                context.activityCompleteOrDidiReassignedToast(
                                                    questionStatusModel
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(dimen_10_dp)
                                )
                            }
                        }
                        if (!isQuestionTypeToggle) {
                            item {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(dimen_10_dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ToggleQuestionBoxComponent(
    modifier: Modifier = Modifier,
    questionIndex: Int,
    questionDisplay: String,
    isRequiredField: Boolean = true,
    optionUiModelList: List<OptionsUiModel>,
    selectedOptionIndex: Int = -1,
    showCardView: Boolean = false,
    maxCustomHeight: Dp,
    questionStatusModel: QuestionStatusModel,
    onAnswerSelection: (questionIndex: Int, optionItemIndex: Int) -> Unit,
) {
    RadioQuestionBoxComponent(
        modifier = modifier,
        questionIndex = questionIndex,
        questionDisplay = questionDisplay,
        isRequiredField = isRequiredField,
        maxCustomHeight = maxCustomHeight,
        selectedOptionIndex = selectedOptionIndex,
        isQuestionTypeToggle = true,
        showCardView = showCardView,
        optionUiModelList = optionUiModelList,
        questionStatusModel = questionStatusModel,
        onAnswerSelection = onAnswerSelection
    )
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun RadioQuestionBoxComponentPreview(
    modifier: Modifier = Modifier
) {

}
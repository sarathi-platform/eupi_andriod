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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nudge.core.getQuestionNumber
import com.nudge.core.showCustomToast
import com.nudge.core.ui.theme.defaultCardElevation
import com.nudge.core.ui.theme.dimen_0_dp
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_18_dp
import com.nudge.core.ui.theme.dimen_2_dp
import com.nudge.core.ui.theme.dimen_50_dp
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel
import com.sarathi.surveymanager.R
import kotlinx.coroutines.launch

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun SingleSelectGridComponent(
    modifier: Modifier = Modifier,
    content: List<ContentList?>? = listOf(),
    questionDisplay: String,
    optionUiModelList: List<OptionsUiModel>,
    areOptionsEnabled: Boolean = true,
    isRequiredField: Boolean = true,
    questionIndex: Int,
    maxCustomHeight: Dp,
    showCardView: Boolean = false,
    isQuestionNumberVisible: Boolean = false,
    isEditAllowed: Boolean = true,
    isQuestionDisplay: Boolean = true,
    optionStateMap: SnapshotStateMap<Pair<Int, Int>, Boolean> = mutableStateMapOf(),
    selectedOptionIndex: Int = -1,
    onAnswerSelection: (questionIndex: Int, optionItemIndex: Int) -> Unit,
    isFromTypeQuestion: Boolean = false,
    onDetailIconClicked: () -> Unit = {},
    navigateToMediaPlayerScreen: (ContentList) -> Unit = {}, // Default empty lambda
) {

    val scope = rememberCoroutineScope()
    val outerState: LazyListState = rememberLazyListState()
    val innerState: LazyGridState = rememberLazyGridState()

    var selectedIndex by remember(questionIndex) { mutableIntStateOf(selectedOptionIndex) }

    val context = LocalContext.current

    SideEffect {
        if (outerState.layoutInfo.visibleItemsInfo.size == 2 && innerState.layoutInfo.totalItemsCount == 0)
            scope.launch { outerState.scrollToItem(outerState.layoutInfo.totalItemsCount) }
        println("outer ${outerState.layoutInfo.visibleItemsInfo.map { it.index }}")
        println("inner ${innerState.layoutInfo.visibleItemsInfo.map { it.index }}")
    }
    val manualMinHeight = dimen_50_dp

    BoxWithConstraints(
        modifier = modifier
            .scrollable(
                state = outerState,
                Orientation.Vertical,
            )
            .wrapContentHeight()
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
                    Modifier.padding(
                        bottom = if (showCardView) dimen_16_dp else dimen_2_dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(
                        dimen_18_dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .wrapContentHeight()
                    ) {
                        if (isQuestionDisplay) {
                            Row(
                                modifier = Modifier.padding(horizontal = dimen_16_dp)
                            ) {
                                QuestionComponent(
                                    isFromTypeQuestionInfoIconVisible = isFromTypeQuestion && content?.isNotEmpty() == true,
                                    onDetailIconClicked = { onDetailIconClicked() },
                                    title = questionDisplay,
                                    questionNumber = getQuestionNumber(
                                        isQuestionNumberVisible,
                                        questionIndex
                                    ),
                                    isRequiredField = isRequiredField
                                )
                            }
                        }

//                        Spacer(modifier = Modifier.height(dimen_10_dp))

                        if (optionUiModelList?.isNotEmpty() == true) {
                            LazyVerticalGrid(
                                userScrollEnabled = false,
                                state = innerState,
                                columns = GridCells.Fixed(2),
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .padding(horizontal = dimen_10_dp)
                                    .heightIn(
                                        min = manualMinHeight,
                                        max = maxCustomHeight + manualMinHeight
                                    ),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                itemsIndexed(
                                    optionUiModelList
                                        ?: emptyList()
                                ) { _index, optionItem ->
                                    RadioButtonOptionComponent(
                                        modifier = Modifier.weight(1f),
                                        index = _index,
                                        optionsItem = optionItem,
                                        isIconRequired = false,
                                        isQuestionTypeToggle = true,
                                        selectedIndex = selectedIndex
                                    ) {
                                        if (isEditAllowed) {
                                            selectedIndex = _index
                                            onAnswerSelection(
                                                questionIndex,
                                                selectedIndex
                                            )
                                        } else {
                                            showCustomToast(
                                                context,
                                                context.getString(R.string.edit_disable_message)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }

                            }
                        }

                        if (showCardView && content?.isNotEmpty() == true) {
                            ContentBottomViewComponent(
                                contents = content,
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
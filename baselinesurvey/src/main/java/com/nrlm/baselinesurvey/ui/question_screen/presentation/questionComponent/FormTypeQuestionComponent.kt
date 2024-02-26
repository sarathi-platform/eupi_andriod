package com.nrlm.baselinesurvey.ui.question_screen.presentation.questionComponent

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.ui.common_components.CTAButtonComponent
import com.nrlm.baselinesurvey.ui.common_components.VerticalAnimatedVisibilityComponent
import com.nrlm.baselinesurvey.ui.question_screen.presentation.QuestionEntityState
import com.nrlm.baselinesurvey.utils.DescriptionContentType
import kotlinx.coroutines.launch

@Composable
fun FormTypeQuestionComponent(
    modifier: Modifier = Modifier,
    questionIndex: Int,
    question: QuestionEntity?,
    showQuestionState: QuestionEntityState = QuestionEntityState.getEmptyStateObject(),
    maxCustomHeight: Dp,
    onAnswerSelection: (questionIndex: Int) -> Unit,
    onMediaTypeDescriptionAction: (descriptionContentType: DescriptionContentType, contentLink: String) -> Unit,
    questionDetailExpanded: (index: Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    val outerState: LazyListState = rememberLazyListState()
    val innerState: LazyListState = rememberLazyListState()
    SideEffect {
        if (outerState.layoutInfo.visibleItemsInfo.size == 2 && innerState.layoutInfo.totalItemsCount == 0)
            scope.launch { outerState.scrollToItem(outerState.layoutInfo.totalItemsCount) }
        println("outer ${outerState.layoutInfo.visibleItemsInfo.map { it.index }}")
        println("inner ${innerState.layoutInfo.visibleItemsInfo.map { it.index }}")
    }
    BoxWithConstraints(
        modifier = modifier
            .scrollable(
                state = outerState,
                Orientation.Vertical,
            )
    ) {

        VerticalAnimatedVisibilityComponent(visible = showQuestionState.showQuestion) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)) {
                CTAButtonComponent(
                    tittle = question?.questionDisplay,
                    Modifier
                        .fillMaxWidth()
                ) {
                    onAnswerSelection(questionIndex)
                }
            }
            /*Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = defaultCardElevation
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
                        Modifier.padding(top = dimen_16_dp),
                        verticalArrangement = Arrangement.spacedBy(dimen_18_dp)
                    ) {
                        LazyColumn(
                            state = outerState,
                            modifier = Modifier
                                .heightIn(min = 110.dp, max = maxCustomHeight)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .padding(bottom = 10.dp)
                                        .padding(horizontal = dimen_16_dp)
                                ) {
                                    Text(
                                        text = "${questionIndex + 1}. ", style = defaultTextStyle,
                                        color = textColorDark
                                    )
                                    HtmlText(
                                        text = "${question?.questionDisplay}",
                                        style = defaultTextStyle,
                                        color = textColorDark,
                                        overflow = TextOverflow.Ellipsis,
                                        softWrap = true
                                    )
                                }
                            }
                            item {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(dimen_16_dp)
                                )
                            }
                            item {
                                Box(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)) {
                                    CTAButtonComponent(
                                        tittle = question?.questionDisplay,
                                        Modifier
                                            .fillMaxWidth()
                                    ) {
                                        onAnswerSelection(questionIndex)
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
    //                            ExpandableDescriptionContentComponent(
    //                                questionDetailExpanded,
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
    //                                }
    //                            )
                            }

                        }
                    }
                }
            }*/
        }

    }
}
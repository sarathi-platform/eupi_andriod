package com.nrlm.baselinesurvey.ui.question_screen.presentation.questionComponent

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.database.entity.ContentEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.ui.common_components.ExpandableDescriptionContentComponent
import com.nrlm.baselinesurvey.ui.common_components.OutlinedCTAButtonComponent
import com.nrlm.baselinesurvey.ui.common_components.SummaryCardComponent
import com.nrlm.baselinesurvey.ui.common_components.VerticalAnimatedVisibilityComponent
import com.nrlm.baselinesurvey.ui.htmltext.HtmlText
import com.nrlm.baselinesurvey.ui.question_screen.presentation.QuestionEntityState
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.defaultCardElevation
import com.nrlm.baselinesurvey.ui.theme.defaultTextStyle
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_18_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_1_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.lightGray2
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.weight_20_percent
import com.nrlm.baselinesurvey.ui.theme.weight_60_percent
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.DescriptionContentType
import com.nrlm.baselinesurvey.utils.findTagForId
import com.nrlm.baselinesurvey.utils.showCustomToast
import com.nrlm.baselinesurvey.utils.tagList
import com.nudge.core.activityCompleteOrDidiReassignedToast
import com.nudge.core.model.QuestionStatusModel
import kotlinx.coroutines.launch

@Composable
fun FormTypeQuestionComponent(
    modifier: Modifier = Modifier,
    questionIndex: Int,
    question: QuestionEntity?,
    showQuestionState: QuestionEntityState = QuestionEntityState.getEmptyStateObject(),
    maxCustomHeight: Dp,
    contests: List<ContentEntity?>? = listOf(),
    itemCount: Int = 0,
    summaryValue: String = BLANK_STRING,
    questionStatusModel: QuestionStatusModel,
    onAnswerSelection: (questionIndex: Int) -> Unit,
    onMediaTypeDescriptionAction: (descriptionContentType: DescriptionContentType, contentLink: String) -> Unit,
    questionDetailExpanded: (index: Int) -> Unit,
    onViewSummaryClicked: (questionId: Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    val outerState: LazyListState = rememberLazyListState()
    val innerState: LazyListState = rememberLazyListState()

    val context = LocalContext.current


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
            Card(
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
                                        .height(dimen_8_dp)
                                )
                            }
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(dimen_10_dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(weight_20_percent)
                                    )
                                    OutlinedCTAButtonComponent(
                                        tittle = question?.questionSummary,
                                        isActive = questionStatusModel.isEditAllowed && !questionStatusModel.isDidiReassigned,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(weight_60_percent)
                                    ) {
                                        if ((itemCount > 0 && isPublicInfraSectionForm(
                                                sectionId = question?.sectionId ?: 0,
                                                surveyId = question?.surveyId ?: 0,
                                                questionId = question?.questionId ?: 0
                                            )) && (questionStatusModel.isEditAllowed && !questionStatusModel.isDidiReassigned)
                                        ) {
                                            showCustomToast(
                                                context,
                                                context.getString(R.string.only_one_entry_can_be_added)
                                            )
                                        } else if (questionStatusModel.isEditAllowed && !questionStatusModel.isDidiReassigned) {
                                            onAnswerSelection(questionIndex)
                                        } else {
                                            context.activityCompleteOrDidiReassignedToast(
                                                questionStatusModel
                                            )
                                        }
                                    }
                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(weight_20_percent)
                                    )
                                }
                            }
                            if (itemCount > 0) {
                                if (!summaryValue.equals(BLANK_STRING) && tagList.findTagForId(
                                        question?.tag ?: 0
                                    ).equals("Livelihood Sources")
                                ) {
                                    item {
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(
                                                    style = SpanStyle(
                                                        fontFamily = NotoSans,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = 14.sp
                                                    )
                                                ) {
                                                    append(stringResource(R.string.total_annual_income_label))
                                                }
                                                withStyle(
                                                    style = SpanStyle(
                                                        fontFamily = NotoSans,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 14.sp
                                                    )
                                                ) {
                                                    append(summaryValue)
                                                }
                                            },
                                            color = blueDark,
                                            style = TextStyle(
                                                fontFamily = NotoSans,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 14.sp
                                            ),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(dimen_16_dp)
                                        )
                                    }
                                }
                                item {
                                    SummaryCardComponent(
                                        itemCount,
                                        question
                                    ) {
                                        onViewSummaryClicked(it)
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

private fun isPublicInfraSectionForm(surveyId: Int, sectionId: Int, questionId: Int): Boolean {
    //TODO Its handle with validation field that come in survey for now we are doing through  question ids
    val questionIds = listOf(8, 9, 11, 111, 12)
    return surveyId == 2 && sectionId == 2 && (questionIds.contains(
        questionId
    ))
}
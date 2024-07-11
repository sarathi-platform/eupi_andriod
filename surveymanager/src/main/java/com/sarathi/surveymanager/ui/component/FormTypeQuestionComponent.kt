package com.sarathi.surveymanager.ui.component

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.theme.NotoSans
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.defaultCardElevation
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_18_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.weight_20_percent
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.data.entities.QuestionEntity
import kotlinx.coroutines.launch

@Composable
fun FormTypeQuestionComponent(
    modifier: Modifier = Modifier,
    questionIndex: Int,
//    question: QuestionEntity?,
//    showQuestionState: QuestionEntityState = QuestionEntityState.getEmptyStateObject(),
    maxCustomHeight: Dp,
//    contests: List<ContentEntity?>? = listOf(),
    itemCount: Int = 0,
    summaryValue: String = BLANK_STRING,
    isEditAllowed: Boolean = true,
    onAnswerSelection: (questionIndex: Int) -> Unit,
//    onMediaTypeDescriptionAction: (descriptionContentType: DescriptionContentType, contentLink: String) -> Unit,
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
//                                    HtmlText(
//                                        text = "${question?.questionDisplay}",
//                                        style = defaultTextStyle,
//                                        color = textColorDark,
//                                        overflow = TextOverflow.Ellipsis,
//                                        softWrap = true
//                                    )
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

                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(weight_20_percent)
                                    )
                                }
                            }
                            if (itemCount > 0) {
                                if (!summaryValue.equals(BLANK_STRING)

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
//                                                    append(stringResource(R.string.total_annual_income_label))
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

                            }
                            item {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 10.dp)
                                )
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

@Preview(showBackground = true)
@Composable
fun FormTypeQuestionComponentPreview() {
   FormTypeQuestionComponent(
       questionIndex = 1,
//       question = ,
       maxCustomHeight = 100.dp,
       onAnswerSelection = {},
       questionDetailExpanded = {}
   ) {

   }
}
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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.model.datamodel.OptionsItem
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.defaultCardElevation
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_1_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.languageItemActiveBg
import com.nrlm.baselinesurvey.ui.theme.lightGray2
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.DescriptionContentType
import com.patsurvey.nudge.customviews.htmltext.HtmlText
import kotlinx.coroutines.launch


@Composable
fun ListTypeQuestion(
    modifier: Modifier = Modifier,
    question: QuestionEntity,
    questionIndex: Int,
    selectedOptionIndex: Int = -1,
    maxCustomHeight: Dp,
    onAnswerSelection: (questionIndex: Int, optionItem: OptionsItem) -> Unit,
    onMediaTypeDescriptionAction: (descriptionContentType: DescriptionContentType, contentLink: String) -> Unit,
    questionDetailExpanded: (index: Int) -> Unit
) {

    val scope = rememberCoroutineScope()
    val outerState: LazyListState = rememberLazyListState()
    val innerState: LazyListState = rememberLazyListState()
    val innerFirstVisibleItemIndex by remember {
        derivedStateOf {
            innerState.firstVisibleItemIndex
        }
    }

    val selectedIndex = remember { mutableStateOf(selectedOptionIndex) }

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
            .heightIn(min = 100.dp, maxCustomHeight)
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
                    verticalArrangement = Arrangement.spacedBy(
                        dimen_10_dp
                    )
                ) {
                    LazyColumn(
                        state = outerState,
                        modifier = Modifier
                            .heightIn(min = 110.dp, max = maxCustomHeight)
                    ) {

                        item {
                            Row(modifier = Modifier.padding(horizontal = dimen_16_dp)) {
                                HtmlText(
                                    text = "${question.questionId}. ",
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
                            Spacer(modifier = Modifier
                                .fillMaxWidth()
                                .height(dimen_16_dp))
                        }

                        item {
                            LazyColumn(
                                userScrollEnabled = false,
                                state = innerState,
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .padding(horizontal = dimen_16_dp)
                                    .heightIn(min = 110.dp, max = maxCustomHeight)
                            ) {
                                itemsIndexed(
                                    question.options ?: emptyList()
                                ) { _index: Int, optionsItem: OptionsItem ->
                                    OptionCard(
                                        buttonTitle = optionsItem.display ?: BLANK_STRING,
                                        index = _index,
                                        selectedIndex = selectedIndex.value,
                                    ) {
                                        selectedIndex.value = questionIndex
                                        onAnswerSelection(questionIndex, optionsItem)
                                    }
                                    Spacer(modifier = Modifier.height(dimen_8_dp))
                                }
                            }
                        }

                        item {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(dimen_10_dp)
                            )
                            Divider(
                                thickness = dimen_1_dp,
                                color = lightGray2,
                                modifier = Modifier.fillMaxWidth()
                            )
                            ExpandableDescriptionContentComponent(
                                questionDetailExpanded,
                                questionIndex,
                                question,
                                imageClickListener = { imageTypeDescriptionContent ->
                                    onMediaTypeDescriptionAction(DescriptionContentType.IMAGE_TYPE_DESCRIPTION_CONTENT, imageTypeDescriptionContent)
                                },
                                videoLinkClicked = { videoTypeDescriptionContent ->
                                    onMediaTypeDescriptionAction(DescriptionContentType.VIDEO_TYPE_DESCRIPTION_CONTENT, videoTypeDescriptionContent)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
 fun ListTypeQuestionPreview() {
    val optionList = mutableListOf<OptionsItem>()
    for (i in 1..5) {
        optionList.add(OptionsItem("Option Value $i", i + 1, i, 1, "Summery"))
    }

    val question = QuestionEntity(
        id = 2,
        questionId = 1,
        questionDisplay = "What is the <b>educational status </b> of adult members in the family?",
        questionSummary = "What is the <b>educational status </b> of adult members in the family?",
        order = 21,
        type =
        "List",
        gotoQuestionId =
        22,
        options = listOf(
            OptionsItem(
                optionId =
                30,
                display =
                "At least <b>1 adult </b> literate member who has <b> Passed Class 10</b>",
                weight =
                0,
                summary =
                "At least 1 adult > Class 10",
                optionValue =
                1,
                optionImage =
                0,
                optionType =
                ""
            ),
            OptionsItem(
                optionId =
                31,
                display =
                "At least <b>1 adult</b> literate member who can read, write Bangle/ Kok Book but has <b>not Passed Class 10</b>",
                weight =
                1,
                summary =
                "At least 1 literate adult < Class 10",
                optionValue =
                2,
                optionImage =
                0,
                optionType =
                ""
            ),
            OptionsItem(
                optionId =
                32,
                display =
                "\"<b>No adult</b> in the family is literate (cannot read or write Bangle / Kok-Bangle)",
                weight =
                2,
                summary =
                "No literate adult",
                optionValue =
                3,
                optionImage =
                0,
                optionType =
                ""
            )
        ),
        questionImageUrl = "Section1_2wheeler.webp",
        surveyId = 1
    )
    BoxWithConstraints() {

        ListTypeQuestion(
            modifier = Modifier.padding(10.dp),
            question = question,
            onAnswerSelection = {
                                questionIndex, optionItem ->
            },
            questionDetailExpanded = {},
            questionIndex = 1,
            maxCustomHeight = maxHeight,
            onMediaTypeDescriptionAction = { descriptionContentType, contentLink ->

            }
        )
    }
}

@Composable
private fun OptionCard(
    modifier: Modifier = Modifier,
    buttonTitle: String,
    index: Int,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(6.dp))
        .background(if (selectedIndex == index) blueDark else languageItemActiveBg)
        .clickable {
            onOptionSelected(index)
        }
        .padding(horizontal = 10.dp)
        .then(modifier)) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopStart,
        ) {
            Row(
                Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HtmlText(
                    text = buttonTitle,
                    style = TextStyle(
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    ),
                    color = if (selectedIndex == index) white else textColorDark
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
fun OptionCardPreview() {
    OptionCard(modifier = Modifier, "Option", index = 0, onOptionSelected = {}, selectedIndex = 0)
}


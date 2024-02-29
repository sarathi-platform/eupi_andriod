package com.nrlm.baselinesurvey.ui.common_components


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.model.datamodel.OptionsItem
import com.nrlm.baselinesurvey.ui.question_screen.presentation.QuestionEntityState
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.defaultCardElevation
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.languageItemActiveBg
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
    showQuestionState: QuestionEntityState = QuestionEntityState.getEmptyStateObject(),
    optionItemEntityList: List<OptionItemEntity>?,
    questionIndex: Int,
    selectedOptionIndex: Int = -1,
    maxCustomHeight: Dp,
    onAnswerSelection: (questionIndex: Int, optionItem: OptionItemEntity) -> Unit,
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

    val selectedIndex = remember { mutableIntStateOf(selectedOptionIndex) }

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
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(dimen_16_dp)
                                )
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
                                        optionItemEntityList ?: listOf()
                                    ) { _index: Int, optionsItem: OptionItemEntity ->
                                        OptionCard(
                                            optionItem = optionsItem,
                                            index = _index,
                                            selectedIndex = selectedIndex.value,
                                        ) {
                                            selectedIndex.value = it
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
                            /*Divider(
                                thickness = dimen_1_dp,
                                color = lightGray2,
                                modifier = Modifier.fillMaxWidth()
                            )
                            ExpandableDescriptionContentComponent(
                                questionDetailExpanded,
                                questionIndex,
                                question,
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
                            )*/
                            }
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
        questionImageUrl = "Section1_2wheeler.webp",
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
    val optionItemEntity = listOf(option1, option2)
    BoxWithConstraints {

        ListTypeQuestion(
            modifier = Modifier.padding(10.dp),
            question = question,
            questionIndex = 1,
            maxCustomHeight = maxHeight,
            optionItemEntityList = optionItemEntity,
            onAnswerSelection = { questionIndex, optionItem ->
            },
            onMediaTypeDescriptionAction = { descriptionContentType, contentLink ->

            }
        ) {}
    }
}

@Composable
public fun OptionCard(
    modifier: Modifier = Modifier,
    optionItem: OptionItemEntity,
    index: Int,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(if (optionItem.optionId == selectedIndex) blueDark else languageItemActiveBg)
            .clickable {
                onOptionSelected(optionItem.optionId ?: -1)
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
                    text = optionItem.display ?: BLANK_STRING,
                    style = TextStyle(
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    ),
                    color = if (optionItem.optionId == selectedIndex) white else textColorDark
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
    OptionCard(modifier = Modifier, option, index = 0, onOptionSelected = {}, selectedIndex = 1)
}


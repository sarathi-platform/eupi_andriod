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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nrlm.baselinesurvey.ui.common_components.EditTextWithTitleComponent
import com.nrlm.baselinesurvey.ui.common_components.ListTypeQuestion
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.entity.FormTypeOption
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.QuestionTypeEvent
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.TypeDropDownComponent
import com.nrlm.baselinesurvey.ui.question_type_screen.viewmodel.QuestionTypeScreenViewModel
import com.nrlm.baselinesurvey.ui.theme.defaultCardElevation
import com.nrlm.baselinesurvey.ui.theme.defaultTextStyle
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_18_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.DescriptionContentType
import com.patsurvey.nudge.customviews.htmltext.HtmlText
import kotlinx.coroutines.launch

@Composable
fun MiscQuestionBoxComponent(
    modifier: Modifier = Modifier,
    questionIndex: Int,
    question: QuestionEntity,
    optionItemEntityList: List<OptionItemEntity>?,
    selectedOptionIndex: Int = -1,
    maxCustomHeight: Dp,
    onAnswerSelection: (questionIndex: Int, optionId: Int, selectedValue: String) -> Unit,
    onMediaTypeDescriptionAction: (descriptionContentType: DescriptionContentType, contentLink: String) -> Unit,
    questionDetailExpanded: (index: Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    var selectedIndex by remember { mutableIntStateOf(selectedOptionIndex) }
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
                                    text = "${question.questionDisplay}",
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
                                    when (optionsItem.optionType) {
                                        QuestionType.Input.name -> {
                                            EditTextWithTitleComponent(
                                                optionsItem.display,
                                                optionsItem.selectedValue ?: ""
                                            ) {
                                                onAnswerSelection(
                                                    questionIndex,
                                                    optionsItem.optionId ?: 0,
                                                    it
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(dimen_8_dp))
                                        }

                                        QuestionType.SingleSelectDropdown.name -> {
                                            TypeDropDownComponent(
                                                optionsItem.display,
                                                optionsItem.selectedValue ?: "Select",
                                                optionsItem.values
                                            ) {
                                                onAnswerSelection(
                                                    questionIndex,
                                                    optionsItem.optionId ?: 0,
                                                    it
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(dimen_8_dp))
                                        }
                                    }
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
        }
    }
}

private fun storeGivenAnswered(
    questionTypeScreenViewModel: QuestionTypeScreenViewModel,
    formTypeOption: FormTypeOption,
    optionId: Int,
    selectedValue: String
) {
    questionTypeScreenViewModel.onEvent(
        QuestionTypeEvent.FormTypeQuestionAnswered(
            surveyId = formTypeOption.surveyId,
            sectionId = formTypeOption.sectionId,
            didiId = formTypeOption.didiId,
            questionId = formTypeOption.questionId,
            optionItemId = optionId,
            selectedValue = selectedValue
        )
    )
}

@Preview(showBackground = true)
@Composable
fun InputQuestionBoxComponentPreview() {
    val question = QuestionEntity(
        id = 2,
        questionId = 1,
        questionDisplay = "What is the <b>educational status </b> of adult members in the family?",
        questionSummary = "What is the <b>educational status </b> of adult members in the family?",
        order = 21,
        type =
        "Input",
        gotoQuestionId =
        22,
        questionImageUrl = "Section1_2wheeler.webp",
        surveyId = 1
    )

    val option1 = OptionItemEntity(
        optionId = 1,
        display = "Members",
        weight = 1,
        summary = "YES",
        optionValue = 1,
        optionImage = "",
        optionType = "Input",
        surveyId = 1,
        questionId = 1,
        id = 1
    )

    val option2 = OptionItemEntity(
        optionId = 2,
        display = "Members_Family",
        weight = 0,
        summary = "NO",
        optionValue = 0,
        optionImage = "",
        optionType = "Input",
        surveyId = 1,
        questionId = 1,
        id = 1
    )
    val option3 = OptionItemEntity(
        optionId = 2,
        display = "Members_Family_1",
        weight = 0,
        summary = "NO",
        optionValue = 0,
        optionImage = "",
        optionType = "Input",
        surveyId = 1,
        questionId = 1,
        id = 1
    )

    val optionItemEntity = listOf(option1, option2, option3)
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
package com.nrlm.baselinesurvey.ui.common_components

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
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
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.model.datamodel.OptionsItem
import com.nrlm.baselinesurvey.ui.theme.defaultCardElevation
import com.nrlm.baselinesurvey.ui.theme.defaultTextStyle
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_18_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_1_dp
import com.nrlm.baselinesurvey.ui.theme.lightGray2
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white
import com.patsurvey.nudge.customviews.htmltext.HtmlText
import kotlinx.coroutines.launch

@Composable
fun RadioQuestionBoxComponent(
    modifier: Modifier = Modifier,
    questionIndex: Int,
    question: QuestionEntity,
    selectedOptionIndex: Int = -1,
    maxCustomHeight: Dp,
    onAnswerSelection: (questionIndex: Int, optionItem: OptionsItem) -> Unit,
    questionDetailExpanded: (index: Int) -> Unit
) {

    val scope = rememberCoroutineScope()
    var selectedIndex by remember { mutableIntStateOf(selectedOptionIndex) }
    val outerState: LazyListState = rememberLazyListState()
    val innerState: LazyGridState = rememberLazyGridState()
    val innerFirstVisibleItemIndex by remember {
        derivedStateOf {
            innerState.firstVisibleItemIndex
        }
    }
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

                            Row(modifier = Modifier.padding(bottom = 10.dp).padding(horizontal = dimen_16_dp)) {
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
                            if (question.options?.isNotEmpty() == true) {
                                LazyVerticalGrid(
                                    userScrollEnabled = false,
                                    state = innerState,
                                    columns = GridCells.Fixed(2),
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .padding(horizontal = dimen_16_dp)
                                        .heightIn(min = 110.dp, max = maxCustomHeight)
                                ) {
                                    itemsIndexed(
                                        question.options ?: emptyList()
                                    ) { _index: Int, optionsItem: OptionsItem ->
                                        RadioButtonOptionComponent(
                                            index = _index,
                                            optionsItem = optionsItem,
                                            selectedIndex = selectedIndex
                                        ) {
                                            selectedIndex = _index
                                            onAnswerSelection(questionIndex, optionsItem)
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
                            InfoComponent(questionDetailExpanded, questionIndex, question)
                        }

                    }
                }
            }

        }


    }


}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun RadioQuestionBoxComponentPreview(
    modifier: Modifier = Modifier,

    ) {
    val question = QuestionEntity(
        id = 1,
        questionId = 1,
        questionDisplay = "Did everyone in your family have at least 2 meals per day in the last 1 month?",
        questionSummary = "Please check if the family is getting ration through the public distribution system (PDS) of the government or not? \n\nPlease check the granary/ where they store their grain and also check with neighbors also to understand the food security of the family",
        order = 1,
        type = "RadioButton",
        gotoQuestionId = 2,
        options = listOf(
            OptionsItem(
                optionId = 1,
                display = "YES",
                weight = 1,
                summary = "YES",
                optionValue = 1,
                optionImage = R.drawable.icon_check,
                optionType = ""
            ),
            OptionsItem(
                optionId = 2,
                display = "NO",
                weight = 0,
                summary = "NO",
                optionValue = 0,
                optionImage = R.drawable.icon_close,
                optionType = ""
            )
        ),
        questionImageUrl = "Section1_GovtService.webp",
        surveyId = 1
    )
    Surface {
        BoxWithConstraints(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
            RadioQuestionBoxComponent(questionIndex = 0, question = question, maxCustomHeight = maxHeight, onAnswerSelection = {
                questionIndex, optionItem ->
            }) {

            }
        }
    }
}
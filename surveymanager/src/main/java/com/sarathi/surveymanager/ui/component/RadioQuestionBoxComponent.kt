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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nudge.core.showCustomToast
import com.nudge.core.ui.theme.defaultCardElevation
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_18_dp
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.data.entities.OptionItemEntity
import com.sarathi.dataloadingmangement.data.entities.QuestionEntity
import com.sarathi.dataloadingmangement.util.constants.QuestionType
import com.sarathi.surveymanager.R
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@Composable
fun RadioQuestionBoxComponent(
    modifier: Modifier = Modifier,
    questionIndex: Int,
    optionItemEntityList: List<OptionItemEntity>?,
    selectedOptionIndex: Int = -1,
    maxCustomHeight: Dp,
    isEditAllowed: Boolean = true,
    onAnswerSelection: (questionIndex: Int, optionItem: OptionItemEntity) -> Unit,
) {

    val scope = rememberCoroutineScope()
    var selectedIndex by remember { mutableIntStateOf(selectedOptionIndex) }
    val outerState: LazyListState = rememberLazyListState()
    val innerState: LazyGridState = rememberLazyGridState()
    SideEffect {
        if (outerState.layoutInfo.visibleItemsInfo.size == 2 && innerState.layoutInfo.totalItemsCount == 0)
            scope.launch { outerState.scrollToItem(outerState.layoutInfo.totalItemsCount) }
        println("outer ${outerState.layoutInfo.visibleItemsInfo.map { it.index }}")
        println("inner ${innerState.layoutInfo.visibleItemsInfo.map { it.index }}")
    }
    val optionDetailVisibilityState = remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

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

                                Row(modifier = Modifier
                                    .padding(bottom = 10.dp)
                                    .padding(horizontal = dimen_16_dp)) {
                                    Text(
                                        text = "${questionIndex + 1}. ", style = defaultTextStyle,
                                        color = textColorDark
                                    )
//                                    HtmlText(
//                                        text = "",
//                                        style = defaultTextStyle,
//                                        color = textColorDark,
//                                        overflow = TextOverflow.Ellipsis,
//                                        softWrap = true
//                                    )
                                }
                            }
                            item {
                                if (optionItemEntityList?.isNotEmpty() == true) {
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
                                            optionItemEntityList ?: emptyList()
                                        ) { _index: Int, optionsItem: OptionItemEntity ->
                                            if (optionsItem.optionType.equals(QuestionType.RadioButton.name)) {
                                                RadioButtonOptionComponent(
                                                    index = _index,
                                                    optionsItem = optionsItem,
                                                    selectedIndex = selectedIndex
                                                ) {
                                                    if (isEditAllowed) {
                                                        selectedIndex = _index
                                                        onAnswerSelection(
                                                            questionIndex,
                                                            optionsItem
                                                        )
                                                    } else {
                                                        showCustomToast(
                                                            context,
                                                            context.getString(R.string.edit_disable_message)
                                                        )
                                                    }
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

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun RadioQuestionBoxComponentPreview(
    modifier: Modifier = Modifier,

    ) {
    val option1 = OptionItemEntity(
        optionId = 1,
//        display = "YES",
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
//        display = "NO",
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
}
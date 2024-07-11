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
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nudge.core.DEFAULT_LANGUAGE_ID
import com.nudge.core.showCustomToast
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
import com.nudge.core.ui.theme.weight_60_percent
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.data.entities.QuestionEntity
import com.sarathi.surveymanager.R
import kotlinx.coroutines.launch

@Composable
fun FormWithNoneTypeQuestionComponent(
    modifier: Modifier = Modifier,
    questionIndex: Int,
    maxCustomHeight: Dp,
    itemCount: Int = 0,
    summaryValue: String = "",
    isEditAllowed: Boolean = true,
//    onAnswerSelection: (questionIndex: Int, isNoneMarkedForForm: Boolean, isFormOpened: Boolean) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val outerState: LazyListState = rememberLazyListState()
    val innerState: LazyListState = rememberLazyListState()
    val innerGridState: LazyGridState = rememberLazyGridState()
//    val no = stringResource(id = R.string.option_no)



    val isNoneMarked = remember {
        mutableStateOf(true)
    }

    val isNoneQuestionAnswered = remember {
        mutableStateOf(false)
    }


    val context = LocalContext.current


    SideEffect {
        if (outerState.layoutInfo.visibleItemsInfo.size == 2 && innerState.layoutInfo.totalItemsCount == 0 && innerGridState.layoutInfo.totalItemsCount == 0)
            scope.launch { outerState.scrollToItem(outerState.layoutInfo.totalItemsCount) }
    }

    val density = LocalDensity.current

    val rootHeight = remember {
        mutableStateOf(0.dp)
    }

    BoxWithConstraints(
        modifier = modifier
            .scrollable(
                state = outerState,
                Orientation.Vertical,
            )
            .heightIn(min = rootHeight.value, maxCustomHeight)
    ) {
        Column {

                BoxWithConstraints(
                    modifier = modifier
                        .scrollable(
                            state = outerState,
                            Orientation.Vertical,
                        )
                        .heightIn(min = 100.dp, maxCustomHeight)
                        .onGloballyPositioned {
                            with(density) {
                                rootHeight.value = rootHeight.value + it.size.height.toDp()
                            }
                        }
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
                                                text = "${questionIndex + 1}. ",
                                                style = defaultTextStyle,
                                                color = textColorDark
                                            )
//                                            HtmlText(
//                                                text = "${showQuestionState.optionItemEntityState.find { it.optionItemEntity?.optionType == QuestionType.FormWithNone.name }?.optionItemEntity?.display}",
//                                                style = defaultTextStyle,
//                                                color = textColorDark,
//                                                overflow = TextOverflow.Ellipsis,
//                                                softWrap = true
//                                            )
                                        }
                                    }
                                    item {
                                        LazyVerticalGrid(
                                            userScrollEnabled = false,
                                            state = innerGridState,
                                            columns = GridCells.Fixed(2),
                                            modifier = Modifier
                                                .wrapContentWidth()
                                                .padding(horizontal = dimen_16_dp)
                                                .heightIn(min = 110.dp, max = maxCustomHeight)
                                        ) {


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

            Column {

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimen_8_dp)
                    )

                    BoxWithConstraints(
                        modifier = modifier
                            .scrollable(
                                state = outerState,
                                Orientation.Vertical,
                            )
                            .heightIn(min = 100.dp, maxCustomHeight)
                            .onGloballyPositioned {
                                with(density) {
                                    rootHeight.value = rootHeight.value + it.size.height.toDp()
                                }
                            }
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
                                                    text = "${questionIndex + 1}.1. ",
                                                    style = defaultTextStyle,
                                                    color = textColorDark
                                                )
//                                                HtmlText(
//                                                    text = "${question?.questionDisplay}",
//                                                    style = defaultTextStyle,
//                                                    color = textColorDark,
//                                                    overflow = TextOverflow.Ellipsis,
//                                                    softWrap = true
//                                                )
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
                                                    tittle ="",
                                                    isActive = isEditAllowed,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .weight(weight_60_percent)
                                                ) {
                                                    if (isEditAllowed) {
//
                                                    } else {
                                                        showCustomToast(
                                                            context,
                                                            context.getString(R.string.edit_disable_message)
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
                                            if (!summaryValue.equals("")
                                                )
                                            {
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

        }
    }
}
@Preview(showBackground = true)
@Composable
fun FormWithNoneTypeQuestionComponentPreview() {
   FormWithNoneTypeQuestionComponent(
       questionIndex =1 ,
       maxCustomHeight =100.dp ,
   )
}

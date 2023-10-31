package com.nrlm.baselinesurvey.ui.question_screen.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.NO_SECTION
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.TYPE_GRID
import com.nrlm.baselinesurvey.TYPE_LIST
import com.nrlm.baselinesurvey.TYPE_RADIO_BUTTON
import com.nrlm.baselinesurvey.model.datamodel.QuestionEntity
import com.nrlm.baselinesurvey.navigation.home.HomeScreens
import com.nrlm.baselinesurvey.ui.common_components.GridTypeComponent
import com.nrlm.baselinesurvey.ui.common_components.ListTypeQuestion
import com.nrlm.baselinesurvey.ui.common_components.LoaderComponent
import com.nrlm.baselinesurvey.ui.common_components.RadioQuestionBoxComponent
import com.nrlm.baselinesurvey.ui.common_components.SearchWithFilterViewComponent
import com.nrlm.baselinesurvey.ui.question_screen.viewmodel.QuestionScreenViewModel
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_18_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_24_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.greyBorder
import com.nrlm.baselinesurvey.ui.theme.progressIndicatorColor
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.smallTextStyle
import com.nrlm.baselinesurvey.ui.theme.smallerTextStyle
import com.nrlm.baselinesurvey.ui.theme.smallerTextStyleNormalWeight
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.trackColor
import com.nrlm.baselinesurvey.ui.theme.white
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun QuestionScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: QuestionScreenViewModel,
    sectionId: Int
) {

    val sectionDetails = viewModel.sectionDetail.value
    val loaderState = viewModel.loaderState.value


    LaunchedEffect(key1 = true) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.init(sectionId)
        delay(100)
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(false))
    }

    val scaffoldState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, skipHalfExpanded = false)
    val scope = rememberCoroutineScope()

    val listState = rememberLazyListState()

    Surface(color = white) {

        LoaderComponent(visible = loaderState.isLoaderVisible)

        if (!loaderState.isLoaderVisible) {

            ModalBottomSheetLayout(
                sheetContent = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                        /*.height(((2 * screenHeight) / 3).dp)*/
                    ) {

                        Column {
//                        BaselineLogger.d("ProgressScreen","BottomSheet : $villages :: size ${villages.size}")
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = dimen_10_dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.info_icon),
                                    contentDescription = "info icon"
                                )
                            }
                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp), color = greyBorder
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = dimen_16_dp, horizontal = dimen_18_dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = sectionDetails.sectionDetails,
                                    color = textColorDark,
                                    style = smallerTextStyleNormalWeight,
                                    modifier = Modifier.padding(horizontal = dimen_10_dp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = dimen_16_dp, horizontal = dimen_24_dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            scaffoldState.hide()
                                        }
                                    }, shape = RoundedCornerShape(
                                        roundedCornerRadiusDefault
                                    ), colors = ButtonDefaults.buttonColors(
                                        backgroundColor = blueDark,
                                        contentColor = white
                                    )
                                ) {
                                    Text(text = "Ok", color = white, style = smallerTextStyle)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                },
                sheetState = scaffoldState,
                sheetElevation = 20.dp,
                sheetBackgroundColor = Color.White,
                sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
            ) {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(dimen_8_dp),
                    modifier = Modifier.padding(horizontal = dimen_16_dp, vertical = dimen_16_dp),
                    state = listState

                ) {
                    item {
                        SearchWithFilterViewComponent(
                            placeholderString = "Search Question",
                            showFilter = false,
                            onFilterSelected = {

                            },
                            onSearchValueChange = {

                            }
                        )
                    }

                    item {
                        TopAppBar(
                            title = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    /*if (sectionDetails.sectionIcon != null) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.sample_step_icon_2),
                                            contentDescription = "section icon",
                                            tint = textColorDark
                                        )
                                        Spacer(modifier = Modifier.width(dimen_4_dp))
                                    }*/
                                    if (!sectionDetails.sectionName.equals(NO_SECTION, true))
                                        Text(
                                            text = sectionDetails.sectionName,
                                            color = textColorDark
                                        )
                                }
                            },
                            navigationIcon = {

                                Icon(
                                    Icons.Filled.ArrowBack,
                                    null,
                                    tint = textColorDark,
                                    modifier = Modifier.clickable {
                                        if (!sectionDetails.sectionName.equals(NO_SECTION, true))
                                            navController.popBackStack()
                                        else
                                            navController.popBackStack(
                                                HomeScreens.SURVEYEE_LIST_SCREEN.route,
                                                false
                                            )
                                    })

                            },
                            actions = {

                                Icon(
                                    painterResource(id = R.drawable.info_icon),
                                    null,
                                    tint = textColorDark,
                                    modifier = Modifier.clickable {
                                        scope.launch {
                                            if (!scaffoldState.isVisible) {
                                                scaffoldState.show()
                                            } else {
                                                scaffoldState.hide()
                                            }
                                        }
                                    }
                                )
                            },
                            backgroundColor = white,
                            elevation = 0.dp,

                            )
                    }

                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(dimen_8_dp)
                                    .padding(top = 1.dp)
                                    .clip(RoundedCornerShape(14.dp)),
                                color = progressIndicatorColor,
                                trackColor = trackColor,
                                progress = 0.2f
                            )
                            Spacer(modifier = Modifier.width(dimen_8_dp))
                            Text(
                                text = "2/4",
                                color = textColorDark,
                                style = smallTextStyle
                            )
                        }

                    }

                    itemsIndexed(
                        items = sectionDetails.questionList ?: emptyList()
                    ) { index, question ->

                        CreateQuestions(question, index, scope, listState)
                    }
                    item {
                        Spacer(modifier = Modifier.width(dimen_24_dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateQuestions(
    question: QuestionEntity?,
    index: Int,
    scope: CoroutineScope,
    listState: LazyListState
) {
    when (question?.type) {
        TYPE_RADIO_BUTTON -> {
            RadioQuestionBoxComponent(index = index, question = question) {
                scope.launch {
                    listState.animateScrollToItem(it + 3, -10)
                }
            }
        }

        TYPE_LIST -> {
            ListTypeQuestion(
                question = question,
                onAnswerSelection = {},
                questionDetailExpanded = {},
                index = index
            )
        }

        TYPE_GRID -> {
            GridTypeComponent(
                question = question,
                index = index,
                onAnswerSelection = {},
                questionDetailExpanded = {}
            )
        }

        else -> {}
    }
}
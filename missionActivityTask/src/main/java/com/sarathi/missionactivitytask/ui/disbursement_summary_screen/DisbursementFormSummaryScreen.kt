package com.sarathi.missionactivitytask.ui.disbursement_summary_screen

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.getFileNameFromURL
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.borderGrey
import com.nudge.core.ui.theme.brownDark
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_20_dp
import com.nudge.core.ui.theme.dimen_24_dp
import com.nudge.core.ui.theme.dimen_2_dp
import com.nudge.core.ui.theme.dimen_3_dp
import com.nudge.core.ui.theme.dimen_4_dp
import com.nudge.core.ui.theme.dimen_56_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.lightBg
import com.nudge.core.ui.theme.quesOptionTextStyle
import com.nudge.core.ui.theme.smallTextStyle
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.model.uiModel.DisbursementFormSummaryUiModel
import com.sarathi.missionactivitytask.navigation.navigateToAddImageScreen
import com.sarathi.missionactivitytask.navigation.navigateToPdfViewerScreen
import com.sarathi.missionactivitytask.ui.components.CircularImageViewComponent
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.missionactivitytask.ui.disbursement_summary_screen.viewmodel.DisbursementFormSummaryScreenViewModel
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
import com.sarathi.surveymanager.ui.component.ButtonPositive
import kotlinx.coroutines.launch


@Composable
fun DisbursementFormSummaryScreen(
    navController: NavController = rememberNavController(),
    viewModel: DisbursementFormSummaryScreenViewModel
) {
    val outerState = rememberLazyListState()
    val innerState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val innerFirstVisibleItemIndex by remember {
        derivedStateOf {
            innerState.firstVisibleItemIndex
        }
    }
    LaunchedEffect(key1 = true) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.onEvent(InitDataEvent.InitDataState)
    }

    ToolBarWithMenuComponent(
        title = "Disbursement summary",
        modifier = Modifier,
        onBackIconClick = { navController.popBackStack() },
        onSearchValueChange = {},
        onBottomUI = {
            BottomAppBar(backgroundColor = white, modifier = Modifier.height(150.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimen_10_dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = dimen_10_dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1.0f)
                                .height(dimen_56_dp)
                                .clickable {
                                    viewModel.generateFormE(false, {})

                                }
                                .border(width = dimen_1_dp, color = borderGrey)
                                .background(white),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = com.sarathi.missionactivitytask.R.drawable.ic_share_icon),
                                contentDescription = "Negative Button",
                                modifier = Modifier.padding(horizontal = dimen_2_dp),
                                colorFilter = ColorFilter.tint(blueDark)
                            )
                            Text(
                                stringResource(com.sarathi.missionactivitytask.R.string.share),
                                style = defaultTextStyle
                            )

                    }
                    Row(
                        modifier = Modifier
                            .padding(start = dimen_10_dp)
                            .weight(1.0f)
                            .clickable {
                                viewModel.generateFormE(true, { filepath ->
                                    navigateToPdfViewerScreen(
                                        navController = navController,
                                        filePath = getFileNameFromURL(filepath)
                                    )
                                })

                                }
                                .height(dimen_56_dp)
                                .border(width = dimen_1_dp, color = borderGrey)
                                .background(white),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center

                        ) {
                            Image(
                                painter = painterResource(id = com.sarathi.missionactivitytask.R.drawable.ic_download_icon),
                                contentDescription = "Negative Button",
                                modifier = Modifier.padding(horizontal = dimen_2_dp),
                                colorFilter = ColorFilter.tint(blueDark)
                            )
                            Text(
                                stringResource(com.sarathi.missionactivitytask.R.string.download),
                                style = defaultTextStyle
                            )

                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        ButtonPositive(
                            buttonTitle = "Attach physical form E",
                            isActive = true,
                            isArrowRequired = false,
                            onClick = {
                                navigateToAddImageScreen(navController = navController)
                            }
                        )
                    }
                }
            }


        },
        onSettingClick = {},
        onContentUI = { a, b, c ->
            BoxWithConstraints(
                modifier = Modifier
                    .scrollable(
                        state = rememberScrollableState {
                            scope.launch {
                                val toDown = it <= 0
                                if (toDown) {
                                    if (outerState.run { firstVisibleItemIndex == layoutInfo.totalItemsCount - 1 }) {
                                        innerState.scrollBy(-it)
                                    } else {
                                        outerState.scrollBy(-it)
                                    }
                                } else {
                                    if (innerFirstVisibleItemIndex == 0 && innerState.firstVisibleItemScrollOffset == 0) {
                                        outerState.scrollBy(-it)
                                    } else {
                                        innerState.scrollBy(-it)
                                    }
                                }
                            }
                            it
                        },
                        Orientation.Vertical,
                    )
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = dimen_16_dp),
                    verticalArrangement = Arrangement.spacedBy(dimen_10_dp)
                ) {
                    viewModel.formList.value.forEach {
                        item {
                            FormMainSummaryCard(
                                maxCustomHeight = maxHeight,
                                formDisburesmentMap = it,
                                viewmodel = viewModel
                            )
                        }
                    }


                }
            }
        }
    )

}

@Composable
private fun MakeDisburesementRow(
    disbursementFormSummaryUiModel: DisbursementFormSummaryUiModel,
    imageUri: Uri?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(white)
            .padding(dimen_10_dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimen_4_dp),
            horizontalArrangement = Arrangement.spacedBy(dimen_10_dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularImageViewComponent(modifier = Modifier, imageUri ?: Uri.EMPTY)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = disbursementFormSummaryUiModel.subjectName,
                    style = defaultTextStyle,
                    color = brownDark
                )
                Text(
                    text = disbursementFormSummaryUiModel.villageName,
                    style = smallTextStyle,
                    color = brownDark
                )
            }

        }
        Column(modifier = Modifier.padding(start = dimen_10_dp, end = dimen_10_dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimen_6_dp)
            ) {
                Row(modifier = Modifier.weight(1.0f)) {
                    Text("Mode:", style = quesOptionTextStyle)
                    Text(
                        modifier = Modifier.padding(horizontal = dimen_3_dp),
                        text = disbursementFormSummaryUiModel.mode,
                        style = smallTextStyle
                    )
                }
                Spacer(modifier = Modifier.width(dimen_20_dp))
                Row(modifier = Modifier.weight(1.0f)) {
                    Text("Nature:", style = quesOptionTextStyle)
                    Text(
                        modifier = Modifier.padding(horizontal = dimen_3_dp),
                        text = disbursementFormSummaryUiModel.nature,
                        style = smallTextStyle
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Amount:", style = quesOptionTextStyle)
                Text(
                    modifier = Modifier.padding(horizontal = dimen_3_dp),
                    text = disbursementFormSummaryUiModel.amount,
                    style = smallTextStyle
                )
            }
        }

    }

}

@SuppressLint("RememberReturnType")
@Composable
fun FormMainSummaryCard(
    modifier: Modifier = Modifier,
    outerState: LazyListState = rememberLazyListState(),
    innerState: LazyListState = rememberLazyListState(),
    maxCustomHeight: Dp,
    viewmodel: DisbursementFormSummaryScreenViewModel?,
    formDisburesmentMap: Map.Entry<Pair<String, String>, List<DisbursementFormSummaryUiModel>>,
) {


    Card(
        colors = CardDefaults.cardColors(
            containerColor = white
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimen_10_dp
        ),
        modifier = Modifier.padding(dimen_5_dp)
    ) {

        Column(modifier = Modifier.background(lightBg)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimen_24_dp)
                    .padding(top = dimen_8_dp), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    formDisburesmentMap.key.second,
                    style = defaultTextStyle.copy(fontSize = 16.sp)
                )
                Text(
                    formDisburesmentMap.key.first, style = defaultTextStyle
                )

            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimen_24_dp)
                    .padding(top = dimen_8_dp), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    "CSG Disburesed", style = quesOptionTextStyle
                )
                Text(
                    " ${formDisburesmentMap.value.sumOf { it.amount.toInt() }}",
                    style = quesOptionTextStyle
                )


            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimen_24_dp)
                    .padding(top = dimen_8_dp), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Number of didis", style = quesOptionTextStyle
                )
                Text(
                    "${formDisburesmentMap.value.distinctBy { it.subjectId }.size}",
                    style = quesOptionTextStyle
                )


            }


            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimen_8_dp)
            )


            HistorySummaryCard(
                modifier = Modifier,
                outerState = outerState,
                innerState = innerState,
                maxCustomHeight = maxCustomHeight,
                disburesmentList = formDisburesmentMap.value,
                viewmodel = viewmodel
            )
        }

    }

}

@Composable
fun HistorySummaryCard(
    modifier: Modifier,
    outerState: LazyListState = rememberLazyListState(),
    innerState: LazyListState = rememberLazyListState(),
    maxCustomHeight: Dp,
    disburesmentList: List<DisbursementFormSummaryUiModel>,
    viewmodel: DisbursementFormSummaryScreenViewModel?,
) {


    BoxWithConstraints(
        modifier = modifier
            .scrollable(
                state = outerState,
                Orientation.Vertical,
            )
            .heightIn(min = 0.dp, maxCustomHeight)
    ) {
        Column(modifier = Modifier.background(white)) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                LazyColumn {
                    item {
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth(),
                            thickness = dimen_1_dp,
                            color = blueDark
                        )
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(dimen_8_dp)
                        )
                    }
                    itemsIndexed(
                        disburesmentList
                    ) { index, disburesement ->
                        FormSummaryCardItem(
                            modifier = modifier,
                            disburesementtoryState = disburesement,
                            viewmodel?.getFilePathUri(disburesement.didiImage)
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun FormSummaryCardItem(
    modifier: Modifier,
    disburesementtoryState: DisbursementFormSummaryUiModel,
    imageUri: Uri?
) {
    MakeDisburesementRow(disbursementFormSummaryUiModel = disburesementtoryState, imageUri)

}
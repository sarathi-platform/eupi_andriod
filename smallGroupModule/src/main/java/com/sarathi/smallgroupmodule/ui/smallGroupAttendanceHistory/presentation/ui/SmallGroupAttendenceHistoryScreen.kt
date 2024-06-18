package com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nudge.core.ui.events.CommonEvents
import com.nudge.core.ui.events.DialogEvents
import com.nudge.core.ui.theme.blueDark
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.data.entities.getSubtitle
import com.sarathi.missionactivitytask.ui.components.BasicCardView
import com.sarathi.missionactivitytask.ui.components.ButtonPositiveComponent
import com.sarathi.missionactivitytask.ui.components.ContentWithImage
import com.sarathi.missionactivitytask.ui.components.CustomDateRangePickerBottomSheetComponent
import com.sarathi.missionactivitytask.ui.components.CustomVerticalSpacer
import com.sarathi.missionactivitytask.ui.components.IconProperties
import com.sarathi.missionactivitytask.ui.components.ImageProperties
import com.sarathi.missionactivitytask.ui.components.SheetHeight
import com.sarathi.missionactivitytask.ui.components.TextProperties
import com.sarathi.missionactivitytask.ui.components.TextWithIconComponent
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.missionactivitytask.ui.components.rememberCustomDateRangePickerSheetState
import com.sarathi.missionactivitytask.ui.components.rememberDateRangePickerBottomSheetProperties
import com.sarathi.missionactivitytask.ui.components.rememberDateRangePickerProperties
import com.sarathi.smallgroupmodule.R
import com.sarathi.smallgroupmodule.data.model.SubjectAttendanceHistoryState
import com.sarathi.smallgroupmodule.navigation.SMALL_GROUP_ATTENDANCE_SCREEN_ROUTE
import com.sarathi.smallgroupmodule.navigation.navigateToAttendanceEditScreen
import com.sarathi.smallgroupmodule.ui.commonUi.CustomDialogComponent
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.presentation.event.SmallGroupAttendanceEvent
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.viewModel.SmallGroupAttendanceHistoryViewModel
import com.sarathi.smallgroupmodule.ui.theme.dateRangeFieldColor
import com.sarathi.smallgroupmodule.ui.theme.defaultTextStyle
import com.sarathi.smallgroupmodule.ui.theme.dimen_10_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_16_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_1_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_24_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_2_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_48_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_56_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_80_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_8_dp
import com.sarathi.smallgroupmodule.ui.theme.green
import com.sarathi.smallgroupmodule.ui.theme.mediumBoldTextStyle
import com.sarathi.smallgroupmodule.ui.theme.mediumTextStyle
import com.sarathi.smallgroupmodule.ui.theme.otpBorderColor
import com.sarathi.smallgroupmodule.ui.theme.redOffline
import com.sarathi.smallgroupmodule.ui.theme.searchFieldBg
import com.sarathi.smallgroupmodule.ui.theme.smallTextStyleMediumWeight
import com.sarathi.smallgroupmodule.ui.theme.textColorDark
import com.sarathi.smallgroupmodule.ui.theme.textColorDark80
import com.sarathi.smallgroupmodule.ui.theme.uncheckedTrackColor
import com.sarathi.smallgroupmodule.ui.theme.white
import com.sarathi.smallgroupmodule.utils.getAttendanceFromBoolean
import com.sarathi.smallgroupmodule.utils.getDate
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SmallGroupAttendanceHistoryScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    smallGroupId: Int,
    smallGroupAttendanceHistoryViewModel: SmallGroupAttendanceHistoryViewModel
) {

    LaunchedEffect(key1 = Unit) {

        smallGroupAttendanceHistoryViewModel.onEvent(
            SmallGroupAttendanceEvent.LoadSmallGroupDetailsForSmallGroupIdEvent(
                smallGroupId
            )
        )

    }

    val sheetState = rememberCustomDateRangePickerSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    val sheetProperties = rememberDateRangePickerBottomSheetProperties(
        sheetState = sheetState,
        modifier = Modifier,
        sheetShape = RoundedCornerShape(topStart = dimen_10_dp, topEnd = dimen_10_dp),
        sheetBackgroundColor = searchFieldBg,
    )

    val dateRangePickerProperties = rememberDateRangePickerProperties()

    val scope = rememberCoroutineScope()

    val outerState = rememberLazyListState()
    val innerState = rememberLazyListState()
    val innerFirstVisibleItemIndex by remember {
        derivedStateOf {
            innerState.firstVisibleItemIndex
        }
    }

    if (smallGroupAttendanceHistoryViewModel.alertDialogState.value.isDialogVisible) {

        CustomDialogComponent(
            title = "Are you sure!",
            message = "You want to delete small group attendance?",
            positiveButtonTitle = stringResource(id = R.string.yes),
            negativeButtonTitle = stringResource(id = R.string.no),
            onPositiveButtonClick = {
                smallGroupAttendanceHistoryViewModel.onEvent(SmallGroupAttendanceEvent.DeleteAttendanceForDateEvent {

                    smallGroupAttendanceHistoryViewModel.onEvent(SmallGroupAttendanceEvent.TerminateDeleteForDateEvent)
                    smallGroupAttendanceHistoryViewModel.onEvent(DialogEvents.ShowDialogEvent(false))

                })
            },
            onNegativeButtonClick = {
                smallGroupAttendanceHistoryViewModel.onEvent(SmallGroupAttendanceEvent.TerminateDeleteForDateEvent)
                smallGroupAttendanceHistoryViewModel.onEvent(DialogEvents.ShowDialogEvent(false))
            }
        )

    }


    /**
     *Not required as no bottom UI present for this screen
     **/
    CustomDateRangePickerBottomSheetComponent(
        customDateRangePickerBottomSheetProperties = sheetProperties,
        dateRangePickerProperties = dateRangePickerProperties,
        sheetHeight = SheetHeight.CustomSheetHeight(dimen_56_dp),
        onSheetConfirmButtonClicked = {
            smallGroupAttendanceHistoryViewModel
                .onEvent(
                    CommonEvents.UpdateDateRange(
                        dateRangePickerProperties.state.selectedStartDateMillis,
                        dateRangePickerProperties.state.selectedEndDateMillis
                    )
                )
            smallGroupAttendanceHistoryViewModel
                .onEvent(
                    SmallGroupAttendanceEvent.LoadSmallGroupAttendanceHistoryOnDateRangeUpdateEvent
                )
            scope.launch {
                sheetState.hide()
            }
        }
    ) {
        ToolBarWithMenuComponent(
            title = smallGroupAttendanceHistoryViewModel.smallGroupDetails.value.smallGroupName,
            modifier = Modifier,
            onBackIconClick = { navController.popBackStack() },
            onSearchValueChange = {},
            isDataAvailable = true,
            onBottomUI = {
                BottomAppBar(
                    modifier = Modifier.height(dimen_80_dp),
                    containerColor = white
                ) {

                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(dimen_10_dp)
                    ) {
                        ButtonPositiveComponent(
                            buttonTitle = "Take Attendance",
                            isActive = true,
                            isArrowRequired = true,
                            onClick = {
                                navController.navigate("$SMALL_GROUP_ATTENDANCE_SCREEN_ROUTE/$smallGroupId")
                            }
                        )
                    }

                }
            },
            onSettingClick = {},
            onContentUI = { paddingValues, b, function ->

                if (smallGroupAttendanceHistoryViewModel.isAttendanceAvailable.value) {
                    BoxWithConstraints(
                        modifier = modifier
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
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = dimen_16_dp),
                                ) {

                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = dimen_8_dp)
                                    ) {

                                        OutlinedTextField(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(white)
                                                .weight(1f)
                                                .clickable {
                                                    scope.launch {
                                                        sheetState.show()
                                                    }
                                                },
                                            value = "${smallGroupAttendanceHistoryViewModel.dateRangeFilter.value.first.getDate()} - ${smallGroupAttendanceHistoryViewModel.dateRangeFilter.value.second.getDate()}",
                                            enabled = true,
                                            readOnly = true,
                                            textStyle = defaultTextStyle,
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = textColorDark,
                                                unfocusedBorderColor = dateRangeFieldColor,
                                                focusedBorderColor = dateRangeFieldColor,
                                                unfocusedContainerColor = white,
                                                focusedContainerColor = white,
                                            ),
                                            label = {
                                                Text(text = "From - To", color = otpBorderColor)
                                            },
                                            placeholder = {
                                                Text(text = "From - To", color = otpBorderColor)
                                            },
                                            trailingIcon = {
                                                IconButton(onClick = {
                                                    scope.launch {
                                                        sheetState.show()
                                                    }
                                                }) {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.calendar),
                                                        contentDescription = "Date Range"
                                                    )
                                                }

                                            },
                                            onValueChange = {}
                                        )
                                    }

                                }
                            }

                            item {
                                Text(
                                    text = "Attendance History: ",
                                    style = defaultTextStyle,
                                    color = textColorDark
                                )
                            }

                            smallGroupAttendanceHistoryViewModel.subjectAttendanceHistoryStateMappingByDate.value?.forEach {
                                item {
                                    AttendanceSummaryCard(
                                        maxCustomHeight = maxHeight,
                                        subjectAttendanceHistoryStateMappingByDate = it,
                                        onDeleteClicked = {
                                            smallGroupAttendanceHistoryViewModel.onEvent(
                                                SmallGroupAttendanceEvent.InitiateDeleteForDateEvent(
                                                    Pair(smallGroupId, it.key)
                                                )
                                            )
                                            smallGroupAttendanceHistoryViewModel.onEvent(
                                                DialogEvents.ShowDialogEvent(true)
                                            )
                                        },
                                        onEditClicked = {
                                            navController.navigateToAttendanceEditScreen(
                                                smallGroupId,
                                                it.key
                                            )
                                        }
                                    )
                                }
                            }

                            item {
                                CustomVerticalSpacer()
                            }

                        }
                    }

                } else {
                    EmptyHistoryView(smallGroupAttendanceHistoryViewModel = smallGroupAttendanceHistoryViewModel) {
                        navController.navigate("$SMALL_GROUP_ATTENDANCE_SCREEN_ROUTE/$smallGroupId")
                    }
                }

            }
        )

    }
}

@Composable
fun EmptyHistoryView(
    modifier: Modifier = Modifier,
    smallGroupAttendanceHistoryViewModel: SmallGroupAttendanceHistoryViewModel,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 75.dp)
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(dimen_10_dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.Center,
        ) {
            TextWithIconComponent(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                iconProperties = IconProperties(
                    painterResource(id = R.drawable.didi_icon),
                    contentDescription = "",
                    blueDark,
                ), textProperties = TextProperties(
                    text = "Total Didis - ${smallGroupAttendanceHistoryViewModel.smallGroupDetails.value.didiCount}",
                    color = blueDark,
                    style = defaultTextStyle
                )
            )
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            ButtonPositiveComponent(
                buttonTitle = "Take Attendance",
                isActive = true,
                isArrowRequired = true,
                onClick = {
                    onClick()
                }
            )
        }
    }
}


@Composable
fun AttendanceSummaryCard(
    modifier: Modifier = Modifier,
    outerState: LazyListState = rememberLazyListState(),
    innerState: LazyListState = rememberLazyListState(),
    maxCustomHeight: Dp,
    subjectAttendanceHistoryStateMappingByDate: Map.Entry<Long, List<SubjectAttendanceHistoryState>>,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    val totalSubjectsCount: MutableState<Int> =
        remember(subjectAttendanceHistoryStateMappingByDate.value.map { it.attendance }) {
            mutableStateOf(subjectAttendanceHistoryStateMappingByDate.value.size)
        }

    val counts: MutableState<Pair<Int, Int>> =
        remember(subjectAttendanceHistoryStateMappingByDate.value.map { it.attendance }) {
            mutableStateOf(
                Pair(
                    subjectAttendanceHistoryStateMappingByDate.value.filter { it.attendance }.size,
                    totalSubjectsCount.value
                )
            )
        }

    val attendancePercentage =
        remember(subjectAttendanceHistoryStateMappingByDate.value.map { it.attendance }) {
            derivedStateOf {
                ((counts.value.first.toFloat() / counts.value.second.toFloat()) * 100).roundToInt()
            }
        }

    val isExpanded = remember { mutableStateOf(false) }

    BasicCardView(
        colors = CardDefaults.cardColors(
            containerColor = white
        ),
        modifier = Modifier
    ) {

        Column(modifier = Modifier) {

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimen_24_dp)
                    .padding(top = dimen_8_dp), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = subjectAttendanceHistoryStateMappingByDate.key.getDate(),
                    color = textColorDark,
                    style = mediumBoldTextStyle
                )

                IconButton(onClick = {
                    isExpanded.value = !isExpanded.value
                }) {
                    Icon(
                        imageVector = if (isExpanded.value) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                        contentDescription = "Expand Button"
                    )
                }

            }
            Box(
                Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Attendance - ${attendancePercentage.value}%",
                    style = defaultTextStyle,
                    color = green,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(horizontal = dimen_24_dp)
                )
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimen_24_dp)
            ) {
                TextWithIconComponent(
                    modifier = modifier.align(Alignment.CenterStart),
                    iconProperties = IconProperties(
                        painterResource(id = R.drawable.didi_icon),
                        contentDescription = null,
                        tint = textColorDark
                    ), textProperties = TextProperties(
                        text = "Total - ${counts.value.first}/${counts.value.second}",
                        style = defaultTextStyle,
                        color = textColorDark
                    )
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
                subjectAttendanceHistoryStateList = subjectAttendanceHistoryStateMappingByDate.value,
                isExpanded = isExpanded.value
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimen_8_dp)
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth(),
                thickness = dimen_1_dp,
                color = uncheckedTrackColor
            )

            Row(
                Modifier
                    .fillMaxWidth()
                    .height(dimen_48_dp), horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                val interactionSource = remember { MutableInteractionSource() }

                TextButton(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimen_10_dp)
                    .weight(1f),
                    onClick = {
                        onEditClicked()
                    }) {
                    TextWithIconComponent(
                        iconProperties = IconProperties(
                            Icons.Outlined.Edit,
                            contentDescription = null,
                            modifier = Modifier
                        ),
                        textProperties = TextProperties(
                            text = "Edit",
                            style = defaultTextStyle,
                            color = textColorDark
                        )
                    )
                }

                Divider(
                    color = uncheckedTrackColor,
                    modifier = Modifier
                        .height(dimen_48_dp)
                        .width(1.dp)
                )
                TextButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimen_10_dp)
                        .weight(1f),
                    onClick = {
                        onDeleteClicked()
                    }) {

                    TextWithIconComponent(
                        modifier = Modifier,
                        iconProperties = IconProperties(
                            painterResource(id = R.drawable.ic_delete_icon),
                            contentDescription = null,
                            tint = redOffline,
                            modifier = Modifier.absolutePadding(top = dimen_2_dp)
                        ), textProperties = TextProperties(
                            text = "Delete",
                            style = defaultTextStyle,
                            color = redOffline
                        )
                    )
                }


            }

        }

    }

}

@Composable
fun HistorySummaryCard(
    modifier: Modifier,
    outerState: LazyListState = rememberLazyListState(),
    innerState: LazyListState = rememberLazyListState(),
    maxCustomHeight: Dp,
    subjectAttendanceHistoryStateList: List<SubjectAttendanceHistoryState>, isExpanded: Boolean
) {


    BoxWithConstraints(
        modifier = modifier
            .scrollable(
                state = outerState,
                Orientation.Vertical,
            )
            .heightIn(min = 0.dp, maxCustomHeight)
    ) {
        Column {
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                LazyColumn {
                    item {
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth(),
                            thickness = dimen_1_dp,
                            color = uncheckedTrackColor
                        )
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(dimen_8_dp)
                        )
                    }
                    itemsIndexed(
                        subjectAttendanceHistoryStateList
                    ) { index, subjectAttendanceHistoryState ->
                        HistorySummaryCardItem(
                            modifier = modifier,
                            subjectAttendanceHistoryState = subjectAttendanceHistoryState
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun HistorySummaryCardItem(
    modifier: Modifier,
    subjectAttendanceHistoryState: SubjectAttendanceHistoryState
) {

    ContentWithImage(
        modifier = modifier.padding(horizontal = dimen_10_dp),
        imageProperties = ImageProperties(
            path = BLANK_STRING,
            contentDescription = "Didi Image"
        )
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {

            Text(
                text = subjectAttendanceHistoryState.subjectEntity.subjectName,
                style = mediumTextStyle,
                color = com.sarathi.smallgroupmodule.ui.theme.blueDark
            )
            Text(
                text = subjectAttendanceHistoryState.subjectEntity.getSubtitle(),
                style = smallTextStyleMediumWeight,
                color = textColorDark80
            )

        }

        Text(
            text = subjectAttendanceHistoryState.attendance.getAttendanceFromBoolean(),
            style = defaultTextStyle,
            color = if (subjectAttendanceHistoryState.attendance) green else redOffline
        )
    }

}
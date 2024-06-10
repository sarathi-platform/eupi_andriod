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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nudge.core.ui.events.CommonEvents
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_14_dp
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.data.entities.getSubtitle
import com.sarathi.missionactivitytask.ui.components.BasicCardView
import com.sarathi.missionactivitytask.ui.components.ButtonPositiveComponent
import com.sarathi.missionactivitytask.ui.components.ContentWithImage
import com.sarathi.missionactivitytask.ui.components.CustomHorizontalSpacer
import com.sarathi.missionactivitytask.ui.components.CustomVerticalSpacer
import com.sarathi.missionactivitytask.ui.components.IconProperties
import com.sarathi.missionactivitytask.ui.components.ImageProperties
import com.sarathi.missionactivitytask.ui.components.TextProperties
import com.sarathi.missionactivitytask.ui.components.TextWithIconComponent
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.smallgroupmodule.R
import com.sarathi.smallgroupmodule.data.model.SubjectAttendanceHistoryState
import com.sarathi.smallgroupmodule.navigation.SMALL_GROUP_ATTENDANCE_SCREEN_ROUTE
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.presentation.event.SmallGroupAttendanceEvent
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.viewModel.SmallGroupAttendanceHistoryViewModel
import com.sarathi.smallgroupmodule.ui.theme.dateRangeFieldColor
import com.sarathi.smallgroupmodule.ui.theme.defaultTextStyle
import com.sarathi.smallgroupmodule.ui.theme.dimen_10_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_16_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_1_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_2_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_48_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_8_dp
import com.sarathi.smallgroupmodule.ui.theme.green
import com.sarathi.smallgroupmodule.ui.theme.mediumBoldTextStyle
import com.sarathi.smallgroupmodule.ui.theme.mediumTextStyle
import com.sarathi.smallgroupmodule.ui.theme.otpBorderColor
import com.sarathi.smallgroupmodule.ui.theme.redOffline
import com.sarathi.smallgroupmodule.ui.theme.searchFieldBg
import com.sarathi.smallgroupmodule.ui.theme.smallTextStyleMediumWeight
import com.sarathi.smallgroupmodule.ui.theme.smallTextStyleWithNormalWeight
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

    val state = rememberDateRangePickerState()

    val showRangePickerDialog = remember {
        mutableStateOf(false)
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
//    var showBottomSheet = remember { mutableStateOf(false) }

    val outerState = rememberLazyListState()
    val innerState = rememberLazyListState()
    val innerFirstVisibleItemIndex by remember {
        derivedStateOf {
            innerState.firstVisibleItemIndex
        }
    }

    if (showRangePickerDialog.value) {
        ModalBottomSheet(
            modifier = Modifier
                .height(600.dp)
                .background(white),
            containerColor = white,
            onDismissRequest = {
                showRangePickerDialog.value = false
            },
            sheetState = sheetState,
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(searchFieldBg)
            ) {
                DateRangePicker(
                    state = state, showModeToggle = false, colors = DatePickerDefaults.colors(
                        containerColor = searchFieldBg,
                        todayDateBorderColor = blueDark,
                        dayInSelectionRangeContainerColor = blueDark.copy(0.5f),
                        selectedDayContainerColor = blueDark,
                        selectedDayContentColor = white
                    ), dateValidator = { selectedDate ->
                        selectedDate < System.currentTimeMillis()
                    })

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .wrapContentWidth()
                        .padding(end = dimen_10_dp, bottom = dimen_10_dp)
                ) {
                    Button(
                        modifier = Modifier,
                        onClick = {
                            smallGroupAttendanceHistoryViewModel
                                .onEvent(
                                    CommonEvents.UpdateDateRange(
                                        state.selectedStartDateMillis,
                                        state.selectedEndDateMillis
                                    )
                                )
                            smallGroupAttendanceHistoryViewModel
                                .onEvent(
                                    SmallGroupAttendanceEvent.LoadSmallGroupAttendanceHistoryOnDateRangeUpdateEvent
                                )
                            showRangePickerDialog.value = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = blueDark
                        )
                    ) {
                        Text(text = "Ok", color = white, style = smallTextStyleWithNormalWeight)
                    }
                }

            }

        }
    }


    ToolBarWithMenuComponent(
        title = smallGroupAttendanceHistoryViewModel.smallGroupDetails.value.smallGroupName,
        modifier = Modifier,
        onBackIconClick = { navController.popBackStack() },
        onSearchValueChange = {},
        isDataAvailable = true,
        onBottomUI = { /*TODO*/ },
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
                            .padding(top = dimen_16_dp)
                            .padding(horizontal = dimen_16_dp),
                        verticalArrangement = Arrangement.spacedBy(dimen_10_dp)
                    ) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                            ) {
                                ButtonPositiveComponent(
                                    buttonTitle = "Take Attendance",
                                    isActive = true,
                                    isArrowRequired = true,
                                    onClick = {
                                        navController.navigate("$SMALL_GROUP_ATTENDANCE_SCREEN_ROUTE/$smallGroupId")
                                    }
                                )
                                CustomVerticalSpacer()

                                Row(
                                    Modifier.fillMaxWidth()
                                ) {

                                    OutlinedTextField(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(white)
                                            .weight(1f)
                                            .clickable {
                                                showRangePickerDialog.value = true
                                            },
                                        value = smallGroupAttendanceHistoryViewModel.dateRangeFilter.value.first.getDate(),
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
                                            Text(text = "From", color = otpBorderColor)
                                        },
                                        placeholder = {
                                            Text(text = "From", color = otpBorderColor)
                                        },
                                        trailingIcon = {
                                            IconButton(onClick = {
                                                showRangePickerDialog.value = true
                                            }) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.calendar),
                                                    contentDescription = "Start Date"
                                                )
                                            }

                                        },
                                        onValueChange = {}
                                    )

                                    CustomHorizontalSpacer()
//                            Spacer(modifier = Modifier.width(dimen_8_dp))

                                    OutlinedTextField(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(white)
                                            .weight(1f)
                                            .clickable {
                                                showRangePickerDialog.value = true
                                            },
                                        value = smallGroupAttendanceHistoryViewModel.dateRangeFilter.value.second.getDate(),
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
                                            Text(text = "To", color = otpBorderColor)
                                        },
                                        placeholder = {
                                            Text(text = "To", color = otpBorderColor)
                                        },
                                        trailingIcon = {
                                            IconButton(onClick = {
                                                showRangePickerDialog.value = true
                                            }) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.calendar),
                                                    contentDescription = "End Date"
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
                                    subjectAttendanceHistoryStateMappingByDate = it
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
    subjectAttendanceHistoryStateMappingByDate: Map.Entry<Long, List<SubjectAttendanceHistoryState>>
) {
    val totalSubjectsCount: MutableState<Int> = remember {
        mutableStateOf(subjectAttendanceHistoryStateMappingByDate.value.size)
    }

    val counts: MutableState<Pair<Int, Int>> = remember {
        mutableStateOf(
            Pair(
                subjectAttendanceHistoryStateMappingByDate.value.filter { it.attendance }.size,
                totalSubjectsCount.value
            )
        )
    }

    val attendancePercentage =
        derivedStateOf {
            ((counts.value.first.toFloat() / counts.value.second.toFloat()) * 100).roundToInt()
        }

    BasicCardView(
        colors = CardDefaults.cardColors(
            containerColor = white
        ),
        modifier = Modifier
            .padding(horizontal = dimen_10_dp)
    ) {

        Column(verticalArrangement = Arrangement.spacedBy(dimen_8_dp), modifier = Modifier) {

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimen_14_dp)
                    .padding(top = dimen_8_dp), horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = subjectAttendanceHistoryStateMappingByDate.key.getDate(),
                    color = textColorDark,
                    style = mediumBoldTextStyle
                )

                TextWithIconComponent(
                    iconProperties = IconProperties(
                        painterResource(id = R.drawable.didi_icon),
                        contentDescription = null,
                        tint = textColorDark
                    ), textProperties = TextProperties(
                        text = "${counts.value.first}/${counts.value.second}",
                        style = defaultTextStyle,
                        color = textColorDark
                    )
                )


            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimen_10_dp)
            ) {
                Text(
                    text = "Attendance - ${attendancePercentage.value}%",
                    style = defaultTextStyle,
                    color = green,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                )
            }

            CustomHorizontalSpacer()
            Divider(
                modifier = Modifier
                    .fillMaxWidth(),
                thickness = dimen_1_dp,
                color = uncheckedTrackColor
            )


            val isExpanded = remember { mutableStateOf(false) }


            DisplayItem(
                modifier = Modifier.padding(horizontal = dimen_10_dp),
                outerState = outerState,
                innerState = innerState,
                maxCustomHeight = maxCustomHeight,
                subjectAttendanceHistoryStateList = subjectAttendanceHistoryStateMappingByDate.value,
                isExpanded = isExpanded.value
            )


            CustomHorizontalSpacer()
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

                TextButton(modifier = Modifier.padding(horizontal = dimen_10_dp), onClick = {

                }) {
                    TextWithIconComponent(
                        iconProperties = IconProperties(
                            Icons.Outlined.Edit,
                            contentDescription = null,
                            modifier = Modifier.absolutePadding(top = dimen_2_dp)
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
                TextButton(modifier = Modifier.padding(horizontal = dimen_10_dp), onClick = {
                    isExpanded.value = !isExpanded.value
                }) {

                    TextWithIconComponent(
                        modifier = Modifier,
                        iconProperties = IconProperties(
                            if (isExpanded.value) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.absolutePadding(top = dimen_2_dp)
                        ), textProperties = TextProperties(
                            text = if (isExpanded.value) "See Less" else "See More",
                            style = defaultTextStyle,
                            color = textColorDark
                        )
                    )
                }


            }

        }

    }

}

@Composable
fun DisplayItem(
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
            .heightIn(min = 100.dp, maxCustomHeight)
    ) {
        LazyColumn {
            itemsIndexed(subjectAttendanceHistoryStateList.take(2)) { index, subjectAttendanceHistoryState ->
                ContentWithImage(
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
        }
        Spacer(modifier = Modifier.height(8.dp))
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            LazyColumn {
                itemsIndexed(subjectAttendanceHistoryStateList) { index, subjectAttendanceHistoryState ->
                    ContentWithImage(
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
            }
        }
    }

}

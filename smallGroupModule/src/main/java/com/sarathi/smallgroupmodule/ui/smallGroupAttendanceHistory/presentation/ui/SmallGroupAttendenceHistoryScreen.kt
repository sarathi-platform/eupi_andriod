package com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nudge.core.ui.events.theme.blueDark
import com.sarathi.missionactivitytask.ui.components.BasicCardView
import com.sarathi.missionactivitytask.ui.components.ButtonPositiveComponent
import com.sarathi.missionactivitytask.ui.components.IconProperties
import com.sarathi.missionactivitytask.ui.components.TextProperties
import com.sarathi.missionactivitytask.ui.components.TextWithIconComponent
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.smallgroupmodule.navigation.SMALL_GROUP_ATTENDANCE_SCREEN_ROUTE
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.presentation.event.SmallGroupAttendanceEvent
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.viewModel.SmallGroupAttendanceHistoryViewModel
import com.sarathi.smallgroupmodule.ui.theme.defaultTextStyle
import com.sarathi.smallgroupmodule.ui.theme.dimen_10_dp
import com.sarathi.smallgroupmodule.ui.theme.progressIndicatorColor
import com.sarathi.smallgroupmodule.ui.theme.smallTextStyleWithNormalWeight
import com.sarathi.smallgroupmodule.ui.theme.white
import com.sarathi.smallgroupmodule.utils.getDate

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

    if (showRangePickerDialog.value) {
        ModalBottomSheet(
            modifier = Modifier.background(white),
            containerColor = white,
            onDismissRequest = {
                showRangePickerDialog.value = false
            },
            sheetState = sheetState,
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .background(white)
            ) {
                DateRangePicker(
                    state = state, showModeToggle = false, colors = DatePickerDefaults.colors(
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
                        onClick = { showRangePickerDialog.value = false },
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
            /*Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 75.dp),
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
                            navController.navigate("$SMALL_GROUP_ATTENDANCE_SCREEN_ROUTE/$smallGroupId")
                        }
                    )
                }
            }*/

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 75.dp),
                verticalArrangement = Arrangement.spacedBy(dimen_10_dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .align(Alignment.CenterHorizontally),
                ) {
                    ButtonPositiveComponent(
                        buttonTitle = "Take Attendance",
                        isActive = true,
                        isArrowRequired = true,
                        onClick = {
                            navController.navigate("$SMALL_GROUP_ATTENDANCE_SCREEN_ROUTE/$smallGroupId")
                        }
                    )

                    Row(
                        Modifier.fillMaxWidth()
                    ) {

                        BasicCardView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(white)
                                .weight(1f)
                                .clickable {
                                    showRangePickerDialog.value = true
                                }
                        ) {
                            TextWithIconComponent(
                                modifier = Modifier
                                    .background(white)
                                    .fillMaxWidth()
                                    .padding(dimen_10_dp),
                                iconProperties = IconProperties(
                                    painterResource(id = com.sarathi.smallgroupmodule.R.drawable.calendar),
                                    contentDescription = "Date Selector",
                                ),
                                textProperties = TextProperties(
                                    text = state.selectedStartDateMillis.getDate(),
                                    color = progressIndicatorColor,
                                    style = defaultTextStyle
                                )
                            )
                        }

                        BasicCardView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(white)
                                .weight(1f)
                                .clickable {
                                    showRangePickerDialog.value = true
                                }
                        ) {
                            TextWithIconComponent(
                                modifier = Modifier
                                    .background(white)
                                    .fillMaxWidth()
                                    .padding(dimen_10_dp),
                                iconProperties = IconProperties(
                                    painterResource(id = com.sarathi.smallgroupmodule.R.drawable.calendar),
                                    contentDescription = "Date Selector",
                                ),
                                textProperties = TextProperties(
                                    text = state.selectedEndDateMillis.getDate(),
                                    color = progressIndicatorColor,
                                    style = defaultTextStyle
                                )
                            )
                        }

                    }

                }
            }
        }
    )

}
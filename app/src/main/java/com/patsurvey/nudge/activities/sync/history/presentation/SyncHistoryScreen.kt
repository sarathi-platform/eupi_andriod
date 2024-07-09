package com.patsurvey.nudge.activities.sync.history.presentation

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nrlm.baselinesurvey.ui.common_components.ToolbarWithMenuComponent
import com.nrlm.baselinesurvey.ui.theme.defaultTextStyle
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_14_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_32_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_56_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_65_dp
import com.nrlm.baselinesurvey.ui.theme.searchFieldBg
import com.nrlm.baselinesurvey.ui.theme.smallTextStyleWithNormalWeight
import com.nrlm.baselinesurvey.ui.theme.text_size_16_sp
import com.nudge.core.EventSyncStatus
import com.nudge.core.utils.CoreLogger
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.sync.history.viewmodel.SyncHistoryViewModel
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.dateRangeFieldColor
import com.patsurvey.nudge.activities.ui.theme.otpBorderColor
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.SYNC_DATA
import com.patsurvey.nudge.utils.changeMilliDateToDate
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SyncHistoryScreen(
    navController: NavController,
    syncType: String,
    viewModel: SyncHistoryViewModel
) {
    val state = rememberDateRangePickerState()
    val context= LocalContext.current
    val showRangePickerDialog = remember {
        mutableStateOf(false)
    }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val sheetState =
        androidx.compose.material.rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = viewModel.eventList) {

        val eventStatusUIList = arrayListOf<Pair<String, Int>>()
        val successConsumerCount =
            viewModel.eventList.value.filter { it.status == EventSyncStatus.CONSUMER_SUCCESS.eventSyncStatus }.size
        if (successConsumerCount > 1) {
            eventStatusUIList.add(
                Pair(
                    context.getString(R.string.consumer_success_event_count),
                    successConsumerCount
                )
            )
        }

        val successProducerCount =
            viewModel.eventList.value.filter { it.status == EventSyncStatus.PRODUCER_SUCCESS.eventSyncStatus }.size
        if (successProducerCount > 1) {
            eventStatusUIList.add(
                Pair(
                    context.getString(R.string.producer_success_event_count),
                    successProducerCount
                )
            )
        }

        val inProgressConsumerCount =
            viewModel.eventList.value.filter { it.status == EventSyncStatus.CONSUMER_IN_PROGRESS.eventSyncStatus }.size
        if (inProgressConsumerCount > 1) {
            eventStatusUIList.add(
                Pair(
                    context.getString(R.string.consumer_inprogress_event_count),
                    inProgressConsumerCount
                )
            )
        }

        val inProgressProducerCount =
            viewModel.eventList.value.filter { it.status == EventSyncStatus.PRODUCER_IN_PROGRESS.eventSyncStatus }.size
        if (inProgressProducerCount > 1) {
            eventStatusUIList.add(
                Pair(
                    context.getString(R.string.producer_inprogress_event_count),
                    inProgressProducerCount
                )
            )
        }

        val failedConsumerCount =
            viewModel.eventList.value.filter { it.status == EventSyncStatus.CONSUMER_FAILED.eventSyncStatus }.size
        if (failedConsumerCount > 1) {
            eventStatusUIList.add(
                Pair(
                    context.getString(R.string.consumer_failed_event_count),
                    failedConsumerCount
                )
            )
        }

        val failedProducerCount =
            viewModel.eventList.value.filter { it.status == EventSyncStatus.PRODUCER_FAILED.eventSyncStatus }.size
        if (failedProducerCount > 1) {
            eventStatusUIList.add(
                Pair(
                    context.getString(R.string.producer_failed_event_count),
                    failedProducerCount
                )
            )
        }
        viewModel._countList.value = eventStatusUIList
    }

    ModalBottomSheetLayout(
        modifier = Modifier,
        sheetShape = RoundedCornerShape(topStart = dimen_10_dp, topEnd = dimen_10_dp),
        sheetState = sheetState,
        sheetBackgroundColor = searchFieldBg,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight - dimen_56_dp)
                    .padding(top = dimen_14_dp)
                    .background(searchFieldBg)
            ) {
                /*DateRangePicker(
                    state = state, showModeToggle = false, colors = DatePickerDefaults.colors(
                        containerColor = searchFieldBg,
                        todayDateBorderColor = blueDark,
                        dayInSelectionRangeContainerColor = blueDark.copy(0.5f),
                        selectedDayContainerColor = blueDark,
                        selectedDayContentColor = white
                    ), dateValidator = { selectedDate ->
                        selectedDate < System.currentTimeMillis()
                    })*/

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .wrapContentWidth()
                        .padding(end = dimen_10_dp, bottom = dimen_10_dp)
                ) {
                    Button(
                        modifier = Modifier,
                        onClick = {
                            viewModel.isDateSelected.value=true
                            viewModel.startDate.value = state.selectedStartDateMillis?.let {
                                changeMilliDateToDate(
                                    it
                                )
                            }
                                ?: BLANK_STRING

                            viewModel.endDate.value = state.selectedEndDateMillis?.let {
                                changeMilliDateToDate(
                                    it
                                )
                            }
                                ?: BLANK_STRING
                            if(viewModel.startDate.value.isNotEmpty() && viewModel.endDate.value.isNotEmpty()){
                                CoreLogger.d(
                                    context = context,
                                    "SyncHistoryScreen",
                                    "Dates: Start Date: ${viewModel.startDate.value} :: End Date: ${viewModel.startDate.value}"
                                )
                                viewModel.getAllEventsBetweenDates(
                                    startDate = state.selectedStartDateMillis ?: System.currentTimeMillis(),
                                    endDate = state.selectedEndDateMillis ?: System.currentTimeMillis()
                                )
                            }
                            scope.launch {
                                sheetState.hide()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = blueDark
                        )
                    ) {
                        Text(text = stringResource(id = R.string.ok_text), color = white, style = smallTextStyleWithNormalWeight)
                    }
                }

            }
        }) {
        ToolbarWithMenuComponent(
            title = stringResource(id = if (syncType == SYNC_DATA) R.string.sync_data_history else R.string.sync_image_history),
            modifier = Modifier.fillMaxSize(),
            isMenuIconRequired = false,
            onBackIconClick = { navController.popBackStack() },
            onBottomUI = { }) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(start = dimen_10_dp, end = dimen_10_dp, top = dimen_65_dp)
                    .fillMaxWidth()

            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {

                    // Start Date view field
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(white)
                            .weight(1f)
                            .clickable {
                                showRangePickerDialog.value = true
                            },
                        value = if(viewModel.isDateSelected.value) "${viewModel.startDate.value} - ${viewModel.endDate.value}" else BLANK_STRING,
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
                            Text(text = stringResource(id = R.string.select_date), color = otpBorderColor)
                        },
                        placeholder = {
                            Text(text = stringResource(id = R.string.select_date), color = otpBorderColor)
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    sheetState.show()
                                }
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.calendar),
                                    contentDescription = stringResource(id = R.string.select_date)
                                )
                            }

                        },
                        onValueChange = {}
                    )
                }

                CreateEventUIList(viewModel, screenHeight)
            }
        }

    }

}

@Composable
private fun CreateEventUIList(
    viewModel: SyncHistoryViewModel,
    screenHeight: Dp
) {
    if (viewModel.countList.value.isNotEmpty()) {
        EventTypeHistoryCard(
            eventDateTime = if (viewModel.isDateSelected.value) "${viewModel.startDate.value} - ${viewModel.endDate.value}" else BLANK_STRING,
            eventStatusList = viewModel.countList.value,
            onCardClick = {}
        )
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(vertical = (screenHeight / 4))
                    .align(
                        Alignment.TopCenter
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.material.Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = textColorDark,
                                fontSize = text_size_16_sp,
                                fontWeight = FontWeight.Normal,
                                fontFamily = NotoSans
                            )
                        ) {
                            append(
                                stringResource(id = R.string.no_history_available_right_now)
                            )
                        }
                    },
                    modifier = Modifier.padding(top = dimen_32_dp)
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun SyncHistoryScreenPreview() {
    SyncHistoryScreen(
        navController = rememberNavController(),
        syncType = SYNC_DATA,
        viewModel = hiltViewModel()
    )
}
package com.patsurvey.nudge.activities.sync.history.presentation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nrlm.baselinesurvey.ui.common_components.ToolbarWithMenuComponent
import com.nrlm.baselinesurvey.ui.theme.defaultTextStyle
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_14_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_56_dp
import com.nrlm.baselinesurvey.ui.theme.searchFieldBg
import com.nrlm.baselinesurvey.ui.theme.smallTextStyleWithNormalWeight
import com.nudge.core.database.converters.DateConverter
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.sync.history.viewmodel.SyncHistoryViewModel
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.dateRangeFieldColor
import com.patsurvey.nudge.activities.ui.theme.otpBorderColor
import com.patsurvey.nudge.activities.ui.theme.smallTextStyle
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
                                Log.d("TAG", "SyncHistoryScreen Vaoes: ")
                                viewModel.getAllEventsBetweenDates(
                                    context = context,
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
                        Text(text = "Ok", color = white, style = smallTextStyleWithNormalWeight)
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
                    .padding(start = 10.dp, end = 10.dp, top = 65.dp)
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
                            Text(text = "Select Date", color = otpBorderColor)
                        },
                        placeholder = {
                            Text(text = "Select Date", color = otpBorderColor)
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    sheetState.show()
                                }
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.calendar),
                                    contentDescription = "Start Date"
                                )
                            }

                        },
                        onValueChange = {}
                    )
                }

                if(viewModel.countList.value.isNotEmpty()) {
                    EventTypeHistoryCard(
                        eventDateTime = if(viewModel.isDateSelected.value) "${viewModel.startDate.value} - ${viewModel.endDate.value}" else BLANK_STRING,
                        eventStatusList = viewModel.countList.value,
                        onCardClick = {}
                    )
                }else {
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
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Normal,
                                            fontFamily = NotoSans
                                        )
                                    ) {
                                        append(
                                            "No History Available Right Now"
                                        )
                                    }
                                },
                                modifier = Modifier.padding(top = 32.dp)
                            )
                        }
                    }

                }
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
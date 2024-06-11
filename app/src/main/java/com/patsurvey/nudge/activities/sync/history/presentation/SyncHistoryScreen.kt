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
import androidx.compose.material.IconButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nrlm.baselinesurvey.ui.common_components.ToolbarWithMenuComponent
import com.nrlm.baselinesurvey.ui.theme.defaultTextStyle
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.searchFieldBg
import com.nrlm.baselinesurvey.ui.theme.smallTextStyleWithNormalWeight
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.dateRangeFieldColor
import com.patsurvey.nudge.activities.ui.theme.otpBorderColor
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.utils.BLANK_STRING

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncHistoryScreen(navController:NavController){
    val state = rememberDateRangePickerState()

    val showRangePickerDialog = remember {
        mutableStateOf(false)
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

//Date Range picker Dialog
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
                        selectedDate <= System.currentTimeMillis()
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
    ToolbarWithMenuComponent(
        title = stringResource(id = R.string.sync_all_data),
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

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround) {

                // Start Date view field
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(white)
                        .weight(1f)
                        .clickable {
                            showRangePickerDialog.value = true
                        },
                    value = BLANK_STRING,
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


                //End Date view field
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(white)
                        .weight(1f)
                        .clickable {
                            showRangePickerDialog.value = true
                        },
                    value = BLANK_STRING,
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


            EventTypeHistoryCard(
                eventDateTime = "16 Jan 1025, 16:10:00",
                totalEventCount = 100,
                successEventCount = 10,
                onCardClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SyncHistoryScreenPreview(){
    SyncHistoryScreen(navController = rememberNavController())
}
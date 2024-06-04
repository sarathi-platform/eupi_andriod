package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.sarathi.dataloadingmangement.data.entities.getSubtitle
import com.sarathi.missionactivitytask.ui.components.BasicCardView
import com.sarathi.missionactivitytask.ui.components.ContentWithImage
import com.sarathi.missionactivitytask.ui.components.CustomVerticalSpacer
import com.sarathi.missionactivitytask.ui.components.IconProperties
import com.sarathi.missionactivitytask.ui.components.ImageProperties
import com.sarathi.missionactivitytask.ui.components.LazyColumnWithVerticalPadding
import com.sarathi.missionactivitytask.ui.components.TextProperties
import com.sarathi.missionactivitytask.ui.components.TextWithIconComponent
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.missionactivitytask.ui.theme.dimen_1_dp
import com.sarathi.missionactivitytask.ui.theme.dimen_24_dp
import com.sarathi.missionactivitytask.ui.theme.dimen_8_dp
import com.sarathi.smallgroupmodule.R
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.viewModel.SmallGroupAttendanceScreenViewModel
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.presentation.event.SmallGroupAttendanceEvent
import com.sarathi.smallgroupmodule.ui.theme.blueDark
import com.sarathi.smallgroupmodule.ui.theme.defaultTextStyle
import com.sarathi.smallgroupmodule.ui.theme.dimen_10_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_6_dp
import com.sarathi.smallgroupmodule.ui.theme.mediumTextStyle
import com.sarathi.smallgroupmodule.ui.theme.progressIndicatorColor
import com.sarathi.smallgroupmodule.ui.theme.searchFieldBg
import com.sarathi.smallgroupmodule.ui.theme.smallTextStyleMediumWeight
import com.sarathi.smallgroupmodule.ui.theme.stepIconCompleted
import com.sarathi.smallgroupmodule.ui.theme.textColorDark80
import com.sarathi.smallgroupmodule.ui.theme.uncheckedTrackColor
import com.sarathi.smallgroupmodule.ui.theme.white
import com.sarathi.smallgroupmodule.utils.getDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SmallGroupAttendanceScreen(
    modifier: Modifier = Modifier,
    smallGroupId: Int = 0,
    navHostController: NavHostController,
    smallGroupAttendanceScreenViewModel: SmallGroupAttendanceScreenViewModel
) {

    LaunchedEffect(key1 = Unit) {

        smallGroupAttendanceScreenViewModel.onEvent(
            SmallGroupAttendanceEvent.LoadSmallGroupDetailsForSmallGroupIdEvent(
                smallGroupId
            )
        )

    }

    val sheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        skipHalfExpanded = false
    )

    val showDatePickerDialog = remember {
        mutableStateOf(false)
    }

    val datePickerState = rememberDatePickerState()

    val coroutineScope = rememberCoroutineScope()

    val selectedDate = remember {
        mutableStateOf(System.currentTimeMillis())
    }

    ToolBarWithMenuComponent(
        title = smallGroupAttendanceScreenViewModel.smallGroupDetails.value.smallGroupName,
        modifier = Modifier,
        onBackIconClick = { /*TODO*/ },
        onSearchValueChange = {},
        isFilterSelected = {},
        isSearch = true,
        isDataAvailable = true,
        onBottomUI = { /*TODO*/ },
        tabBarView = { /*TODO*/ }) {


        Column(
            verticalArrangement = Arrangement.spacedBy(dimen_8_dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    white
                )
                .padding(horizontal = dimen_10_dp)
        ) {
            if (showDatePickerDialog.value) {

                DatePickerDialog(
                    colors = DatePickerDefaults.colors(
                        containerColor = searchFieldBg
                    ),
                    onDismissRequest = { showDatePickerDialog.value = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                selectedDate.value = datePickerState.selectedDateMillis!!
                                showDatePickerDialog.value = false
                            },
                            content = { Text("Ok") }
                        )
                    }) {
                    DatePicker(state = datePickerState, dateValidator = { selectedDate ->
                        selectedDate < System.currentTimeMillis()
                    })
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    dimen_6_dp
                )
            ) {

                BasicCardView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(white)
                        .weight(0.7f)
                        .clickable {
                            showDatePickerDialog.value = true
                        }
                ) {
                    TextWithIconComponent(
                        modifier = Modifier
                            .background(white)
                            .fillMaxWidth()
                            .padding(dimen_10_dp),
                        iconProperties = IconProperties(
                            painterResource(id = R.drawable.calendar),
                            contentDescription = "Date Selector",
                        ),
                        textProperties = TextProperties(
                            text = selectedDate.value.getDate(),
                            color = progressIndicatorColor,
                            style = defaultTextStyle
                        )
                    )
                }

                BasicCardView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(white)
                        .weight(0.3f)
                        .clickable {
                            showDatePickerDialog.value = true
                        },
                    colors = CardDefaults.cardColors(containerColor = white)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(white)
                            .padding(dimen_10_dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(text = "All", style = defaultTextStyle, color = progressIndicatorColor)
                        Switch(
                            modifier = Modifier
                                .height(dimen_24_dp),
                            checked = smallGroupAttendanceScreenViewModel.isAllSelected.value,
                            colors = SwitchDefaults
                                .colors(
                                    checkedThumbColor = white,
                                    checkedTrackColor = stepIconCompleted,
                                    uncheckedThumbColor = white,
                                    uncheckedTrackColor = uncheckedTrackColor,
                                    uncheckedBorderColor = uncheckedTrackColor
                                ),
                            onCheckedChange = {
                                smallGroupAttendanceScreenViewModel.onEvent(
                                    SmallGroupAttendanceEvent.MarkAttendanceForAll(it)
                                )
                            }
                        )
                    }

                }

            }

            LazyColumnWithVerticalPadding() {

                itemsIndexed(smallGroupAttendanceScreenViewModel.smallGroupAttendanceEntityState.value) { index, subjectState ->
                    AttendanceItem(
                        smallGroupAttendanceEntityState = subjectState
                    ) {
                        smallGroupAttendanceScreenViewModel.onEvent(
                            SmallGroupAttendanceEvent.MarkAttendanceForSubject(
                                it,
                                subjectState.subjectId ?: 0
                            )
                        )
                    }

                }

            }
        }


    }
}

@Composable
fun AttendanceItem(
    modifier: Modifier = Modifier,
    smallGroupAttendanceEntityState: SmallGroupAttendanceEntityState,
    onCheckedChange: (Boolean) -> Unit
) {

    val subjectAttendance = remember {
        mutableStateOf(smallGroupAttendanceEntityState.attendance)
    }

    Column(Modifier.fillMaxWidth()) {
        CustomVerticalSpacer()
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

            ContentWithImage(
                imageProperties = ImageProperties(
                    path = smallGroupAttendanceEntityState.subjectEntity.crpImageName,
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
                        text = smallGroupAttendanceEntityState.subjectEntity.subjectName,
                        style = mediumTextStyle,
                        color = blueDark
                    )
                    Text(
                        text = smallGroupAttendanceEntityState.subjectEntity.getSubtitle(),
                        style = smallTextStyleMediumWeight,
                        color = textColorDark80
                    )

                }

                Switch(
                    modifier = Modifier
                        .height(dimen_24_dp),
                    checked = subjectAttendance.value,
                    colors = SwitchDefaults
                        .colors(
                            checkedThumbColor = white,
                            checkedTrackColor = stepIconCompleted,
                            uncheckedThumbColor = white,
                            uncheckedTrackColor = uncheckedTrackColor,
                            uncheckedBorderColor = uncheckedTrackColor
                        ),
                    onCheckedChange = {
                        subjectAttendance.value = it
                        onCheckedChange(it)
                    }
                )
            }

        }

        CustomVerticalSpacer()
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimen_10_dp), thickness = dimen_1_dp, uncheckedTrackColor
        )
    }


}
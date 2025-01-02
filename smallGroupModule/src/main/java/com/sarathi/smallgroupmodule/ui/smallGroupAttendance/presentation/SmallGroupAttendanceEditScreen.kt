package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.presentation

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.commonUi.BasicCardView
import com.nudge.core.ui.commonUi.LazyColumnWithVerticalPadding
import com.nudge.core.ui.events.DialogEvents
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.missionactivitytask.ui.components.ButtonPositiveComponent
import com.sarathi.missionactivitytask.ui.components.IconProperties
import com.sarathi.missionactivitytask.ui.components.TextProperties
import com.sarathi.missionactivitytask.ui.components.TextWithIconComponent
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.smallgroupmodule.R
import com.sarathi.smallgroupmodule.ui.commonUi.CustomDialogComponent
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.viewModel.SmallGroupAttendanceEditScreenViewModel
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.presentation.event.SmallGroupAttendanceEvent
import com.sarathi.smallgroupmodule.ui.theme.defaultTextStyle
import com.sarathi.smallgroupmodule.ui.theme.dimen_100_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_10_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_24_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_6_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_80_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_8_dp
import com.sarathi.smallgroupmodule.ui.theme.progressIndicatorColor
import com.sarathi.smallgroupmodule.ui.theme.stepIconCompleted
import com.sarathi.smallgroupmodule.ui.theme.uncheckedTrackColor
import com.sarathi.smallgroupmodule.ui.theme.white
import com.sarathi.smallgroupmodule.utils.getDate

@Composable
fun SmallGroupAttendanceEditScreen(
    smallGroupId: Int = 0,
    selectedDate: Long,
    navHostController: NavHostController,
    smallGroupAttendanceEditScreenViewModel: SmallGroupAttendanceEditScreenViewModel,
    onSettingIconClicked: () -> Unit
) {

    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        smallGroupAttendanceEditScreenViewModel.onEvent(
            SmallGroupAttendanceEvent.LoadSmallGroupAttendanceForGroupForDateEvent(
                smallGroupId,
                selectedDate
            )
        )
        smallGroupAttendanceEditScreenViewModel.onEvent(InitDataEvent.InitDataState)

    }

    val smallGroupAttendanceList =
        smallGroupAttendanceEditScreenViewModel.smallGroupAttendanceEntityState


    val showAlertDialog = remember {
        mutableStateOf(false)
    }


    if (smallGroupAttendanceEditScreenViewModel.alertDialogState.value.isDialogVisible) {

        CustomDialogComponent(
            title = smallGroupAttendanceEditScreenViewModel.stringResource(
                context,
                R.string.confirmation_alert_dialog_title),

            message =smallGroupAttendanceEditScreenViewModel.stringResource(
                context,
                R.string.delete_attendance_confirmation_msg),
            positiveButtonTitle = smallGroupAttendanceEditScreenViewModel.stringResource(
                context,
                R.string.yes),
            negativeButtonTitle =  smallGroupAttendanceEditScreenViewModel.stringResource(
                context,
                R.string.no),
            onPositiveButtonClick = {
                smallGroupAttendanceEditScreenViewModel.onEvent(SmallGroupAttendanceEvent.UpdateAttendanceForDateEvent)
                smallGroupAttendanceEditScreenViewModel.onEvent(DialogEvents.ShowDialogEvent(false))
                navHostController.popBackStack()
            },
            onNegativeButtonClick = {
                smallGroupAttendanceEditScreenViewModel.onEvent(DialogEvents.ShowDialogEvent(false))
            }
        )

    }

    ToolBarWithMenuComponent(
        title = smallGroupAttendanceEditScreenViewModel.smallGroupDetails.value.smallGroupName,
        modifier = Modifier,
        onBackIconClick = { navHostController.popBackStack() },
        onSearchValueChange = {},
        isSearch = true,
        isDataNotAvailable = smallGroupAttendanceEditScreenViewModel.smallGroupDetails.value.smallGroupId == 0,
        onRetry = {},
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
                        buttonTitle = smallGroupAttendanceEditScreenViewModel.stringResource(
                            context,
                            R.string.submit),
                        isActive = true
                    ) {
                        if (smallGroupAttendanceEditScreenViewModel.selectedItems.value.filter { it.value }
                                .isEmpty()) {
                            smallGroupAttendanceEditScreenViewModel.onEvent(
                                DialogEvents.ShowDialogEvent(
                                    true
                                )
                            )
                        } else {
                            smallGroupAttendanceEditScreenViewModel.onEvent(
                                SmallGroupAttendanceEvent.UpdateAttendanceForDateEvent
                            )
                            navHostController.popBackStack()
                        }
                    }
                }

            }
        },
        onSettingClick = {
            onSettingIconClicked()
        },
        onContentUI = { paddingValues, b, function ->
            Column(
                verticalArrangement = Arrangement.spacedBy(dimen_8_dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        white
                    )
                    .padding(horizontal = dimen_10_dp)
            ) {
                if (showAlertDialog.value) {
                    CustomDialogComponent(
                        title = BLANK_STRING,
                        message =  smallGroupAttendanceEditScreenViewModel.stringResource(
                            context,
                            R.string.data_change_not_allow) ,
                        positiveButtonTitle = smallGroupAttendanceEditScreenViewModel.stringResource(
                            context,
                            R.string.ok),
                        onPositiveButtonClick = {
                            showAlertDialog.value = false
                        },
                        onNegativeButtonClick = {
                            showAlertDialog.value = false
                        }
                    )

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
                                showAlertDialog.value = true
                            }
                    ) {
                        TextWithIconComponent(
                            modifier = Modifier
                                .background(white)
                                .fillMaxWidth()
                                .padding(dimen_10_dp),
                            iconProperties = IconProperties(
                                painterResource(id = R.drawable.calendar),
                                contentDescription =smallGroupAttendanceEditScreenViewModel.stringResource(
                                    context,
                                    R.string.data_selector),
                                ),
                            textProperties = TextProperties(
                                text = smallGroupAttendanceEditScreenViewModel.selectedDate.value.getDate(),
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

//                                showDatePickerDialog.value = true
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
                            Text(
                                text = smallGroupAttendanceEditScreenViewModel.stringResource(
                                    context,
                                    R.string.all),
                            style = defaultTextStyle,
                                color = progressIndicatorColor
                            )

                            Switch(
                                modifier = Modifier
                                    .height(dimen_24_dp),
                                checked = smallGroupAttendanceEditScreenViewModel.allSelected.value,
                                colors = SwitchDefaults
                                    .colors(
                                        checkedThumbColor = white,
                                        checkedTrackColor = stepIconCompleted,
                                        uncheckedThumbColor = white,
                                        uncheckedTrackColor = uncheckedTrackColor,
                                        uncheckedBorderColor = uncheckedTrackColor
                                    ),
                                onCheckedChange = { isAllSelected ->
                                    smallGroupAttendanceEditScreenViewModel.onEvent(
                                        SmallGroupAttendanceEvent.MarkAttendanceForAllEvent(
                                            isAllSelected
                                        )
                                    )
                                }
                            )
                        }

                    }

                }

                LazyColumnWithVerticalPadding() {

                    itemsIndexed(smallGroupAttendanceList.value) { index, subjectState ->
                        AttendanceItem(
                            smallGroupAttendanceEntityState = subjectState,
                            selectedItems = smallGroupAttendanceEditScreenViewModel.selectedItems
                        ) {
                            smallGroupAttendanceEditScreenViewModel.onEvent(
                                SmallGroupAttendanceEvent.MarkAttendanceForSubjectEvent(
                                    it,
                                    subjectState.subjectId ?: 0
                                )
                            )
                        }

                    }

                    item {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(dimen_100_dp)
                        )
                    }

                }
            }
        }
    )
}
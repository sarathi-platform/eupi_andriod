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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import com.nudge.core.showCustomToast
import com.nudge.core.ui.commonUi.BasicCardView
import com.nudge.core.ui.commonUi.ContentWithImage
import com.nudge.core.ui.commonUi.CustomDatePickerComponent
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.commonUi.ImageProperties
import com.nudge.core.ui.commonUi.LazyColumnWithVerticalPadding
import com.nudge.core.ui.commonUi.rememberCustomDatePickerDialogProperties
import com.nudge.core.ui.commonUi.rememberCustomDatePickerState
import com.nudge.core.ui.commonUi.rememberDatePickerProperties
import com.nudge.core.ui.events.CommonEvents
import com.nudge.core.ui.events.DialogEvents
import com.sarathi.dataloadingmangement.data.entities.getSubtitle
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.missionactivitytask.ui.components.ButtonPositiveWithLoaderComponent
import com.sarathi.missionactivitytask.ui.components.IconProperties
import com.sarathi.missionactivitytask.ui.components.SearchWithFilterViewComponent
import com.sarathi.missionactivitytask.ui.components.TextProperties
import com.sarathi.missionactivitytask.ui.components.TextWithIconComponent
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.smallgroupmodule.R
import com.sarathi.smallgroupmodule.navigation.navigateToHistoryScreenFromAttendance
import com.sarathi.smallgroupmodule.ui.commonUi.CustomDialogComponent
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.viewModel.SmallGroupAttendanceScreenViewModel
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.presentation.event.SmallGroupAttendanceEvent
import com.sarathi.smallgroupmodule.ui.theme.blueDark
import com.sarathi.smallgroupmodule.ui.theme.defaultTextStyle
import com.sarathi.smallgroupmodule.ui.theme.dimen_100_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_10_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_1_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_24_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_6_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_80_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_8_dp
import com.sarathi.smallgroupmodule.ui.theme.mediumTextStyle
import com.sarathi.smallgroupmodule.ui.theme.progressIndicatorColor
import com.sarathi.smallgroupmodule.ui.theme.smallTextStyleMediumWeight
import com.sarathi.smallgroupmodule.ui.theme.stepIconCompleted
import com.sarathi.smallgroupmodule.ui.theme.textColorDark80
import com.sarathi.smallgroupmodule.ui.theme.uncheckedTrackColor
import com.sarathi.smallgroupmodule.ui.theme.white
import com.sarathi.smallgroupmodule.utils.getDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SmallGroupAttendanceScreen(
    smallGroupId: Int = 0,
    navHostController: NavHostController,
    smallGroupAttendanceScreenViewModel: SmallGroupAttendanceScreenViewModel,
    onSettingIconClicked: () -> Unit
) {

    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        smallGroupAttendanceScreenViewModel.onEvent(LoaderEvent.UpdateLoaderState(false))
        smallGroupAttendanceScreenViewModel.onEvent(
            SmallGroupAttendanceEvent.LoadSmallGroupDetailsForSmallGroupIdEvent(
                smallGroupId
            )
        )
        smallGroupAttendanceScreenViewModel.onEvent(InitDataEvent.InitDataState)

    }

    val smallGroupAttendanceList =
        smallGroupAttendanceScreenViewModel.filteredSmallGroupAttendanceEntityState


    val showDatePickerDialog = remember {
        mutableStateOf(false)
    }

    val datePickerState =
        rememberCustomDatePickerState()

    val datePickerProperties = rememberDatePickerProperties(
        state = datePickerState,
        dateValidator = { selectedDate ->
            smallGroupAttendanceScreenViewModel.dateValidator(selectedDate)
        }
    )

    val datePickerDialogProperties = rememberCustomDatePickerDialogProperties()

    if (smallGroupAttendanceScreenViewModel.alertDialogState.value.isDialogVisible) {

        CustomDialogComponent(
            title = smallGroupAttendanceScreenViewModel.stringResource(
                context,
                R.string.confirmation_alert_dialog_title
            ),
            message = smallGroupAttendanceScreenViewModel.stringResource(
                context,
                R.string.do_you_want_mark_all_absent
            ),
            positiveButtonTitle = smallGroupAttendanceScreenViewModel.stringResource(
                context,
                R.string.yes
            ),
            negativeButtonTitle = smallGroupAttendanceScreenViewModel.stringResource(
                context,
                R.string.no
            ),
            onPositiveButtonClick = {
                smallGroupAttendanceScreenViewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
                smallGroupAttendanceScreenViewModel.onEvent(SmallGroupAttendanceEvent.SubmitAttendanceForDateEvent {
                    smallGroupAttendanceScreenViewModel.onEvent(DialogEvents.ShowDialogEvent(false))
                    if (it) {
                        smallGroupAttendanceScreenViewModel.onEvent(
                            LoaderEvent.UpdateLoaderState(
                                false
                            )
                        )
                        navHostController.navigateToHistoryScreenFromAttendance(smallGroupId)
                    } else {
                        smallGroupAttendanceScreenViewModel.onEvent(
                            LoaderEvent.UpdateLoaderState(
                                false
                            )
                        )
                        showCustomToast(
                            context = context,
                            msg = smallGroupAttendanceScreenViewModel.stringResource(
                                context,
                                R.string.attendance_already_marked,
                                smallGroupAttendanceScreenViewModel.selectedDate.value.getDate()
                            )
                        )
                    }
                })

            },
            onNegativeButtonClick = {
                smallGroupAttendanceScreenViewModel.onEvent(DialogEvents.ShowDialogEvent(false))
            }
        )

    }

    ToolBarWithMenuComponent(
        title = smallGroupAttendanceScreenViewModel.smallGroupDetails.value.smallGroupName,
        modifier = Modifier,
        onBackIconClick = { navHostController.navigateUp() },
        onSearchValueChange = {},
        isSearch = true,
        isDataNotAvailable = smallGroupAttendanceScreenViewModel.smallGroupDetails.value.smallGroupId == 0,
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
                    ButtonPositiveWithLoaderComponent(
                        buttonTitle = smallGroupAttendanceScreenViewModel.stringResource(
                            context,
                            R.string.submit
                        ),
                        isActive = true,
                        showLoader = smallGroupAttendanceScreenViewModel.loaderState.value.isLoaderVisible
                    ) {
                        if (smallGroupAttendanceScreenViewModel.selectedItems.value.filter { it.value }
                                .isEmpty()) {
                            smallGroupAttendanceScreenViewModel.onEvent(
                                DialogEvents.ShowDialogEvent(
                                    true
                                )
                            )
                        } else {
                            smallGroupAttendanceScreenViewModel.onEvent(
                                LoaderEvent.UpdateLoaderState(
                                    true
                                )
                            )
                            smallGroupAttendanceScreenViewModel.onEvent(
                                SmallGroupAttendanceEvent.SubmitAttendanceForDateEvent {
                                    if (it) {
                                        smallGroupAttendanceScreenViewModel.onEvent(
                                            LoaderEvent.UpdateLoaderState(
                                                false
                                            )
                                        )
                                        navHostController.navigateToHistoryScreenFromAttendance(
                                            smallGroupId
                                        )
                                    } else {
                                        smallGroupAttendanceScreenViewModel.onEvent(
                                            LoaderEvent.UpdateLoaderState(
                                                false
                                            )
                                        )
                                        showCustomToast(
                                            context = context,
                                            msg =smallGroupAttendanceScreenViewModel.stringResource(
                                                context,
                                                R.string.attendance_already_marked,
                                                smallGroupAttendanceScreenViewModel.selectedDate.value.getDate()
                                            )
                                        )
                                    }
                                }
                            )
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

                CustomDatePickerComponent(
                    datePickerProperties = datePickerProperties,
                    datePickerDialogProperties = datePickerDialogProperties,
                    onDismissRequest = {
                        datePickerDialogProperties.hide()
                    },
                    onConfirmButtonClicked = {
                        smallGroupAttendanceScreenViewModel.selectedDate.value =
                            datePickerState.selectedDateMillis!!
                        datePickerDialogProperties.hide()
                        showDatePickerDialog.value = false
                    }
                )

                SearchWithFilterViewComponent(
                    placeholderString = smallGroupAttendanceScreenViewModel.stringResource(
                        context,
                        R.string.search_didi
                    ),
                    showFilter = false,
                    onFilterSelected = {
                        /**
                         * Not required as not filter available for this screen.
                         **/
                    },
                    onSearchValueChange = { searchQuery ->
                        smallGroupAttendanceScreenViewModel.onEvent(
                            CommonEvents.SearchValueChangedEvent(
                                searchQuery,
                                null
                            )
                        )
                    }
                )

                CustomVerticalSpacer()

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
                                datePickerDialogProperties.show()
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
                                text = smallGroupAttendanceScreenViewModel.selectedDate.value.getDate(),
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
                            Text(
                                text = smallGroupAttendanceScreenViewModel.stringResource(
                                    context,
                                    R.string.all
                                ),
                                style = defaultTextStyle,
                                color = progressIndicatorColor,
                                overflow = TextOverflow.Ellipsis
                            )

                            Switch(
                                modifier = Modifier
                                    .height(dimen_24_dp),
                                checked = smallGroupAttendanceScreenViewModel.allSelected.value,
                                colors = SwitchDefaults
                                    .colors(
                                        checkedThumbColor = white,
                                        checkedTrackColor = stepIconCompleted,
                                        uncheckedThumbColor = white,
                                        uncheckedTrackColor = uncheckedTrackColor,
                                        uncheckedBorderColor = uncheckedTrackColor
                                    ),
                                onCheckedChange = { isAllSelected ->
                                    smallGroupAttendanceScreenViewModel.onEvent(
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
                            selectedItems = smallGroupAttendanceScreenViewModel.selectedItems
                        ) {
                            smallGroupAttendanceScreenViewModel.onEvent(
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

@Composable
fun AttendanceItem(
    modifier: Modifier = Modifier,
    smallGroupAttendanceEntityState: SmallGroupAttendanceEntityState,
    selectedItems: MutableState<Map<Int, Boolean>>,
    onCheckedChange: (Boolean) -> Unit
) {

    Column(
        Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
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
                    checked = selectedItems.value[smallGroupAttendanceEntityState.subjectId]
                        ?: false,
                    colors = SwitchDefaults
                        .colors(
                            checkedThumbColor = white,
                            checkedTrackColor = stepIconCompleted,
                            uncheckedThumbColor = white,
                            uncheckedTrackColor = uncheckedTrackColor,
                            uncheckedBorderColor = uncheckedTrackColor
                        ),
                    onCheckedChange = {
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
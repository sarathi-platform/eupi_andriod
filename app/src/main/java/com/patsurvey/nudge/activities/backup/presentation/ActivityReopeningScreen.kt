package com.patsurvey.nudge.activities.backup.presentation

import android.text.TextUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.ui.mission_summary_screen.presentation.auditTrailDetail
import com.nudge.core.TabsCore
import com.nudge.core.enums.SubTabs
import com.nudge.core.enums.TabsEnum
import com.nudge.core.showCustomToast
import com.nudge.core.ui.commonUi.CustomSubTabLayout
import com.nudge.core.ui.commonUi.LoaderComponent
import com.nudge.core.ui.commonUi.componet_.component.ButtonPositive
import com.nudge.core.ui.commonUi.componet_.component.ToolBarWithMenuComponent
import com.nudge.core.ui.commonUi.customVerticalSpacer
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.defaultTextSpanStyle
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_50_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.greenOnline
import com.nudge.core.ui.theme.greyBorderColor
import com.nudge.core.ui.theme.redOffline
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.nudge.core.ui.theme.white
import com.nudge.core.value
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.backup.viewmodel.ActivityReopeningScreenViewModel
import com.patsurvey.nudge.activities.settings.domain.SettingTagEnum
import com.sarathi.dataloadingmangement.model.uiModel.ActivityUiModel
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel
import com.sarathi.dataloadingmangement.util.event.InitDataEvent

@Composable
fun ActivityReopeningScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ActivityReopeningScreenViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    val tabs = listOf(SubTabs.Step1, SubTabs.Step2)

    LaunchedEffect(key1 = Unit) {
        TabsCore.setSubTabIndex(TabsEnum.SettingsTab.tabIndex, tabs.indexOf(SubTabs.Step1))
        viewModel.onEvent(InitDataEvent.InitDataState)
    }



    ToolBarWithMenuComponent(
        title = stringResource(R.string.mark_activity_in_progress),
        modifier = modifier,
        onBackIconClick = { navController.navigateUp() },
        showSettingsButton = false,
        onSearchValueChange = {},
        onBottomUI = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .zIndex(1f),
            ) {
                when (TabsCore.getSubTabForTabIndex(TabsEnum.SettingsTab.tabIndex)) {
                    tabs.indexOf(SubTabs.Step1) -> {
                        ButtonPositive(
                            buttonTitle = stringResource(id = R.string.next),
                            isActive = viewModel.isNextButtonForStep1Active.value,
                            isArrowRequired = true,
                            onClick = {
                                viewModel.onEvent(InitDataEvent.InitActivityListState(viewModel.selectedMissionId.value))
                                TabsCore.setSubTabIndex(
                                    TabsEnum.SettingsTab.tabIndex,
                                    tabs.indexOf(SubTabs.Step2)
                                )
                            }
                        )
                    }

                    tabs.indexOf(SubTabs.Step2) -> {
                        ButtonPositive(
                            buttonTitle = stringResource(id = R.string.submit),
                            isActive = viewModel.isNextButtonForStep2Active.value,
                            isArrowRequired = true,
                            onClick = {
                                viewModel.reopenActivities { success ->
                                    if (success) {
                                        showCustomToast(context, "Activities marked In Progress")
                                        navController.navigateUp()
                                        auditTrailDetail(viewModel.auditTrailUseCase,context.getString(
                                        R.string.audit_trail_action,"Activities marked In Progress"))

                                    }
                                }
                            }
                        )
                    }
                }
            }
        },
        onSettingClick = {},
        onContentUI = {

            if (viewModel.loaderState.value.isLoaderVisible) {

                LoaderComponent(
                    modifier = Modifier
                        .fillMaxSize()
                )


            } else {

                Column(
                    verticalArrangement = Arrangement.spacedBy(dimen_10_dp), modifier = Modifier
                        .padding(horizontal = dimen_16_dp)
                ) {
                    CustomSubTabLayout(
                        parentTabIndex = TabsEnum.SettingsTab.tabIndex,
                        tabs,
                        enableClickOnTab = {
                            viewModel.isNextButtonForStep1Active.value && viewModel.activityList.value.isNotEmpty()
                        }
                    )

                    when (TabsCore.getSubTabForTabIndex(TabsEnum.SettingsTab.tabIndex)) {
                        tabs.indexOf(SubTabs.Step1) -> {
                            Step1TabContent(viewModel)
                        }

                        tabs.indexOf(SubTabs.Step2) -> {
                            Step2TabContent(viewModel)
                        }
                    }

                }
            }
        }
    )
}

@Composable
private fun Step1TabContent(viewModel: ActivityReopeningScreenViewModel) {
    LazyColumn(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(dimen_10_dp)
    ) {

        item {
            Text(text = buildAnnotatedString {

                withStyle(defaultTextSpanStyle.copy(color = blueDark)) {
                    append(stringResource(R.string.select_mission))
                }

                withStyle(defaultTextSpanStyle.copy(color = redOffline)) {
                    append("*")
                }

            })
        }

        itemsIndexed(viewModel.missionList.value) { index, item ->
            Step1Card(item = item, viewModel = viewModel) { missionUiModel ->
                viewModel.onMissionSelected(missionUiModel)
            }
        }

        customVerticalSpacer(size = dimen_50_dp)
    }
}

@Composable
private fun Step1Card(
    item: MissionUiModel,
    viewModel: ActivityReopeningScreenViewModel,
    onCardClicked: (item: MissionUiModel) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimen_6_dp)
            .border(
                dimen_1_dp,
                color = getMissionCardBorderColor(item, viewModel),
                shape = RoundedCornerShape(
                    roundedCornerRadiusDefault
                )
            )
            .background(
                white, RoundedCornerShape(
                    roundedCornerRadiusDefault
                )
            )
            .clickable {
                onCardClicked(item)
            },
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimen_8_dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = viewModel.isMissionSelected(item.missionId),
                onClick = {
                    onCardClicked(item)
                },
                colors = RadioButtonDefaults.colors(
                    selectedColor = blueDark,
                    unselectedColor = greyBorderColor
                )
            )

            Text(text = item.description, style = defaultTextStyle, color = blueDark)
        }
    }
}

@Composable
private fun Step2TabContent(viewModel: ActivityReopeningScreenViewModel) {
    LazyColumn(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(dimen_10_dp)
    ) {

        item {
            Text(
                text = buildAnnotatedString {

                    withStyle(defaultTextSpanStyle.copy(color = blueDark)) {
                        val missionName =
                            viewModel.missionList.value.find { it.missionId == viewModel.selectedMissionId.value }?.description.value()
                        append(stringResource(R.string.select_activity_for))
                        if (!TextUtils.isEmpty(missionName))
                            append(" $missionName")
                    }

                    withStyle(defaultTextSpanStyle.copy(color = redOffline)) {
                        append("*")
                    }

                },
                maxLines = 2
            )
        }


        /**
         * Select All Card
         * */
        if (viewModel.activityList.value.isNotEmpty()) {
            item {
                Step2Card(
                    item = ActivityUiModel.getSelectAllActivityUiModel(
                        viewModel.selectedMissionId.value,
                        stringResource(R.string.select_all)
                    ),
                    viewModel = viewModel,
                    isSelectAllEnabled = viewModel.isSelectAllEnabled.value
                ) { item ->
                    viewModel.isSelectAllEnabled.value = !viewModel.isSelectAllEnabled.value
                    viewModel.onActivitySelected(item, true)
                }
            }
        }

        itemsIndexed(viewModel.activityList.value) { index, item ->
            Step2Card(
                item = item,
                viewModel = viewModel,
                isSelectAllEnabled = viewModel.isSelectAllEnabled.value
            ) { activityUiModel ->
                viewModel.onActivitySelected(activityUiModel)
            }
        }

        customVerticalSpacer()
    }
}

@Composable
private fun Step2Card(
    item: ActivityUiModel,
    viewModel: ActivityReopeningScreenViewModel,
    isSelectAllEnabled: Boolean,
    onCardClicked: (item: ActivityUiModel) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                dimen_1_dp,
                color = getActivityCardBorderColor(
                    item,
                    viewModel,
                    item.description.equals(stringResource(R.string.select_all), true)
                ),
                shape = RoundedCornerShape(
                    roundedCornerRadiusDefault
                )
            )
            .background(
                white, RoundedCornerShape(
                    roundedCornerRadiusDefault
                )
            )

            .clickable {
                onCardClicked(item)
            },
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimen_8_dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = dimen_6_dp)
        ) {
            Checkbox(
                checked = viewModel.isActivitySelected(item.activityId) || isSelectAllEnabled,
                onCheckedChange = {
                    onCardClicked(item)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = greenOnline,
                    checkmarkColor = white,
                    uncheckedColor = greyBorderColor
                )
            )

            Text(text = item.description, style = defaultTextStyle, color = blueDark)
        }
    }
}

fun getMissionCardBorderColor(
    item: MissionUiModel,
    viewModel: ActivityReopeningScreenViewModel
): Color {
    return if (item.missionId == viewModel.selectedMissionId.value) blueDark else greyBorderColor
}

fun getActivityCardBorderColor(
    item: ActivityUiModel,
    viewModel: ActivityReopeningScreenViewModel,
    isSelectAll: Boolean
): Color {
    return if (isSelectAll) {
        if (viewModel.isSelectAllEnabled.value) blueDark else greyBorderColor
    } else {
        if (viewModel.selectedActivityIdList.contains(item.activityId)) blueDark else greyBorderColor
    }
}

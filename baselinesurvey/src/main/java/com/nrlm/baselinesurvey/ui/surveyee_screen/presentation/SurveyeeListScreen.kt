package com.nrlm.baselinesurvey.ui.surveyee_screen.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.ALL_TAB
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.THIS_WEEK_TAB
import com.nrlm.baselinesurvey.ui.common_components.DoubleButtonBox
import com.nrlm.baselinesurvey.ui.common_components.ToolbarWithMenuComponent
import com.nrlm.baselinesurvey.ui.common_components.common_events.EventWriterEvents
import com.nrlm.baselinesurvey.ui.description_component.presentation.ModelBottomSheetDescriptionContentComponent
import com.nrlm.baselinesurvey.ui.surveyee_screen.presentation.SurveyeeListScreenActions.CheckBoxClicked
import com.nrlm.baselinesurvey.ui.surveyee_screen.viewmodel.SurveyeeScreenViewModel
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.showCustomToast
import com.nrlm.baselinesurvey.utils.states.FilterListState
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.unmatchedOrangeColor
import com.nudge.navigationmanager.graphs.navigateToSectionListScreen
import com.nudge.navigationmanager.routes.Step_Complition_Screen_ROUTE_NAME
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SurveyeeListScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: SurveyeeScreenViewModel,
    missionId: Int,
    activityName: String,
    activityDate: String,
    activityId: Int
) {
    val context = LocalContext.current
    val scaffoldState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, skipHalfExpanded = false)
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel.init(missionId, activityName, activityId)
    }
    LaunchedEffect(viewModel.isEnableNextBTn.value) {
        if (viewModel.isEnableNextBTn.value && viewModel.filteredSurveyeeListState.value.isNotEmpty() && viewModel.activity?.status == SectionStatus.INPROGRESS.name) {
            scaffoldState.show()
        }
    }

    val loaderState = viewModel.loaderState.value

    val isFilterAppliedState = remember {
        mutableStateOf(FilterListState())
    }

    val isSelectionEnabled = remember {
        mutableStateOf(false)
    }


    var selectedTabIndex = remember { mutableIntStateOf(1) }

    val tabs = listOf(THIS_WEEK_TAB, ALL_TAB)

    val pullRefreshState = rememberPullRefreshState(
        loaderState.isLoaderVisible,
        {
            if (BaselineCore.isOnline.value) {
                viewModel.refreshData()
            } else {
                showCustomToast(
                    context,
                    context.getString(R.string.refresh_failed_please_try_again)
                )
            }

        })

    ModelBottomSheetDescriptionContentComponent(
        modifier = Modifier
            .fillMaxSize(),
        sheetContent = {
            Column(
                modifier = Modifier.padding(dimen_10_dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = stringResource(R.string.since_you_have_completed_all_the_tasks_please_complete_the_activity),
                        style = newMediumTextStyle.copy(color = blueDark)
                    )
                    IconButton(onClick = {
                        scope.launch {
                            scaffoldState.hide()
                        }
                    }, modifier = Modifier.size(48.dp)) {
                        Icon(
                            painter = painterResource(id = com.sarathi.dataloadingmangement.R.drawable.icon_close),
                            contentDescription = "Close",
                            tint = blueDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.on_completing_the_activity_you_will_not_be_able_to_edit_the_details),
                    style = newMediumTextStyle.copy(color = unmatchedOrangeColor)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimen_10_dp),
                ) {
                    if (viewModel.isEnableNextBTn.value && viewModel.filteredSurveyeeListState.value.isNotEmpty() && viewModel.activity?.status == SectionStatus.INPROGRESS.name) {

                        DoubleButtonBox(
                            modifier = Modifier
                                .shadow(10.dp),
                            positiveButtonText = stringResource(id = R.string.complete) + " " + activityName.replace(
                                oldValue = "Conduct ",
                                newValue = BLANK_STRING
                            ),
                            negativeButtonText = stringResource(id = R.string.go_back_text),
                            negativeButtonRequired = false,
                            isPositiveButtonActive = viewModel.isEnableNextBTn.value,
                            positiveButtonOnClick = {
                                viewModel.onEvent(
                                    SurveyeeListEvents.UpdateActivityAllTask(
                                        activityId,
                                        viewModel.isEnableNextBTn.value
                                    )
                                )

                                viewModel.onEvent(
                                    EventWriterEvents.UpdateActivityStatusEvent(
                                        missionId,
                                        activityId,
                                        SectionStatus.COMPLETED
                                    )
                                )
                                navController.navigate(
                                    "${Step_Complition_Screen_ROUTE_NAME}/${
                                        context.getString(
                                            R.string.activity_completed_message, activityName
                                        )
                                    }"
                                )
                                // navController.navigate("$SECTION_SCREEN_ROUTE_NAME/$didiId/$surveyId")
                            },
                            negativeButtonOnClick = {

                            }
                        )
                    }


                }
            }
        },
        sheetState = scaffoldState,
        sheetElevation = 20.dp,
        sheetBackgroundColor = Color.White,
        sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
    ) {
        ToolbarWithMenuComponent(
            title = activityName,
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            onBackIconClick = { navController.popBackStack() },
            onBottomUI = {
                if (viewModel.isEnableNextBTn.value && viewModel.filteredSurveyeeListState.value.isNotEmpty() && viewModel.activity?.status == SectionStatus.INPROGRESS.name) {

                    DoubleButtonBox(
                        modifier = Modifier
                            .shadow(10.dp),
                        positiveButtonText = stringResource(id = R.string.complete) + " " + activityName.replace(
                            oldValue = "Conduct ",
                            newValue = BLANK_STRING
                        ),
                        negativeButtonText = stringResource(id = R.string.go_back_text),
                        negativeButtonRequired = false,
                        isPositiveButtonActive = viewModel.isEnableNextBTn.value,
                        positiveButtonOnClick = {
                            viewModel.onEvent(
                                SurveyeeListEvents.UpdateActivityAllTask(
                                    activityId,
                                    viewModel.isEnableNextBTn.value
                                )
                            )

                            viewModel.onEvent(
                                EventWriterEvents.UpdateActivityStatusEvent(
                                    missionId,
                                    activityId,
                                    SectionStatus.COMPLETED
                                )
                            )
                            navController.navigate(
                                "${Step_Complition_Screen_ROUTE_NAME}/${
                                    context.getString(
                                        R.string.activity_completed_message, activityName
                                    )
                                }"
                            )
                            // navController.navigate("$SECTION_SCREEN_ROUTE_NAME/$didiId/$surveyId")
                        },
                        negativeButtonOnClick = {

                        }
                    )
                }
            },
            onContentUI = {
                AllSurveyeeListTab(
                    paddingValues = it,
                    loaderState = loaderState,
                    pullRefreshState = pullRefreshState,
                    viewModel = viewModel,
                    isSelectionEnabled = isSelectionEnabled,
                    navController = navController,
                    activityName = activityName,
                    activityDate = activityDate,
                    activityId = activityId,
                    onActionEvent = { surveyeeListScreenActions ->
                        when (surveyeeListScreenActions) {
                            is CheckBoxClicked -> {
                                if (surveyeeListScreenActions.isChecked) {
                                    viewModel.checkedItemsState.value.add(
                                        surveyeeListScreenActions.surveyeeEntity.didiId ?: -1
                                    )
                                } else {
                                    viewModel.checkedItemsState.value.remove(
                                        surveyeeListScreenActions.surveyeeEntity.didiId
                                    )
                                }
                            }

                            is SurveyeeListScreenActions.IsFilterApplied -> {
                                isFilterAppliedState.value = isFilterAppliedState.value.copy(
                                    isFilterApplied = surveyeeListScreenActions.isFilterAppliedState.isFilterApplied
                                )
                            }

                            else -> {}
                        }
                    }
                )
            })
    }

}

fun handleButtonClick(
    buttonName: ButtonName,
    surveyeeId: Int,
    surveyId: Int,
    navController: NavController,
    activityName: String = BLANK_STRING
) {
    when (buttonName) {
        is ButtonName.START_BUTTON -> {
            navController.navigateToSectionListScreen(surveyeeId, surveyId)
        }

        is ButtonName.CONTINUE_BUTTON -> {
            navController.navigateToSectionListScreen(surveyeeId, surveyId)
        }

        is ButtonName.NEGATIVE_BUTTON -> {

        }

        is ButtonName.SHOW_BUTTON -> {
            navController.navigateToSectionListScreen(surveyeeId, surveyId)
        }

        is ButtonName.EXPORT_BUTTON -> {

        }

        else -> {

        }
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SurveyeeListScreenPreview(
    modifier: Modifier = Modifier
) {
    // SurveyeeListScreen(navController = rememberNavController(), viewModel = hiltViewModel())
}
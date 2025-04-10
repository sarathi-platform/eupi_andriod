package com.nrlm.baselinesurvey.ui.surveyee_screen.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.ALL_TAB
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.THIS_WEEK_TAB
import com.nrlm.baselinesurvey.navigation.home.Step_Complition_Screen_ROUTE_NAME
import com.nrlm.baselinesurvey.navigation.home.navigateToSectionListScreen
import com.nrlm.baselinesurvey.ui.common_components.DoubleButtonBox
import com.nrlm.baselinesurvey.ui.common_components.ToolbarWithMenuComponent
import com.nrlm.baselinesurvey.ui.common_components.common_events.EventWriterEvents
import com.nrlm.baselinesurvey.ui.surveyee_screen.presentation.SurveyeeListScreenActions.CheckBoxClicked
import com.nrlm.baselinesurvey.ui.surveyee_screen.viewmodel.SurveyeeScreenViewModel
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.showCustomToast
import com.nrlm.baselinesurvey.utils.states.FilterListState
import com.nrlm.baselinesurvey.utils.states.SectionStatus

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


    LaunchedEffect(key1 = true) {
        viewModel.init(missionId, activityName, activityId)
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

    ToolbarWithMenuComponent(
        title = activityName,
        modifier = Modifier.fillMaxSize(),
        navController=navController,
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

fun handleButtonClick(
    buttonName: ButtonName,
    surveyeeId: Int,
    surveyId: Int,
    navController: NavController,
    activityName: String = BLANK_STRING
) {
    when (buttonName) {
        is ButtonName.START_BUTTON -> {
            navigateToSectionListScreen(surveyeeId, surveyId, navController)
        }

        is ButtonName.CONTINUE_BUTTON -> {
            navigateToSectionListScreen(surveyeeId, surveyId, navController)
        }

        is ButtonName.NEGATIVE_BUTTON -> {

        }

        is ButtonName.SHOW_BUTTON -> {
            navigateToSectionListScreen(surveyeeId, surveyId, navController)
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
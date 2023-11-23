package com.nrlm.baselinesurvey.ui.surveyee_screen.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nrlm.baselinesurvey.ALL_TAB
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.THIS_WEEK_TAB
import com.nrlm.baselinesurvey.navigation.home.SECTION_SCREEN_ROUTE_NAME
import com.nrlm.baselinesurvey.ui.common_components.PrimarySecandaryButtonBoxPreFilled
import com.nrlm.baselinesurvey.ui.surveyee_screen.presentation.SurveyeeListScreenActions.CheckBoxClicked
import com.nrlm.baselinesurvey.ui.surveyee_screen.viewmodel.SurveyeeScreenViewModel
import com.nrlm.baselinesurvey.ui.theme.defaultCardElevation
import com.nrlm.baselinesurvey.ui.theme.smallTextStyle
import com.nrlm.baselinesurvey.ui.theme.smallTextStyleWithNormalWeight
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.unselectedTabColor
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.states.FilterListState
import com.nrlm.baselinesurvey.utils.showCustomToast

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SurveyeeListScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: SurveyeeScreenViewModel
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.init()
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
                if (selectedTabIndex.intValue == tabs.indexOf(THIS_WEEK_TAB)) {
                    //Handle API Call
                } else {
                    //Handle API/DB Call
                }
            } else {
                showCustomToast(
                    context,
                    context.getString(R.string.refresh_failed_please_try_again)
                )
            }

        })

    Scaffold(
        topBar = {
            TabRow(
                selectedTabIndex = selectedTabIndex.intValue,
                containerColor = Color.White,
                contentColor = textColorDark,
                modifier = Modifier
                    .shadow(defaultCardElevation)
                    .zIndex(1f),
            ) {
                tabs.forEachIndexed { tabIndex, tab ->
                    Tab(
                        selected = tabIndex == selectedTabIndex.intValue,
                        onClick = { selectedTabIndex.intValue = tabIndex },
                        text = {
                            Text(
                                text = if (tab == THIS_WEEK_TAB) stringResource(R.string.this_week_tab_title) else stringResource(
                                    R.string.all_tab_title
                                ),
                                style = if (tabIndex == selectedTabIndex.intValue) smallTextStyle else smallTextStyleWithNormalWeight
                            )
                        },
                        selectedContentColor = textColorDark,
                        unselectedContentColor = unselectedTabColor
                    )
                }

            }
        },
        floatingActionButton = {
            if (!isSelectionEnabled.value && !loaderState.isLoaderVisible && selectedTabIndex.intValue != 0) {
                androidx.compose.material.FloatingActionButton(
                    onClick = {
                        isSelectionEnabled.value = true
                    },
                    backgroundColor = white
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_fab_convert_check_box),
                        contentDescription = ""
                    )
                }
            }
        },
        bottomBar = {
            if (isSelectionEnabled.value && !loaderState.isLoaderVisible && selectedTabIndex.intValue != 0) {
                PrimarySecandaryButtonBoxPreFilled(
                    modifier = Modifier,
                    primaryButtonText = stringResource(id = R.string.more_item_text),
                    secandaryButtonText = stringResource(id = R.string.cancel_tola_text),
                    secandaryButtonRequired = true,
                    primaryButtonOnClick = {
                        viewModel.onEvent(
                            SurveyeeListEvents.MoveDidisThisWeek(
                                viewModel.checkedItemsState.value,
                                true
                            )
                        )
                        isSelectionEnabled.value = false
                        viewModel.onEvent(SurveyeeListEvents.CancelAllSelection(isFilterAppliedState.value.isFilterApplied))
                    },
                    secandaryButtonOnClick = {
//                        isCancelBtnClick.value = true
                        isSelectionEnabled.value = false
                        viewModel.onEvent(SurveyeeListEvents.CancelAllSelection(isFilterAppliedState.value.isFilterApplied))
                    }
                )
            }

        },
        containerColor = white
    ) {

        when (selectedTabIndex.intValue) {
            0 -> {
                ThisWeekSurvyeeListTab(
                    paddingValues = it,
                    loaderState = loaderState,
                    pullRefreshState = pullRefreshState,
                    viewModel = viewModel,
                    navController = navController,
                    onActionEvent = { surveyeeListScreenActions ->

                    }
                )
            }

            1 -> {
                AllSurveyeeListTab(
                    paddingValues = it,
                    loaderState = loaderState,
                    pullRefreshState = pullRefreshState,
                    viewModel = viewModel,
                    isSelectionEnabled = isSelectionEnabled,
                    navController = navController,
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
                        }
                    }
                )
            }
        }
    }
}

fun handleButtonClick(buttonName: ButtonName, surveyeeId: Int, navController: NavController) {
    when (buttonName) {
        is ButtonName.START_BUTTON -> {
            navController.navigate("$SECTION_SCREEN_ROUTE_NAME/$surveyeeId")
        }

        is ButtonName.NEGATIVE_BUTTON -> {

        }

        is ButtonName.SHOW_BUTTON -> {

        }

        is ButtonName.EXPORT_BUTTON -> {

        }
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SurveyeeListScreenPreview(
    modifier: Modifier = Modifier
) {
    SurveyeeListScreen(navController = rememberNavController(), viewModel = hiltViewModel())
}
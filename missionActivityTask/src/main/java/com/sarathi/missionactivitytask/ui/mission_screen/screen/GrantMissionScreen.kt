package com.sarathi.missionactivitytask.ui.mission_screen.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.isOnline
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.theme.blueDark
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.ui.basic_content.component.BasicMissionCard
import com.sarathi.missionactivitytask.ui.components.SearchWithFilterViewComponent
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.missionactivitytask.ui.mission_screen.viewmodel.MissionScreenViewModel
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
import com.sarathi.missionactivitytask.utils.event.SearchEvent

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GrantMissionScreen(
    navController: NavController = rememberNavController(),
    viewModel: MissionScreenViewModel = hiltViewModel(),
    onSettingClick: () -> Unit,
    onNavigationToActivity: (isBaselineMission: Boolean, mission: MissionUiModel) -> Unit
) {
    val context = LocalContext.current
    val pullRefreshState = rememberPullRefreshState(
        viewModel.loaderState.value.isLoaderVisible,
        {
            if (isOnline(context)) {
                viewModel.refreshData()
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.refresh_failed_please_try_again),
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    LaunchedEffect(key1 = true) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.onEvent(InitDataEvent.InitDataState)
    }
    DisposableEffect(key1 = true) {
        onDispose {
            viewModel.onEvent(LoaderEvent.UpdateLoaderState(false))
        }
    }
    ToolBarWithMenuComponent(
        title = "SARATHI",
        modifier = Modifier.fillMaxSize(),
        iconResId = R.drawable.ic_sarathi_logo,
        navController = navController,
        onBackIconClick = { navController.popBackStack() },
        isSearch = true,
        isDataNotAvailable = viewModel.missionList.value.isEmpty() && !viewModel.loaderState.value.isLoaderVisible,
        onSearchValueChange = { searchedTerm ->
            viewModel.onEvent(SearchEvent.PerformSearch(searchedTerm, true))
        },
        onBottomUI = {
        },
        onRetry = {
            viewModel.refreshData()
        },
        onContentUI = { paddingValues, isSearch, onSearchValueChanged ->
            if (isSearch) {
                SearchWithFilterViewComponent(placeholderString = "Search",
                    filterSelected = false,
                    modifier = Modifier.padding(horizontal = 10.dp),
                    showFilter = false,
                    onFilterSelected = {},
                    onSearchValueChange = { queryTerm ->
                        onSearchValueChanged(queryTerm)
                    })
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState)
            )
            {
                PullRefreshIndicator(
                    refreshing = viewModel.loaderState.value.isLoaderVisible,
                    state = pullRefreshState,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .zIndex(1f),
                    contentColor = blueDark,
                )
                if (viewModel.filterMissionList.value.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn {
                        items(viewModel.filterMissionList.value) { mission ->
                            BasicMissionCard(
                                status = mission.missionStatus,
                                countStatusText = context.getString(R.string.activities_completed),
                                totalCount = mission.activityCount,
                                pendingCount = mission.pendingActivityCount,
                                title = mission.description,
                                needToShowProgressBar = true,
                                primaryButtonText = context.getString(R.string.start),
                                onPrimaryClick = {
                                    onNavigationToActivity(
                                        mission.description.contains(
                                            "Baseline",
                                            true
                                        ),
                                        mission
                                    ) //TODO handle navigation to activity based on mission.
                                    /*navigateToActivityScreen(
                                        navController,
                                        missionName = mission.description,
                                        missionId = mission.missionId,
                                        isMissionCompleted = mission.missionStatus == SurveyStatusEnum.COMPLETED.name
                                    )*/
                                }
                            )
                            CustomVerticalSpacer()
                        }
                        item {
                            CustomVerticalSpacer()
                        }
                    }
                }
            }

        },
        onSettingClick = onSettingClick
    )


}
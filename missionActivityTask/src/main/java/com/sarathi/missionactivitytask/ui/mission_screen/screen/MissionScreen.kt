package com.sarathi.missionactivitytask.ui.mission_screen.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.enums.TabsEnum
import com.nudge.core.isOnline
import com.nudge.core.model.FilterUiModel
import com.nudge.core.ui.ShowLoadingEffect
import com.nudge.core.ui.commonUi.CustomFixedCountSubTabLayoutWithCallBack
import com.nudge.core.ui.commonUi.CustomHorizontalSpacer
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.commonUi.FilterRowItem
import com.nudge.core.ui.commonUi.componet_.component.LoadingDataComponent
import com.nudge.core.ui.commonUi.customVerticalSpacer
import com.nudge.core.ui.events.CommonEvents
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_12_dp
import com.nudge.core.ui.theme.dimen_14_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_50_dp
import com.nudge.core.ui.theme.dimen_56_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.smallTextStyle
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.utils.CoreLogger
import com.sarathi.dataloadingmangement.NUMBER_ZERO
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel
import com.sarathi.dataloadingmangement.ui.component.ShowCustomDialog
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.MAT_MODULE
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.MISSION_SCREEN
import com.sarathi.missionactivitytask.navigation.navigateToApiFailedScreen
import com.sarathi.missionactivitytask.ui.basic_content.component.BasicMissionCardV2
import com.sarathi.missionactivitytask.ui.components.SearchWithFilterViewComponent
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.missionactivitytask.ui.mission_screen.viewmodel.MissionScreenViewModel
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
import com.sarathi.missionactivitytask.utils.event.SearchEvent
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MissionScreen(
    navController: NavController = rememberNavController(),
    viewModel: MissionScreenViewModel = hiltViewModel(),
    onSettingClick: () -> Unit,
    onBackPressed: () -> Unit,
    onNavigationToActivity: (isBaselineMission: Boolean, mission: MissionUiModel) -> Unit
) {
    val context = LocalContext.current

    val stateHandle = navController.currentBackStackEntry?.savedStateHandle
    val completedMissionId = remember { stateHandle?.getLiveData<Int>("missionId") }

    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val pullRefreshState = rememberPullRefreshState(
        viewModel.loaderState.value.isLoaderVisible,
        {
            if (isOnline(context)) {
                viewModel.refreshData()
            } else {
                Toast.makeText(
                    context,
                    viewModel.getString(context, R.string.refresh_failed_please_try_again),
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    LaunchedEffect(key1 = Unit) {
        //viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.onEvent(InitDataEvent.InitDataState)
    }
    DisposableEffect(key1 = true) {
        onDispose {
            viewModel.onEvent(LoaderEvent.UpdateLoaderState(false))
        }
    }
    LaunchedEffect(viewModel.filterMissionList.collectAsState(listOf()).value) {
        try {
            if (completedMissionId?.value != 0 && completedMissionId?.value != null && viewModel.filterMissionList.value.isNotEmpty()) {
                val index = viewModel.filterMissionList.value.map { it.missionId }
                    .indexOf(completedMissionId.value)
                if (index > 0) {
                    delay(1000)
                    lazyListState.animateScrollToItem(index)
                }
            }
        } catch (ex: Exception) {
            CoreLogger.e(
                tag = "MissionScreen",
                msg = "LaunchedEffect: ex -> ${ex.message}",
                ex = ex,
                stackTrace = true
            )
        }

    }

    DisposableEffect(key1 = LocalContext.current) {

        onDispose {
            completedMissionId?.value = NUMBER_ZERO
        }
    }

    val showAppExitDialog = remember {
        mutableStateOf(false)
    }
    val dataNotLoadedDialog = remember {
        mutableStateOf(false)
    }
    BackHandler {
        showAppExitDialog.value = true
    }

    if (showAppExitDialog.value) {
        ShowCustomDialog(
            title = viewModel.stringResource(context, R.string.are_you_sure),
            message = viewModel.stringResource(context, R.string.do_you_want_to_exit_the_app),
            positiveButtonTitle = viewModel.stringResource(context, R.string.exit),
            negativeButtonTitle = viewModel.stringResource(context, R.string.cancel),
            onNegativeButtonClick = {
                showAppExitDialog.value = false
            },
            onPositiveButtonClick = {
                onBackPressed()
            }
        )
    }
    if (dataNotLoadedDialog.value) {
        ShowCustomDialog(
            message = viewModel.stringResource(context, R.string.data_not_Loaded),
            positiveButtonTitle = viewModel.stringResource(context, R.string.ok),
            onNegativeButtonClick = {
                dataNotLoadedDialog.value = false
            },
            onPositiveButtonClick = {
                dataNotLoadedDialog.value = false
            }
        )
    }


    ToolBarWithMenuComponent(
        title = "SARATHI",
        modifier = Modifier.fillMaxSize(),
        iconResId = R.drawable.ic_sarathi_logo,
        navController = navController,
        onBackIconClick = { navController.popBackStack() },
        isSearch = true,
        isDataNotAvailable = viewModel.missionList.collectAsState(listOf()).value.isEmpty() && !viewModel.loaderState.value.isLoaderVisible,
        onSearchValueChange = { searchedTerm ->
            viewModel.onEvent(SearchEvent.PerformSearch(searchedTerm, true))
        },
        onBottomUI = {
        },
        onRetry = {
            viewModel.refreshData()
        },
        onContentUI = { paddingValues, isSearch, onSearchValueChanged ->
            LoadingDataComponent(
                title = "Mission Screen",
                apiStatus = viewModel.allApiStatus.value,
                progressState = viewModel.progressState,
                onViewDetailsClick = {
                    navigateToApiFailedScreen(
                        navController = navController,
                        screenName = MISSION_SCREEN,
                        moduleName = MAT_MODULE
                    )
                }
            )
            if (isSearch) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimen_5_dp),
                    verticalArrangement = Arrangement.spacedBy(dimen_14_dp)
                ) {
                    
                    Box(
                        modifier = Modifier
                            .padding(horizontal = dimen_10_dp)
                    ) {
                        CustomFixedCountSubTabLayoutWithCallBack(
                            parentTabIndex = TabsEnum.MissionTab.tabIndex,
                            tabs = viewModel.tabs,
                            countMap = viewModel.countMap
                        ) {
                            completedMissionId?.value = 0
                            viewModel.onEvent(CommonEvents.OnSubTabChanged)
                        }
                    }

                    SearchWithFilterViewComponent(
                        placeholderString = viewModel.stringResource(
                            context,
                            R.string.search_by_mission
                        ),
                        filterSelected = false,
                        modifier = Modifier.padding(horizontal = dimen_10_dp),
                        showFilter = false,
                        onFilterSelected = {},
                        onSearchValueChange = { queryTerm ->
                            onSearchValueChanged(queryTerm)
                        }
                    )
                }

                LazyRow(
                    modifier = Modifier
                        .padding(horizontal = dimen_12_dp)
                ) {
                    itemsIndexed(viewModel.missionFilterUtils.getMissionFiltersList()) { index: Int, item: FilterUiModel ->
                        FilterRowItem(
                            item = item,
                            selectedItem = viewModel.missionFilterUtils.getSelectedMissionFilterValue()
                        ) {
                            viewModel.onEvent(CommonEvents.OnFilterUiModelSelected(item))
                        }
                        CustomHorizontalSpacer()
                    }
                }

            }
            if (viewModel.loaderState.value.isLoaderVisible && viewModel.filterMissionList.collectAsState().value.isEmpty()) {
                ShowLoadingEffect()
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pullRefresh(pullRefreshState)
                ) {
                    PullRefreshIndicator(
                        refreshing = viewModel.loaderState.value.isLoaderVisible,
                        state = pullRefreshState,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .zIndex(1f),
                        contentColor = blueDark,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.padding(bottom = dimen_50_dp)
                    ) {

                        customVerticalSpacer()

                        item {
                            Text(
                                text = viewModel.filteredListLabel.value,
                                style = smallTextStyle.copy(color = textColorDark),
                                modifier = Modifier.padding(horizontal = dimen_16_dp)
                            )
                        }

                        customVerticalSpacer()

                        items(viewModel.filterMissionList.value) { mission ->
                            BasicMissionCardV2(
                                status = mission.missionStatus,
                                filterUiModel = viewModel.getFilterUiModelForMission(mission.programLivelihoodReferenceId),
                                totalCount = mission.activityCount,
                                pendingCount = mission.pendingActivityCount,
                                title = mission.description,
                                needToShowProgressBar = true,
                                livelihoodType = mission.livelihoodType,
                                livelihoodOrder = mission.livelihoodOrder,
                                primaryButtonText = viewModel.getString(context, R.string.start),
                                onPrimaryClick = {
                                    completedMissionId?.value = 0
                                    viewModel.isMissionLoaded(
                                        missionId = mission.missionId,
                                        programId = mission.programId,
                                        onComplete = { isDataLoaded ->
                                            if (!isDataLoaded && !isOnline(context = context)) {
                                                dataNotLoadedDialog.value = true
                                            } else {
                                                onNavigationToActivity(
                                                    viewModel.isBaselineV1Mission(mission.description), //TODO Temp code to be removed after data is fetched from server.
                                                    mission
                                                )
                                            }

                                        })
                                }
                            )
                            CustomVerticalSpacer()
                        }

                        customVerticalSpacer(size = dimen_56_dp)

                    }
                }
            }
        },
        onSettingClick = onSettingClick
    )


}

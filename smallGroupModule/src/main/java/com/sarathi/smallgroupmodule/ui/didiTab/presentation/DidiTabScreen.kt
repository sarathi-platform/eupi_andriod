package com.sarathi.smallgroupmodule.ui.didiTab.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nudge.core.TabsCore
import com.nudge.core.enums.SubTabs
import com.nudge.core.enums.TabsEnum
import com.nudge.core.isOnline
import com.nudge.core.showCustomToast
import com.nudge.core.ui.commonUi.CustomSubTabLayout
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.events.CommonEvents
import com.nudge.core.ui.theme.blueDark
import com.sarathi.dataloadingmangement.ui.component.ShowCustomDialog
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.missionactivitytask.ui.components.SearchWithFilterViewComponent
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
import com.sarathi.smallgroupmodule.R
import com.sarathi.smallgroupmodule.ui.didiTab.viewModel.DidiTabViewModel
import com.sarathi.smallgroupmodule.ui.smallGroupSubTab.presentation.SmallGroupSubTab
import com.sarathi.smallgroupmodule.ui.theme.dimen_10_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_16_dp
import com.sarathi.dataloadingmangement.R as DataLoadingRes
import com.sarathi.smallgroupmodule.R as Res

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DidiTabScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    didiTabViewModel: DidiTabViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    onSettingClicked: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        didiTabViewModel.onEvent(InitDataEvent.InitDataState)
    }

    val showAppExitDialog = remember {
        mutableStateOf(false)
    }

    BackHandler {
        showAppExitDialog.value = true
    }

    if (showAppExitDialog.value) {
        ShowCustomDialog(
            title = stringResource(id = com.sarathi.missionactivitytask.R.string.are_you_sure),
            message = stringResource(id = com.sarathi.missionactivitytask.R.string.do_you_want_to_exit_the_app),
            positiveButtonTitle = stringResource(id = com.sarathi.missionactivitytask.R.string.exit),
            negativeButtonTitle = stringResource(id = com.sarathi.missionactivitytask.R.string.cancel),
            onNegativeButtonClick = {
                showAppExitDialog.value = false
            },
            onPositiveButtonClick = {
                onBackPressed()
            }
        )
    }


    val pullToRefreshState = rememberPullRefreshState(
        refreshing = didiTabViewModel.loaderState.value.isLoaderVisible,
        onRefresh = {
            if (isOnline(context)) {
                didiTabViewModel.refreshData()
            } else {
                showCustomToast(
                    context,
                    context.getString(com.sarathi.missionactivitytask.R.string.refresh_failed_please_try_again)
                )
            }
        }
    )

    DisposableEffect(key1 = true) {
        onDispose {
            didiTabViewModel.onEvent(LoaderEvent.UpdateLoaderState(false))
        }
    }

    val isSearchActive = remember {
        mutableStateOf(false)
    }

    val didiList = didiTabViewModel.filteredDidiList

    val tabs = listOf(SubTabs.DidiTab, SubTabs.SmallGroupTab)

    ToolBarWithMenuComponent(
        title = stringResource(id = DataLoadingRes.string.app_name),
        dataNotLoadMsg = if (didiTabViewModel.isSubjectApiStatusFailed.value) stringResource(
            R.string.not_able_to_load
        ) else stringResource(R.string.no_didi_s_assigned_to_you),
        modifier = modifier,
        isSearch = true,
        iconResId = Res.drawable.ic_sarathi_logo,
        onBackIconClick = { /*TODO*/ },
        isDataNotAvailable = (didiList.value.isEmpty() && !isSearchActive.value && !didiTabViewModel
            .loaderState.value.isLoaderVisible),
        onSearchValueChange = {

        },
        onBottomUI = {
            /**
             *Not required as no bottom UI present for this screen
             **/
        },
        onSettingClick = {
            onSettingClicked()
        },
        onRetry = {},
        onContentUI = { paddingValues, b, function ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullToRefreshState)
            ) {
                PullRefreshIndicator(
                    refreshing = didiTabViewModel.loaderState.value.isLoaderVisible,
                    state = pullToRefreshState,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .zIndex(1f),
                    contentColor = blueDark,
                )
                if (didiList.value.isNotEmpty() || didiTabViewModel.filteredSmallGroupList.value.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = dimen_16_dp)
                            .padding(top = dimen_10_dp),
                        verticalArrangement = Arrangement.spacedBy(dimen_10_dp)
                    ) {

                        CustomSubTabLayout(
                            parentTabIndex = TabsEnum.DidiUpcmTab.tabIndex,
                            tabs,
                            didiTabViewModel.countMap
                        )

                        Column {
                            SearchWithFilterViewComponent(
                                placeholderString = when (TabsCore.getSubTabForTabIndex(TabsEnum.DidiUpcmTab.tabIndex)) {
                                    SubTabs.DidiTab.id -> stringResource(R.string.search_didi)
                                    SubTabs.SmallGroupTab.id -> stringResource(R.string.search_by_small_groups)
                                    else -> stringResource(R.string.search_didi)
                                },
                                showFilter = false,
                                onFilterSelected = {

                                },
                                onSearchValueChange = { searchQuery ->
                                    isSearchActive.value = searchQuery.isNotEmpty()
                                    didiTabViewModel.onEvent(
                                        CommonEvents.SearchValueChangedEvent(
                                            searchQuery,
                                            TabsCore.getSubTabForTabIndex(TabsEnum.DidiUpcmTab.tabIndex)
                                        )
                                    )
                                }
                            )
                            CustomVerticalSpacer()
                            when (TabsCore.getSubTabForTabIndex(TabsEnum.DidiUpcmTab.tabIndex)) {
                                SubTabs.DidiTab.id -> {
                                    DidiSubTab(
                                        didiTabViewModel = didiTabViewModel,
                                        didiList = didiList.value
                                    )
                                }

                                SubTabs.SmallGroupTab.id -> SmallGroupSubTab(
                                    didiTabViewModel = didiTabViewModel,
                                    smallGroupList = didiTabViewModel.filteredSmallGroupList.value,
                                    navHostController = navHostController
                                )
                            }
                        }
                    }
                }
            }
        }
    )

}




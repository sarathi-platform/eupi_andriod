package com.sarathi.smallgroupmodule.ui.didiTab.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nudge.core.BLANK_STRING
import com.nudge.core.TabsCore
import com.nudge.core.enums.SubTabs
import com.nudge.core.enums.TabsEnum
import com.nudge.core.isOnline
import com.nudge.core.showCustomToast
import com.nudge.core.ui.commonUi.CustomSubTabLayout
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.commonUi.componet_.component.ButtonPositive
import com.nudge.core.ui.events.CommonEvents
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_40_dp
import com.nudge.core.ui.theme.dimen_56_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.eventTextColor
import com.nudge.core.ui.theme.greenOnline
import com.nudge.core.ui.theme.greyBorderColor
import com.nudge.core.ui.theme.mediumTextStyle
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.white
import com.nudge.core.value
import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.dataloadingmangement.ui.component.ShowCustomDialog
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.missionactivitytask.ui.components.SearchWithFilterViewComponent
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
import com.sarathi.smallgroupmodule.R
import com.sarathi.smallgroupmodule.navigation.navigateToShgVerificationScreen
import com.sarathi.smallgroupmodule.ui.commonUi.ButtonOutline
import com.sarathi.smallgroupmodule.ui.didiTab.viewModel.DidiTabViewModel
import com.sarathi.smallgroupmodule.ui.smallGroupSubTab.presentation.SmallGroupSubTab
import com.sarathi.smallgroupmodule.ui.theme.dimen_10_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_16_dp
import kotlinx.coroutines.launch
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
            title = didiTabViewModel.stringResource(
                R.string.are_you_sure
            ),
            message = didiTabViewModel.stringResource(
                R.string.do_you_want_to_exit_the_app
            ),
            positiveButtonTitle = didiTabViewModel.stringResource(
                R.string.exit
            ),
            negativeButtonTitle = didiTabViewModel.stringResource(
                R.string.cancel
            ),
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
                    didiTabViewModel.stringResource(
                        R.string.refresh_failed_please_try_again
                    )
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

    val scaffoldState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, skipHalfExpanded = true)

    val coroutineScope = rememberCoroutineScope()

    val tabs = listOf(SubTabs.DidiTab, SubTabs.SmallGroupTab)
    /**
     *Not required as no bottom UI present for this screen
     **/
    ModalBottomSheetLayout(
        sheetState = scaffoldState,
        sheetContent = {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        modifier = Modifier
                            .fillMaxWidth(),
                        backgroundColor = white
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = dimen_16_dp)
                        ) {
                            Text(
                                didiTabViewModel.getString(R.string.filters_title),
                                style = mediumTextStyle.copy(textColorDark)
                            )
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        scaffoldState.hide()
                                    }
                                },
                                modifier = Modifier
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "close Button",
                                    tint = blueDark
                                )
                            }
                        }
                    }
                },
                bottomBar = {
                    Box(
                        modifier = Modifier
                            .padding(bottom = dimen_56_dp)
                    ) {
                        BottomAppBar(
                            Modifier
                                .fillMaxWidth(), backgroundColor = white
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()

                                    .padding(horizontal = dimen_16_dp),
                                horizontalArrangement = Arrangement.spacedBy(dimen_8_dp)
                            ) {
                                ButtonOutline(
                                    modifier = Modifier
                                        .weight(1.0f)
                                        .fillMaxWidth()
                                        .height(dimen_40_dp),
                                    buttonTitle = didiTabViewModel.getString(R.string.clear_button_label),
                                    icon = null,
                                    borderColor = eventTextColor
                                ) {
                                    didiTabViewModel.onEvent(
                                        CommonEvents.OnVerificationStatusFilterSelected(
                                            null
                                        )
                                    )
                                }
                                ButtonPositive(
                                    modifier = Modifier.weight(1f),
                                    buttonTitle = didiTabViewModel.getString(R.string.apply_button_label),
                                    isActive = true,
                                    isArrowRequired = false,
                                    onClick = {
                                        didiTabViewModel.onEvent(
                                            CommonEvents.OnVerificationFilterApplied(
                                                didiTabViewModel.selectedFilters.toList()
                                            )
                                        )
                                        coroutineScope.launch {
                                            scaffoldState.hide()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            ) { it ->
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(top = it.calculateTopPadding())
                        .padding(com.nudge.core.ui.theme.dimen_16_dp),
                    verticalArrangement = Arrangement.spacedBy(dimen_8_dp)
                ) {
                    SearchWithFilterViewComponent(
                        placeholderString = didiTabViewModel.getString(R.string.search_filter_placeholder),
                        showFilter = false,
                        onFilterSelected = {},
                        onSearchValueChange = { searchQuery ->
                            isSearchActive.value = searchQuery.isNotEmpty()
                            didiTabViewModel.onEvent(
                                CommonEvents.SearchValueChangedEvent(
                                    searchQuery,
                                    -1
                                )
                            )
                        }
                    )
                    CustomVerticalSpacer()
                    didiTabViewModel.finalFilterValue.value.forEach { filterItem ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    didiTabViewModel.onEvent(
                                        CommonEvents.OnVerificationStatusFilterSelected(
                                            filterItem
                                        )
                                    )
                                },

                            ) {
                            Text(filterItem.value, style = newMediumTextStyle.copy(textColorDark))
                            Checkbox(
                                checked = didiTabViewModel.selectedFilters.contains(filterItem),
                                onCheckedChange = {
                                    didiTabViewModel.onEvent(
                                        CommonEvents.OnVerificationStatusFilterSelected(
                                            filterItem
                                        )
                                    )
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = greenOnline,
                                    checkmarkColor = white,
                                    uncheckedColor = greyBorderColor
                                )
                            )
                        }
                    }
                }
            }
        }
    ) {
        ToolBarWithMenuComponent(
            title = didiTabViewModel.stringResource(
                DataLoadingRes.string.app_name
            ),
            dataNotLoadMsg = if (didiTabViewModel.isSubjectApiStatusFailed.value)
                didiTabViewModel.stringResource(
                    R.string.not_able_to_load
                ) else
                didiTabViewModel.stringResource(
                    R.string.no_didi_s_assigned_to_you
                ),
            modifier = modifier,
            isSearch = true,
            iconResId = Res.drawable.ic_sarathi_logo,
            onBackIconClick = { /*TODO*/ },
            isDataNotAvailable = (didiList.value.isEmpty() && didiTabViewModel.filteredSmallGroupList.value.isEmpty() && !isSearchActive.value && !didiTabViewModel
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

                    showNoResultUI(
                        didiTabViewModel,
                        didiList,
                        Modifier.align(Alignment.Center),
                        isSearchActive.value
                    )
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
                                    SubTabs.DidiTab.id -> didiTabViewModel.stringResource(
                                        R.string.search_didi
                                    )

                                    SubTabs.SmallGroupTab.id ->
                                        didiTabViewModel.stringResource(
                                            R.string.search_by_small_groups
                                        )

                                    else -> didiTabViewModel.stringResource(
                                        R.string.search_didi
                                    )

                                },
                                filterIconSelected = R.drawable.filter_active_icon,
                                filterIconUnSelected = R.drawable.filter_icon,
                                filterSelected = didiTabViewModel.selectedFilters.isNotEmpty(),
                                showFilter = TabsCore.getSubTabForTabIndex(TabsEnum.DidiUpcmTab.tabIndex) == SubTabs.DidiTab.id,
                                onFilterSelected = {
                                    coroutineScope.launch {
                                        scaffoldState.show()
                                    }
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
                            if (didiList.value.isNotEmpty() || didiTabViewModel.filteredSmallGroupList.value.isNotEmpty()) {
                                when (TabsCore.getSubTabForTabIndex(TabsEnum.DidiUpcmTab.tabIndex)) {
                                    SubTabs.DidiTab.id -> {
                                        DidiSubTab(
                                            didiTabViewModel = didiTabViewModel,
                                            didiList = didiList.value,
                                            onShgVerifyClick = {
                                                navHostController.navigateToShgVerificationScreen(
                                                    it.subjectId.value(),
                                                    it.subjectName,
                                                    it.villageName
                                                )
                                            }
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
}

@Composable
private fun showNoResultUI(
    didiTabViewModel: DidiTabViewModel,
    didiList: State<List<SubjectEntity>>,
    modifier: Modifier,
    isSearchActive: Boolean
) {
    val validationMessage = validateAndMessage(didiTabViewModel, didiList, isSearchActive)

    if (didiTabViewModel.isSearchListEmpty.value && validationMessage.isNotEmpty()) {
        if (!didiTabViewModel.loaderState.value.isLoaderVisible) {
            Text(
                text = validationMessage,
                style = defaultTextStyle,
                color = textColorDark,
                modifier = modifier
            )
        }
    }
}


private fun validateAndMessage(
    didiTabViewModel: DidiTabViewModel,
    didiList: State<List<SubjectEntity>>,
    isSearchActive: Boolean
): String {
    val subTabId = TabsCore.getSubTabForTabIndex(TabsEnum.DidiUpcmTab.tabIndex)

    val isListEmpty = when (subTabId) {
        SubTabs.DidiTab.id -> didiList.value.isEmpty()
        SubTabs.SmallGroupTab.id -> didiTabViewModel.filteredSmallGroupList.value.isEmpty()
        else -> return BLANK_STRING
    }

    didiTabViewModel.isSearchListEmpty.value = isListEmpty

    return if (isListEmpty && isSearchActive) {
        didiTabViewModel.getString(R.string.no_result_found)
    } else if (didiTabViewModel.selectedFilters.isNotEmpty()) {
        "No result for selected filter"
    } else {
        when (subTabId) {
            SubTabs.DidiTab.id -> didiTabViewModel.getString(R.string.no_didi_s_assigned_to_you)
            SubTabs.SmallGroupTab.id -> didiTabViewModel.getString(R.string.no_small_group_assgned_label)
            else -> BLANK_STRING
        }
    }
}





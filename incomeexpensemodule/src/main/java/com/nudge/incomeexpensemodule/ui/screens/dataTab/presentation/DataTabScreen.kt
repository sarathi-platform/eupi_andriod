package com.nudge.incomeexpensemodule.ui.screens.dataTab.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.incomeexpensemodule.R
import com.nudge.core.enums.SubTabs
import com.nudge.core.enums.TabsEnum
import com.nudge.core.isOnline
import com.nudge.core.model.uiModel.LivelihoodModel
import com.nudge.core.showCustomToast
import com.nudge.core.ui.commonUi.BottomSheetScaffoldComponent
import com.nudge.core.ui.commonUi.CustomIconButton
import com.nudge.core.ui.commonUi.CustomSubTabLayout
import com.nudge.core.ui.commonUi.CustomTextViewComponent
import com.nudge.core.ui.commonUi.SimpleSearchComponent
import com.nudge.core.ui.commonUi.TextProperties
import com.nudge.core.ui.commonUi.ToolBarWithMenuComponent
import com.nudge.core.ui.commonUi.rememberCustomBottomSheetScaffoldProperties
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_50_dp
import com.nudge.core.ui.theme.dimen_56_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.white
import com.nudge.core.value
import com.nudge.incomeexpensemodule.ui.SubjectLivelihoodEventSummaryCard
import com.nudge.incomeexpensemodule.ui.screens.dataTab.viewModel.DataTabScreenViewModel
import com.sarathi.dataloadingmangement.ui.component.ShowCustomDialog
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DataTabScreen(
    modifier: Modifier = Modifier,
    dataTabScreenViewModel: DataTabScreenViewModel,
    navHostController: NavHostController,
    onBackPressed: () -> Unit,
    onSettingClicked: () -> Unit
) {

    val context = LocalContext.current

    val customBottomSheetScaffoldProperties = rememberCustomBottomSheetScaffoldProperties()

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        dataTabScreenViewModel.onEvent(InitDataEvent.InitDataState)
    }

    val showAppExitDialog = remember {
        mutableStateOf(false)
    }

    BackHandler {
        showAppExitDialog.value = true
    }

    if (showAppExitDialog.value) {
        ShowCustomDialog(
            title = stringResource(id = R.string.are_you_sure),
            message = stringResource(id = R.string.do_you_want_to_exit_the_app),
            positiveButtonTitle = stringResource(id = R.string.exit),
            negativeButtonTitle = stringResource(id = R.string.cancel),
            onNegativeButtonClick = {
                showAppExitDialog.value = false
            },
            onPositiveButtonClick = {
                onBackPressed()
            }
        )
    }

    val pullToRefreshState = rememberPullRefreshState(
        refreshing = dataTabScreenViewModel.loaderState.value.isLoaderVisible,
        onRefresh = {
            if (isOnline(context)) {
                dataTabScreenViewModel.refreshData()
            } else {
                showCustomToast(
                    context,
                    context.getString(R.string.refresh_failed_please_try_again)
                )
            }
        }
    )

    DisposableEffect(key1 = true) {
        onDispose {
            dataTabScreenViewModel.onEvent(LoaderEvent.UpdateLoaderState(false))
        }
    }

    val isSearchActive = remember {
        mutableStateOf(false)
    }

    val tabs = listOf<SubTabs>(SubTabs.All, SubTabs.NoEntryMonthTab, SubTabs.NoEntryWeekTab)


    Surface(modifier = Modifier.padding(bottom = dimen_56_dp)) {
        BottomSheetScaffoldComponent<LivelihoodModel>(
            bottomSheetScaffoldProperties = customBottomSheetScaffoldProperties,
            bottomSheetContentItemList = dataTabScreenViewModel.filters.toList(),
            onBottomSheetItemSelected = {

            }
        ) {
            ToolBarWithMenuComponent(
                title = stringResource(id = com.sarathi.dataloadingmangement.R.string.app_name),
                modifier = modifier,
                isSearch = true,
                iconResId = R.drawable.ic_sarathi_logo,
                onBackIconClick = { /*TODO*/ },
                isDataNotAvailable = (false && !isSearchActive.value && !dataTabScreenViewModel.loaderState.value.isLoaderVisible),
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
                            refreshing = dataTabScreenViewModel.loaderState.value.isLoaderVisible,
                            state = pullToRefreshState,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .zIndex(1f),
                            contentColor = blueDark,
                        )

                        if (true) {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = dimen_16_dp)
                                    .padding(top = dimen_10_dp),
                                verticalArrangement = Arrangement.spacedBy(dimen_10_dp)
                            ) {

                                CustomSubTabLayout(
                                    parentTabIndex = TabsEnum.DataTab.tabIndex,
                                    tabs = tabs,
                                    dataTabScreenViewModel.countMap
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(dimen_8_dp)
                                ) {

                                    SimpleSearchComponent(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f),
                                        placeholderString = "Search by didis", //TODO pick this from string file with translations
                                        searchFieldHeight = dimen_50_dp,
                                        onSearchValueChange = {
                                            //TODO Handle search
                                        }
                                    )

                                    CustomIconButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                customBottomSheetScaffoldProperties.sheetState.show()
                                            }
                                        },
                                        icon = painterResource(id = R.drawable.filter_icon),
                                        iconTintColor = if (dataTabScreenViewModel.isFilterApplied.value) white else blueDark,
                                        contentDescription = "filter_list",
                                        colors = IconButtonDefaults.iconButtonColors(
                                            containerColor = if (dataTabScreenViewModel.isFilterApplied.value) blueDark else Color.Transparent,
                                            contentColor = if (dataTabScreenViewModel.isFilterApplied.value) white else blueDark
                                        )
                                    )

                                    CustomIconButton(
                                        onClick = { /*TODO*/ },
                                        icon = painterResource(id = R.drawable.sort_icon),
                                        contentDescription = "Sort List"
                                    )

                                }

                                if (dataTabScreenViewModel.isFilterApplied.value) {
                                    CustomTextViewComponent(
                                        textProperties = TextProperties.getBasicTextProperties(
                                            text = buildAnnotatedString {
                                                //TODO write function to get this string for the filter applied.
                                                append("Showing ")
                                                withStyle(
                                                    SpanStyle(
                                                        color = textColorDark,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                ) {
                                                    append(dataTabScreenViewModel.filteredSubjectList.value.size.toString())
                                                }
                                                append(" results for ")
                                                withStyle(
                                                    SpanStyle(
                                                        color = textColorDark,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                ) {
                                                    append(
                                                        dataTabScreenViewModel.filters.toList()
                                                            .find { it.id == dataTabScreenViewModel.selectedFilterValue.value }?.name.value()
                                                    )
                                                }
                                            }
                                        )
                                    )
                                }

                                LazyColumn(verticalArrangement = Arrangement.spacedBy(dimen_8_dp)) {

                                    itemsIndexed(dataTabScreenViewModel.filteredSubjectList.value) { index, subject ->
                                        SubjectLivelihoodEventSummaryCard(
                                            subjectId = subject.subjectId!!,
                                            name = subject.subjectName,
                                            address = subject.houseNo + ", " + subject.cohortName,
                                            location = subject.villageName,
                                            lastUpdated = "",
                                            income = "",
                                            expense = "",
                                            assetValue = "",
                                            onAssetValueItemClicked = {

                                            }
                                        ) {

                                        }
                                    }

                                }

                            }
                        }

                    }

                }
            )
        }
    }


}
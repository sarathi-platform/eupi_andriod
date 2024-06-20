package com.sarathi.smallgroupmodule.ui.didiTab.presentation

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nudge.core.ui.events.CommonEvents
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.missionactivitytask.ui.components.CustomVerticalSpacer
import com.sarathi.missionactivitytask.ui.components.SearchWithFilterViewComponent
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.smallgroupmodule.R
import com.sarathi.smallgroupmodule.SmallGroupCore
import com.sarathi.smallgroupmodule.ui.TabItem
import com.sarathi.smallgroupmodule.ui.didiTab.viewModel.DidiTabViewModel
import com.sarathi.smallgroupmodule.ui.smallGroupSubTab.presentation.SmallGroupSubTab
import com.sarathi.smallgroupmodule.ui.theme.dimen_10_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_16_dp
import com.sarathi.dataloadingmangement.R as DataLoadingRes
import com.sarathi.smallgroupmodule.R as Res

@Composable
fun DidiTabScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    didiTabViewModel: DidiTabViewModel = hiltViewModel(),
    onSettingClicked: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {

        didiTabViewModel.onEvent(InitDataEvent.InitDataState)

    }

    val isSearchActive = remember {
        mutableStateOf(false)
    }

    val didiList = didiTabViewModel.filteredDidiList

    val tabs = listOf(DidiSubTabsEnum.DidiTab, DidiSubTabsEnum.SmallGroupTab)

    ToolBarWithMenuComponent(
        title = stringResource(id = DataLoadingRes.string.app_name),
        modifier = modifier,
        isSearch = true,
        iconResId = Res.drawable.ic_sarathi_logo,
        onBackIconClick = { /*TODO*/ },
        isDataAvailable = (didiList.value.isEmpty() && !isSearchActive.value),
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
            Column(
                modifier = Modifier
                    .padding(horizontal = dimen_16_dp)
                    .padding(top = dimen_16_dp),
                verticalArrangement = Arrangement.spacedBy(dimen_10_dp)
            ) {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimen_10_dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {

                    tabs.forEachIndexed { index, tab ->

                        val count = getCount(index, didiTabViewModel)
                        val tabName = getTabName(context, tab)
                        TabItem(
                            isSelected = SmallGroupCore.tabIndex.value == index,
                            onClick = {
                                SmallGroupCore.tabIndex.value = index
                            },
                            text = "$tabName ($count)"
                        )
                    }

                }

                Column {
                    SearchWithFilterViewComponent(
                        placeholderString = when (SmallGroupCore.tabIndex.value) {
                            DidiSubTabsEnum.DidiTab.id -> "Search by didis"
                            DidiSubTabsEnum.SmallGroupTab.id -> "Search by small groups"
                            else -> "Search by didis"
                        },
                        showFilter = false,
                        onFilterSelected = {

                        },
                        onSearchValueChange = { searchQuery ->
                            isSearchActive.value = searchQuery.isNotEmpty()
                            didiTabViewModel.onEvent(
                                CommonEvents.SearchValueChangedEvent(
                                    searchQuery,
                                    (SmallGroupCore.tabIndex.value as Int)
                                )
                            )
                        }
                    )
                    CustomVerticalSpacer()
                    when (SmallGroupCore.tabIndex.value) {
                        DidiSubTabsEnum.DidiTab.id -> {
                            DidiSubTab(
                                didiTabViewModel = didiTabViewModel,
                                didiList = didiList.value
                            )
                        }

                        DidiSubTabsEnum.SmallGroupTab.id -> SmallGroupSubTab(
                            didiTabViewModel = didiTabViewModel,
                            smallGroupList = didiTabViewModel.filteredSmallGroupList.value,
                            navHostController = navHostController
                        )
                    }
                }


            }
        }
    )

}

fun getTabName(context: Context, tab: DidiSubTabsEnum): String {
    return when (tab) {
        DidiSubTabsEnum.DidiTab -> context.getString(R.string.didi_sub_tab_title)
        DidiSubTabsEnum.SmallGroupTab -> context.getString(R.string.small_group_sub_tab_title)
    }
}

fun getCount(tabIndex: Int, didiTabViewModel: DidiTabViewModel): Int {
    return when (tabIndex) {
        DidiSubTabsEnum.DidiTab.id -> didiTabViewModel.totalCount.value
        DidiSubTabsEnum.SmallGroupTab.id -> didiTabViewModel.totalSmallGroupCount.value
        else -> {
            0
        }
    }
}

enum class DidiSubTabsEnum(val id: Int) {
    DidiTab(0),
    SmallGroupTab(1)

}
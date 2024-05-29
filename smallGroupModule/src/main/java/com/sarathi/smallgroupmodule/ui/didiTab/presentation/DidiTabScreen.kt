package com.sarathi.smallgroupmodule.ui.didiTab.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
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
    didiTabViewModel: DidiTabViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {

        didiTabViewModel.onEvent(InitDataEvent.InitDataState)

    }

    val didiList = didiTabViewModel.didiList

    val tabs = listOf("Didi", "Small Group")
    var tabIndex by remember { mutableStateOf(0) }


    ToolBarWithMenuComponent(
        title = stringResource(id = DataLoadingRes.string.app_name),
        modifier = Modifier,
        isSearch = true,
        iconResId = Res.drawable.ic_sarathi_logo,
        onBackIconClick = { /*TODO*/ },
        isDataAvailable = didiList.value.isNotEmpty(),
        isFilterSelected = {},
        onSearchValueChange = {

        },
        onBottomUI = { /*TODO*/ },
        tabBarView = {


        }
    ) {

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

                    val isSelected = remember {
                        derivedStateOf {
                            tabIndex == index
                        }
                    }

                    val count = getCount(index, didiTabViewModel)
                    TabItem(
                        isSelected = isSelected.value,
                        onClick = {
                            tabIndex = index
                        },
                        text = tab + " ($count)"
                    )
                }

            }

            when (tabIndex) {
                0 -> DidiSubTab(didiTabViewModel = didiTabViewModel, didiList = didiList.value)
                1 -> SmallGroupSubTab(
                    didiTabViewModel = didiTabViewModel,
                    smallGroupList = didiTabViewModel.smallGroupList.value,
                    navHostController = navHostController
                )
            }

        }

    }

}

fun getCount(tabIndex: Int, didiTabViewModel: DidiTabViewModel): Int {
    return when (tabIndex) {
        0 -> didiTabViewModel.totalCount.value
        1 -> didiTabViewModel.totalSmallGroupCount.value
        else -> {
            0
        }
    }
}
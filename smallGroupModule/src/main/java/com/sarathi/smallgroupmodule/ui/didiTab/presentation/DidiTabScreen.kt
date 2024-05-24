package com.sarathi.smallgroupmodule.ui.didiTab.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.sarathi.missionactivitytask.ui.components.CustomVerticalSpacer
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.smallgroupmodule.ui.CustomTabLayout
import com.sarathi.smallgroupmodule.ui.didiTab.viewModel.DidiTabViewModel
import com.sarathi.smallgroupmodule.ui.didiTab.viewModel.TestClass
import com.sarathi.smallgroupmodule.ui.theme.blueDark
import com.sarathi.smallgroupmodule.ui.theme.dimen_10_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_16_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_8_dp
import com.sarathi.dataloadingmangement.R as DataLoadingRes
import com.sarathi.missionactivitytask.R as MatRes
import com.sarathi.smallgroupmodule.R as Res

@Composable
fun DidiTabScreen(
    modifier: Modifier = Modifier,
    didiTabViewModel: DidiTabViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {

        didiTabViewModel.onEvent(TestClass.TestDataLoadingEvent(context))

    }

    val didiList = didiTabViewModel.didiList

    ToolBarWithMenuComponent(
        title = stringResource(id = DataLoadingRes.string.app_name),
        modifier = Modifier,
        isSearch = true,
        iconResId = Res.drawable.ic_sarathi_logo,
        onBackIconClick = { /*TODO*/ },
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

            CustomTabLayout()


            Row(Modifier.fillMaxWidth()) {

                Icon(
                    painter = painterResource(id = MatRes.drawable.didi_icon),
                    contentDescription = "",
                    tint = blueDark
                )

                Spacer(
                    modifier = Modifier
                        .width(dimen_8_dp)
                )

                Text(text = "Total Didis - ${didiTabViewModel.totalCount.value}")

            }

            CustomVerticalSpacer()

            LazyColumn(modifier = Modifier) {

                itemsIndexed(didiList.value) { index, item ->

                    DidiTabCard(subjectEntity = item) {

                    }


                }

            }

        }

    }

}
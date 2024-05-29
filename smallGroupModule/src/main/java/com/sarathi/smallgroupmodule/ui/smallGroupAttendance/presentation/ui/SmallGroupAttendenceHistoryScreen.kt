package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.ui.components.ButtonPositiveComponent
import com.sarathi.missionactivitytask.ui.components.IconProperties
import com.sarathi.missionactivitytask.ui.components.TextProperties
import com.sarathi.missionactivitytask.ui.components.TextWithIconComponent
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.missionactivitytask.ui.theme.defaultTextStyle
import com.sarathi.missionactivitytask.ui.theme.dimen_10_dp
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.presentation.event.SmallGroupAttendanceHistoryEvent
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.viewModel.SmallGroupAttendanceHistoryViewModel
import com.sarathi.smallgroupmodule.ui.theme.blueDark

@Composable
fun SmallGroupAttendanceHistoryScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    smallGroupId: Int,
    smallGroupAttendanceHistoryViewModel: SmallGroupAttendanceHistoryViewModel
) {

    LaunchedEffect(key1 = Unit) {

        smallGroupAttendanceHistoryViewModel.onEvent(
            SmallGroupAttendanceHistoryEvent.LoadSmallGroupDetailsForSmallGroupIdEvent(
                smallGroupId
            )
        )

    }

    ToolBarWithMenuComponent(
        title = smallGroupAttendanceHistoryViewModel.smallGroupDetails.value.smallGroupName,
        modifier = Modifier,
        onBackIconClick = { navController.popBackStack() },
        onSearchValueChange = {},
        isFilterSelected = {},
        isDataAvailable = true,
        onBottomUI = { /*TODO*/ },
        tabBarView = { /*TODO*/ }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 75.dp),
            verticalArrangement = Arrangement.spacedBy(dimen_10_dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.Center,
            ) {
                TextWithIconComponent(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    iconProperties = IconProperties(
                        painterResource(id = R.drawable.didi_icon),
                        contentDescription = "",
                        blueDark,
                    ), textProperties = TextProperties(
                        text = "Total Didis - ${smallGroupAttendanceHistoryViewModel.smallGroupDetails.value.didiCount}",
                        color = blueDark,
                        style = defaultTextStyle
                    )
                )
                Spacer(modifier = Modifier.padding(vertical = 10.dp))
                ButtonPositiveComponent(
                    buttonTitle = "Take Attendance",
                    isActive = true,
                    isArrowRequired = true,
                    onClick = {
                        //TODO Handle Navigation
                    }
                )
            }
        }
    }

}
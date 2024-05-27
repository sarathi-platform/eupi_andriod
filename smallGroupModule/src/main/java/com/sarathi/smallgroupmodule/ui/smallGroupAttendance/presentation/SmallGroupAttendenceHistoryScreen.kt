package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.ui.components.ButtonPositiveComponent
import com.sarathi.missionactivitytask.ui.components.IconProperties
import com.sarathi.missionactivitytask.ui.components.TextProperties
import com.sarathi.missionactivitytask.ui.components.TextWithIconComponent
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.missionactivitytask.ui.theme.defaultTextStyle
import com.sarathi.missionactivitytask.ui.theme.dimen_10_dp
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.viewModel.SmallGroupAttendanceHistoryViewMode
import com.sarathi.smallgroupmodule.ui.theme.blueDark

@Composable
fun SmallGroupAttendanceHistoryScreen(
    modifier: Modifier = Modifier,
    smallGroupId: Int,
    smallGroupAttendanceHistoryViewMode: SmallGroupAttendanceHistoryViewMode
) {

    //TODO fetch small group details for the smallGroupId based on date.

    ToolBarWithMenuComponent(
        title = "", //TODO add small group name for title
        modifier = Modifier,
        onBackIconClick = { /*TODO*/ },
        onSearchValueChange = {},
        isFilterSelected = {},
        isDataAvailable = false,
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
                    iconProperties = IconProperties(
                        painterResource(id = R.drawable.didi_icon),
                        contentDescription = "",
                        blueDark,
                    ), textProperties = TextProperties(
                        text = "Total Didis - ",
                        color = blueDark,
                        style = defaultTextStyle
                    )
                )
                Spacer(modifier = Modifier.padding(vertical = 10.dp))
                ButtonPositiveComponent(
                    buttonTitle = "Take Attendance",
                    isActive = true,
                    isArrowRequired = false,
                    onClick = {

                    }
                )
            }
        }
    }

}
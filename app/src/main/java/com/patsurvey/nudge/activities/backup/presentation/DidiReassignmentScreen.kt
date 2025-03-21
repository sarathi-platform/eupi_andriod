package com.patsurvey.nudge.activities.backup.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.ui.common_components.ToolbarComponent
import com.nrlm.baselinesurvey.ui.theme.white
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.textColorDark

@Composable
fun DidiReassignmentScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    BackHandler { navController.navigateUp() }

    Scaffold(
        backgroundColor = white,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ToolbarComponent(
                title = "Didi Reassignment",
                modifier = Modifier
            ) {
                navController.navigateUp()
            }
        },
        bottomBar = {}
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(it.calculateTopPadding()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    dimen_10_dp
                )
            ) {
                Text(
                    "Didi Reassignment is in progress",
                    color = textColorDark,
                    style = defaultTextStyle
                )
                CircularProgressIndicator()
            }
        }
    }

}
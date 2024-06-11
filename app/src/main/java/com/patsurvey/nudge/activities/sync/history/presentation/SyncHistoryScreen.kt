package com.patsurvey.nudge.activities.sync.history.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nrlm.baselinesurvey.ui.common_components.ToolbarWithMenuComponent
import com.patsurvey.nudge.R

@Composable
fun SyncHistoryScreen(navController:NavController){
    ToolbarWithMenuComponent(
        title = stringResource(id = R.string.sync_all_data),
        modifier = Modifier.fillMaxSize(),
        isMenuIconRequired = false,
        onBackIconClick = { navController.popBackStack() },
        onBottomUI = { }) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(start = 10.dp, end = 10.dp, top = 65.dp)
                .fillMaxWidth()

        ) {
            EventTypeHistoryCard(
                eventDateTime = "16 Jan 1025, 16:10:00",
                totalEventCount = 100,
                successEventCount = 10,
                onCardClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SyncHistoryScreenPreview(){
    SyncHistoryScreen(navController = rememberNavController())
}
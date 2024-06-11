package com.patsurvey.nudge.activities.sync.home.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nrlm.baselinesurvey.ui.common_components.ToolbarWithMenuComponent
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.newMediumTextStyle
import com.patsurvey.nudge.activities.ui.theme.white

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SyncHomeScreen(
    navController: NavController,
) {
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                Button(
                    onClick = {
                              
                    },
                    colors = ButtonDefaults.buttonColors(blueDark),
                    modifier = Modifier.align(
                        Alignment.BottomEnd
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.sync_all_data),
                        color = white,
                        modifier = Modifier,
                        style = newMediumTextStyle
                    )
                }
            }

            EventTypeCard(
                title = stringResource(id = R.string.sync_data),
                totalEventCount = 100,
                successEventCount = 30,
                onRefreshClick = {},
                onCardClick = {}
            )
            EventTypeCard(
                title = stringResource(id = R.string.sync_images),
                totalEventCount = 100,
                successEventCount = 12,
                onRefreshClick = {},
                onCardClick = {}
            )

        }
    }

}

@Preview(showBackground = true)
@Composable
fun SyncHomeScreenPreview() {
    SyncHomeScreen(navController = rememberNavController())
}

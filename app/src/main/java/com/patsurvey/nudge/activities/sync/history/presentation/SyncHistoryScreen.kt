package com.patsurvey.nudge.activities.sync.history.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nrlm.baselinesurvey.ui.common_components.ToolbarWithMenuComponent
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_32_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_65_dp
import com.nrlm.baselinesurvey.ui.theme.text_size_16_sp
import com.nudge.core.SYNC_DATA
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.sync.history.viewmodel.SyncHistoryViewModel
import com.patsurvey.nudge.activities.sync.home.presentation.LastSyncTime
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.textColorDark

@Composable
fun SyncHistoryScreen(
    navController: NavController,
    syncType: String,
    viewModel: SyncHistoryViewModel
) {
    val context= LocalContext.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    LaunchedEffect(key1 = Unit) {
        viewModel.getAllEventStatusForUser(context)
    }
    ToolbarWithMenuComponent(
        title = stringResource(
            id = R.string.sync_data_history
        ),
        modifier = Modifier.fillMaxSize(),
        isMenuIconRequired = false,
        onBackIconClick = { navController.popBackStack() },
        onBottomUI = { }) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(start = dimen_10_dp, end = dimen_10_dp, top = dimen_65_dp)
                .fillMaxWidth()

        ) {
            LastSyncTime(
                lastSyncTime = viewModel.lastSyncTime.longValue,
                mobileNumber = viewModel.getUserMobileNumber()
            ) {}
            CreateEventUIList(viewModel, screenHeight)
        }
    }

}

@Composable
private fun CreateEventUIList(
    viewModel: SyncHistoryViewModel,
    screenHeight: Dp
) {
    if (viewModel.countDataList.value.isNotEmpty()) {
        EventTypeHistoryCard(
            cardTitle = stringResource(R.string.sync_data),
            totalEvents = viewModel.totalDataEventCount.value,
            eventStatusList = viewModel.eventStatusDataUIList
        ) { }

        EventTypeHistoryCard(
            cardTitle = stringResource(R.string.sync_images),
            totalEvents = viewModel.totalImageEventCount.value,
            eventStatusList = viewModel.eventStatusImageUIList
        ) { }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(vertical = (screenHeight / 4))
                    .align(
                        Alignment.TopCenter
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.material.Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = textColorDark,
                                fontSize = text_size_16_sp,
                                fontWeight = FontWeight.Normal,
                                fontFamily = NotoSans
                            )
                        ) {
                            append(
                                stringResource(id = R.string.no_history_available_right_now)
                            )
                        }
                    },
                    modifier = Modifier.padding(top = dimen_32_dp)
                )
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun SyncHistoryScreenPreview() {
    SyncHistoryScreen(
        navController = rememberNavController(),
        syncType = SYNC_DATA,
        viewModel = hiltViewModel()
    )
}
package com.nrlm.baselinesurvey.ui.mission_summary_screen.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.navigation.home.navigateBackToMissionScreen
import com.nrlm.baselinesurvey.ui.common_components.ButtonPositive
import com.nrlm.baselinesurvey.ui.common_components.StepsBox
import com.nrlm.baselinesurvey.ui.mission_summary_screen.viewModel.MissionSummaryViewModel
import com.nrlm.baselinesurvey.ui.theme.black100Percent
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.inprogressYellow
import com.nrlm.baselinesurvey.ui.theme.largeTextStyle
import com.nrlm.baselinesurvey.ui.theme.newMediumTextStyle


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MissionSummaryScreen(
    navController: NavController = rememberNavController(),
    missionId: Int = 0,
    missionName: String,
    missionDate: String,
    viewModel: MissionSummaryViewModel = hiltViewModel()
) {
    val activities =
        viewModel.activities.value

    LaunchedEffect(key1 = true) {
        viewModel.init(missionId)
    }
    Scaffold(bottomBar = {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            ButtonPositive(
                buttonTitle = "Go Back",
                isActive = true,
                isLeftArrow = true

            ) {
                navigateBackToMissionScreen(navController)
            }
        }

    }
    ) {
        if (activities.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    text = missionName,
                    style = largeTextStyle,
                    color = blueDark
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    text = stringResource(id = R.string.due_by_x, missionDate),
                    style = newMediumTextStyle,
                    color = black100Percent

                )
                LazyColumn(
                ) {
                    itemsIndexed(
                        items = activities
                    ) { index, activity ->
                        StepsBox(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            boxTitle = activity.activityName,
                            subTitle = stringResource(
                                id = R.string.x_dii_pending,
                                activity.activityTaskSize
                            ),
                            stepNo = activity.activityTypeId,
                            index = 1,
                            iconResourceId = R.drawable.ic_mission_inprogress,
                            backgroundColor = inprogressYellow,
                            onclick = {
                                navController.navigate("add_didi_graph/${activity.activityName}/${missionId}/${activity.endDate}")
                            })
                    }

                }
            }

        }

    }

}
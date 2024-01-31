package com.nrlm.baselinesurvey.ui.mission_screen.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.navigation.navgraph.Graph
import com.nrlm.baselinesurvey.ui.common_components.ButtonPositive
import com.nrlm.baselinesurvey.ui.common_components.StepsBox
import com.nrlm.baselinesurvey.ui.mission_screen.viewmodel.MissionViewModel
import com.nrlm.baselinesurvey.ui.theme.largeTextStyle
import com.nrlm.baselinesurvey.ui.theme.newMediumTextStyle


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun MissionSummaryScreen(
    navController: NavController = rememberNavController(),
    missionId: Int = 0,
    viewModel: MissionViewModel = hiltViewModel()
) {
    var missions =
        viewModel.filterMissionList.value.filter { s -> missionId == s.missionId }

    LaunchedEffect(key1 = true) {
        viewModel.init()
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

            ) {}
        }

    }
    ) {
        if (missions.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    text = missions[0].missionName,
                    style = largeTextStyle
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    text = missions[0].endDate,
                    style = newMediumTextStyle
                )
                LazyColumn(
                ) {

                    items(missions[0].activities) { activity ->
                        StepsBox(
                            modifier = Modifier.padding(5.dp),
                            boxTitle = activity.activityName,
                            subTitle = activity.reviewer,
                            stepNo = activity.activityTypeId,
                            index = 1,
                            iconResourceId = R.drawable.ic_group_icon,
                            onclick = {
                                navController.navigate(Graph.ADD_DIDI)
                            })
                    }

                }
            }

        }

    }

}
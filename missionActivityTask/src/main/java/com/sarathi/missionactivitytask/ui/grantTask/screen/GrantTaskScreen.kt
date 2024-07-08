package com.sarathi.missionactivitytask.ui.grantTask.screen

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sarathi.missionactivitytask.ui.grantTask.viewmodel.GrantTaskScreenViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GrantTaskScreen(
    navController: NavController = rememberNavController(),
    viewModel: GrantTaskScreenViewModel = hiltViewModel(),
    missionId: Int,
    activityName: String,
    activityId: Int,
    onSettingClick: () -> Unit
) {
    TaskScreen(missionId = missionId, activityId = activityId, activityName = activityName) {

    }
}


package com.patsurvey.nudge.activities

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.patsurvey.nudge.activities.ui.bpc.bpc_village_screen.BpcVillageSelectionScreen

@Composable
fun VillageScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    navController: NavController,
    viewModel: VillageScreenViewModel = hiltViewModel(),
    onNavigateToSetting:()->Unit
) {

    if (/*viewModel.isUserBpc()*/true) {
        BpcVillageSelectionScreen(
            navController = navController,
        ) {
            onNavigateToSetting()
        }
    } else {
        VillageSelectionScreen(navController = navController, viewModel = hiltViewModel()) {
            onNavigateToSetting()
        }
    }

}
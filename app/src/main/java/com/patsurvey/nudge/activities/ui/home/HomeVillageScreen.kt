package com.patsurvey.nudge.activities.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nudge.navigationmanager.graphs.AuthScreen
import com.patsurvey.nudge.activities.VillageScreen
import com.patsurvey.nudge.activities.ui.login.LoginScreen
import com.patsurvey.nudge.data.prefs.PrefRepo

@Composable
fun HomeVillageScreen(
    navController: NavHostController,
    prefRepo: PrefRepo
) {
    if (prefRepo.getAccessToken()?.isNotEmpty()==true) {
        VillageScreen(navController = navController) {
            navController.navigate(AuthScreen.AUTH_SETTING_SCREEN.route)
       }
    } else {
        LoginScreen(
            navController,
            viewModel = hiltViewModel(),
            modifier = Modifier.fillMaxSize()
        )
    }


}

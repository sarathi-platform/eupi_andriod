package com.patsurvey.nudge.navigation.home

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.patsurvey.nudge.navigation.navgraph.Graph

@Composable
fun NavHomeGraph(navController: NavHostController, prefRepo: PrefRepo) {
    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = HomeScreens.PROGRESS_SCREEN.route
    ) {

    }
}

sealed class HomeScreens(val route: String) {
    object PROGRESS_SCREEN : HomeScreens(route = "progress_screen")
}


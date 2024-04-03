package com.patsurvey.nudge.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nudge.core.ui.navigation.CoreGraph
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.navigation.baseline.BSHomeNavScreen
import com.patsurvey.nudge.navigation.selection.HomeNavScreen


@Composable
fun RootNavigationGraph(navController: NavHostController,prefRepo: PrefRepo){
    NavHost(navController = navController,
        route = CoreGraph.ROOT,
        startDestination = CoreGraph.AUTHENTICATION
    ){
        authNavGraph(navController)
        composable(route = CoreGraph.HOME) {
            HomeNavScreen(prefRepo = prefRepo)
        }
        composable(route = CoreGraph.BASE_HOME) {
            BSHomeNavScreen()
        }
    }
}
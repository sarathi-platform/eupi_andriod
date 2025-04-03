package com.patsurvey.nudge.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nudge.core.enums.AppConfigKeysEnum
import com.nudge.navigationmanager.graphs.NudgeNavigationGraph
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.navigation.selection.HomeNavScreen


@Composable
fun RootNavigationGraph(navController: NavHostController,prefRepo: PrefRepo){
    NavHost(navController = navController,
        route = NudgeNavigationGraph.ROOT,
        startDestination = NudgeNavigationGraph.AUTHENTICATION
    ){
        authNavGraph(navController, prefRepo.getPref(AppConfigKeysEnum.V2TheameEnable.name, false))
        composable(route = NudgeNavigationGraph.HOME) {
            HomeNavScreen(prefRepo = prefRepo)
        }
    }
}
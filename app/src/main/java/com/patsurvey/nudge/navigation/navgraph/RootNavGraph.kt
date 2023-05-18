package com.patsurvey.nudge.navigation.navgraph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.navigation.authNavGraph
import com.patsurvey.nudge.navigation.home.HomeNavScreen
import com.patsurvey.nudge.utils.ARG_DIDI_DETAILS
import com.patsurvey.nudge.utils.ARG_STEP_ID
import com.patsurvey.nudge.utils.ARG_STEP_INDEX
import com.patsurvey.nudge.utils.ARG_VILLAGE_ID


@Composable
fun RootNavigationGraph(navController: NavHostController,prefRepo: PrefRepo){
    NavHost(navController = navController,
        route = Graph.ROOT,
        startDestination = Graph.AUTHENTICATION
    ){
        authNavGraph(navController)
        composable(route = Graph.HOME) {
            HomeNavScreen(prefRepo = prefRepo)
        }
    }
}

object Graph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = "auth_graph"
    const val HOME = "home_graph"
    const val DETAILS = "details_graph/{$ARG_VILLAGE_ID}/{$ARG_STEP_ID}/{$ARG_STEP_INDEX}"
    const val ADD_DIDI = "add_didi_graph/{$ARG_DIDI_DETAILS}"
    const val SOCIAL_MAPPING = "social_mapping_graph/{$ARG_VILLAGE_ID}/{$ARG_STEP_ID}"
    const val WEALTH_RANKING = "wealth_ranking/{$ARG_VILLAGE_ID}/{$ARG_STEP_ID}"
    const val PAT_SCREENS = "pat_screens/{$ARG_VILLAGE_ID}/{$ARG_STEP_ID}"
}
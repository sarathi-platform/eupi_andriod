package com.patsurvey.nudge.navigation.navgraph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.navigation.authNavGraph
import com.patsurvey.nudge.navigation.home.HomeNavScreen
import com.patsurvey.nudge.utils.ARG_DIDI_DETAILS_ID
import com.patsurvey.nudge.utils.ARG_IS_STEP_COMPLETE
import com.patsurvey.nudge.utils.ARG_STEP_ID
import com.patsurvey.nudge.utils.ARG_STEP_INDEX
import com.patsurvey.nudge.utils.ARG_USER_TYPE
import com.patsurvey.nudge.utils.ARG_VILLAGE_ID


@Composable
fun RootNavigationGraph(navController: NavHostController,prefRepo: PrefRepo){
    NavHost(navController = navController,
        route = Graph.ROOT,
        startDestination = Graph.AUTHENTICATION
    ){
        authNavGraph(navController)
        composable(route = Graph.HOME, arguments = listOf(
            navArgument(
                    name = ARG_USER_TYPE
                    ) {
                type = NavType.StringType
            }
        )) {
            HomeNavScreen(prefRepo = prefRepo)
        }
    }
}

object Graph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = "auth_graph"
    const val MAT_GRAPH = "mat_graph"
    const val HOME = "home_graph/{$ARG_USER_TYPE}"
    const val DETAILS = "details_graph/{$ARG_VILLAGE_ID}/{$ARG_STEP_ID}/{$ARG_STEP_INDEX}"
    const val ADD_DIDI = "add_didi_graph/{$ARG_DIDI_DETAILS_ID}"
    const val SOCIAL_MAPPING = "social_mapping_graph/{$ARG_VILLAGE_ID}/{$ARG_STEP_ID}"
    const val WEALTH_RANKING = "wealth_ranking/{$ARG_VILLAGE_ID}/{$ARG_STEP_ID}"
    const val PAT_SCREENS = "pat_screens/{$ARG_VILLAGE_ID}/{$ARG_STEP_ID}"
    const val SETTING_GRAPH = "setting_graph"
    const val LOGOUT_GRAPH = "logout_graph"
    const val VO_ENDORSEMENT_GRAPH = "vo_endorsement_graph/{$ARG_VILLAGE_ID}/{$ARG_STEP_ID}/{$ARG_IS_STEP_COMPLETE}"
    const val BPC_GRAPH = "bpc_graph/{$ARG_VILLAGE_ID}/{$ARG_STEP_ID}"
}
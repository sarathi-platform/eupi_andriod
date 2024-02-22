package com.nrlm.baselinesurvey.navigation.navgraph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nrlm.baselinesurvey.ARG_ACTIVITY_DATE
import com.nrlm.baselinesurvey.ARG_ACTIVITY_ID
import com.nrlm.baselinesurvey.ARG_IS_STEP_COMPLETE
import com.nrlm.baselinesurvey.ARG_MISSION_ID
import com.nrlm.baselinesurvey.ARG_STEP_ID
import com.nrlm.baselinesurvey.ARG_STEP_INDEX
import com.nrlm.baselinesurvey.ARG_SURVEY_ID
import com.nrlm.baselinesurvey.ARG_VILLAGE_ID
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.navigation.authNavGraph
import com.nrlm.baselinesurvey.navigation.home.HomeNavScreen


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
    const val ADD_DIDI =
        "add_didi_graph/{$ARG_ACTIVITY_ID}/{$ARG_MISSION_ID}/{$ARG_ACTIVITY_DATE}/{$ARG_SURVEY_ID}"
    const val SOCIAL_MAPPING = "social_mapping_graph/{$ARG_VILLAGE_ID}/{$ARG_STEP_ID}"
    const val WEALTH_RANKING = "wealth_ranking/{$ARG_VILLAGE_ID}/{$ARG_STEP_ID}"
    const val PAT_SCREENS = "pat_screens/{$ARG_VILLAGE_ID}/{$ARG_STEP_ID}"
    const val SETTING_GRAPH = "setting_graph"
    const val LOGOUT_GRAPH = "logout_graph"
    const val VO_ENDORSEMENT_GRAPH = "vo_endorsement_graph/{$ARG_VILLAGE_ID}/{$ARG_STEP_ID}/{$ARG_IS_STEP_COMPLETE}"
    const val BPC_GRAPH = "bpc_graph/{$ARG_VILLAGE_ID}/{$ARG_STEP_ID}"
}
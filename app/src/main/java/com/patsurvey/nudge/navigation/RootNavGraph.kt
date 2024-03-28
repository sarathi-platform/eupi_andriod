package com.patsurvey.nudge.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nudge.core.ui.navigation.CoreGraph
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.navigation.authNavGraph
import com.patsurvey.nudge.navigation.selection.HomeNavScreen
import com.patsurvey.nudge.utils.ARG_DIDI_DETAILS_ID
import com.patsurvey.nudge.utils.ARG_IS_STEP_COMPLETE
import com.patsurvey.nudge.utils.ARG_STEP_ID
import com.patsurvey.nudge.utils.ARG_STEP_INDEX
import com.patsurvey.nudge.utils.ARG_USER_TYPE
import com.patsurvey.nudge.utils.ARG_VILLAGE_ID


@Composable
fun RootNavigationGraph(navController: NavHostController,prefRepo: PrefRepo){
    NavHost(navController = navController,
        route = CoreGraph.ROOT,
        startDestination = CoreGraph.AUTHENTICATION
    ){
        authNavGraph(navController)
        composable(route = CoreGraph.HOME) {
            HomeDeciderScreen(prefRepo = prefRepo)
        }
    }
}
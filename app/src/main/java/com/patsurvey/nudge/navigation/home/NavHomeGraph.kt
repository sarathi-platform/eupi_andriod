package com.patsurvey.nudge.navigation.home


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.patsurvey.nudge.activities.AddDidiScreen
import com.patsurvey.nudge.activities.DidiScreen
import com.patsurvey.nudge.activities.ProgressScreen
import com.patsurvey.nudge.activities.StepCompletionScreen
import com.patsurvey.nudge.activities.ui.transect_walk.TransectWalkScreen
import com.patsurvey.nudge.navigation.navgraph.Graph
import com.patsurvey.nudge.utils.*

@Composable
fun NavHomeGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = HomeScreens.PROGRESS_SCREEN.route
    ) {
        composable(route = HomeScreens.PROGRESS_SCREEN.route) {
            ProgressScreen(
                stepsNavHostController = navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxWidth()
            ){ villageId,stepId,index->
                when(index){
                    0->navController.navigate("details_graph/$villageId/$stepId/$index")
                    1->navController.navigate("social_mapping_graph/$villageId/$stepId")
                }
            }
        }

        composable(route = HomeScreens.DIDI_SCREEN.route){
            DidiScreen(
                navController = navController,
                modifier = Modifier
                    .fillMaxSize(),
                didiViewModel = hiltViewModel(),-1,-1){
                navController.navigate("add_didi_graph/$ADD_DIDI_BLANK_STRING"){
                    launchSingleTop = true
                }
            }
        }
        detailsNavGraph(navController = navController)
        addDidiNavGraph(navController = navController)
        socialMappingNavGraph(navController=navController)
    }
}
sealed class HomeScreens(val route: String) {
    object PROGRESS_SCREEN : HomeScreens(route = "progress_screen")
    object DIDI_SCREEN : HomeScreens(route = "didi_screen/{$ARG_PAGE_FROM}")
}

fun NavGraphBuilder.detailsNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.DETAILS,
        startDestination = DetailsScreen.TRANSECT_WALK_SCREEN.route,
        arguments = listOf(navArgument(ARG_VILLAGE_ID) {
            type = NavType.IntType
        }, navArgument(ARG_STEP_ID) {
            type = NavType.IntType
        })
    ) {
        composable(
            route = DetailsScreen.TRANSECT_WALK_SCREEN.route,
            arguments = listOf(navArgument(ARG_VILLAGE_ID) {
                type = NavType.IntType
            }, navArgument(ARG_STEP_ID) {
                type = NavType.IntType
            })
        ) {
            TransectWalkScreen(
                navController = navController,
                modifier = Modifier
                    .fillMaxSize(),
                viewModel = hiltViewModel(),
                villageId = it.arguments?.getInt(ARG_VILLAGE_ID) ?: 0,
                stepId = it.arguments?.getInt(ARG_STEP_ID) ?: -1
            )
        }

        composable(
            route = DetailsScreen.STEP_COMPLETION_SCREEN.route,
            arguments = listOf(navArgument(ARG_COMPLETION_MESSAGE) {
                type = NavType.StringType
            })
        ) {
            StepCompletionScreen(navController = navController, modifier = Modifier, message = it.arguments?.getString(ARG_COMPLETION_MESSAGE) ?: ""){
                navController.navigate(Graph.HOME){
                    popUpTo(HomeScreens.PROGRESS_SCREEN.route){
                        inclusive = true
                    }
                }
            }
        }

    }
}

sealed class DetailsScreen(val route: String) {
    object ADD_DIDI_SCREEN : DetailsScreen(route = "add_didi_screen")
    object TRANSECT_WALK_SCREEN : DetailsScreen(route ="transect_walk_screen")
    object STEP_COMPLETION_SCREEN : DetailsScreen(route ="step_completion_screen/{$ARG_COMPLETION_MESSAGE}")
}


fun NavGraphBuilder.addDidiNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.ADD_DIDI,
        startDestination = DetailsScreen.ADD_DIDI_SCREEN.route,
        arguments = listOf(navArgument(ARG_DIDI_DETAILS) {
            type = NavType.StringType
            nullable=true
        })
    ) {
        composable(route = DetailsScreen.ADD_DIDI_SCREEN.route,
            arguments = listOf(navArgument(ARG_DIDI_DETAILS) {
                type = NavType.StringType
                nullable=true
                defaultValue = null
            })){
            AddDidiScreen(
                navController=navController,
                modifier = Modifier
                    .fillMaxSize(),
                didiDetails = it.arguments?.getString(ARG_DIDI_DETAILS) ?: BLANK_STRING,
                didiViewModel = hiltViewModel(),
            ){
                navController.popBackStack()
            }
        }
    }
}

fun NavGraphBuilder.socialMappingNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.SOCIAL_MAPPING,
        startDestination = SocialMappingScreen.SM_DIDI_SCREEN.route,
        arguments = listOf(navArgument(ARG_VILLAGE_ID) {
            type = NavType.IntType
        }, navArgument(ARG_STEP_ID) {
            type = NavType.IntType
        })
    ) {
        composable(route = SocialMappingScreen.SM_DIDI_SCREEN.route,
            arguments = listOf(navArgument(ARG_VILLAGE_ID) {
            type = NavType.IntType
        }, navArgument(ARG_STEP_ID) {
            type = NavType.IntType
        })){
            DidiScreen(
                navController = navController,
                modifier = Modifier
                    .fillMaxSize(),
                didiViewModel = hiltViewModel(),
                villageId = it.arguments?.getInt(ARG_VILLAGE_ID) ?: 0,
                stepId = it.arguments?.getInt(ARG_STEP_ID) ?: -1){
                navController.navigate("add_didi_graph/$ADD_DIDI_BLANK_STRING"){
                    launchSingleTop = true
                }
            }
        }

        composable(
            route = SocialMappingScreen.SM_STEP_COMPLETION_SCREEN.route,
            arguments = listOf(navArgument(ARG_COMPLETION_MESSAGE) {
                type = NavType.StringType
            })
        ) {
            StepCompletionScreen(navController = navController, modifier = Modifier, message = it.arguments?.getString(ARG_COMPLETION_MESSAGE) ?: ""){
                navController.navigate(Graph.HOME){
                    popUpTo(HomeScreens.PROGRESS_SCREEN.route){
                        inclusive = true
                    }
                }
            }
        }
    }
}


sealed class SocialMappingScreen(val route: String) {
    object SM_DIDI_SCREEN : SocialMappingScreen(route = "sm_didi_screen")
    object SM_STEP_COMPLETION_SCREEN: SocialMappingScreen(route ="sm_step_completion_screen/{$ARG_COMPLETION_MESSAGE}")
}
package com.patsurvey.nudge.navigation.home


import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.patsurvey.nudge.activities.*
import com.patsurvey.nudge.activities.ui.socialmapping.WealthRankingScreen
import com.patsurvey.nudge.activities.ui.socialmapping.WealthRankingViewModel
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
                Log.e("index",""+index)
                when(index){
                    0->navController.navigate("details_graph/$villageId/$stepId/$index")
                    1->navController.navigate("social_mapping_graph/$villageId/$stepId")
                    2->navController.navigate("wealth_ranking/$villageId/$stepId")
                    3->navController.navigate("pat_screens/$villageId/$stepId")
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
        wealthRankingNavGraph(navController = navController)
        patNavGraph(navController = navController)
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


fun NavGraphBuilder.wealthRankingNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.WEALTH_RANKING,
        startDestination = WealthRankingScreens.WEALTH_RANKING_SCREEN.route,
        arguments = listOf(navArgument(ARG_VILLAGE_ID) {
            type = NavType.IntType
        }, navArgument(ARG_STEP_ID) {
            type = NavType.IntType
        })
    ) {

        composable(
            route = WealthRankingScreens.WEALTH_RANKING_SCREEN.route,
            arguments = listOf(navArgument(ARG_VILLAGE_ID) {
                type = NavType.IntType
            }, navArgument(ARG_STEP_ID) {
                type = NavType.IntType
            })
        ) {
            WealthRankingScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize(),
                villageId = it.arguments?.getInt(ARG_VILLAGE_ID) ?: 0,
                stepId = it.arguments?.getInt(ARG_STEP_ID) ?: -1
            )
        }

        composable(
            route = WealthRankingScreens.PAT_IMAGE_PREVIEW_SCREEN.route
        ) {
            PatImagePreviewScreen(viewModal = hiltViewModel())
        }

        composable(
            route = WealthRankingScreens.WEALTH_RANKING_SURVEY.route,
            arguments = listOf(navArgument(ARG_VILLAGE_ID) {
                type = NavType.IntType
            }, navArgument(ARG_STEP_ID) {
                type = NavType.IntType
            })
        ) {
            ParticipatoryWealthRankingSurvey(
                navController = navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize(),
                villageId = it.arguments?.getInt(ARG_VILLAGE_ID) ?: 0,
                stepId = it.arguments?.getInt(ARG_STEP_ID) ?: -1
            )
        }

        composable(
            route = WealthRankingScreens.STEP_COMPLETION_SCREEN.route,
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

sealed class WealthRankingScreens(val route: String) {
    object WEALTH_RANKING_SCREEN : WealthRankingScreens(route = "wealth_ranking")
    object WEALTH_RANKING_SURVEY :  WealthRankingScreens(route = "wealth_ranking_survey")
    object STEP_COMPLETION_SCREEN : WealthRankingScreens(route ="step_completion_screen/{$ARG_COMPLETION_MESSAGE}")

    object PAT_IMAGE_PREVIEW_SCREEN :  PatScreens(route = "pat_image_preview_screen")
}

fun NavGraphBuilder.patNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.PAT_SCREENS,
        startDestination = PatScreens.PAT_LIST_SCREEN.route ,
        arguments = listOf(navArgument(ARG_VILLAGE_ID) {
            type = NavType.IntType
        }, navArgument(ARG_STEP_ID) {
            type = NavType.IntType
        })
    ) {
        composable(
            route = PatScreens.PAT_LIST_SCREEN.route,
            arguments = listOf(navArgument(ARG_VILLAGE_ID) {
                type = NavType.IntType
            }, navArgument(ARG_STEP_ID) {
                type = NavType.IntType
            })
        ) {
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
    }
}

sealed class PatScreens(val route: String) {
    object PAT_LIST_SCREEN : PatScreens(route = "pat_list_screen")
    object PAT_IMAGE_PREVIEW_SCREEN :  PatScreens(route = "pat_image_preview_screen")
    object PAT_IMAGE_CAPTURE_SCREEN : PatScreens(route ="pat_image_capture_screen")
    object STEP_COMPLETION_SCREEN : PatScreens(route ="step_completion_screen/{$ARG_COMPLETION_MESSAGE}")
}
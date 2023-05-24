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
import com.patsurvey.nudge.activities.*
import com.patsurvey.nudge.activities.settings.SettingScreen
import com.patsurvey.nudge.activities.survey.QuestionScreen
import com.patsurvey.nudge.activities.ui.digital_forms.DigitalFormAScreen
import com.patsurvey.nudge.activities.ui.selectlanguage.LanguageScreen
import com.patsurvey.nudge.activities.ui.socialmapping.ParticipatoryWealthRankingSurvey
import com.patsurvey.nudge.activities.ui.socialmapping.WealthRankingScreen
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
                modifier = Modifier.fillMaxWidth(),
                onNavigateToStep = { villageId, stepId, index ->
                    when (index) {
                        0 -> navController.navigate("details_graph/$villageId/$stepId/$index")
                        1 -> navController.navigate("social_mapping_graph/$villageId/$stepId")
                        2 -> navController.navigate("wealth_ranking/$villageId/$stepId")
                        3 -> navController.navigate("pat_screens/$villageId/$stepId")
                    }
                },
                onNavigateToSetting = {
                    navController.navigate(Graph.SETTING_GRAPH)
                }
            )
        }

        composable(route = HomeScreens.DIDI_SCREEN.route) {
            DidiScreen(
                navController = navController,
                modifier = Modifier
                    .fillMaxSize(),
                didiViewModel = hiltViewModel(), -1, -1
            ) {
                navController.navigate("add_didi_graph/$ADD_DIDI_BLANK_STRING") {
                    launchSingleTop = true
                }
            }
        }
        detailsNavGraph(navController = navController)
        addDidiNavGraph(navController = navController)
        socialMappingNavGraph(navController = navController)
        wealthRankingNavGraph(navController = navController)
        patNavGraph(navController = navController)
        settingNavGraph(navController = navController)
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
            StepCompletionScreen(
                navController = navController,
                modifier = Modifier,
                message = it.arguments?.getString(ARG_COMPLETION_MESSAGE) ?: ""
            ) {
                navController.navigate(Graph.HOME) {
                    popUpTo(HomeScreens.PROGRESS_SCREEN.route) {
                        inclusive = true
                    }
                }
            }
        }

    }
}

sealed class DetailsScreen(val route: String) {
    object ADD_DIDI_SCREEN : DetailsScreen(route = "add_didi_screen")
    object TRANSECT_WALK_SCREEN : DetailsScreen(route = "transect_walk_screen")
    object STEP_COMPLETION_SCREEN :
        DetailsScreen(route = "step_completion_screen/{$ARG_COMPLETION_MESSAGE}")
}


fun NavGraphBuilder.addDidiNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.ADD_DIDI,
        startDestination = DetailsScreen.ADD_DIDI_SCREEN.route,
        arguments = listOf(navArgument(ARG_DIDI_DETAILS) {
            type = NavType.StringType
            nullable = true
        })
    ) {
        composable(route = DetailsScreen.ADD_DIDI_SCREEN.route,
            arguments = listOf(navArgument(ARG_DIDI_DETAILS) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) {
            AddDidiScreen(
                navController = navController,
                modifier = Modifier
                    .fillMaxSize(),
                didiDetails = it.arguments?.getString(ARG_DIDI_DETAILS) ?: BLANK_STRING,
                didiViewModel = hiltViewModel(),
            ) {
                navController.popBackStack()
            }
        }
    }
}

/*fun NavGraphBuilder.didiPatSurveyNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.DIDI_PAT_SUMMARY,
        startDestination = PatScreens.PAT_IMAGE_PREVIEW_SCREEN.route,
        arguments = listOf(navArgument(ARG_DIDI_DETAILS) {
            type = NavType.StringType
            nullable = true
        })
    ) {
        composable(route = PatScreens.PAT_IMAGE_PREVIEW_SCREEN.route,
            arguments = listOf(navArgument(ARG_DIDI_DETAILS) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) {
            PatDidiSummaryScreen(
                navController = navController,
                modifier = Modifier
                    .fillMaxSize(),
                didiId = it.arguments?.getString(ARG_DIDI_DETAILS) ?: BLANK_STRING,
                patDidiSummaryViewModel = hiltViewModel(),
            ) {
                navController.popBackStack()
            }
        }
    }
}*/

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
            })
        ) {
            DidiScreen(
                navController = navController,
                modifier = Modifier
                    .fillMaxSize(),
                didiViewModel = hiltViewModel(),
                villageId = it.arguments?.getInt(ARG_VILLAGE_ID) ?: 0,
                stepId = it.arguments?.getInt(ARG_STEP_ID) ?: -1
            ) {
                navController.navigate("add_didi_graph/$ADD_DIDI_BLANK_STRING") {
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
            StepCompletionScreen(
                navController = navController,
                modifier = Modifier,
                message = it.arguments?.getString(ARG_COMPLETION_MESSAGE) ?: ""
            ) {
                navController.navigate(Graph.HOME) {
                    popUpTo(HomeScreens.PROGRESS_SCREEN.route) {
                        inclusive = true
                    }
                }
            }
        }
    }
}


sealed class SocialMappingScreen(val route: String) {
    object SM_DIDI_SCREEN : SocialMappingScreen(route = "sm_didi_screen")
    object SM_STEP_COMPLETION_SCREEN :
        SocialMappingScreen(route = "sm_step_completion_screen/{$ARG_COMPLETION_MESSAGE}")
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
            route = WealthRankingScreens.WEALTH_RANKING_SURVEY.route,
            arguments = listOf(navArgument(ARG_STEP_ID) {
                type = NavType.IntType
            },
                navArgument(ARG_IS_STEP_COMPLETE) {
                    type = NavType.BoolType
                }
            )
        ) {
            ParticipatoryWealthRankingSurvey(
                navController = navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize(),
                stepId = it.arguments?.getInt(ARG_STEP_ID) ?: -1,
                isStepComplete = it.arguments?.getBoolean(ARG_IS_STEP_COMPLETE) ?: false
            )
        }

        composable(
            route = WealthRankingScreens.WR_STEP_COMPLETION_SCREEN.route,
            arguments = listOf(navArgument(ARG_COMPLETION_MESSAGE) {
                type = NavType.StringType
            })
        ) {
            StepCompletionScreen(
                navController = navController,
                modifier = Modifier,
                message = it.arguments?.getString(ARG_COMPLETION_MESSAGE) ?: ""
            ) {
                navController.navigate(WealthRankingScreens.DIGITAL_FORM_A_SCREEN.route)

            }
        }

        composable(
            route = WealthRankingScreens.DIGITAL_FORM_A_SCREEN.route
        ) {
            DigitalFormAScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

sealed class WealthRankingScreens(val route: String) {
    object WEALTH_RANKING_SCREEN : WealthRankingScreens(route = "wealth_ranking")
    object WEALTH_RANKING_SURVEY :
        WealthRankingScreens(route = "wealth_ranking_survey/{$ARG_STEP_ID}/{$ARG_IS_STEP_COMPLETE}")

    object WR_STEP_COMPLETION_SCREEN :
        WealthRankingScreens(route = "wr_step_completion_screen/{$ARG_COMPLETION_MESSAGE}")

    object DIGITAL_FORM_A_SCREEN : WealthRankingScreens(route = "digital_form_a_screen")
}

fun NavGraphBuilder.patNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.PAT_SCREENS,
        startDestination = PatScreens.PAT_LIST_SCREEN.route,
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
                stepId = it.arguments?.getInt(ARG_STEP_ID) ?: -1
            ) {
                navController.navigate("add_didi_graph/$ADD_DIDI_BLANK_STRING") {
                    launchSingleTop = true
                }
            }
        }

        composable(route = PatScreens.DIDI_PAT_SUMMARY_SCREEN.route,
            arguments = listOf(navArgument(ARG_DIDI_ID) {
                type = NavType.IntType
            })){
            PatDidiSummaryScreen(
                navController=navController,
                modifier = Modifier
                    .fillMaxSize(),
                didiId = it.arguments?.getInt(ARG_DIDI_ID) ?: 0,
                patDidiSummaryViewModel = hiltViewModel(),
            ) {
                navController.popBackStack()
            }
        }

        composable(
            route = PatScreens.YES_NO_QUESTION_SCREEN.route,
            listOf(navArgument(ARG_DIDI_ID) {
                type = NavType.IntType
            })
        ) {
            QuestionScreen(
                navController = navController,
                modifier = Modifier.fillMaxSize(),
                viewModel = hiltViewModel(),
                didiId = it.arguments?.getInt(ARG_DIDI_ID) ?: 0
            )
        }
        composable(
            route = PatScreens.PAT_SECTION_ONE_SUMMARY_SCREEN.route,
            listOf(navArgument(ARG_DIDI_ID) {
                type = NavType.IntType
            })
        ) {
            PatSurvaySectionSummaryScreen(
                navController = navController,
                modifier = Modifier
                    .fillMaxSize(),
                patSectionSummaryViewModel = hiltViewModel(),
                didiId = it.arguments?.getInt(ARG_DIDI_ID) ?: 0
            )
        }
    }
}

sealed class PatScreens(val route: String) {
    object PAT_LIST_SCREEN : PatScreens(route = "pat_list_screen")
    object DIDI_PAT_SUMMARY_SCREEN : PatScreens(route = "didi_pat_summary/{$ARG_DIDI_ID}")

    object YES_NO_QUESTION_SCREEN : PatScreens(route = "yes_no_question_screen/{$ARG_DIDI_ID}")
    object STEP_COMPLETION_SCREEN :
        PatScreens(route = "step_completion_screen/{$ARG_COMPLETION_MESSAGE}")

    object PAT_SECTION_ONE_SUMMARY_SCREEN :
        PatScreens(route = "pat_section_one_summary_screen/{$ARG_DIDI_ID}")
}

fun NavGraphBuilder.settingNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.SETTING_GRAPH,
        startDestination = SettingScreens.SETTING_SCREEN.route
    ) {
        composable(route = SettingScreens.SETTING_SCREEN.route) {
            SettingScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(route = SettingScreens.LANGUAGE_SCREEN.route) {
            LanguageScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize(),
                pageFrom = ARG_FROM_SETTING
            )
        }
    }
}

sealed class SettingScreens(val route: String) {
    object SETTING_SCREEN : SettingScreens(route = "setting_screen")
    object LANGUAGE_SCREEN : SettingScreens(route = "language_screen")
}
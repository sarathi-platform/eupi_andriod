package com.patsurvey.nudge.navigation.selection

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.nrlm.baselinesurvey.ui.profile.presentation.ProfileBSScreen
import com.nrlm.baselinesurvey.ui.surveyee_screen.presentation.DataLoadingScreenComponent
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.model.MissionUiModel
import com.nudge.incomeexpensemodule.ui.screens.dataTab.presentation.DataTabScreen
import com.nudge.navigationmanager.graphs.AuthScreen
import com.nudge.navigationmanager.graphs.HomeScreens
import com.nudge.navigationmanager.graphs.LogoutScreens
import com.nudge.navigationmanager.graphs.NudgeNavigationGraph
import com.nudge.navigationmanager.graphs.SettingScreens
import com.nudge.navigationmanager.routes.MISSION_SUMMARY_SCREEN_ROUTE_NAME
import com.patsurvey.nudge.activities.AddDidiScreen
import com.patsurvey.nudge.activities.DidiScreen
import com.patsurvey.nudge.activities.FinalStepCompletionScreen
import com.patsurvey.nudge.activities.PatDidiSummaryScreen
import com.patsurvey.nudge.activities.PatSurvaySectionSummaryScreen
import com.patsurvey.nudge.activities.PatSurveyCompleteSummary
import com.patsurvey.nudge.activities.StepCompletionScreen
import com.patsurvey.nudge.activities.VillageScreen
import com.patsurvey.nudge.activities.backup.presentation.ExportImportScreen
import com.patsurvey.nudge.activities.settings.presentation.SettingBSScreen
import com.patsurvey.nudge.activities.survey.PatSuccessScreen
import com.patsurvey.nudge.activities.survey.PatSurvaySectionTwoSummaryScreen
import com.patsurvey.nudge.activities.survey.QuestionScreen
import com.patsurvey.nudge.activities.survey.SingleQuestionScreen
import com.patsurvey.nudge.activities.survey.SurveySummary
import com.patsurvey.nudge.activities.ui.bpc.bpc_add_more_did_screens.BpcAddMoreDidiScreen
import com.patsurvey.nudge.activities.ui.bpc.bpc_didi_list_screens.BpcDidiListScreen
import com.patsurvey.nudge.activities.ui.bpc.progress_screens.BpcProgressScreen
import com.patsurvey.nudge.activities.ui.bpc.score_comparision.ScoreComparisionScreen
import com.patsurvey.nudge.activities.ui.digital_forms.DigitalFormAScreen
import com.patsurvey.nudge.activities.ui.digital_forms.DigitalFormBScreen
import com.patsurvey.nudge.activities.ui.digital_forms.DigitalFormCScreen
import com.patsurvey.nudge.activities.ui.digital_forms.FormImageViewerScreen
import com.patsurvey.nudge.activities.ui.digital_forms.PdfViewer
import com.patsurvey.nudge.activities.ui.home.HomeUserScreen
import com.patsurvey.nudge.activities.ui.home.HomeVillageScreen
import com.patsurvey.nudge.activities.ui.login.LoginScreen
import com.patsurvey.nudge.activities.ui.login.OtpVerificationScreen
import com.patsurvey.nudge.activities.ui.selectlanguage.LanguageScreen
import com.patsurvey.nudge.activities.ui.socialmapping.ParticipatoryWealthRankingSurvey
import com.patsurvey.nudge.activities.ui.socialmapping.WealthRankingScreen
import com.patsurvey.nudge.activities.ui.transect_walk.TransectWalkScreen
import com.patsurvey.nudge.activities.ui.vo_endorsement.FormPictureScreen
import com.patsurvey.nudge.activities.ui.vo_endorsement.VoEndorsementStepScreen
import com.patsurvey.nudge.activities.ui.vo_endorsement.VoEndorsementSummaryScreen
import com.patsurvey.nudge.activities.video.FullscreenView
import com.patsurvey.nudge.activities.video.VideoListScreen
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.navigation.baseline.BSNavHomeGraph
import com.patsurvey.nudge.utils.ADD_DIDI_BLANK_ID
import com.patsurvey.nudge.utils.ARG_COMPLETION_MESSAGE
import com.patsurvey.nudge.utils.ARG_DIDI_DETAILS_ID
import com.patsurvey.nudge.utils.ARG_DIDI_ID
import com.patsurvey.nudge.utils.ARG_DIDI_STATUS
import com.patsurvey.nudge.utils.ARG_FORM_PATH
import com.patsurvey.nudge.utils.ARG_FOR_REPLACEMENT
import com.patsurvey.nudge.utils.ARG_FROM_PAT_SURVEY
import com.patsurvey.nudge.utils.ARG_FROM_SCREEN
import com.patsurvey.nudge.utils.ARG_FROM_SETTING
import com.patsurvey.nudge.utils.ARG_FROM_VO_ENDORSEMENT_SCREEN
import com.patsurvey.nudge.utils.ARG_IMAGE_PATH
import com.patsurvey.nudge.utils.ARG_IS_STEP_COMPLETE
import com.patsurvey.nudge.utils.ARG_MOBILE_NUMBER
import com.patsurvey.nudge.utils.ARG_PAT_SUCCESS_MESSAGE
import com.patsurvey.nudge.utils.ARG_QUESTION_INDEX
import com.patsurvey.nudge.utils.ARG_SECTION_TYPE
import com.patsurvey.nudge.utils.ARG_STEP_ID
import com.patsurvey.nudge.utils.ARG_VIDEO_ID
import com.patsurvey.nudge.utils.ARG_VILLAGE_ID
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.sarathi.missionactivitytask.navigation.MatNavigation
import com.sarathi.smallgroupmodule.navigation.SmallGroupNavigation
import com.sarathi.smallgroupmodule.ui.didiTab.presentation.DidiTabScreen


@Composable
fun NavHomeGraph(navController: NavHostController, prefRepo: PrefRepo) {
    NavHost(
        navController = navController,
        route = NudgeNavigationGraph.HOME_SUB_GRAPH,
        startDestination = HomeScreens.PROGRESS_SEL_SCREEN.route
    ) {
        composable(route = HomeScreens.PROGRESS_SEL_SCREEN.route) {
//            HomeNavScreen(prefRepo = prefRepo)
            HomeUserScreen(navController = navController, prefRepo = prefRepo) {

            }
        }

        composable(route = HomeScreens.BPC_PROGRESS_SEL_SCREEN.route) {
            BpcProgressScreen(
                bpcProgreesScreenViewModel = hiltViewModel(),
                navController = navController,
                modifier = Modifier.fillMaxWidth(),
                onNavigateToStep = { villageId, stepId ->
                    navController.navigate("bpc_graph/$villageId/$stepId")
                },
                onNavigateToSetting = {
                    navController.navigate(NudgeNavigationGraph.SETTING_GRAPH)
                },
                onBackClick = {
                    navController.navigate(HomeScreens.VILLAGE_SELECTION_SCREEN.route)
                }
            )
        }

        composable(route = HomeScreens.VILLAGE_SELECTION_SCREEN.route) {
            VillageScreen(navController = navController) {
                navController.navigate(AuthScreen.AUTH_SETTING_SCREEN.route)
            }
        }


        composable(route = HomeScreens.DIDI_SEL_SCREEN.route) {
            DidiScreen(
                navController = navController,
                modifier = Modifier
                    .fillMaxSize(),
                didiViewModel = hiltViewModel(), -1, -1,
                onNavigateToAddDidi = {
                    navController.navigate("add_didi_graph/$ADD_DIDI_BLANK_ID") {
                        launchSingleTop = true
                    }
                },
                onNavigateToSummary = {

                }
            )
        }

        composable(route = HomeScreens.DIDI_TAB_SCREEN.route) {
            DidiTabScreen(
                navHostController = navController,
                onBackPressed = {
                    finishActivity()
                }) {
                navController.navigateToSettingScreen()
            }
        }

        composable(route = HomeScreens.DATA_TAB_SCREEN.route) {
            DataTabScreen(
                dataTabScreenViewModel = hiltViewModel(),
                navHostController = navController,
                onBackPressed = {
                    finishActivity()
                }) {
                navController.navigateToSettingScreen()
            }
        }

        detailsNavGraph(navController = navController)
        addDidiNavGraph(navController = navController)
        socialMappingNavGraph(navController = navController)
        wealthRankingNavGraph(navController = navController)
        patNavGraph(navController = navController)
        settingNavGraph(navController = navController)
        voEndorsmentNavGraph(navController = navController)
        logoutGraph(navController = navController, prefRepo)
        bpcDidiListNavGraph(navController = navController)
        BSNavHomeGraph(navController)
        MatNavigation(
            navController = navController,
            onSettingIconClick = { navController.navigateToSettingScreen() },
            onBackPressed = {
                finishActivity()
            },
            onNavigateToBaselineMission = { mission: MissionUiModel ->
                navController.navigate("$MISSION_SUMMARY_SCREEN_ROUTE_NAME/${mission.missionId}/${mission.description}")
            }
        )
        SmallGroupNavigation(
            navController = navController,
            onSettingIconClick = { navController.navigateToSettingScreen() })
    }
}

fun finishActivity() {
    (CoreAppDetails.getApplicationDetails()?.activity)?.finish()
}

fun NavHostController.navigateToSettingScreen() {
    this.navigate(NudgeNavigationGraph.SETTING_GRAPH)
}

/*sealed class HomeScreens(val route: String) {
    object PROGRESS_SCREEN : HomeScreens(route = "progress_screen")

    object BPC_PROGRESS_SCREEN : HomeScreens(route = "bpc_progress_screen")

    object DIDI_SCREEN : HomeScreens(route = "didi_screen/{$ARG_PAGE_FROM}")

    object DIDI_TAB_SCREEN : HomeScreens("didi_tab_screen")

    object VILLAGE_SELECTION_SCREEN : HomeScreens(route = "home_village_selection_screen")
}*/

fun NavGraphBuilder.detailsNavGraph(navController: NavHostController) {
    navigation(
        route = NudgeNavigationGraph.DETAILS,
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
                navController.navigate(NudgeNavigationGraph.HOME_SUB_GRAPH) {
                    popUpTo(HomeScreens.PROGRESS_SEL_SCREEN.route) {
                        inclusive = true
                        saveState = false
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
        route = NudgeNavigationGraph.ADD_DIDI,
        startDestination = DetailsScreen.ADD_DIDI_SCREEN.route,
        arguments = listOf(navArgument(ARG_DIDI_DETAILS_ID) {
            type = NavType.IntType
        })
    ) {
        composable(route = DetailsScreen.ADD_DIDI_SCREEN.route,
            arguments = listOf(navArgument(ARG_DIDI_DETAILS_ID) {
                type = NavType.IntType
            })
        ) {
            AddDidiScreen(
                navController = navController,
                modifier = Modifier
                    .fillMaxSize(),
                didiDetailId = it.arguments?.getInt(ARG_DIDI_DETAILS_ID) ?: 0,
                didiViewModel = hiltViewModel(),
            ) {
                navController.popBackStack()
            }
        }
    }
}


fun NavGraphBuilder.socialMappingNavGraph(navController: NavHostController) {
    navigation(
        route = NudgeNavigationGraph.SOCIAL_MAPPING,
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
                stepId = it.arguments?.getInt(ARG_STEP_ID) ?: -1,
                onNavigateToAddDidi = {
                    navController.navigate("add_didi_graph/$ADD_DIDI_BLANK_ID") {
                        launchSingleTop = true
                    }
                },
                onNavigateToSummary = {

                }
            )
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
                navController.navigate(NudgeNavigationGraph.HOME_SUB_GRAPH) {
                    popUpTo(HomeScreens.PROGRESS_SEL_SCREEN.route) {
                        inclusive = true
                        saveState = false
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
        route = NudgeNavigationGraph.WEALTH_RANKING,
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
        route = NudgeNavigationGraph.PAT_SCREENS,
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
                stepId = it.arguments?.getInt(ARG_STEP_ID) ?: -1,
                onNavigateToAddDidi = {
                    navController.navigate("add_didi_graph/$ADD_DIDI_BLANK_ID") {
                        launchSingleTop = true
                    }
                },
                onNavigateToSummary = {
                    navController.navigate("pat_survey_summary/${it.arguments?.getInt(ARG_STEP_ID)}/true")
                }
            )
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
            }, navArgument(ARG_SECTION_TYPE){
                type = NavType.StringType
            }, navArgument(ARG_QUESTION_INDEX){
                type = NavType.IntType
            })
        ) {
            QuestionScreen(
                navController = navController,
                modifier = Modifier.fillMaxSize(),
                viewModel = hiltViewModel(),
                didiId = it.arguments?.getInt(ARG_DIDI_ID) ?: 0,
                sectionType = it.arguments?.getString(ARG_SECTION_TYPE) ?: TYPE_EXCLUSION,
                questionIndex = it.arguments?.getInt(ARG_QUESTION_INDEX) ?: 0
            )
        }

        composable(route = PatScreens.SINGLE_QUESTION_SCREEN.route,
            listOf(navArgument(ARG_DIDI_ID){
                type = NavType.IntType
            }, navArgument(ARG_SECTION_TYPE){
                type = NavType.StringType
            }, navArgument(ARG_QUESTION_INDEX){
                type = NavType.IntType
            })
        ){
          SingleQuestionScreen(
              navController = navController,
              modifier = Modifier.fillMaxSize(),
              viewModel = hiltViewModel(),
              didiId = it.arguments?.getInt(ARG_DIDI_ID) ?: 0,
              sectionType = it.arguments?.getString(ARG_SECTION_TYPE) ?: TYPE_EXCLUSION,
              questionIndex = it.arguments?.getInt(ARG_QUESTION_INDEX) ?: 0
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

        composable(
            route = PatScreens.PAT_SECTION_TWO_SUMMARY_SCREEN.route,
            listOf(navArgument(ARG_DIDI_ID) {
                type = NavType.IntType
            })
        ) {
            PatSurvaySectionTwoSummaryScreen(
                navController = navController,
                modifier = Modifier
                    .fillMaxSize(),
                patSectionSummaryViewModel = hiltViewModel(),
                didiId = it.arguments?.getInt(ARG_DIDI_ID) ?: 0
            )
        }

        composable(route = PatScreens.PAT_COMPLETE_DIDI_SUMMARY_SCREEN.route,
            arguments = listOf(navArgument(ARG_DIDI_ID) {
                type = NavType.IntType
            }, navArgument(ARG_FROM_SCREEN) {
                type = NavType.StringType
            }
            )
        ) {
            PatSurveyCompleteSummary(
                navController = navController,
                modifier = Modifier
                    .fillMaxSize(),
                patSectionSummaryViewModel = hiltViewModel(),
                didiId = it.arguments?.getInt(ARG_DIDI_ID) ?: 0,
                fromScreen = it.arguments?.getString(ARG_FROM_SCREEN) ?: BLANK_STRING
            )
        }

        composable(
            route = PatScreens.PAT_SURVEY_SUMMARY.route,
            arguments = listOf(navArgument(ARG_STEP_ID) {
                type = NavType.IntType
            },
                navArgument(ARG_IS_STEP_COMPLETE) {
                    type = NavType.BoolType
                }
            )
        ) {
            SurveySummary(navController = navController, surveySummaryViewModel = hiltViewModel(), fromScreen = ARG_FROM_PAT_SURVEY, stepId = it.arguments?.getInt(ARG_STEP_ID) ?: -1, isStepComplete = it.arguments?.getBoolean(ARG_IS_STEP_COMPLETE) ?: false)
        }

        composable(
            route = PatScreens.PAT_STEP_COMPLETION_SCREEN.route,
            arguments = listOf(navArgument(ARG_COMPLETION_MESSAGE) {
                type = NavType.StringType
            })
        ) {
            StepCompletionScreen(
                navController = navController,
                modifier = Modifier,
                message = it.arguments?.getString(ARG_COMPLETION_MESSAGE) ?: ""
            ) {
                navController.navigate(PatScreens.PAT_DIGITAL_FORM_B_SCREEN.route)

            }
        }


        composable(
            route = PatScreens.PAT_DIGITAL_FORM_B_SCREEN.route
        ) {
            DigitalFormBScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(
            route = PatScreens.PAT_SUCCESS_SCREEN.route, arguments = listOf(navArgument(
                ARG_PAT_SUCCESS_MESSAGE
            ) {
                type = NavType.StringType
            })
        ) {
            PatSuccessScreen(
                viewModel = hiltViewModel(),
                navController = navController,
                messages = it.arguments?.getString(
                    ARG_PAT_SUCCESS_MESSAGE
                ) ?: BLANK_STRING
            )
        }
    }
}

sealed class PatScreens(val route: String) {
    object PAT_LIST_SCREEN : PatScreens(route = "pat_list_screen")
    object DIDI_PAT_SUMMARY_SCREEN : PatScreens(route = "didi_pat_summary/{$ARG_DIDI_ID}")

    object YES_NO_QUESTION_SCREEN : PatScreens(route = "yes_no_question_screen/{$ARG_DIDI_ID}/{$ARG_SECTION_TYPE}/{$ARG_QUESTION_INDEX}")
    object SINGLE_QUESTION_SCREEN : PatScreens(route = "single_question_screen/{$ARG_DIDI_ID}/{$ARG_SECTION_TYPE}/{$ARG_QUESTION_INDEX}")
    object STEP_COMPLETION_SCREEN :
        PatScreens(route = "step_completion_screen/{$ARG_COMPLETION_MESSAGE}")

    object PAT_SECTION_ONE_SUMMARY_SCREEN :
        PatScreens(route = "pat_section_one_summary_screen/{$ARG_DIDI_ID}")
    object PAT_SECTION_TWO_SUMMARY_SCREEN :
        PatScreens(route = "pat_section_two_summary_screen/{$ARG_DIDI_ID}")
    object PAT_COMPLETE_DIDI_SUMMARY_SCREEN : PatScreens(route = "pat_complete_didi_summary_screen/{$ARG_DIDI_ID}/{$ARG_FROM_SCREEN}")

    object PAT_SURVEY_SUMMARY : PatScreens(route = "pat_survey_summary/{$ARG_STEP_ID}/{$ARG_IS_STEP_COMPLETE}")

    object PAT_STEP_COMPLETION_SCREEN : PatScreens(route = "pat_step_completion_screen/{$ARG_COMPLETION_MESSAGE}")

    object PAT_DIGITAL_FORM_B_SCREEN : PatScreens(route = "pat_digital_form_b_screen")

    object PAT_SUCCESS_SCREEN : PatScreens(route = "pat_success_screen/{$ARG_PAT_SUCCESS_MESSAGE}")

}

fun NavGraphBuilder.settingNavGraph(navController: NavHostController) {
    navigation(
        route = NudgeNavigationGraph.SETTING_GRAPH,
        startDestination = SettingScreens.SETTING_SCREEN.route
    ) {
        composable(route = SettingScreens.SETTING_SCREEN.route) {
            SettingBSScreen(
                navController = navController,
                viewModel = hiltViewModel()
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
        
        composable(route = SettingScreens.VIDEO_LIST_SCREEN.route) {
            VideoListScreen(navController = navController, modifier = Modifier, viewModel = hiltViewModel())
        }
        
        composable(
            route = SettingScreens.VIDEO_PLAYER_SCREEN.route,
            arguments = listOf(navArgument(ARG_VIDEO_ID){
                type = NavType.IntType
            })
        ) {
            FullscreenView(navController = navController, viewModel =  hiltViewModel(), videoId = it.arguments?.getInt(ARG_VIDEO_ID) ?: -1)
        }

        composable(route = SettingScreens.PROFILE_SCREEN.route) {
            ProfileBSScreen(navController = navController, viewModel = hiltViewModel())
        }

        composable(route = SettingScreens.FORM_A_SCREEN.route) {
            DigitalFormAScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize(),
                fromScreen = ARG_FROM_SETTING
            )
        }
        composable(route = SettingScreens.FORM_B_SCREEN.route) {
            DigitalFormBScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize(),
                fromScreen = ARG_FROM_SETTING
            )
        }

        composable(route = SettingScreens.FORM_C_SCREEN.route) {
            DigitalFormCScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize(),
                fromScreen = ARG_FROM_SETTING
            )
        }
        composable(route = SettingScreens.PDF_VIEWER.route, arguments = listOf(
            navArgument(ARG_FORM_PATH){
                type = NavType.StringType
            }
        )) {
            PdfViewer(filePath = it.arguments?.getString(ARG_FORM_PATH) ?: "", modifier = Modifier, navController = navController)
        }

        composable(route = SettingScreens.IMAGE_VIEWER.route, arguments = listOf(
            navArgument(ARG_IMAGE_PATH) {
                type = NavType.StringType
            }
        )) {
            FormImageViewerScreen(navController = navController, fileName =  it.arguments?.getString(ARG_IMAGE_PATH) ?: "", viewModel = hiltViewModel())
        }

        composable(route = SettingScreens.BACKUP_RECOVERY_SCREEN.route){
            ExportImportScreen(navController = navController, viewModel = hiltViewModel())
        }

    }
}



fun NavGraphBuilder.voEndorsmentNavGraph(navController: NavHostController) {
    navigation(route = NudgeNavigationGraph.VO_ENDORSEMENT_GRAPH,
        startDestination = VoEndorsmentScreeens.VO_ENDORSMENT_LIST_SCREEN.route,
        arguments = listOf(navArgument(ARG_VILLAGE_ID) {
            type = NavType.IntType
        }, navArgument(ARG_STEP_ID) {
            type = NavType.IntType
        }, navArgument(ARG_IS_STEP_COMPLETE) {
            type = NavType.BoolType
        })
    ) {
        composable(
            route = VoEndorsmentScreeens.VO_ENDORSMENT_LIST_SCREEN.route
        ) {
            VoEndorsementStepScreen(viewModel = hiltViewModel(),
                navController = navController,
                modifier = Modifier.fillMaxSize(),
                isStepComplete = it.arguments?.getBoolean(ARG_IS_STEP_COMPLETE) ?: false,
                stepId = it.arguments?.getInt(ARG_STEP_ID) ?: -1){
                navController.navigate("vo_endorsement_survey_summary/${ it.arguments?.getInt(ARG_STEP_ID) ?: -1}/true")
            }
        }

        composable(
            VoEndorsmentScreeens.FORM_PICTURE_SCREEN.route,
        arguments = listOf(
            navArgument(ARG_STEP_ID){
                type = NavType.IntType
            }
        )
        ) {
            FormPictureScreen(navController = navController, formPictureScreenViewModel = hiltViewModel(), stepId = it.arguments?.getInt(
                ARG_STEP_ID) ?: -1)
        }

        composable(
            VoEndorsmentScreeens.VO_ENDORSEMENT_SUMMARY_SCREEN.route,
            arguments = listOf(navArgument(ARG_DIDI_ID){
            type = NavType.IntType
        },navArgument(ARG_DIDI_STATUS){
                type = NavType.IntType
            })) {
            VoEndorsementSummaryScreen( navController=navController,viewModel = hiltViewModel(),
                didiId = it.arguments?.getInt(ARG_DIDI_ID) ?: 0,
                didiStatus = it.arguments?.getInt(ARG_DIDI_STATUS) ?: 0
            )
        }

        composable(
            route = VoEndorsmentScreeens.VO_ENDORSEMENT_SURVEY_SUMMARY.route,
            arguments = listOf(navArgument(ARG_STEP_ID) {
                type = NavType.IntType
            },
                navArgument(ARG_IS_STEP_COMPLETE) {
                    type = NavType.BoolType
                }
            )
        ) {
            SurveySummary(navController = navController, surveySummaryViewModel = hiltViewModel(), fromScreen = ARG_FROM_VO_ENDORSEMENT_SCREEN, stepId = it.arguments?.getInt(ARG_STEP_ID) ?: -1, isStepComplete = it.arguments?.getBoolean(ARG_IS_STEP_COMPLETE) ?: false)
        }

        composable(
            route = VoEndorsmentScreeens.VO_ENDORSEMENT_STEP_COMPLETION_SCREEN.route,
            arguments = listOf(navArgument(ARG_COMPLETION_MESSAGE) {
                type = NavType.StringType
            })
        ) {

            FinalStepCompletionScreen(
                navController = navController,
                modifier = Modifier,
                message = it.arguments?.getString(ARG_COMPLETION_MESSAGE) ?: "")
            {
                navController.navigate(VoEndorsmentScreeens.VO_ENDORSEMENT_DIGITAL_FORM_C_SCREEN.route)
            }
        }

        composable(
            route = VoEndorsmentScreeens.VO_ENDORSEMENT_DIGITAL_FORM_C_SCREEN.route
        ) {
            DigitalFormCScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize(),
            )
        }

        composable(route = VoEndorsmentScreeens.IMAGE_VIEWER.route, arguments = listOf(
            navArgument(ARG_IMAGE_PATH) {
                type = NavType.StringType
            }
        )) {
            FormImageViewerScreen(navController = navController, fileName =  it.arguments?.getString(ARG_IMAGE_PATH) ?: "", viewModel = hiltViewModel())
        }

        composable(route = VoEndorsmentScreeens.FORM_A_SCREEN.route) {
            DigitalFormAScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize(),
                fromScreen = ARG_FROM_SETTING
            )
        }
        composable(route = VoEndorsmentScreeens.FORM_B_SCREEN.route) {
            DigitalFormBScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize(),
                fromScreen = ARG_FROM_SETTING
            )
        }
    }
}

sealed class VoEndorsmentScreeens(val route: String) {
    object VO_ENDORSMENT_LIST_SCREEN : VoEndorsmentScreeens(route = "vo_endorsment_list_screen")

    object FORM_PICTURE_SCREEN : VoEndorsmentScreeens(route = "form_picture_screen/{$ARG_STEP_ID}")

    object  VO_ENDORSEMENT_SUMMARY_SCREEN: VoEndorsmentScreeens(route = "vo_endorsement_summary_screen/{$ARG_DIDI_ID}/{$ARG_DIDI_STATUS}")

    object VO_ENDORSEMENT_SURVEY_SUMMARY: VoEndorsmentScreeens(route = "vo_endorsement_survey_summary/{$ARG_STEP_ID}/{$ARG_IS_STEP_COMPLETE}")

    object VO_ENDORSEMENT_STEP_COMPLETION_SCREEN: VoEndorsmentScreeens(route = "vo_endorsement_step_completion_screen/{$ARG_COMPLETION_MESSAGE}")
    object VO_ENDORSEMENT_DIGITAL_FORM_C_SCREEN : VoEndorsmentScreeens(route = "vo_endorsement_digital_form_c_screen")
    object IMAGE_VIEWER : VoEndorsmentScreeens(route = "vo_image_viewer/{$ARG_IMAGE_PATH}")

    object FORM_A_SCREEN : VoEndorsmentScreeens(route = "vo_form_a_screen")
    object FORM_B_SCREEN : VoEndorsmentScreeens(route = "vo_form_b_screen")

}

fun NavGraphBuilder.logoutGraph(navController: NavHostController,prefRepo: PrefRepo){
    navigation(route = NudgeNavigationGraph.LOGOUT_GRAPH,
        startDestination = LogoutScreens.LOG_LOGIN_SCREEN.route,
    ) {

        composable(route = LogoutScreens.LOG_HOME_SCREEN.route) {
            HomeVillageScreen(
                navController,
                prefRepo = prefRepo
            )
        }
        composable(route = LogoutScreens.LOG_LOGIN_SCREEN.route) {
            LoginScreen(
                navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(
            route = LogoutScreens.LOG_OTP_VERIFICATION.route,
            arguments = listOf(navArgument(ARG_MOBILE_NUMBER) {
                type = NavType.StringType
            })
        ) {
            OtpVerificationScreen(
                navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize(),
                it.arguments?.getString(ARG_MOBILE_NUMBER).toString()
            )
        }

        composable(route = LogoutScreens.LOG_VILLAGE_SELECTION_SCREEN.route) {
            VillageScreen(navController = navController) {
                navController.navigate(NudgeNavigationGraph.SETTING_GRAPH)
            }
        }
        composable(route = LogoutScreens.LOG_DATA_LOADING_SCREEN.route) {
            DataLoadingScreenComponent(viewModel = hiltViewModel(), navController = navController)
        }
    }
}

fun NavGraphBuilder.bpcDidiListNavGraph(navController: NavHostController) {

    navigation(
        route = NudgeNavigationGraph.BPC_GRAPH,
        startDestination = BpcDidiListScreens.BPC_DIDI_LIST.route,
        arguments = listOf(navArgument(ARG_VILLAGE_ID) {
            type = NavType.IntType
        }, navArgument(ARG_STEP_ID) {
            type = NavType.IntType
        })
    ) {
        composable(
            route = BpcDidiListScreens.BPC_DIDI_LIST.route,
            arguments = listOf(navArgument(ARG_VILLAGE_ID) {
                type = NavType.IntType
            }, navArgument(ARG_STEP_ID) {
                type = NavType.IntType
            })
        ) {
            BpcDidiListScreen(
                bpcDidiListViewModel = hiltViewModel(),
                navController = navController,
                villageId = it.arguments?.getInt(ARG_VILLAGE_ID) ?: 0,
                stepId = it.arguments?.getInt(ARG_STEP_ID) ?: -1
            )
        }
        composable(
            route = BpcDidiListScreens.BPC_ADD_MORE_DIDI_LIST.route,
            arguments = listOf(navArgument(ARG_FOR_REPLACEMENT){
                type = NavType.BoolType
            })
        ) {
            BpcAddMoreDidiScreen(
                bpcAddMoreDidiViewModel = hiltViewModel(),
                navController = navController,
                forReplace = it.arguments?.getBoolean(ARG_FOR_REPLACEMENT) ?: false
            )
        }

        composable(
            route = BpcDidiListScreens.YES_NO_QUESTION_SCREEN.route,
            listOf(navArgument(ARG_DIDI_ID) {
                type = NavType.IntType
            }, navArgument(ARG_SECTION_TYPE){
                type = NavType.StringType
            }, navArgument(ARG_QUESTION_INDEX){
                type = NavType.IntType
            })
        ) {
            QuestionScreen(
                navController = navController,
                modifier = Modifier.fillMaxSize(),
                viewModel = hiltViewModel(),
                didiId = it.arguments?.getInt(ARG_DIDI_ID) ?: 0,
                sectionType = it.arguments?.getString(ARG_SECTION_TYPE) ?: TYPE_EXCLUSION,
                questionIndex = it.arguments?.getInt(ARG_QUESTION_INDEX) ?: 0
            )
        }

        composable(
            route = BpcDidiListScreens.SINGLE_QUESTION_SCREEN.route,
            listOf(navArgument(ARG_DIDI_ID) {
                type = NavType.IntType
            }, navArgument(ARG_SECTION_TYPE){
                type = NavType.StringType
            }, navArgument(ARG_QUESTION_INDEX){
                type = NavType.IntType
            })
        ) {
            SingleQuestionScreen(
                navController = navController,
                modifier = Modifier.fillMaxSize(),
                viewModel = hiltViewModel(),
                didiId = it.arguments?.getInt(ARG_DIDI_ID) ?: 0,
                sectionType = it.arguments?.getString(ARG_SECTION_TYPE) ?: TYPE_EXCLUSION,
                questionIndex = it.arguments?.getInt(ARG_QUESTION_INDEX) ?: 0
            )
        }

        composable(
            route = BpcDidiListScreens.PAT_SECTION_ONE_SUMMARY_SCREEN.route,
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

        composable(
            route = BpcDidiListScreens.PAT_SECTION_TWO_SUMMARY_SCREEN.route,
            listOf(navArgument(ARG_DIDI_ID) {
                type = NavType.IntType
            })
        ) {
            PatSurvaySectionTwoSummaryScreen(
                navController = navController,
                modifier = Modifier
                    .fillMaxSize(),
                patSectionSummaryViewModel = hiltViewModel(),
                didiId = it.arguments?.getInt(ARG_DIDI_ID) ?: 0
            )
        }

        composable(route = BpcDidiListScreens.PAT_COMPLETE_DIDI_SUMMARY_SCREEN.route,
            arguments = listOf(navArgument(ARG_DIDI_ID) {
                type = NavType.IntType
            }, navArgument(ARG_FROM_SCREEN) {
                type = NavType.StringType
            }
            )
        ) {
            PatSurveyCompleteSummary(
                navController = navController,
                modifier = Modifier
                    .fillMaxSize(),
                patSectionSummaryViewModel = hiltViewModel(),
                didiId = it.arguments?.getInt(ARG_DIDI_ID) ?: 0,
                fromScreen = it.arguments?.getString(ARG_FROM_SCREEN) ?: BLANK_STRING
            )
        }

        composable(
            route = BpcDidiListScreens.PAT_SURVEY_SUMMARY.route,
            arguments = listOf(navArgument(ARG_STEP_ID) {
                type = NavType.IntType
            },
                navArgument(ARG_IS_STEP_COMPLETE) {
                    type = NavType.BoolType
                }
            )
        ) {
            SurveySummary(navController = navController, surveySummaryViewModel = hiltViewModel(), fromScreen = ARG_FROM_PAT_SURVEY, stepId = it.arguments?.getInt(ARG_STEP_ID) ?: -1, isStepComplete = it.arguments?.getBoolean(ARG_IS_STEP_COMPLETE) ?: false)
        }

        composable(route = BpcDidiListScreens.DIDI_PAT_SUMMARY_SCREEN.route,
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
            route = BpcDidiListScreens.PAT_STEP_COMPLETION_SCREEN.route,
            arguments = listOf(navArgument(ARG_COMPLETION_MESSAGE) {
                type = NavType.StringType
            })
        ) {
            StepCompletionScreen(
                navController = navController,
                modifier = Modifier,
                message = it.arguments?.getString(ARG_COMPLETION_MESSAGE) ?: ""
            ) {
                navController.navigate(BpcDidiListScreens.BPC_SCORE_COMPARISION_SCREEN.route)
            }
        }

        composable(route = BpcDidiListScreens.BPC_SCORE_COMPARISION_SCREEN.route){
            ScoreComparisionScreen(navController = navController, viewModel = hiltViewModel())
        }


    }
}

sealed class BpcDidiListScreens(val route: String) {
    object BPC_DIDI_LIST : BpcDidiListScreens(route = "bpc_did_list")

    object BPC_ADD_MORE_DIDI_LIST : BpcDidiListScreens(route = "bpc_add_more_didi_list/{$ARG_FOR_REPLACEMENT}")

    object DIDI_PAT_SUMMARY_SCREEN : BpcDidiListScreens(route = "bcp_didi_pat_summary/{$ARG_DIDI_ID}")

    object YES_NO_QUESTION_SCREEN : BpcDidiListScreens(route = "bpc_yes_no_question_screen/{$ARG_DIDI_ID}/{$ARG_SECTION_TYPE}/{$ARG_QUESTION_INDEX}")
    object SINGLE_QUESTION_SCREEN : PatScreens(route = "bpc_single_question_screen/{$ARG_DIDI_ID}/{$ARG_SECTION_TYPE}/{$ARG_QUESTION_INDEX}")
    object STEP_COMPLETION_SCREEN :
        BpcDidiListScreens(route = "step_completion_screen/{$ARG_COMPLETION_MESSAGE}")

    object PAT_SECTION_ONE_SUMMARY_SCREEN :
        BpcDidiListScreens(route = "bpc_pat_section_one_summary_screen/{$ARG_DIDI_ID}")
    object PAT_SECTION_TWO_SUMMARY_SCREEN :
        BpcDidiListScreens(route = "bpc_pat_section_two_summary_screen/{$ARG_DIDI_ID}")
    object PAT_COMPLETE_DIDI_SUMMARY_SCREEN : BpcDidiListScreens(route = "bpc_pat_complete_didi_summary_screen/{$ARG_DIDI_ID}/{$ARG_FROM_SCREEN}")

    object PAT_SURVEY_SUMMARY : BpcDidiListScreens(route = "bpc_pat_survey_summary/{$ARG_STEP_ID}/{$ARG_IS_STEP_COMPLETE}")

    object PAT_STEP_COMPLETION_SCREEN : BpcDidiListScreens(route = "bpc_pat_step_completion_screen/{$ARG_COMPLETION_MESSAGE}")

    object BPC_SCORE_COMPARISION_SCREEN: BpcDidiListScreens(route = "bpc_score_comparison_screen")

}
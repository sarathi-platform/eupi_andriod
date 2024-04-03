package com.patsurvey.nudge.navigation.baseline

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.nrlm.baselinesurvey.ARG_ACTIVITY_DATE
import com.nrlm.baselinesurvey.ARG_ACTIVITY_ID
import com.nrlm.baselinesurvey.ARG_COMPLETION_MESSAGE
import com.nrlm.baselinesurvey.ARG_DIDI_ID
import com.nrlm.baselinesurvey.ARG_FROM_SCREEN
import com.nrlm.baselinesurvey.ARG_FROM_SECTION_SCREEN
import com.nrlm.baselinesurvey.ARG_MISSION_DATE
import com.nrlm.baselinesurvey.ARG_MISSION_ID
import com.nrlm.baselinesurvey.ARG_MISSION_NAME
import com.nrlm.baselinesurvey.ARG_MOBILE_NUMBER
import com.nrlm.baselinesurvey.ARG_QUESTION_ID
import com.nrlm.baselinesurvey.ARG_QUESTION_NAME
import com.nrlm.baselinesurvey.ARG_SECTION_ID
import com.nrlm.baselinesurvey.ARG_SURVEY_ID
import com.nrlm.baselinesurvey.ARG_VIDEO_PATH
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.ui.auth.presentation.LoginScreenComponent
import com.nrlm.baselinesurvey.ui.auth.presentation.OtpVerificationScreenComponent
import com.nrlm.baselinesurvey.ui.common_components.FinalStepCompletionScreen
import com.nrlm.baselinesurvey.ui.common_components.StepCompletionScreen
import com.nrlm.baselinesurvey.ui.form_response_summary_screen.presentation.FormQuestionSummaryScreen
import com.nrlm.baselinesurvey.ui.language.presentation.LanguageScreenComponent
import com.nrlm.baselinesurvey.ui.mission_screen.presentation.MissionScreen_1
import com.nrlm.baselinesurvey.ui.mission_summary_screen.presentation.MissionSummaryScreen
import com.nrlm.baselinesurvey.ui.profile.presentation.ProfileBSScreen
import com.nrlm.baselinesurvey.ui.question_screen.presentation.QuestionScreenHandler
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.FormTypeQuestionScreen
import com.nrlm.baselinesurvey.ui.search.presentation.SearchScreens
import com.nrlm.baselinesurvey.ui.section_screen.presentation.SectionListScreen
import com.nrlm.baselinesurvey.ui.splash.presentaion.SplashScreenComponent
import com.nrlm.baselinesurvey.ui.start_screen.presentation.BaseLineStartScreen
import com.nrlm.baselinesurvey.ui.surveyee_screen.presentation.DataLoadingScreenComponent
import com.nrlm.baselinesurvey.ui.surveyee_screen.presentation.SurveyeeListScreen
import com.nrlm.baselinesurvey.ui.video_player.presentation.FullscreenView
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nudge.core.ui.navigation.BASELINE_START_SCREEN_ROUTE_NAME
import com.nudge.core.ui.navigation.BSHomeScreens
import com.nudge.core.ui.navigation.CoreGraph
import com.nudge.core.ui.navigation.FORM_QUESTION_SUMMARY_SCREEN_ROUTE_NAME
import com.nudge.core.ui.navigation.FORM_TYPE_QUESTION_SCREEN_ROUTE_NAME
import com.nudge.core.ui.navigation.QUESTION_SCREEN_ROUTE_NAME
import com.nudge.core.ui.navigation.SEARCH_SCREEN_ROUTE_NAME
import com.nudge.core.ui.navigation.SECTION_SCREEN_ROUTE_NAME
import com.nudge.core.ui.navigation.navigateBackToMissionScreen
import com.patsurvey.nudge.navigation.selection.logoutGraph
import com.patsurvey.nudge.navigation.selection.settingNavGraph

@Composable
fun BSNavHomeGraph(navController: NavHostController, modifier: Modifier) {
    NavHost(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        navController = navController,
        route = CoreGraph.BASE_HOME,
        startDestination = BSHomeScreens.DATA_LOADING_SCREEN.route
    ) {

        composable(route = BSHomeScreens.DATA_LOADING_SCREEN.route) {
            DataLoadingScreenComponent(viewModel = hiltViewModel(), navController = navController)
        }
        composable(route = BSHomeScreens.MISSION_SUMMARY_SCREEN.route, arguments = listOf(
            navArgument(name = ARG_MISSION_ID) {
                type = NavType.IntType
            },
            navArgument(name = ARG_MISSION_NAME) {
                type = NavType.StringType
            }, navArgument(name = ARG_MISSION_DATE) {
                type = NavType.StringType
            }
        )) {
            MissionSummaryScreen(
                navController = navController, missionId = it.arguments?.getInt(
                    ARG_MISSION_ID
                ) ?: 0, missionName = it.arguments?.getString(ARG_MISSION_NAME) ?: "",
                missionDate = it.arguments?.getString(ARG_MISSION_DATE) ?: ""
            )
        }

        composable(route = BSHomeScreens.SURVEYEE_LIST_SCREEN.route) {
            SurveyeeListScreen(
                viewModel = hiltViewModel(),
                navController = navController,
                activityName = "",
                missionId = 0,
                activityDate = "",
                activityId = 0
            )
        }

        composable(
            route = BSHomeScreens.SECTION_SCREEN.route, arguments = listOf(
                navArgument(
                    name = ARG_DIDI_ID
                ) {
                    type = NavType.IntType
                }, navArgument(
                    name = ARG_SURVEY_ID
                ) {
                    type = NavType.IntType
                }
            )) {
            SectionListScreen(
                navController, viewModel = hiltViewModel(), didiId = it.arguments?.getInt(
                    ARG_DIDI_ID
                ) ?: 0,
                surveyId = it.arguments?.getInt(ARG_SURVEY_ID) ?: 0
            )
        }

        composable(route = BSHomeScreens.QUESTION_SCREEN.route, arguments = listOf(
            navArgument(
                name = ARG_SECTION_ID
            ) {
                type = NavType.IntType
            },
            navArgument(
                name = ARG_DIDI_ID
            ) {
                type = NavType.IntType
            },
            navArgument(
                name = ARG_SURVEY_ID
            ) {
                type = NavType.IntType
            }
        )) {
            QuestionScreenHandler(navController = navController,
                viewModel = hiltViewModel(),
                didiId = it.arguments?.getInt(
                    ARG_DIDI_ID
                ) ?: 0,
                sectionId = it.arguments?.getInt(
                    ARG_SECTION_ID
                ) ?: 0,
                surveyId = it.arguments?.getInt(
                    ARG_SURVEY_ID
                ) ?: 0
            )

            /*QuestionScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                surveyeeId = it.arguments?.getInt(
                    ARG_DIDI_ID
                ) ?: 0,
                sectionId = it.arguments?.getInt(
                    ARG_SECTION_ID
                ) ?: 0
            )*/
        }


        composable(
            route = BSHomeScreens.VIDEO_PLAYER_SCREEN.route, arguments = listOf(
                navArgument(
                    name = ARG_VIDEO_PATH
                ) {
                    type = NavType.StringType
                }
            )
        ) {
            FullscreenView(
                navController = navController,
                videoPath = it.arguments?.getString(ARG_VIDEO_PATH) ?: BLANK_STRING
            )
        }
        composable(route = BSHomeScreens.FormTypeQuestionScreen.route, arguments = listOf(
            navArgument(name = ARG_QUESTION_NAME) {
                type = NavType.StringType
            }, navArgument(name = ARG_SURVEY_ID) {
                type = NavType.IntType
            },
            navArgument(name = ARG_SECTION_ID) {
                type = NavType.IntType
            },
            navArgument(name = ARG_QUESTION_ID) {
                type = NavType.IntType
            },
            navArgument(name = ARG_DIDI_ID) {
                type = NavType.IntType
            },

        )) {
            FormTypeQuestionScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                it.arguments?.getString(ARG_QUESTION_NAME) ?: "",
                it.arguments?.getInt(ARG_SURVEY_ID) ?: -1,
                it.arguments?.getInt(ARG_SECTION_ID) ?: -1,
                it.arguments?.getInt(ARG_QUESTION_ID) ?: -1,
                surveyeeId = it.arguments?.getInt(ARG_DIDI_ID) ?: -1,
                referenceId = BaselineCore.getReferenceId()
            )
        }
        composable(route = BSHomeScreens.BaseLineStartScreen.route, arguments = listOf(
            navArgument(
                name = ARG_DIDI_ID
            ) {
                type = NavType.IntType
            }, navArgument(
                name = ARG_SURVEY_ID
            ) {
                type = NavType.IntType
            }
        )) {
            BaseLineStartScreen(
                navController = navController,
                baseLineStartViewModel = hiltViewModel(),
                it.arguments?.getInt(ARG_DIDI_ID) ?: -1,
                surveyId = it.arguments?.getInt(ARG_SURVEY_ID) ?: -1
            )
        }

        composable(route = BSHomeScreens.SearchScreen.route, arguments = listOf(
            navArgument(
                name = ARG_SURVEY_ID
            ) {
                type = NavType.IntType
            },
            navArgument(
                name = ARG_DIDI_ID
            ) {
                type = NavType.IntType
            },
            navArgument(
                name = ARG_FROM_SCREEN
            ) {
                type = NavType.StringType
            }
        )) {
            SearchScreens(viewModel = hiltViewModel(), navController = navController, surveyId = it
                .arguments?.getInt(ARG_SURVEY_ID) ?: -1, surveyeeId = it.arguments?.getInt(ARG_DIDI_ID) ?: -1, fromScreen = it.arguments?.getString(
                ARG_FROM_SCREEN) ?: ARG_FROM_SECTION_SCREEN)
        }

        composable(route = BSHomeScreens.Home_SCREEN.route) {
            //  HomeScreen(navController=navController)
            //   MissionScreen(navController = navController, viewModel = hiltViewModel())
            MissionScreen_1(navController = navController, viewModel = hiltViewModel())
        }

        composable(route = BSHomeScreens.MISSION_SCREEN.route) {
            // MissionScreen(navController = navController, viewModel = hiltViewModel())
            MissionScreen_1(navController = navController, viewModel = hiltViewModel())

        }

        composable(route = BSHomeScreens.DIDI_SCREEN.route) {
            SurveyeeListScreen(
                viewModel = hiltViewModel(),
                navController = navController,
                activityName = "",
                missionId = 0,
                activityDate = "",
                activityId = 0
            )
        }
        composable(
            route = BSHomeScreens.Final_StepComplitionScreen.route,
            arguments = listOf(navArgument(ARG_COMPLETION_MESSAGE) {
                type = NavType.StringType
            })
        ) {
            FinalStepCompletionScreen(
                navController = navController,
                modifier = Modifier,
                message = it.arguments?.getString(ARG_COMPLETION_MESSAGE) ?: ""
            )
            {
                navController.popBackStack()
                // navController.navigate(HomeScreens.VO_ENDORSEMENT_DIGITAL_FORM_C_SCREEN.route)
            }
        }

        composable(
            route = BSHomeScreens.STEP_COMPLETION_SCREEN.route,
            arguments = listOf(navArgument(ARG_COMPLETION_MESSAGE) {
                type = NavType.StringType
            })
        ) {
            StepCompletionScreen(
                navController = navController,
                modifier = Modifier,
                message = it.arguments?.getString(ARG_COMPLETION_MESSAGE) ?: ""
            ) {
                navController.navigateBackToMissionScreen()
            }
        }

        composable(
            route = BSHomeScreens.FORM_QUESTION_SUMMARY_SCREEN.route,
            arguments = listOf(
                navArgument(ARG_SURVEY_ID) {
                    type = NavType.IntType
                },
                navArgument(ARG_SECTION_ID) {
                    type = NavType.IntType
                },
                navArgument(ARG_QUESTION_ID) {
                    type = NavType.IntType
                },
                navArgument(ARG_DIDI_ID) {
                    type = NavType.IntType
                }
            )
        ) {
            FormQuestionSummaryScreen(
                formResponseSummaryScreenViewModel = hiltViewModel(),
                navController = navController,
                surveyId = it.arguments?.getInt(ARG_SURVEY_ID) ?: 0,
                sectionId = it.arguments?.getInt(ARG_SECTION_ID) ?: 0,
                questionId = it.arguments?.getInt(ARG_QUESTION_ID) ?: 0,
                surveyeeId = it.arguments?.getInt(ARG_DIDI_ID) ?: 0
            )

        }

        missionSummaryGraph(navController = navController)
        settingNavGraph(navController=navController)
        logoutGraph(navController =navController)
    }

}


fun NavGraphBuilder.missionSummaryGraph(navController: NavHostController) {
    navigation(
        route = CoreGraph.MISSION_SUMMARY_GRAPH,
        startDestination = BSHomeScreens.SURVEYEE_LIST_SCREEN.route, arguments = listOf(
            navArgument(
                name = ARG_ACTIVITY_ID
            ) {
                type = NavType.StringType
            }, navArgument(
                name = ARG_MISSION_ID
            ) {
                type = NavType.IntType
            }, navArgument(
                name = ARG_ACTIVITY_DATE
            ) {
                type = NavType.StringType
            }, navArgument(
                name = ARG_SURVEY_ID
            ) {
                type = NavType.IntType
            }
        )
    ) {
        composable(
            route = BSHomeScreens.SURVEYEE_LIST_SCREEN.route
        ) {
            SurveyeeListScreen(
                viewModel = hiltViewModel(),
                navController = navController,
                activityName = it.arguments?.getString(
                    ARG_ACTIVITY_ID
                ) ?: "",
                missionId = it.arguments?.getInt(ARG_MISSION_ID) ?: 0,
                activityDate = it.arguments?.getString(ARG_ACTIVITY_DATE) ?: "",
                activityId = it.arguments?.getInt(ARG_SURVEY_ID) ?: 0
            )
        }

    }

}












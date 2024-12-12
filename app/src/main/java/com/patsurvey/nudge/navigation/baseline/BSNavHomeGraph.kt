package com.patsurvey.nudge.navigation.baseline

import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.nrlm.baselinesurvey.ARG_ACTIVITY_DATE
import com.nrlm.baselinesurvey.ARG_ACTIVITY_ID
import com.nrlm.baselinesurvey.ARG_COMPLETION_MESSAGE
import com.nrlm.baselinesurvey.ARG_DIDI_ID
import com.nrlm.baselinesurvey.ARG_FROM_SCREEN
import com.nrlm.baselinesurvey.ARG_FROM_SECTION_SCREEN
import com.nrlm.baselinesurvey.ARG_MISSION_ID
import com.nrlm.baselinesurvey.ARG_MISSION_NAME
import com.nrlm.baselinesurvey.ARG_QUESTION_ID
import com.nrlm.baselinesurvey.ARG_SECTION_ID
import com.nrlm.baselinesurvey.ARG_SUB_MISSION_DETAIL_NAME
import com.nrlm.baselinesurvey.ARG_SUB_MISSION_NAME
import com.nrlm.baselinesurvey.ARG_SURVEY_ID
import com.nrlm.baselinesurvey.ARG_VIDEO_PATH
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.ui.common_components.FinalStepCompletionScreen
import com.nrlm.baselinesurvey.ui.common_components.StepCompletionScreen
import com.nrlm.baselinesurvey.ui.form_response_summary_screen.presentation.FormQuestionSummaryScreen
import com.nrlm.baselinesurvey.ui.mission_screen.presentation.MissionScreen
import com.nrlm.baselinesurvey.ui.mission_summary_screen.presentation.MissionSummaryScreen
import com.nrlm.baselinesurvey.ui.question_screen.presentation.QuestionScreenHandler
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.FormTypeQuestionScreen
import com.nrlm.baselinesurvey.ui.search.presentation.SearchScreens
import com.nrlm.baselinesurvey.ui.section_screen.presentation.SectionListScreen
import com.nrlm.baselinesurvey.ui.start_screen.presentation.BaseLineStartScreen
import com.nrlm.baselinesurvey.ui.surveyee_screen.presentation.DataLoadingScreenComponent
import com.nrlm.baselinesurvey.ui.surveyee_screen.presentation.SurveyeeListScreen
import com.nrlm.baselinesurvey.ui.video_player.presentation.FullscreenView
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nudge.navigationmanager.graphs.HomeScreens
import com.nudge.navigationmanager.graphs.LogoutScreens
import com.nudge.navigationmanager.graphs.NudgeNavigationGraph
import com.nudge.navigationmanager.utils.NavigationParams


fun NavGraphBuilder.BSNavHomeGraph(navController: NavHostController) {
    navigation(
        route = NudgeNavigationGraph.BASE_HOME,
        startDestination = HomeScreens.DATA_LOADING_SCREEN.route
    ) {

        composable(route = LogoutScreens.LOG_DATA_LOADING_SCREEN.route,
            arguments = listOf(
                navArgument(ARG_MISSION_ID) {
                    type = NavType.IntType
                },
                navArgument(ARG_MISSION_NAME) {
                    type = NavType.StringType
                },
                navArgument(ARG_SUB_MISSION_NAME) {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument(ARG_SUB_MISSION_DETAIL_NAME) {
                    type = NavType.StringType
                    nullable = true
                }
                )
        ) {
            DataLoadingScreenComponent(
                viewModel = hiltViewModel(),
                navController = navController,
                missionId = it.arguments?.getInt(ARG_MISSION_ID) ?: -1,
                missionDescription = it.arguments?.getString(ARG_MISSION_NAME)
                    ?: com.patsurvey.nudge.utils.BLANK_STRING,
                missionSubDescription = it.arguments?.getString(ARG_SUB_MISSION_NAME)
                    ?: BLANK_STRING,
                missionSubDescriptionDetail = it.arguments?.getString(ARG_SUB_MISSION_DETAIL_NAME)
                    ?: BLANK_STRING
                )
        }
        composable(route = HomeScreens.MISSION_SUMMARY_SCREEN.route, arguments = listOf(
            navArgument(name = ARG_MISSION_ID) {
                type = NavType.IntType
            },
            navArgument(name = ARG_MISSION_NAME) {
                type = NavType.StringType
            },
            navArgument(name = ARG_MISSION_NAME) {
                type = NavType.StringType
            },
            navArgument(name = ARG_SUB_MISSION_NAME) {
                type = NavType.StringType
                nullable = true
            },
            navArgument(name = ARG_SUB_MISSION_DETAIL_NAME) {
                type = NavType.StringType
                nullable = true
            }
        )) {
            MissionSummaryScreen(
                navController = navController, missionId = it.arguments?.getInt(
                    ARG_MISSION_ID
                ) ?: 0,
                missionName = it.arguments?.getString(ARG_MISSION_NAME) ?: BLANK_STRING,
                missionSubName = it.arguments?.getString(ARG_SUB_MISSION_NAME) ?: BLANK_STRING,
                missionSubNameDetail = it.arguments?.getString(ARG_SUB_MISSION_DETAIL_NAME)
                    ?: BLANK_STRING
            )
        }

        composable(route = HomeScreens.SURVEYEE_LIST_SCREEN.route) {
            SurveyeeListScreen(
                viewModel = hiltViewModel(),
                navController = navController,
                activityName = "",
                missionId = 0,
                activityDate = "",
                activityId = 0,
                missionSubTitle = ""
            )
        }

        composable(
            route = HomeScreens.SECTION_SCREEN.route, arguments = listOf(
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

        composable(route = HomeScreens.QUESTION_SCREEN.route, arguments = listOf(
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
        }


        composable(
            route = HomeScreens.VIDEO_PLAYER_SCREEN.route, arguments = listOf(
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
        composable(route = HomeScreens.FormTypeQuestionScreen.route, arguments = listOf(
            navArgument(name = ARG_SURVEY_ID) {
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
                surveyID = it.arguments?.getInt(ARG_SURVEY_ID) ?: -1,
                sectionId = it.arguments?.getInt(ARG_SECTION_ID) ?: -1,
                questionId = it.arguments?.getInt(ARG_QUESTION_ID) ?: -1,
                surveyeeId = it.arguments?.getInt(ARG_DIDI_ID) ?: -1,
                referenceId = BaselineCore.getReferenceId()
            )
        }
        composable(route = HomeScreens.BaseLineStartScreen.route, arguments = listOf(
            navArgument(
                name = ARG_DIDI_ID
            ) {
                type = NavType.IntType
            }, navArgument(
                name = ARG_SURVEY_ID
            ) {
                type = NavType.IntType
            }, navArgument(
                name = ARG_SECTION_ID
            ) {
                type = NavType.IntType
            }
        )) {
            BaseLineStartScreen(
                navController = navController,
                baseLineStartViewModel = hiltViewModel(),
                it.arguments?.getInt(ARG_DIDI_ID) ?: -1,
                surveyId = it.arguments?.getInt(ARG_SURVEY_ID) ?: -1,
                sectionId = it.arguments?.getInt(ARG_SECTION_ID) ?: -1
            )
        }

        composable(route = HomeScreens.SearchScreen.route, arguments = listOf(
            navArgument(
                name = ARG_SURVEY_ID
            ) {
                type = NavType.IntType
            },
            navArgument(name = NavigationParams.ARG_SECTION_ID.value) {
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
            SearchScreens(
                viewModel = hiltViewModel(),
                navController = navController,
                surveyId = it
                    .arguments?.getInt(ARG_SURVEY_ID) ?: -1,
                sectionId = it.arguments?.getInt(NavigationParams.ARG_SECTION_ID.value) ?: 0,
                surveyeeId = it.arguments?.getInt(ARG_DIDI_ID) ?: -1,
                fromScreen = it.arguments?.getString(
                    ARG_FROM_SCREEN
                ) ?: ARG_FROM_SECTION_SCREEN
            )
        }

//        composable(route = HomeScreens.Home_SCREEN.route) {
//            MissionScreen(navController = navController, viewModel = hiltViewModel())
//        }

        composable(route = HomeScreens.MISSION_SCREEN.route) {
            MissionScreen(navController = navController, viewModel = hiltViewModel())
        }

        composable(route = HomeScreens.BS_DIDI_DETAILS_SCREEN.route) {
            SurveyeeListScreen(
                viewModel = hiltViewModel(),
                navController = navController,
                activityName = "",
                missionId = 0,
                activityDate = "",
                activityId = 0,
                missionSubTitle = ""
            )
        }
        composable(
            route = HomeScreens.Final_StepComplitionScreen.route,
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
            }
        }

        composable(
            route = HomeScreens.STEP_COMPLETION_SCREEN.route,
            arguments = listOf(navArgument(ARG_COMPLETION_MESSAGE) {
                type = NavType.StringType
            })
        ) {
            StepCompletionScreen(
                navController = navController,
                modifier = Modifier,
                message = it.arguments?.getString(ARG_COMPLETION_MESSAGE) ?: ""
            ) {
                navController.navigateUp()
                navController.navigateUp()
                navController.navigateUp()
//                navController.navigateBackToMissionScreen()
            }
        }

        composable(
            route = HomeScreens.FORM_QUESTION_SUMMARY_SCREEN.route,
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

        composable(
            route = HomeScreens.SURVEYEE_LIST_SCREEN_WITH_PARAMS.route,
            arguments = listOf(
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
                },
                navArgument(name = ARG_SUB_MISSION_NAME) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ){
            SurveyeeListScreen(
                viewModel = hiltViewModel(),
                navController = navController,
                activityName = it.arguments?.getString(
                    ARG_ACTIVITY_ID
                ) ?: "",
                missionId = it.arguments?.getInt(ARG_MISSION_ID) ?: 0,
                activityDate = it.arguments?.getString(ARG_ACTIVITY_DATE) ?: "",
                missionSubTitle = it.arguments?.getString(ARG_SUB_MISSION_NAME) ?: "",
                activityId = it.arguments?.getInt(ARG_SURVEY_ID) ?: 0
            )
        }

    }

}
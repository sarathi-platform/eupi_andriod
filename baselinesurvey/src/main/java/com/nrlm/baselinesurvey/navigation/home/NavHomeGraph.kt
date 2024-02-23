package com.nrlm.baselinesurvey.navigation.home

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
import com.nrlm.baselinesurvey.ARG_FORM_QUESTION_RESPONSE_REFERENCE_ID
import com.nrlm.baselinesurvey.ARG_FROM_HOME
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
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.navigation.AuthScreen
import com.nrlm.baselinesurvey.navigation.navgraph.Graph
import com.nrlm.baselinesurvey.ui.auth.presentation.LoginScreenComponent
import com.nrlm.baselinesurvey.ui.auth.presentation.OtpVerificationScreenComponent
import com.nrlm.baselinesurvey.ui.common_components.FinalStepCompletionScreen
import com.nrlm.baselinesurvey.ui.common_components.StepCompletionScreen
import com.nrlm.baselinesurvey.ui.language.presentation.LanguageScreenComponent
import com.nrlm.baselinesurvey.ui.mission_screen.presentation.MissionScreen_1
import com.nrlm.baselinesurvey.ui.mission_summary_screen.presentation.MissionSummaryScreen
import com.nrlm.baselinesurvey.ui.profile.presentation.ProfileBSScreen
import com.nrlm.baselinesurvey.ui.question_screen.presentation.QuestionScreenHandler
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.FormTypeQuestionScreen
import com.nrlm.baselinesurvey.ui.search.presentation.SearchScreens
import com.nrlm.baselinesurvey.ui.section_screen.presentation.SectionListScreen
import com.nrlm.baselinesurvey.ui.setting.presentation.SettingBSScreen
import com.nrlm.baselinesurvey.ui.splash.presentaion.SplashScreenComponent
import com.nrlm.baselinesurvey.ui.start_screen.presentation.BaseLineStartScreen
import com.nrlm.baselinesurvey.ui.surveyee_screen.presentation.DataLoadingScreenComponent
import com.nrlm.baselinesurvey.ui.surveyee_screen.presentation.SurveyeeListScreen
import com.nrlm.baselinesurvey.ui.video_player.presentation.FullscreenView
import com.nrlm.baselinesurvey.utils.BaselineCore

@Composable
fun NavHomeGraph(navController: NavHostController, prefRepo: PrefRepo, modifier: Modifier) {
    NavHost(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        navController = navController,
        route = Graph.HOME,
        startDestination = HomeScreens.DATA_LOADING_SCREEN.route
    ) {

        composable(route = HomeScreens.DATA_LOADING_SCREEN.route) {
            DataLoadingScreenComponent(viewModel = hiltViewModel(), navController = navController)
        }
        composable(route = HomeScreens.MISSION_SUMMARY_SCREEN.route, arguments = listOf(
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

        composable(route = HomeScreens.SURVEYEE_LIST_SCREEN.route) {
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
        composable(route = HomeScreens.BaseLineStartScreen.route, arguments = listOf(
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

        composable(route = HomeScreens.SearchScreen.route, arguments = listOf(
            navArgument(
                name = ARG_SURVEY_ID
            ) {
                type = NavType.IntType
            }
        )) {
            SearchScreens(viewModel = hiltViewModel(), navController = navController, surveyId = it
                .arguments?.getInt(ARG_SURVEY_ID) ?: -1)
        }

        composable(route = HomeScreens.Home_SCREEN.route) {
            //  HomeScreen(navController=navController)
            //   MissionScreen(navController = navController, viewModel = hiltViewModel())
            MissionScreen_1(navController = navController, viewModel = hiltViewModel())
        }

        composable(route = HomeScreens.MISSION_SCREEN.route) {
            // MissionScreen(navController = navController, viewModel = hiltViewModel())
            MissionScreen_1(navController = navController, viewModel = hiltViewModel())

        }

        composable(route = HomeScreens.DIDI_SCREEN.route) {
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
                // navController.navigate(HomeScreens.VO_ENDORSEMENT_DIGITAL_FORM_C_SCREEN.route)
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
                navigateBackToMissionScreen(navController)
//                navController.popBackStack()
//                navController.navigate(HomeScreens.MISSION_SCREEN.route)

            }
        }


        addDidiNavGraph(navController = navController)
        settingNavGraph(navHostController = navController)
        logoutNavGraph(navController=navController)
    }

}


fun NavGraphBuilder.addDidiNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.ADD_DIDI,
        startDestination = HomeScreens.SURVEYEE_LIST_SCREEN.route, arguments = listOf(
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
            route = HomeScreens.SURVEYEE_LIST_SCREEN.route
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

fun NavGraphBuilder.settingNavGraph(navHostController: NavHostController){
    navigation(
        route = Graph.SETTING_GRAPH,
        startDestination = SettingBSScreens.SETTING_SCREEN.route
    ){
        composable(
            route = SettingBSScreens.SETTING_SCREEN.route
        )   {
            SettingBSScreen(viewModel = hiltViewModel(), navController = navHostController)
        }

        composable(route = SettingBSScreens.LANGUAGE_SCREEN.route
        ) {
            LanguageScreenComponent(
                navController = navHostController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize(),
                pageFrom = ARG_FROM_HOME
            )

        }

        composable(route = SettingBSScreens.PROFILE_SCREEN.route
        ) {
            ProfileBSScreen(navController = navHostController, viewModel = hiltViewModel())
        }
    }
}

sealed class SettingBSScreens(val route: String){
    object SETTING_SCREEN : SettingBSScreens(route = SETTING_ROUTE_NAME)
    object LANGUAGE_SCREEN : SettingBSScreens(route =LANGUAGE_SCREEN_ROUTE_NAME )
    object PROFILE_SCREEN : SettingBSScreens(route =PROFILE_BS_SCREEN_ROUTE_NAME )
}

sealed class HomeScreens(val route: String) {
    object DATA_LOADING_SCREEN : HomeScreens(route = DATA_LOADING_SCREEN_ROUTE_NAME)
    object SECTION_SCREEN :
        HomeScreens(route = "$SECTION_SCREEN_ROUTE_NAME/{$ARG_DIDI_ID}/{$ARG_SURVEY_ID}")

    object QUESTION_SCREEN :
        HomeScreens(route = "$QUESTION_SCREEN_ROUTE_NAME/{$ARG_SECTION_ID}/{$ARG_DIDI_ID}/{$ARG_SURVEY_ID}")

    object SURVEYEE_LIST_SCREEN :
        HomeScreens(route = "$SURVEYEE_LIST_SCREEN_ROUTE_NAME/{$ARG_ACTIVITY_ID}")

    object VIDEO_PLAYER_SCREEN :
        HomeScreens(route = "$VIDEO_PLAYER_SCREEN_ROUTE_NAME/{$ARG_VIDEO_PATH}")

    object FormTypeQuestionScreen :
        HomeScreens(route = "${FORM_TYPE_QUESTION_SCREEN_ROUTE_NAME}/{$ARG_QUESTION_NAME}/{$ARG_SURVEY_ID}/{$ARG_SECTION_ID}/{$ARG_QUESTION_ID}/{$ARG_DIDI_ID}?{$ARG_FORM_QUESTION_RESPONSE_REFERENCE_ID}")

    object BaseLineStartScreen :
        HomeScreens(route = "$BASELINE_START_SCREEN_ROUTE_NAME/{$ARG_DIDI_ID}/{$ARG_SURVEY_ID}")

    object SearchScreen : HomeScreens(route = "$SEARCH_SCREEN_ROUTE_NAME/{$ARG_SURVEY_ID}")
    object Home_SCREEN : HomeScreens(route = HOME_SCREEN_ROUTE_NAME)
    object MISSION_SCREEN : HomeScreens(route = MISSION_SCREEN_ROUTE_NAME)
    object DIDI_SCREEN : HomeScreens(route = DIDI_SCREEN_ROUTE_NAME)
    object MISSION_SUMMARY_SCREEN :
        HomeScreens(route = "$MISSION_SUMMARY_SCREEN_ROUTE_NAME/{$ARG_MISSION_ID}/{$ARG_MISSION_NAME}/{$ARG_MISSION_DATE}")

    object Final_StepComplitionScreen :
        HomeScreens(route = "$Final_Step_Complition_Screen_ROUTE_NAME/{$ARG_COMPLETION_MESSAGE}")

    object STEP_COMPLETION_SCREEN :
        HomeScreens(route = "$Step_Complition_Screen_ROUTE_NAME/{$ARG_COMPLETION_MESSAGE}")

}

const val DATA_LOADING_SCREEN_ROUTE_NAME = "data_loading_screen"
const val SECTION_SCREEN_ROUTE_NAME = "section_screen"
const val QUESTION_SCREEN_ROUTE_NAME = "question_screen"
const val SURVEYEE_LIST_SCREEN_ROUTE_NAME = "surveyee_list_screen"
const val VIDEO_PLAYER_SCREEN_ROUTE_NAME = "video_player_screen"
const val FORM_TYPE_QUESTION_SCREEN_ROUTE_NAME = "form_type_question_screen"
const val BASELINE_START_SCREEN_ROUTE_NAME = "baseline_start_screen"
const val SEARCH_SCREEN_ROUTE_NAME = "search_screen"
const val HOME_SCREEN_ROUTE_NAME = "home_screen"
const val MISSION_SCREEN_ROUTE_NAME = "mission_screen"
const val DIDI_SCREEN_ROUTE_NAME = "didi_screen"
const val MISSION_SUMMARY_SCREEN_ROUTE_NAME = "mission_summary_screen"
const val Final_Step_Complition_Screen_ROUTE_NAME = "final_step_complition_screen"
const val Step_Complition_Screen_ROUTE_NAME = "step_complition_screen"
const val SETTING_ROUTE_NAME = "setting_screen"
const val LANGUAGE_SCREEN_ROUTE_NAME = "language_screen"
const val PROFILE_BS_SCREEN_ROUTE_NAME = "profile_bs_screen"


fun navigateToBaseLineStartScreen(surveyeeId: Int, survyId: Int, navController: NavController) {
    navController.navigate("$BASELINE_START_SCREEN_ROUTE_NAME/$surveyeeId/$survyId")
}

fun navigateBackToSurveyeeListScreen(navController: NavController) {
    navController.popBackStack(HomeScreens.SURVEYEE_LIST_SCREEN.route, false)
}

fun navigateBackToMissionSummaryScreen(navController: NavController) {
    navController.popBackStack(HomeScreens.MISSION_SUMMARY_SCREEN.route, false)
}

fun navigateBackToDidiScreen(navController: NavController) {
    navController.popBackStack(HomeScreens.DIDI_SCREEN.route, false)
}

fun navigateBackToMissionScreen(navController: NavController) {
    navController.popBackStack(HomeScreens.Home_SCREEN.route, false)
}

fun NavController.navigateBackToSectionListScreen(surveyeeId: Int, surveyeId: Int) {
    this.popBackStack(HomeScreens.SECTION_SCREEN.route, true)
    navigateToSectionListScreen(surveyeeId = surveyeeId, surveyeId = surveyeId, this)
}

fun navigateToQuestionScreen(
    didiId: Int,
    sectionId: Int,
    surveyId: Int,
    navController: NavController
) {
    navController.navigate("$QUESTION_SCREEN_ROUTE_NAME/${sectionId}/$didiId/$surveyId")
}

fun navigateToSectionListScreen(surveyeeId: Int, surveyeId: Int, navController: NavController) {
    navController.navigate("$SECTION_SCREEN_ROUTE_NAME/$surveyeeId/$surveyeId")
}

fun navigateToSearchScreen(navController: NavController, surveyeId: Int) {
    navController.navigate("$SEARCH_SCREEN_ROUTE_NAME/$surveyeId")
}

fun navigateToFormTypeQuestionScreen(navController: NavController, question: QuestionEntity, sectionDetails: SectionListItem, surveyeeId: Int) {
    navController.navigate("$FORM_TYPE_QUESTION_SCREEN_ROUTE_NAME/${question.questionDisplay}/${sectionDetails.surveyId}/${sectionDetails.sectionId}/${question.questionId}/${surveyeeId}")

}

sealed class LogoutBSScreens(val route: String) {
    object LOG_LOGIN_SCREEN : LogoutBSScreens(route = "login_screen")
    object LOG_SURVEYEE_LIST_SCREEN : LogoutBSScreens(route = "surveyee_list_screen")
    object LOG_OTP_VERIFICATION : LogoutBSScreens(route = "otp_verification_screen/{$ARG_MOBILE_NUMBER}")

    object LOG_START_SCREEN : LogoutBSScreens(route = "start_screen")

}

fun NavGraphBuilder.logoutNavGraph(navController: NavHostController){
    navigation(
        route = Graph.LOGOUT_GRAPH,
        startDestination = LogoutBSScreens.LOG_LOGIN_SCREEN.route
    ){
        composable(
            route = LogoutBSScreens.LOG_LOGIN_SCREEN.route
        )   {
            LoginScreenComponent(
                navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(
            route = LogoutBSScreens.LOG_OTP_VERIFICATION.route,
            arguments = listOf(navArgument(ARG_MOBILE_NUMBER) {
                type = NavType.StringType
            })
        ) {
            OtpVerificationScreenComponent(
                navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize(),
                it.arguments?.getString(ARG_MOBILE_NUMBER).toString()
            )
        }

        composable(route = LogoutBSScreens.LOG_SURVEYEE_LIST_SCREEN.route) {
            /*VillageSelectionScreen(navController = navController, viewModel = hiltViewModel()){
                navController.navigate(AuthScreen.AUTH_SETTING_SCREEN.route)
            }*/
//            VillageSelectionScreen()
            SurveyeeListScreen(
                viewModel = hiltViewModel(),
                navController = navController,
                activityName = "",
                missionId = 0,
                activityDate = "",
                activityId = 0
            )
        }
        composable(route = LogoutBSScreens.LOG_START_SCREEN.route) {
            SplashScreenComponent(
                navController = navController, modifier = Modifier.fillMaxSize(),
                hiltViewModel()
            )
        }

    }
}



package com.sarathi.missionactivitytask.navigation

import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.nudge.core.BLANK_STRING
import com.sarathi.contentmodule.media.MediaScreen
import com.sarathi.contentmodule.media.PdfViewer
import com.sarathi.contentmodule.ui.content_detail_screen.screen.ContentDetailScreen
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ACTIVITY_COMPLETION_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ACTIVITY_SCREEN_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ADD_IMAGE_SCREEN_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_ACTIVITY_CONFIG_ID
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_ACTIVITY_ID
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_ACTIVITY_MASSAGE
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_ACTIVITY_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_CONTENT_KEY
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_CONTENT_SCREEN_CATEGORY
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_CONTENT_TITLE
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_CONTENT_TYPE
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_FORM_PATH
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_GRANT_ID
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_GRANT_TYPE
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_MAT_ID
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_MISSION_COMPLETED
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_MISSION_ID
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_MISSION_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_REFERENCE_ID
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_SANCTIONED_AMOUNT
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_SECTION_ID
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_SUBJECT_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_SUBJECT_TYPE
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_SURVEY_ID
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_TASK_ID
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_TOTAL_SUBMITTED_AMOUNT
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.CONTENT_DETAIL_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.DISBURSEMENT_SUMMARY_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.GRANT_SURVEY_SUMMARY_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.GRANT_TASK_SCREEN_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.MAT_GRAPH
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.MEDIA_PLAYER_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.MISSION_FINAL_STEP_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.PDF_VIEWER_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.SURVEY_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.ui.add_image_screen.screen.SubmitPhysicalFormScreen
import com.sarathi.missionactivitytask.ui.disbursement_summary_screen.DisbursementFormSummaryScreen
import com.sarathi.missionactivitytask.ui.grantTask.screen.GrantTaskScreen
import com.sarathi.missionactivitytask.ui.grant_activity_screen.screen.ActivityScreen
import com.sarathi.missionactivitytask.ui.mission_screen.screen.GrantMissionScreen
import com.sarathi.missionactivitytask.ui.step_completion_screen.ActivitySuccessScreen
import com.sarathi.missionactivitytask.ui.step_completion_screen.FinalStepCompletionScreen
import com.sarathi.surveymanager.ui.screen.DisbursementSummaryScreen
import com.sarathi.surveymanager.ui.screen.SurveyScreen


fun NavGraphBuilder.MatNavigation(
    navController: NavHostController,
    onSettingIconClick: () -> Unit
) {
    navigation(
        route = MAT_GRAPH,
        startDestination = MATHomeScreens.MissionScreen.route
    ) {

        composable(route = MATHomeScreens.MissionScreen.route) {
            GrantMissionScreen(
                navController = navController, viewModel = hiltViewModel(),
                onSettingClick = onSettingIconClick
            )
        }
        composable(
            route = MATHomeScreens.ActivityScreen.route, arguments = listOf(
                navArgument(name = ARG_MISSION_ID) {
                    type = NavType.IntType
                },
                navArgument(name = ARG_MISSION_NAME) {
                    type = NavType.StringType
                },
                navArgument(name = ARG_MISSION_COMPLETED) {
                    type = NavType.BoolType
                })
        ) {
            ActivityScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                missionId = it.arguments?.getInt(
                    ARG_MISSION_ID
                ) ?: 0,
                missionName = it.arguments?.getString(
                    ARG_MISSION_NAME
                ) ?: BLANK_STRING,
                isMissionCompleted = it.arguments?.getBoolean(
                    ARG_MISSION_COMPLETED
                ) ?: false,
                onSettingClick = onSettingIconClick
            )
        }
        composable(
            route = MATHomeScreens.GrantTaskScreen.route, arguments = listOf(
                navArgument(name = ARG_MISSION_ID) {
                    type = NavType.IntType
                },
                navArgument(name = ARG_ACTIVITY_ID) {
                    type = NavType.IntType
                },
                navArgument(name = ARG_ACTIVITY_NAME) {
                    type = NavType.StringType
                })
        ) {
            GrantTaskScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                missionId = it.arguments?.getInt(
                    ARG_MISSION_ID
                ) ?: 0,
                activityId = it.arguments?.getInt(
                    ARG_ACTIVITY_ID
                ) ?: 0,
                activityName = it.arguments?.getString(
                    ARG_ACTIVITY_NAME
                ) ?: BLANK_STRING,
                onSettingClick = onSettingIconClick
            )
        }

        composable(route = MATHomeScreens.MediaPlayerScreen.route, arguments = listOf(
            navArgument(
                name = ARG_CONTENT_KEY
            ) {
                type = NavType.StringType
            },
            navArgument(
                name = ARG_CONTENT_TYPE
            ) {
                type = NavType.StringType
            },
            navArgument(
                name = ARG_CONTENT_TITLE
            ) {
                type = NavType.StringType
            }
        )
        ) {
            MediaScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                fileType = it.arguments?.getString(
                    ARG_CONTENT_TYPE
                ) ?: BLANK_STRING,
                key = it.arguments?.getString(
                    ARG_CONTENT_KEY
                ) ?: BLANK_STRING,
                contentTitle = it.arguments?.getString(
                    ARG_CONTENT_TITLE
                ) ?: BLANK_STRING
            )
        }


        composable(route = MATHomeScreens.ContentDetailScreen.route, arguments = listOf(
            navArgument(
                name = ARG_MAT_ID
            ) {
                type = NavType.IntType
            },
            navArgument(
                name = ARG_CONTENT_SCREEN_CATEGORY
            ) {
                type = NavType.IntType
            }
        )) {
            ContentDetailScreen(
                navController = navController, viewModel = hiltViewModel(),
                onNavigateToMediaScreen = { fileType, key, contentTitle ->
                    navigateToMediaPlayerScreen(
                        navController = navController,
                        contentKey = key,
                        contentType = fileType,
                        contentTitle = contentTitle
                    )

                }, matId = it.arguments?.getInt(
                    ARG_MAT_ID
                ) ?: 0,
                contentType = it.arguments?.getInt(
                    ARG_CONTENT_SCREEN_CATEGORY
                ) ?: 0
            )
        }
        composable(
            route = MATHomeScreens.SurveyScreen.route,
            arguments = listOf(
                navArgument(name = ARG_TASK_ID) {
                    type = NavType.IntType
                },
                navArgument(name = ARG_SECTION_ID) {
                    type = NavType.IntType
                },
                navArgument(name = ARG_SURVEY_ID) {
                    type = NavType.IntType
                },
                navArgument(name = ARG_SUBJECT_TYPE) {
                    type = NavType.StringType
                },
                navArgument(name = ARG_SUBJECT_NAME) {
                    type = NavType.StringType
                },
                navArgument(name = ARG_REFERENCE_ID) {
                    type = NavType.StringType
                },
                navArgument(name = ARG_ACTIVITY_CONFIG_ID) {
                    type = NavType.IntType
                },
                navArgument(name = ARG_GRANT_ID) {
                    type = NavType.IntType
                },
                navArgument(name = ARG_GRANT_TYPE) {
                    type = NavType.StringType
                },
                navArgument(name = ARG_SANCTIONED_AMOUNT) {
                    type = NavType.IntType
                },
                navArgument(name = ARG_TOTAL_SUBMITTED_AMOUNT) {
                    type = NavType.IntType
                },
            ),
        ) {
            SurveyScreen(
                navController = navController, viewModel = hiltViewModel(),
                taskId = it.arguments?.getInt(
                    ARG_TASK_ID
                ) ?: 0,
                surveyId = it.arguments?.getInt(
                    ARG_SURVEY_ID
                ) ?: 0,
                sectionId = it.arguments?.getInt(
                    ARG_SECTION_ID
                ) ?: 0,
                subjectType = it.arguments?.getString(
                    ARG_SUBJECT_TYPE
                ) ?: BLANK_STRING,
                subjectName = it.arguments?.getString(
                    ARG_SUBJECT_NAME
                ) ?: BLANK_STRING,
                referenceId = it.arguments?.getString(
                    ARG_REFERENCE_ID
                ) ?: BLANK_STRING,
                activityConfigId = it.arguments?.getInt(
                    ARG_ACTIVITY_CONFIG_ID
                ) ?: 0,
                grantId = it.arguments?.getInt(
                    ARG_GRANT_ID
                ) ?: 0,
                grantType = it.arguments?.getString(
                    ARG_GRANT_TYPE
                ) ?: BLANK_STRING,
                sanctionedAmount = it.arguments?.getInt(
                    ARG_SANCTIONED_AMOUNT
                ) ?: 0,
                totalSubmittedAmount = it.arguments?.getInt(
                    ARG_TOTAL_SUBMITTED_AMOUNT
                ) ?: 0,

                )
        }
        composable(
            route = MATHomeScreens.DisbursementSurveyScreen.route, arguments = listOf(
                navArgument(name = ARG_TASK_ID) {
                    type = NavType.IntType
                },
                navArgument(name = ARG_SECTION_ID) {
                    type = NavType.IntType
                },
                navArgument(name = ARG_SURVEY_ID) {
                    type = NavType.IntType
                },
                navArgument(name = ARG_SUBJECT_TYPE) {
                    type = NavType.StringType
                },
                navArgument(name = ARG_SUBJECT_NAME) {
                    type = NavType.StringType
                },
                navArgument(name = ARG_ACTIVITY_CONFIG_ID) {
                    type = NavType.IntType
                },
                navArgument(name = ARG_SANCTIONED_AMOUNT) {
                    type = NavType.IntType
                },

                )
        ) {
            DisbursementSummaryScreen(
                navController = navController, viewModel = hiltViewModel(),
                taskId = it.arguments?.getInt(
                    ARG_TASK_ID
                ) ?: 0,
                surveyId = it.arguments?.getInt(
                    ARG_SURVEY_ID
                ) ?: 0,
                sectionId = it.arguments?.getInt(
                    ARG_SECTION_ID
                ) ?: 0,
                subjectType = it.arguments?.getString(
                    ARG_SUBJECT_TYPE
                ) ?: BLANK_STRING,
                subjectName = it.arguments?.getString(ARG_SUBJECT_NAME) ?: BLANK_STRING,
                activityConfigId = it.arguments?.getInt(
                    ARG_ACTIVITY_CONFIG_ID
                ) ?: 0,
                onSettingClick = onSettingIconClick,
                onNavigateSurveyScreen = { referenceId, activityConfigId, grantId, grantType, sanctionAmount, totalSubmittedAmount ->
                    navigateToSurveyScreen(
                        navController, surveyId = it.arguments?.getInt(
                            ARG_SURVEY_ID
                        ) ?: 0, sectionId = it.arguments?.getInt(
                            ARG_SECTION_ID
                        ) ?: 0, taskId = it.arguments?.getInt(
                            ARG_TASK_ID
                        ) ?: 0, subjectType = it.arguments?.getString(
                            ARG_SUBJECT_TYPE
                        ) ?: BLANK_STRING,
                        subjectName = it.arguments?.getString(ARG_SUBJECT_NAME) ?: BLANK_STRING,
                        referenceId = referenceId,
                        activityConfigId = activityConfigId,
                        grantId = grantId,
                        grantType = grantType,
                        sanctionedAmount = sanctionAmount,
                        totalSubmittedAmount = totalSubmittedAmount
                    )
                },
                onNavigateSuccessScreen = { msg ->
                    navigateToActivityCompletionScreen(navController, msg)
                },
                sanctionedAmount = it.arguments?.getInt(
                    ARG_SANCTIONED_AMOUNT
                ) ?: 0,
            )
        }

        composable(route = MATHomeScreens.ActivityCompletionScreen.route, arguments = listOf(
            navArgument(
                name = ARG_ACTIVITY_MASSAGE
            ) {
                type = NavType.StringType
            }
        )) {
            ActivitySuccessScreen(
                onNavigateBack = {
                    navController.popBackStack(
                        MATHomeScreens.GrantTaskScreen.route,
                        inclusive = false
                    )
                },
                navController = navController, message = it.arguments?.getString(
                    ARG_ACTIVITY_MASSAGE
                ) ?: BLANK_STRING
            )
        }

        composable(route = MATHomeScreens.FinalStepCompletionScreen.route) {
            FinalStepCompletionScreen(navController = navController) {
            }
        }
        composable(route = MATHomeScreens.DisbursmentSummaryScreen.route, arguments = listOf(
            navArgument(name = ARG_ACTIVITY_ID) {
                type = NavType.IntType
            },
            navArgument(name = ARG_MISSION_ID) {
                type = NavType.IntType
            }
        )) {
            DisbursementFormSummaryScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                activityId = it.arguments?.getInt(ARG_ACTIVITY_ID) ?: 0,
                missionId = it.arguments?.getInt(ARG_MISSION_ID) ?: 0
            )
        }
        composable(route = MATHomeScreens.PdfViewerScreen.route, arguments = listOf(
            navArgument(ARG_FORM_PATH) {
                type = NavType.StringType
            }
        )) {
            PdfViewer(
                filePath = it.arguments?.getString(ARG_FORM_PATH) ?: BLANK_STRING,
                modifier = Modifier,
                navController = navController
            )
        }
        composable(route = MATHomeScreens.AddImageScreen.route, arguments = listOf(
            navArgument(ARG_ACTIVITY_ID) {
                type = NavType.IntType
            }
        )) {
            SubmitPhysicalFormScreen(
                viewModel = hiltViewModel(),
                navController = navController,
                activityId = it.arguments?.getInt(ARG_ACTIVITY_ID) ?: 0
            )
        }
    }
}



fun navigateToContentDetailScreen(
    navController: NavController,
    matId: Int,
    contentScreenCategory: Int
) {
    navController.navigate("$CONTENT_DETAIL_SCREEN_ROUTE_NAME/$matId/$contentScreenCategory")
}

fun navigateToDisbursmentSummaryScreen(
    navController: NavController, activityId: Int, missionId: Int
) {
    navController.navigate("$DISBURSEMENT_SUMMARY_SCREEN_ROUTE_NAME/$activityId/$missionId")
}

fun navigateToSurveyScreen(
    navController: NavController,
    surveyId: Int,
    sectionId: Int,
    taskId: Int,
    subjectType: String,
    subjectName: String,
    referenceId: String,
    activityConfigId: Int,
    grantId: Int,
    grantType: String,
    sanctionedAmount: Int?,
    totalSubmittedAmount: Int?,
) {
    navController.navigate("$SURVEY_SCREEN_ROUTE_NAME/$surveyId/$taskId/$sectionId/$subjectType/$subjectName/$referenceId/$activityConfigId/$grantId/$grantType/$sanctionedAmount/$totalSubmittedAmount")
}

fun navigateToGrantSurveySummaryScreen(
    navController: NavController,
    surveyId: Int,
    sectionId: Int,
    taskId: Int,
    subjectType: String,
    subjectName: String,
    activityConfigId: Int,
    sanctionedAmount: Int?,
) {
    navController.navigate("$GRANT_SURVEY_SUMMARY_SCREEN_ROUTE_NAME/$surveyId/$taskId/$sectionId/$subjectType/$subjectName/$activityConfigId/$sanctionedAmount")
}


fun navigateToActivityCompletionScreen(navController: NavController, activityMsg: String) {
    navController.navigate("$ACTIVITY_COMPLETION_SCREEN_ROUTE_NAME/$activityMsg")
}

fun navigateToFinalStepCompletionScreen(navController: NavController) {
    navController.navigate(MISSION_FINAL_STEP_SCREEN_ROUTE_NAME)
}
fun navigateToPdfViewerScreen(navController: NavController, filePath: String) {
    navController.navigate("$PDF_VIEWER_SCREEN_ROUTE_NAME/$filePath")
}

fun navigateToMediaPlayerScreen(
    navController: NavController,
    contentKey: String,
    contentType: String,
    contentTitle: String
) {
    navController.navigate("$MEDIA_PLAYER_SCREEN_ROUTE_NAME/$contentKey/$contentType/$contentTitle")
}

fun navigateToActivityScreen(
    navController: NavController,
    missionId: Int,
    missionName: String,
    isMissionCompleted: Boolean
) {
    navController.navigate("$ACTIVITY_SCREEN_SCREEN_ROUTE_NAME/$missionId/$missionName/$isMissionCompleted")
}

fun navigateToAddImageScreen(navController: NavController, activityId: Int) {
    navController.navigate("$ADD_IMAGE_SCREEN_SCREEN_ROUTE_NAME/$activityId")
}

fun navigateToTaskScreen(
    navController: NavController,
    missionId: Int,
    activityId: Int,
    activityName: String
) {
    navController.navigate("$GRANT_TASK_SCREEN_SCREEN_ROUTE_NAME/$missionId/$activityId/$activityName")
}

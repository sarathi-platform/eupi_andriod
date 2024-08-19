package com.sarathi.missionactivitytask.navigation

import android.text.TextUtils
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
import com.nudge.core.LIVELIHOOD
import com.nudge.core.value
import com.nudge.navigationmanager.graphs.NudgeNavigationGraph.MAT_GRAPH
import com.sarathi.contentmodule.media.MediaScreen
import com.sarathi.contentmodule.media.PdfViewer
import com.sarathi.contentmodule.ui.content_detail_screen.screen.ContentDetailScreen
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ACTIVITY_COMPLETION_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ACTIVITY_SCREEN_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ADD_IMAGE_SCREEN_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_ACTIVITY_CONFIG_ID
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_ACTIVITY_ID
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_ACTIVITY_MASSAGE
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_ACTIVITY_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_ACTIVITY_PENDING_COUNT
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_ACTIVITY_TOTAL_COUNT
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_ACTIVITY_TYPE
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_CONTENT_KEY
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_CONTENT_SCREEN_CATEGORY
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_CONTENT_TITLE
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_CONTENT_TYPE
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_FORM_PATH
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_GRANT_ID
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_GRANT_TYPE
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_IS_FROM_ACTIVITY
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_IS_FROM_SETTING_SCREEN
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
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_TASK_ID_LIST
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_TOOLBAR_TITLE
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_TOTAL_SUBMITTED_AMOUNT
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.CONTENT_DETAIL_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.DISBURSEMENT_SUMMARY_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.GRANT_SURVEY_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.GRANT_SURVEY_SUMMARY_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.GRANT_TASK_SCREEN_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.LIVELIHOOD_DROPDOWN_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.LIVELIHOOD_TASK_SCREEN_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.MAT_SECTION_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.MEDIA_PLAYER_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.MISSION_FINAL_STEP_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.PDF_VIEWER_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.SURVEY_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.SURVEY_TASK_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.ui.add_image_screen.screen.SubmitPhysicalFormScreen
import com.sarathi.missionactivitytask.ui.disbursement_summary_screen.DisbursementFormSummaryScreen
import com.sarathi.missionactivitytask.ui.grantTask.screen.GrantTaskScreen
import com.sarathi.missionactivitytask.ui.grantTask.screen.LivelihoodTaskScreen
import com.sarathi.missionactivitytask.ui.grant_activity_screen.screen.ActivityScreen
import com.sarathi.missionactivitytask.ui.mission_screen.screen.MissionScreen
import com.sarathi.missionactivitytask.ui.step_completion_screen.ActivitySuccessScreen
import com.sarathi.missionactivitytask.ui.step_completion_screen.FinalStepCompletionScreen
import com.sarathi.missionactivitytask.ui.surveyTask.SurveyTaskScreen
import com.sarathi.surveymanager.ui.screen.BaseSurveyScreen
import com.sarathi.surveymanager.ui.screen.DisbursementSummaryScreen
import com.sarathi.surveymanager.ui.screen.GrantSurveyScreen
import com.sarathi.surveymanager.ui.screen.SurveyScreen
import com.sarathi.surveymanager.ui.screen.livelihood.LivelihoodDropDownScreen
import com.sarathi.surveymanager.ui.screen.sectionScreen.SectionScreen
import com.nudge.core.model.MissionUiModel as CoreMissionUiModel


fun NavGraphBuilder.MatNavigation(
    navController: NavHostController,
    onSettingIconClick: () -> Unit,
    onNavigateToBaselineMission: (mission: CoreMissionUiModel) -> Unit,
    onBackPressed: () -> Unit
) {
    navigation(
        route = MAT_GRAPH,
        startDestination = MATHomeScreens.MissionScreen.route
    ) {

        composable(route = MATHomeScreens.MissionScreen.route) {
            MissionScreen(
                navController = navController, viewModel = hiltViewModel(),
                onSettingClick = onSettingIconClick,
                onBackPressed = onBackPressed
            ) { isBaselineMission, mission: MissionUiModel ->
                if (isBaselineMission) {
                    onNavigateToBaselineMission(
                        CoreMissionUiModel(
                            missionId = mission.missionId,
                            description = mission.description,
                            missionStatus = mission.missionStatus,
                            activityCount = mission.activityCount,
                            pendingActivityCount = mission.pendingActivityCount
                        )
                    )
                } else {
                    navigateToActivityScreen(
                        navController,
                        missionName = mission.description,
                        missionId = mission.missionId,
                        isMissionCompleted = mission.missionStatus == SurveyStatusEnum.COMPLETED.name
                    )
                }
            }
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
        composable(
            route = MATHomeScreens.LivelihoodTaskScreen.route, arguments = listOf(
                navArgument(name = ARG_MISSION_ID) {
                    type = NavType.IntType
                },
                navArgument(name = ARG_ACTIVITY_ID) {
                    type = NavType.IntType
                },
                navArgument(name = ARG_ACTIVITY_NAME) {
                    type = NavType.StringType
                },
                navArgument(name = ARG_ACTIVITY_PENDING_COUNT) {
                    type = NavType.IntType
                },
                navArgument(name = ARG_ACTIVITY_TOTAL_COUNT) {
                    type = NavType.IntType
                },

            )
        ) {
          LivelihoodTaskScreen(
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
              pendingCount  = it.arguments?.getInt(
                  ARG_ACTIVITY_PENDING_COUNT
              ) ?: 0,
              totalCount= it.arguments?.getInt(
                  ARG_ACTIVITY_TOTAL_COUNT
              ) ?: 0,
                onSettingClick = onSettingIconClick
            )
        }

        composable(
            route = MATHomeScreens.SurveyTaskScreen.route, arguments = listOf(
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
            SurveyTaskScreen(
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

        composable(
            route = MATHomeScreens.MediaPlayerScreen.route, arguments = listOf(
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
                onSettingIconClicked = {
                    onSettingIconClick()
                },
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
            route = MATHomeScreens.BaseSurveyScreen.route,
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
                navArgument(name = ARG_TOOLBAR_TITLE) {
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
            BaseSurveyScreen(
                navController = navController, viewModel = hiltViewModel(),
                onSettingClick = onSettingIconClick,
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
                toolbarTitle = it.arguments?.getString(
                    ARG_TOOLBAR_TITLE
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
                onAnswerSelect = { questionUiModel ->

                },
                onSubmitButtonClick = {}
            )
        }
        composable(
            route = MATHomeScreens.GrantSurveyScreen.route,
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
                navArgument(name = ARG_TOOLBAR_TITLE) {
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
            GrantSurveyScreen(
                navController = navController, viewModel = hiltViewModel(),
                onSettingClick = onSettingIconClick,
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
                toolbarTitle = it.arguments?.getString(
                    ARG_TOOLBAR_TITLE
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
                navArgument(name = ARG_TOOLBAR_TITLE) {
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
                navArgument(name = ARG_ACTIVITY_ID) {
                    type = NavType.IntType
                },
                navArgument(name = ARG_MISSION_ID) {
                    type = NavType.IntType
                }
            ),
        ) {

            SurveyScreen(
                navController = navController, viewModel = hiltViewModel(),
                onSettingClick = onSettingIconClick,
                missionId = it.arguments?.getInt(ARG_MISSION_ID).value(),
                activityId = it.arguments?.getInt(ARG_ACTIVITY_ID).value(),
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
                toolbarTitle = it.arguments?.getString(
                    ARG_TOOLBAR_TITLE
                ) ?: BLANK_STRING,

                activityConfigId = it.arguments?.getInt(
                    ARG_ACTIVITY_CONFIG_ID
                ) ?: 0,
                grantId = it.arguments?.getInt(
                    ARG_GRANT_ID
                ) ?: 0,
                activityType = it.arguments?.getString(
                    ARG_GRANT_TYPE
                ) ?: BLANK_STRING,
                sanctionedAmount = it.arguments?.getInt(
                    ARG_SANCTIONED_AMOUNT
                ) ?: 0,
                totalSubmittedAmount = it.arguments?.getInt(
                    ARG_TOTAL_SUBMITTED_AMOUNT
                ) ?: 0
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
                    navigateToGrantSurveyScreen(
                        navController, surveyId = it.arguments?.getInt(
                            ARG_SURVEY_ID
                        ) ?: 0, sectionId = it.arguments?.getInt(
                            ARG_SECTION_ID
                        ) ?: 0, taskId = it.arguments?.getInt(
                            ARG_TASK_ID
                        ) ?: 0, subjectType = it.arguments?.getString(
                            ARG_SUBJECT_TYPE
                        ) ?: BLANK_STRING,
                        toolbarName = it.arguments?.getString(ARG_SUBJECT_NAME) ?: BLANK_STRING,
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
            },
            navArgument(
                name = ARG_IS_FROM_ACTIVITY
            ) {
                type = NavType.BoolType
            },
            navArgument(
                name = ARG_ACTIVITY_NAME
            ) {
                type = NavType.StringType
                nullable = true
            }
        )) {
            ActivitySuccessScreen(
                onNavigateBack = { isFromActivity ,activityRoutePath->
                    if (activityRoutePath.contains(LIVELIHOOD)) {
                        navController.popBackStack(
                            MATHomeScreens.LivelihoodTaskScreen.route,
                            inclusive = isFromActivity
                        )
                    }
                    else{
                        navController.popBackStack(
                            MATHomeScreens.GrantTaskScreen.route,
                            inclusive = isFromActivity
                        )
                    }
                },
                navController = navController, message = it.arguments?.getString(
                    ARG_ACTIVITY_MASSAGE
                ) ?: BLANK_STRING,
                activityRoutePath = it.arguments?.getString(ARG_ACTIVITY_NAME) ?: BLANK_STRING,
                isFromActivitySuccess = it.arguments?.getBoolean(ARG_IS_FROM_ACTIVITY) ?: false

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
            },
            navArgument(name = ARG_TASK_ID_LIST) {
                type = NavType.StringType
                nullable = true
                defaultValue = BLANK_STRING
            },
            navArgument(name = ARG_IS_FROM_SETTING_SCREEN) {
                type = NavType.BoolType
            }
        )) {
            DisbursementFormSummaryScreen(
                navController = navController,
                onSettingClick = onSettingIconClick,
                viewModel = hiltViewModel(),
                activityId = it.arguments?.getInt(ARG_ACTIVITY_ID) ?: 0,
                missionId = it.arguments?.getInt(ARG_MISSION_ID) ?: 0,
                isFormSettingScreen = it.arguments?.getBoolean(ARG_IS_FROM_SETTING_SCREEN) ?: false,
                taskList = it.arguments?.getString(ARG_TASK_ID_LIST) ?: BLANK_STRING
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
            },
            navArgument(ARG_TASK_ID_LIST) {
                type = NavType.StringType
                defaultValue = BLANK_STRING
                nullable = true
            }
        )) {
            SubmitPhysicalFormScreen(
                viewModel = hiltViewModel(),
                navController = navController,
                activityId = it.arguments?.getInt(ARG_ACTIVITY_ID) ?: 0,
                taskIdList = it.arguments?.getString(ARG_TASK_ID_LIST) ?: BLANK_STRING
            )
        }

        composable(
            route = MATHomeScreens.SectionScreen.route,
            arguments = listOf(
                navArgument(name = ARG_TASK_ID) {
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
                navArgument(name = ARG_ACTIVITY_TYPE) {
                    type = NavType.StringType
                },
                navArgument(name = ARG_ACTIVITY_ID) {
                    type = NavType.IntType
                },
                navArgument(name = ARG_MISSION_ID) {
                    type = NavType.IntType
                }
            )
        ) {
            SectionScreen(
                sectionScreenViewModel = hiltViewModel(),
                navController = navController,
                missionId = it.arguments?.getInt(ARG_MISSION_ID).value(),
                activityId = it.arguments?.getInt(ARG_ACTIVITY_ID).value(),
                surveyId = it.arguments?.getInt(ARG_SURVEY_ID).value(),
                taskId = it.arguments?.getInt(ARG_TASK_ID).value(),
                subjectType = it.arguments?.getString(ARG_SUBJECT_TYPE).value(),
                subjectName = it.arguments?.getString(ARG_SUBJECT_NAME).value(),
                activityType = it.arguments?.getString(ARG_ACTIVITY_NAME).value(),
                activityConfigId = it.arguments?.getInt(ARG_ACTIVITY_CONFIG_ID).value(),
                sanctionedAmount = it.arguments?.getInt(ARG_SANCTIONED_AMOUNT).value(),
                onNavigateToGrantSurveySummaryScreen = { navController, surveyId, sectionId, taskId, subjectType, subjectName, activityConfigId, sanctionedAmount ->
                    navigateToGrantSurveySummaryScreen(
                        navController = navController,
                        surveyId = surveyId,
                        sectionId = sectionId,
                        taskId = taskId,
                        subjectType = subjectType,
                        subjectName = subjectName,
                        activityConfigId = activityConfigId,
                        sanctionedAmount = sanctionedAmount
                    )
                },
                onSettingClick = onSettingIconClick,
                onNavigateSuccessScreen = { msg ->
                    navigateToActivityCompletionScreen(
                        navController,
                        msg
                    )
                },
                onNavigateToMediaScreen = { navController, contentKey, contentType, contentTitle ->

                },
                onNavigateToQuestionScreen = { surveyId, sectionId, taskId, sectionName, subjectType, activityConfigId, missionId, activityId ->
                    navigateToSurveyScreen(
                        navController = navController,
                        missionId = missionId,
                        activityId = activityId,
                        surveyId = surveyId,
                        sectionId = sectionId,
                        taskId = taskId,
                        subjectType = subjectType,
                        toolbarName = sectionName,
                        activityConfigId = activityConfigId,
                        grantId = 0,
                        activityType = "Survey",
                        sanctionedAmount = 0,
                        totalSubmittedAmount = 0
                    )
                }
            )
        }
    }
    composable(route = MATHomeScreens.LivelihoodDropDownScreen.route, arguments = listOf(
        navArgument(ARG_TASK_ID) {
            type = NavType.IntType
        },
        navArgument(name = ARG_ACTIVITY_ID) {
            type = NavType.IntType
        },
        navArgument(name = ARG_MISSION_ID) {
            type = NavType.IntType
        },
        navArgument(ARG_SUBJECT_NAME) {
            type = NavType.StringType
        }
    )) {
        LivelihoodDropDownScreen(
            navController = navController,
            viewModel = hiltViewModel(),
            taskId = it.arguments?.getInt(ARG_TASK_ID).value(),
            activityId = it.arguments?.getInt(
                ARG_ACTIVITY_ID
            ).value(),
            missionId = it.arguments?.getInt(ARG_MISSION_ID).value(),
            subjectName = it.arguments?.getString(
                ARG_SUBJECT_NAME
            ).value(),
            onSettingClicked = {

            })
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
    navController: NavController,
    activityId: Int,
    missionId: Int,
    taskIdList: String,
    isFromSettingScreen: Boolean
) {
    var taskIdListWithNullable = if (!TextUtils.isEmpty(taskIdList)) taskIdList else null

    navController.navigate("$DISBURSEMENT_SUMMARY_SCREEN_ROUTE_NAME/$activityId/$missionId/$taskIdListWithNullable/$isFromSettingScreen")
}

fun navigateToSurveyScreen(
    navController: NavController,
    missionId: Int,
    activityId: Int,
    surveyId: Int,
    sectionId: Int,
    taskId: Int,
    subjectType: String,
    toolbarName: String,
    activityConfigId: Int,
    grantId: Int,
    activityType: String,
    sanctionedAmount: Int?,
    totalSubmittedAmount: Int?,
) {
    navController.navigate("$SURVEY_SCREEN_ROUTE_NAME/$surveyId/$taskId/$sectionId/$subjectType/$toolbarName/$activityConfigId/$grantId/$activityType/$sanctionedAmount/$totalSubmittedAmount/$missionId/$activityId")
}

fun navigateToGrantSurveyScreen(
    navController: NavController,
    surveyId: Int,
    sectionId: Int,
    taskId: Int,
    subjectType: String,
    toolbarName: String,
    referenceId: String,
    activityConfigId: Int,
    grantId: Int,
    grantType: String,
    sanctionedAmount: Int?,
    totalSubmittedAmount: Int?,
) {
    navController.navigate("$GRANT_SURVEY_SCREEN_ROUTE_NAME/$surveyId/$taskId/$sectionId/$subjectType/$toolbarName/$referenceId/$activityConfigId/$grantId/$grantType/$sanctionedAmount/$totalSubmittedAmount")
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

fun navigateToSectionScreen(
    navController: NavController,
    missionId: Int,
    activityId: Int,
    surveyId: Int,
    taskId: Int,
    subjectType: String,
    subjectName: String,
    activityType: String?,
    activityConfigId: Int,
    sanctionedAmount: Int?,
) {
    navController.navigate("$MAT_SECTION_SCREEN_ROUTE_NAME/$surveyId/$taskId/$activityType/$subjectType/$subjectName/$activityConfigId/$sanctionedAmount/$activityId/$missionId")
}


fun navigateToActivityCompletionScreen(
    navController: NavController,
    activityMsg: String,
    isFromActivity: Boolean = false,
    activityRoutePath: String = BLANK_STRING
) {
    var activityNameWithNullable = if (!TextUtils.isEmpty(activityRoutePath)) activityRoutePath else null
    navController.navigate("$ACTIVITY_COMPLETION_SCREEN_ROUTE_NAME/$activityMsg/$isFromActivity/$activityNameWithNullable")
}

fun navigateToLivelihoodDropDownScreen(
    navController: NavController,
    taskId: Int,
    activityId: Int,
    missionId: Int,
    subjectName: String
) {
    navController.navigate("$LIVELIHOOD_DROPDOWN_SCREEN_ROUTE_NAME/$taskId/$activityId/$missionId/$subjectName")
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

fun navigateToAddImageScreen(navController: NavController, activityId: Int, taskIdList: String) {
    var taskIdListWithNullable = if (!TextUtils.isEmpty(taskIdList)) taskIdList else null

    navController.navigate("$ADD_IMAGE_SCREEN_SCREEN_ROUTE_NAME/$activityId/$taskIdListWithNullable")
}

fun navigateToGrantTaskScreen(
    navController: NavController,
    missionId: Int,
    activityId: Int,
    activityName: String
) {
    navController.navigate("$GRANT_TASK_SCREEN_SCREEN_ROUTE_NAME/$missionId/$activityId/$activityName")
}
fun navigateToLivelihoodTaskScreen(
    navController: NavController,
    missionId: Int,
    activityId: Int,
    activityName: String,
    pendingCount:Int,
    totalCount:Int
) {
    navController.navigate("$LIVELIHOOD_TASK_SCREEN_SCREEN_ROUTE_NAME/$missionId/$activityId/$activityName/$pendingCount/$totalCount")
}

fun navigateToSurveyTaskScreen(
    navController: NavController,
    missionId: Int,
    activityId: Int,
    activityName: String
) {
    navController.navigate("$SURVEY_TASK_SCREEN_ROUTE_NAME/$missionId/$activityId/$activityName")
}

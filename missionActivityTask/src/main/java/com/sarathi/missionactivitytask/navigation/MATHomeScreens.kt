package com.sarathi.missionactivitytask.navigation

import com.sarathi.missionactivitytask.constants.MissionActivityConstants

sealed class MATHomeScreens(val route: String) {
    object MissionScreen :
        MATHomeScreens(route = MissionActivityConstants.MISSION_SCREEN_ROUTE_NAME)

    object ActivityScreen :
        MATHomeScreens(route = "${MissionActivityConstants.ACTIVITY_SCREEN_SCREEN_ROUTE_NAME}/{${MissionActivityConstants.ARG_MISSION_ID}}/{${MissionActivityConstants.ARG_MISSION_NAME}}/{${MissionActivityConstants.ARG_MISSION_COMPLETED}}/{${MissionActivityConstants.ARG_PROGRAM_ID}}")

    object GrantTaskScreen :
        MATHomeScreens(route = "${MissionActivityConstants.GRANT_TASK_SCREEN_SCREEN_ROUTE_NAME}/{${MissionActivityConstants.ARG_MISSION_ID}}/{${MissionActivityConstants.ARG_ACTIVITY_ID}}/{${MissionActivityConstants.ARG_ACTIVITY_NAME}}/{${MissionActivityConstants.ARG_PROGRAM_ID}}")

    object ContentDetailScreen :
        MATHomeScreens(route = "${MissionActivityConstants.CONTENT_DETAIL_SCREEN_ROUTE_NAME}/{${MissionActivityConstants.ARG_MAT_ID}}/{${MissionActivityConstants.ARG_CONTENT_SCREEN_CATEGORY}}")

    object MediaPlayerScreen :
        MATHomeScreens(route = "${MissionActivityConstants.MEDIA_PLAYER_SCREEN_ROUTE_NAME}/{${MissionActivityConstants.ARG_CONTENT_KEY}}/{${MissionActivityConstants.ARG_CONTENT_TYPE}}/{${MissionActivityConstants.ARG_CONTENT_TITLE}}")

    object SurveyScreen :
        MATHomeScreens(route = "${MissionActivityConstants.SURVEY_SCREEN_ROUTE_NAME}/{${MissionActivityConstants.ARG_SURVEY_ID}}/{${MissionActivityConstants.ARG_TASK_ID}}/{${MissionActivityConstants.ARG_SECTION_ID}}/{${MissionActivityConstants.ARG_SUBJECT_TYPE}}/{${MissionActivityConstants.ARG_SUBJECT_NAME}}/{${MissionActivityConstants.ARG_REFERENCE_ID}}/{${MissionActivityConstants.ARG_ACTIVITY_CONFIG_ID}}/{${MissionActivityConstants.ARG_GRANT_ID}}/{${MissionActivityConstants.ARG_GRANT_TYPE}}/{${MissionActivityConstants.ARG_SANCTIONED_AMOUNT}}/{${MissionActivityConstants.ARG_TOTAL_SUBMITTED_AMOUNT}}")

    object DisbursementSurveyScreen :
        MATHomeScreens(route = "${MissionActivityConstants.GRANT_SURVEY_SUMMARY_SCREEN_ROUTE_NAME}/{${MissionActivityConstants.ARG_SURVEY_ID}}/{${MissionActivityConstants.ARG_TASK_ID}}/{${MissionActivityConstants.ARG_SECTION_ID}}/{${MissionActivityConstants.ARG_SUBJECT_TYPE}}/{${MissionActivityConstants.ARG_SUBJECT_NAME}}/{${MissionActivityConstants.ARG_ACTIVITY_CONFIG_ID}}/{${MissionActivityConstants.ARG_SANCTIONED_AMOUNT}}")

    object ActivityCompletionScreen :
        MATHomeScreens(route = "${MissionActivityConstants.ACTIVITY_COMPLETION_SCREEN_ROUTE_NAME}/{${MissionActivityConstants.ARG_ACTIVITY_MASSAGE}}/{${MissionActivityConstants.ARG_IS_FROM_ACTIVITY}}")

    object FinalStepCompletionScreen :
        MATHomeScreens(route = MissionActivityConstants.MISSION_FINAL_STEP_SCREEN_ROUTE_NAME)

    object DisbursmentSummaryScreen :
        MATHomeScreens(route = "${MissionActivityConstants.DISBURSEMENT_SUMMARY_SCREEN_ROUTE_NAME}/{${MissionActivityConstants.ARG_ACTIVITY_ID}}/{${MissionActivityConstants.ARG_MISSION_ID}}/{${MissionActivityConstants.ARG_TASK_ID_LIST}}/{${MissionActivityConstants.ARG_IS_FROM_SETTING_SCREEN}}")

    object AddImageScreen :
        MATHomeScreens(route = "${MissionActivityConstants.ADD_IMAGE_SCREEN_SCREEN_ROUTE_NAME}/{${MissionActivityConstants.ARG_ACTIVITY_ID}}/{${MissionActivityConstants.ARG_TASK_ID_LIST}}")
    object PdfViewerScreen :
        MATHomeScreens(route = "${MissionActivityConstants.PDF_VIEWER_SCREEN_ROUTE_NAME}/{${MissionActivityConstants.ARG_FORM_PATH}}")


}

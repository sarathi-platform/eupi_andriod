package com.nudge.navigationmanager.graphs

import androidx.navigation.NavController
import com.nudge.navigationmanager.routes.*
import com.nudge.navigationmanager.utils.NavigationParams

object NudgeNavigationGraph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = "auth_graph"
    val HOME = "home_graph/{${NavigationParams.ARG_USER_TYPE.value}}"
    val BASE_HOME = "base_home_graph"
    val DETAILS = "details_graph/{${NavigationParams.ARG_VILLAGE_ID.value}}/{${NavigationParams.ARG_STEP_ID.value}}/{${NavigationParams.ARG_STEP_INDEX.value}}"
    val ADD_DIDI = "add_didi_graph/{${NavigationParams.ARG_DIDI_DETAILS_ID.value}}"
    val SOCIAL_MAPPING = "social_mapping_graph/{${NavigationParams.ARG_VILLAGE_ID.value}}/{${NavigationParams.ARG_STEP_ID.value}}"
    val WEALTH_RANKING = "wealth_ranking/{${NavigationParams.ARG_VILLAGE_ID.value}}/{${NavigationParams.ARG_STEP_ID.value}}"
    val PAT_SCREENS = "pat_screens/{${NavigationParams.ARG_VILLAGE_ID.value}}/{${NavigationParams.ARG_STEP_ID.value}}"
    val SETTING_GRAPH = "setting_graph"
    val LOGOUT_GRAPH = "logout_graph"
    val VO_ENDORSEMENT_GRAPH = "vo_endorsement_graph/{${NavigationParams.ARG_VILLAGE_ID.value}}/{${NavigationParams.ARG_STEP_ID.value}}/{${NavigationParams.ARG_IS_STEP_COMPLETE.value}}"
    val BPC_GRAPH = "bpc_graph/{${NavigationParams.ARG_VILLAGE_ID.value}}/{${NavigationParams.ARG_STEP_ID.value}}"
     val MISSION_SUMMARY_GRAPH =
        "mission_summary_graph/{${NavigationParams.ARG_ACTIVITY_ID.value}}/{${NavigationParams.ARG_MISSION_ID.value}}/{${NavigationParams.ARG_ACTIVITY_DATE.value}}/{${NavigationParams.ARG_SURVEY_ID.value}}"
}

/**
 * Description: NavController methods to open specific screen
 */
fun NavController.navigateToSearchScreen(
    surveyeId: Int,
    surveyeeId: Int,
    fromScreen: String
) {
    this.navigate("$SEARCH_SCREEN_ROUTE_NAME/$surveyeId/$surveyeeId/$fromScreen")
}

fun NavController.navigateToBaseLineStartScreen(surveyeeId: Int, survyId: Int) {
    this.navigate("$BASELINE_START_SCREEN_ROUTE_NAME/$surveyeeId/$survyId")
}

fun NavController.navigateToFormTypeQuestionScreen(
    questionDisplay: String,
    questionId: Int,
    surveyId: Int,
    sectionId: Int,
    surveyeeId: Int
) {
    this.navigate("$FORM_TYPE_QUESTION_SCREEN_ROUTE_NAME/${questionDisplay}/${surveyId}/${sectionId}/${questionId}/${surveyeeId}")

}

fun NavController.navigateBackToMissionScreen() {
    this.popBackStack(BSHomeScreens.Home_SCREEN.route, false)
}
fun NavController.navigateBackToSurveyeeListScreen() {
    this.popBackStack(BSHomeScreens.SURVEYEE_LIST_SCREEN.route, false)
}

fun NavController.navigateBackToDidiScreen() {
    this.popBackStack(BSHomeScreens.DIDI_SCREEN.route, false)
}
fun NavController.navigateToFormQuestionSummaryScreen(
    surveyId: Int,
    sectionId: Int,
    questionId: Int,
    didiId: Int
) {
    this.navigate("$FORM_QUESTION_SUMMARY_SCREEN_ROUTE_NAME/$surveyId/$sectionId/$questionId/$didiId")
}

fun NavController.navigateBackToSectionListScreen(surveyeeId: Int, surveyeId: Int) {
    this.popBackStack(BSHomeScreens.SECTION_SCREEN.route, true)
    this.navigateToSectionListScreen(surveyeeId = surveyeeId, surveyeId = surveyeId)
}

fun NavController.navigateToSectionListScreen(surveyeeId: Int, surveyeId: Int) {
    this.navigate("$SECTION_SCREEN_ROUTE_NAME/$surveyeeId/$surveyeId")
}

fun NavController.navigateToSelectedSectionFromSearch(didiId: Int, sectionId: Int, surveyId: Int, isFromQuestionSearch: Boolean = true) {
    if (isFromQuestionSearch) {
        this.popBackStack(BSHomeScreens.SECTION_SCREEN.route, true)
    }
    this.navigateToQuestionScreen(didiId = didiId, sectionId = sectionId, surveyId = surveyId)
}
fun NavController.navigateToQuestionScreen(
    didiId: Int,
    sectionId: Int,
    surveyId: Int
) {
    this.navigate("$QUESTION_SCREEN_ROUTE_NAME/${sectionId}/$didiId/$surveyId")
}



sealed class BSHomeScreens(val route: String) {
    object DATA_LOADING_SCREEN : BSHomeScreens(route = DATA_LOADING_SCREEN_ROUTE_NAME)
    object SECTION_SCREEN :
        BSHomeScreens(route = "$SECTION_SCREEN_ROUTE_NAME/{${NavigationParams.ARG_DIDI_ID.value}}/{${NavigationParams.ARG_SURVEY_ID.value}}")

    object QUESTION_SCREEN :
        BSHomeScreens(route = "$QUESTION_SCREEN_ROUTE_NAME/{${NavigationParams.ARG_SECTION_ID.value}}/{${NavigationParams.ARG_DIDI_ID.value}}/{${NavigationParams.ARG_SURVEY_ID.value}}")

    object SURVEYEE_LIST_SCREEN :
        BSHomeScreens(route = "$SURVEYEE_LIST_SCREEN_ROUTE_NAME/{${NavigationParams.ARG_ACTIVITY_ID.value}}")

    object VIDEO_PLAYER_SCREEN :
        BSHomeScreens(route = "$VIDEO_PLAYER_SCREEN_ROUTE_NAME/{${NavigationParams.ARG_VIDEO_PATH.value}}")

    object FormTypeQuestionScreen :
        BSHomeScreens(route = "$FORM_TYPE_QUESTION_SCREEN_ROUTE_NAME/{${NavigationParams.ARG_QUESTION_NAME.value}}/{${NavigationParams.ARG_SURVEY_ID.value}}/{${NavigationParams.ARG_SECTION_ID.value}}/{${NavigationParams.ARG_QUESTION_ID.value}}/{${NavigationParams.ARG_DIDI_ID.value}}?{${NavigationParams.ARG_FORM_QUESTION_RESPONSE_REFERENCE_ID.value}}")

    object BaseLineStartScreen :
        BSHomeScreens(route = "$BASELINE_START_SCREEN_ROUTE_NAME/{${NavigationParams.ARG_DIDI_ID.value}}/{${NavigationParams.ARG_SURVEY_ID.value}}")

    object SearchScreen :
        BSHomeScreens(route = "$SEARCH_SCREEN_ROUTE_NAME/{${NavigationParams.ARG_SURVEY_ID.value}}/{${NavigationParams.ARG_DIDI_ID.value}}/{${NavigationParams.ARG_FROM_SCREEN.value}}")

    object Home_SCREEN : BSHomeScreens(route = HOME_SCREEN_ROUTE_NAME)
    object MISSION_SCREEN : BSHomeScreens(route = MISSION_SCREEN_ROUTE_NAME)
    object DIDI_SCREEN : BSHomeScreens(route = DIDI_SCREEN_ROUTE_NAME)
    object MISSION_SUMMARY_SCREEN :
        BSHomeScreens(route = "$MISSION_SUMMARY_SCREEN_ROUTE_NAME/{${NavigationParams.ARG_MISSION_ID.value}}/{${NavigationParams.ARG_MISSION_NAME.value}}/{${NavigationParams.ARG_MISSION_DATE.value}}")

    object Final_StepComplitionScreen :
        BSHomeScreens(route = "$Final_Step_Complition_Screen_ROUTE_NAME/{${NavigationParams.ARG_COMPLETION_MESSAGE.value}}")

    object STEP_COMPLETION_SCREEN :
        BSHomeScreens(route = "$Step_Complition_Screen_ROUTE_NAME/{${NavigationParams.ARG_COMPLETION_MESSAGE.value}}")

    object FORM_QUESTION_SUMMARY_SCREEN : BSHomeScreens(
        route = "$FORM_QUESTION_SUMMARY_SCREEN_ROUTE_NAME/{${NavigationParams.ARG_SURVEY_ID.value}}/{${NavigationParams.ARG_SECTION_ID.value}}/{${NavigationParams.ARG_QUESTION_ID.value}}/{${NavigationParams.ARG_DIDI_ID.value}}"
    )

}

sealed class SettingScreens(val route: String) {
    object SETTING_SCREEN : SettingScreens(route = SETTING_ROUTE_NAME)
    object LANGUAGE_SCREEN : SettingScreens(route = LANGUAGE_SCREEN_ROUTE_NAME)
    object BUG_LOGGING_SCREEN : SettingScreens(route = BUG_LOGGING_ROUTE_NAME)
    object VIDEO_LIST_SCREEN : SettingScreens(route = VIDEO_SCREEN_ROUTE_NAME)
    object VIDEO_PLAYER_SCREEN : SettingScreens(route = "$VIDEO_PLAYER_SCREEN_ROUTE_NAME/{${NavigationParams.ARG_VIDEO_ID.value}}")
    object PROFILE_SCREEN : SettingScreens(route = PROFILE_SCREEN_ROUTE_NAME)
    object FORM_A_SCREEN : SettingScreens(route = FORM_A_SCREEN_ROUTE_NAME)
    object FORM_B_SCREEN : SettingScreens(route = FORM_B_SCREEN_ROUTE_NAME)
    object FORM_C_SCREEN : SettingScreens(route = FORM_C_SCREEN_ROUTE_NAME)
    object PDF_VIEWER : SettingScreens(route = "$PDF_VIEWER_ROUTE_NAME/{${NavigationParams.ARG_FORM_PATH.value}}")
    object IMAGE_VIEWER : SettingScreens(route = "$IMAGE_VIEWER_ROUTE_NAME/{${NavigationParams.ARG_IMAGE_PATH.value}}")
}

sealed class LogoutScreens(val route: String){
    object LOG_LOGIN_SCREEN : LogoutScreens(route = LOGIN_SCREEN_ROUTE_NAME)
    object LOG_VILLAGE_SELECTION_SCREEN : LogoutScreens(route = VILLAGE_SELECTION_ROUTE_NAME)
    object LOG_DATA_LOADING_SCREEN : LogoutScreens(route = DATA_LOADING_SCREEN_ROUTE_NAME)

    object LOG_OTP_VERIFICATION : LogoutScreens(route = "$OTP_VERIFICATION_ROUTE_NAME/{${NavigationParams.ARG_MOBILE_NUMBER.value}}")
}






package com.nudge.navigationmanager.graphs

import androidx.navigation.NavController
import com.nudge.navigationmanager.routes.ACTIVITY_REOPENING_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.AUTH_BUG_LOGGING_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.AUTH_LANGUAGE_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.AUTH_LOGIN_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.AUTH_OTP_VERIFICATION_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.AUTH_PROFILE_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.AUTH_SETTING_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.AUTH_START_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.AUTH_VIDEO_LIST_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.AUTH_VIDEO_PLAYER_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.AUTH_VILLAGE_SELECTION_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.BACKUP_RECOVERY_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.BASELINE_DIDI_DETAILS_ROUTE_NAME
import com.nudge.navigationmanager.routes.BASELINE_QUESTION_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.BASELINE_START_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.BPC_PROGRESS_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.BUG_LOGGING_ROUTE_NAME
import com.nudge.navigationmanager.routes.DATA_LOADING_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.DIDI_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.EXPORT_BACKUP_FILE_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.FORM_A_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.FORM_B_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.FORM_C_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.FORM_QUESTION_SUMMARY_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.FORM_TYPE_QUESTION_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.Final_Step_Complition_Screen_ROUTE_NAME
import com.nudge.navigationmanager.routes.HOME_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.IMAGE_VIEWER_ROUTE_NAME
import com.nudge.navigationmanager.routes.LANGUAGE_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.LOGIN_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.LOGOUT_HOME_ROUTE_NAME
import com.nudge.navigationmanager.routes.MISSION_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.MISSION_SUMMARY_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.OTP_VERIFICATION_ROUTE_NAME
import com.nudge.navigationmanager.routes.PDF_VIEWER_ROUTE_NAME
import com.nudge.navigationmanager.routes.PROFILE_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.PROGRESS_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.SEARCH_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.SECTION_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.SETTING_ROUTE_NAME
import com.nudge.navigationmanager.routes.SURVEYEE_LIST_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.Step_Complition_Screen_ROUTE_NAME
import com.nudge.navigationmanager.routes.VIDEO_PLAYER_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.VIDEO_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.routes.VILLAGE_SELECTION_ROUTE_NAME
import com.nudge.navigationmanager.routes.VILLAGE_SELECTION_SCREEN_ROUTE_NAME
import com.nudge.navigationmanager.utils.NavigationParams

object NudgeNavigationGraph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = "auth_graph"
    val HOME = "home_graph"
    val BASE_HOME = "base_home_graph"
    val HOME_SUB_GRAPH = "home_sub_graph"
    val DETAILS =
        "details_graph/{${NavigationParams.ARG_VILLAGE_ID.value}}/{${NavigationParams.ARG_STEP_ID.value}}/{${NavigationParams.ARG_STEP_INDEX.value}}"
    val ADD_DIDI = "add_didi_graph/{${NavigationParams.ARG_DIDI_DETAILS_ID.value}}"
    val SOCIAL_MAPPING =
        "social_mapping_graph/{${NavigationParams.ARG_VILLAGE_ID.value}}/{${NavigationParams.ARG_STEP_ID.value}}"
    val WEALTH_RANKING =
        "wealth_ranking/{${NavigationParams.ARG_VILLAGE_ID.value}}/{${NavigationParams.ARG_STEP_ID.value}}"
    val PAT_SCREENS =
        "pat_screens/{${NavigationParams.ARG_VILLAGE_ID.value}}/{${NavigationParams.ARG_STEP_ID.value}}"
    val SETTING_GRAPH = "setting_graph"
    val LOGOUT_GRAPH = "logout_graph"
    val VO_ENDORSEMENT_GRAPH =
        "vo_endorsement_graph/{${NavigationParams.ARG_VILLAGE_ID.value}}/{${NavigationParams.ARG_STEP_ID.value}}/{${NavigationParams.ARG_IS_STEP_COMPLETE.value}}"
    val BPC_GRAPH =
        "bpc_graph/{${NavigationParams.ARG_VILLAGE_ID.value}}/{${NavigationParams.ARG_STEP_ID.value}}"
    val MISSION_SUMMARY_GRAPH =
        "mission_summary_graph/{${NavigationParams.ARG_ACTIVITY_ID.value}}/{${NavigationParams.ARG_MISSION_ID.value}}/{${NavigationParams.ARG_ACTIVITY_DATE.value}}/{${NavigationParams.ARG_SURVEY_ID.value}}"
    val MAT_GRAPH = "mat_graph"
    val SMALL_GROUP_GRAPH = "small_group_graph"
    val INCOME_EXPENSE_GRAPH = "income_expense_graph"

}

/**
 * Description: NavController methods to open specific screen
 */
fun NavController.navigateToSearchScreen(
    surveyeId: Int,
    sectionId: Int = 0,
    surveyeeId: Int,
    fromScreen: String
) {
    this.navigate("$SEARCH_SCREEN_ROUTE_NAME/$surveyeId/$sectionId/$surveyeeId/$fromScreen")
}

fun NavController.navigateToBaseLineStartScreen(surveyeeId: Int, survyId: Int,sectionId:Int) {
    this.navigate("$BASELINE_START_SCREEN_ROUTE_NAME/$surveyeeId/$survyId/$sectionId")
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
    this.navigateUp()
    this.navigateUp()
}
fun NavController.navigateBackToSurveyeeListScreen() {
    this.popBackStack(HomeScreens.SURVEYEE_LIST_SCREEN.route, false)
}

fun NavController.navigateBackToDidiScreen() {
    this.popBackStack(HomeScreens.BS_DIDI_DETAILS_SCREEN.route, false)
}
fun NavController.navigateToFormQuestionSummaryScreen(
    surveyId: Int,
    sectionId: Int,
    questionId: Int,
    didiId: Int
) {
    this.navigate("$FORM_QUESTION_SUMMARY_SCREEN_ROUTE_NAME/$surveyId/$sectionId/$questionId/$didiId")
}

fun NavController.navigateToSurveyListWithParamsScreen(activityName:String,missionId:Int,activityDate:String,surveyId:Int){
    this.navigate("$SURVEYEE_LIST_SCREEN_ROUTE_NAME/$activityName/$missionId/$activityDate/$surveyId")
}
fun NavController.navigateBackToSectionListScreen(surveyeeId: Int, surveyeId: Int) {
    this.popBackStack(HomeScreens.SECTION_SCREEN.route, true)
    this.navigateToSectionListScreen(surveyeeId = surveyeeId, surveyeId = surveyeId)
}

fun NavController.navigateToSectionListScreen(surveyeeId: Int, surveyeId: Int) {
    this.navigate("$SECTION_SCREEN_ROUTE_NAME/$surveyeeId/$surveyeId")
}

fun NavController.navigateToSelectedSectionFromSearch(didiId: Int, sectionId: Int, surveyId: Int, isFromQuestionSearch: Boolean = true) {
    if (isFromQuestionSearch) {
        this.popBackStack(HomeScreens.SECTION_SCREEN.route, true)
    }
    this.navigateToQuestionScreen(didiId = didiId, sectionId = sectionId, surveyId = surveyId)
}
fun NavController.navigateToQuestionScreen(
    didiId: Int,
    sectionId: Int,
    surveyId: Int
) {
    this.navigate("$BASELINE_QUESTION_SCREEN_ROUTE_NAME/${sectionId}/${didiId}/${surveyId}")
}

sealed class HomeScreens(val route: String) {

    object PROGRESS_SEL_SCREEN : HomeScreens(route = PROGRESS_SCREEN_ROUTE_NAME)
    object BPC_PROGRESS_SEL_SCREEN : HomeScreens(route = BPC_PROGRESS_SCREEN_ROUTE_NAME)
    object DIDI_SEL_SCREEN : HomeScreens(route = DIDI_SCREEN_ROUTE_NAME)
    object VILLAGE_SELECTION_SCREEN : HomeScreens(route = VILLAGE_SELECTION_SCREEN_ROUTE_NAME)
    object DATA_LOADING_SCREEN : HomeScreens(route = DATA_LOADING_SCREEN_ROUTE_NAME)
    object SECTION_SCREEN :
        HomeScreens(route = "$SECTION_SCREEN_ROUTE_NAME/{${NavigationParams.ARG_DIDI_ID.value}}/{${NavigationParams.ARG_SURVEY_ID.value}}")

    object QUESTION_SCREEN :
        HomeScreens(route = "$BASELINE_QUESTION_SCREEN_ROUTE_NAME/{${NavigationParams.ARG_SECTION_ID.value}}/{${NavigationParams.ARG_DIDI_ID.value}}/{${NavigationParams.ARG_SURVEY_ID.value}}")

    object SURVEYEE_LIST_SCREEN :
        HomeScreens(route = "$SURVEYEE_LIST_SCREEN_ROUTE_NAME/{${NavigationParams.ARG_ACTIVITY_ID.value}}")

    object VIDEO_PLAYER_SCREEN :
        HomeScreens(route = "$VIDEO_PLAYER_SCREEN_ROUTE_NAME/{${NavigationParams.ARG_VIDEO_PATH.value}}")

    object FormTypeQuestionScreen :
        HomeScreens(route = "$FORM_TYPE_QUESTION_SCREEN_ROUTE_NAME/{${NavigationParams.ARG_QUESTION_NAME.value}}/{${NavigationParams.ARG_SURVEY_ID.value}}/{${NavigationParams.ARG_SECTION_ID.value}}/{${NavigationParams.ARG_QUESTION_ID.value}}/{${NavigationParams.ARG_DIDI_ID.value}}?{${NavigationParams.ARG_FORM_QUESTION_RESPONSE_REFERENCE_ID.value}}")

    object BaseLineStartScreen :
        HomeScreens(route = "$BASELINE_START_SCREEN_ROUTE_NAME/{${NavigationParams.ARG_DIDI_ID.value}}/{${NavigationParams.ARG_SURVEY_ID.value}}/{${NavigationParams.ARG_SECTION_ID.value}}")

    object SearchScreen :
        HomeScreens(route = "$SEARCH_SCREEN_ROUTE_NAME/{${NavigationParams.ARG_SURVEY_ID.value}}/{${NavigationParams.ARG_SECTION_ID.value}}/{${NavigationParams.ARG_DIDI_ID.value}}/{${NavigationParams.ARG_FROM_SCREEN.value}}")

    object Home_SCREEN : HomeScreens(route = HOME_SCREEN_ROUTE_NAME)
    object MISSION_SCREEN : HomeScreens(route = MISSION_SCREEN_ROUTE_NAME)
    object BS_DIDI_DETAILS_SCREEN : HomeScreens(route = BASELINE_DIDI_DETAILS_ROUTE_NAME)
    object MISSION_SUMMARY_SCREEN :
        HomeScreens(route = "$MISSION_SUMMARY_SCREEN_ROUTE_NAME/{${NavigationParams.ARG_MISSION_ID.value}}/{${NavigationParams.ARG_MISSION_NAME.value}}")

    object Final_StepComplitionScreen :
        HomeScreens(route = "$Final_Step_Complition_Screen_ROUTE_NAME/{${NavigationParams.ARG_COMPLETION_MESSAGE.value}}")

    object STEP_COMPLETION_SCREEN :
        HomeScreens(route = "$Step_Complition_Screen_ROUTE_NAME/{${NavigationParams.ARG_COMPLETION_MESSAGE.value}}")

    object FORM_QUESTION_SUMMARY_SCREEN : HomeScreens(
        route = "$FORM_QUESTION_SUMMARY_SCREEN_ROUTE_NAME/{${NavigationParams.ARG_SURVEY_ID.value}}/{${NavigationParams.ARG_SECTION_ID.value}}/{${NavigationParams.ARG_QUESTION_ID.value}}/{${NavigationParams.ARG_DIDI_ID.value}}"
    )

    object SURVEYEE_LIST_SCREEN_WITH_PARAMS :
        HomeScreens(route = "$SURVEYEE_LIST_SCREEN_ROUTE_NAME/{${NavigationParams.ARG_ACTIVITY_ID.value}}/{${NavigationParams.ARG_MISSION_ID.value}}/{${NavigationParams.ARG_ACTIVITY_DATE.value}}/{${NavigationParams.ARG_SURVEY_ID.value}}")

    object DIDI_TAB_SCREEN : HomeScreens("didi_tab_screen")

    object DATA_TAB_SCREEN : HomeScreens("data_tab_screen")
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
    object EXPORT_BACKUP_FILE_SCREEN : SettingScreens(route = EXPORT_BACKUP_FILE_SCREEN_ROUTE_NAME)
    object BACKUP_RECOVERY_SCREEN : SettingScreens(route = BACKUP_RECOVERY_SCREEN_ROUTE_NAME)
    object ACTIVITY_REOPENING_SCREEN : SettingScreens(route = ACTIVITY_REOPENING_SCREEN_ROUTE_NAME)
}

sealed class LogoutScreens(val route: String){
    object LOG_LOGIN_SCREEN : LogoutScreens(route = LOGIN_SCREEN_ROUTE_NAME)
    object LOG_VILLAGE_SELECTION_SCREEN : LogoutScreens(route = VILLAGE_SELECTION_ROUTE_NAME)
    object LOG_DATA_LOADING_SCREEN :
        LogoutScreens(route = "$DATA_LOADING_SCREEN_ROUTE_NAME/{${NavigationParams.ARG_MISSION_ID.value}}/{${NavigationParams.ARG_MISSION_NAME.value}}")
    object LOG_HOME_SCREEN : LogoutScreens(route = LOGOUT_HOME_ROUTE_NAME)

    object LOG_OTP_VERIFICATION : LogoutScreens(route = "$OTP_VERIFICATION_ROUTE_NAME/{${NavigationParams.ARG_MOBILE_NUMBER.value}}")
}

sealed class AuthScreen(val route: String) {
    object START_SCREEN : AuthScreen(route = AUTH_START_SCREEN_ROUTE_NAME)
    object LANGUAGE_SCREEN : AuthScreen(route = AUTH_LANGUAGE_SCREEN_ROUTE_NAME)
    object BUG_LOGGING_SCREEN : AuthScreen(route = AUTH_BUG_LOGGING_SCREEN_ROUTE_NAME)
    object LOGIN : AuthScreen(route = AUTH_LOGIN_SCREEN_ROUTE_NAME)
    object VILLAGE_SELECTION_SCREEN : AuthScreen(route = AUTH_VILLAGE_SELECTION_SCREEN_ROUTE_NAME)
    object OTP_VERIFICATION : AuthScreen(route = "$AUTH_OTP_VERIFICATION_SCREEN_ROUTE_NAME/{${NavigationParams.ARG_MOBILE_NUMBER.value}}")
    object AUTH_SETTING_SCREEN : AuthScreen(route = AUTH_SETTING_SCREEN_ROUTE_NAME)
    object PROFILE_SCREEN : AuthScreen(route = AUTH_PROFILE_SCREEN_ROUTE_NAME)
    object VIDEO_LIST_SCREEN : AuthScreen(route = AUTH_VIDEO_LIST_SCREEN_ROUTE_NAME)
    object VIDEO_PLAYER_SCREEN : AuthScreen(route = "$AUTH_VIDEO_PLAYER_SCREEN_ROUTE_NAME/{${NavigationParams.ARG_VIDEO_ID.value}}")

    object DATA_LOADING_SCREEN :AuthScreen(route = DATA_LOADING_SCREEN_ROUTE_NAME)
}







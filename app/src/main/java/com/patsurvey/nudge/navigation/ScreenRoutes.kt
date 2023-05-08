package com.patsurvey.nudge.navigation

import com.patsurvey.nudge.utils.ARG_MOBILE_NUMBER
import com.patsurvey.nudge.utils.ARG_VILLAGE_ID
import com.patsurvey.nudge.utils.ARG_VILLAGE_NAME

enum class ScreenRoutes(val route: String) {
    START_SCREEN("start_screen"),
    LANGUAGE_SCREEN("language_screen"),
    LOGIN_SCREEN("login_screen"),
    OTP_VERIFICATION_SCREEN("otp_verification_screen/{$ARG_MOBILE_NUMBER}"),
    HOME_SCREEN("home_route"),
    PROFILE_SCREEN("profile_screen"),
    DIDI_SCREEN("didi_screen"),
    ADD_DIDI_SCREEN("add_didi_screen"),
    MORE_SCREEN("more_screen"),
    PROGRESS_SCREEN("progress_screen"),
    TRANSECT_WALK_SCREEN("transect_walk_screen/{$ARG_VILLAGE_ID}"),
    RANKED_DIDI_LIST_SCREEN("ranked_didi_list_screen"),
    DIGITAL_FORM_A_SCREEN("digital_form_a_screen"),
    SOCIAL_MAPPING_SCREEN("social_mapping_screen"),
    LOGIN_HOME_SCREEN("login_home_screen"),
    VILLAGE_SELECTION_SCREEN("village_selection_screen");


    override fun toString(): String {
        return route
    }
}
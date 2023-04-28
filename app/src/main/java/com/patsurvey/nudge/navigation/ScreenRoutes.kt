package com.patsurvey.nudge.navigation

enum class ScreenRoutes(val route: String) {
    START_SCREEN("start_screen"),
    LANGUAGE_SCREEN("language_screen"),
    LOGIN_SCREEN("login_screen"),
    OTP_VERIFICATION_SCREEN("otp_verification_screen"),
    HOME_SCREEN("home_route"),
    PROFILE_SCREEN("profile_screen"),
    DIDI_SCREEN("didi_screen"),
    MORE_SCREEN("more_screen"),
    PROGRESS_SCREEN("progress_scree"),
    TRANSECT_WALK_SCREEN("transect_walk_screen"),
    RANKED_DIDI_LIST_SCREEN("ranked_didi_list_screen"),
    DIGITAL_FORM_A_SCREEN("digital_form_a_screen"),
    SOCIAL_MAPPING_SCREEN("social_mapping_screen");


    override fun toString(): String {
        return route
    }
}
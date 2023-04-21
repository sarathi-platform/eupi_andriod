package com.patsurvey.nudge.navigation

enum class ScreenRoutes(val route: String) {
    START_SCREEN("start_screen"),
    LANGUAGE_SCREEN("language_screen"),
    HOME_SCREEN("home_route"),
    PROFILE_SCREEN("profile_screen"),
    DIDI_SCREEN("didi_screen"),
    MORE_SCREEN("more_screen");

    override fun toString(): String {
        return route
    }
}
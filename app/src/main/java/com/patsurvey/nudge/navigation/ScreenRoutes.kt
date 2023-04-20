package com.patsurvey.nudge.navigation

enum class ScreenRoutes(val route: String) {
    START_SCREEN("start_screen"),
    LANGUAGE_SCREEN("language_screen");

    override fun toString(): String {
        return route
    }
}
package com.patsurvey.nudge.utils

import androidx.compose.ui.graphics.painter.Painter
import com.nudge.core.enums.TabsEnum

data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: Painter,
    val tabItem: TabsEnum
)
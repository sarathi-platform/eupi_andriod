package com.nrlm.baselinesurvey.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.greenActiveIcon
import com.nrlm.baselinesurvey.ui.theme.smallestTextStyle

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val screens = listOf(
        BottomNavItem(
            stringResource(R.string.mission),
            "home_screen",
            painterResource(R.drawable.ic_mission_icon)
        ),
        BottomNavItem(
            stringResource(R.string.task),
            "mission_screen",
            painterResource(R.drawable.ic_mission_icon)
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarDestination = screens.any { it.route == currentDestination?.route }
    if (bottomBarDestination) {
        BottomNavigation(
            modifier = Modifier.shadow(
                elevation = 10.dp
            ),
            backgroundColor = Color.White
        ) {
            screens.forEach { screen ->
                AddItem(
                    screen = screen,
                    currentDestination = currentDestination,
                    navController = navController,
                )
            }
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomNavItem,
    currentDestination: NavDestination?,
    navController: NavHostController,
) {
    val selected = currentDestination?.hierarchy?.any {
        it.route == screen.route
    } == true
    BottomNavigationItem(
        alwaysShowLabel = true,
        icon = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .padding(horizontal = 27.dp)
                        .background(if (selected) blueDark else Color.White)
                        .border(
                            width = 2.dp, color = if (selected) blueDark else Color.White,
                            shape = RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp)
                        )
                )
                Icon(
                    painter = screen.icon,
                    contentDescription = screen.name,
                    tint = if (selected) greenActiveIcon else blueDark,
                    modifier = Modifier.padding(top = 5.dp)
                )
                Text(
                    text = screen.name,
                    textAlign = TextAlign.Center,
                    style = smallestTextStyle,
                    color = if (selected) greenActiveIcon else blueDark
                )
            }
        },
        selected = selected,
        onClick = {
            //  prefRepo.saveFromPage(ARG_FROM_HOME)
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        }
    )
}


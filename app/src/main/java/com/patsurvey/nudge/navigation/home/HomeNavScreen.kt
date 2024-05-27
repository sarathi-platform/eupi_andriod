package com.patsurvey.nudge.navigation.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
import androidx.navigation.compose.rememberNavController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.greenActiveIcon
import com.patsurvey.nudge.activities.ui.theme.smallestTextStyle
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.utils.ARG_FROM_HOME
import com.patsurvey.nudge.utils.BPC_USER_TYPE
import com.patsurvey.nudge.utils.BottomNavItem
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_NAME
import com.patsurvey.nudge.utils.UPCM_USER

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeNavScreen(navController: NavHostController = rememberNavController(), prefRepo: PrefRepo) {
    Scaffold(
        bottomBar = { BottomBar(navController = navController, prefRepo) }
    ) {
        NavHomeGraph(navController = navController, prefRepo)
    }
}

@Composable
fun BottomBar(navController: NavHostController, prefRepo: PrefRepo) {
    val screens = listOf(
        BottomNavItem(
            stringResource(R.string.progress_item_text),
            if ((prefRepo.getPref(PREF_KEY_TYPE_NAME, "") ?: "").equals(BPC_USER_TYPE, true))
                HomeScreens.BPC_PROGRESS_SCREEN.route
            else
                HomeScreens.PROGRESS_SCREEN.route,
            if (prefRepo.getLoggedInUserType() == UPCM_USER) painterResource(id = R.drawable.ic_mission_icon) else painterResource(
                R.drawable.progress_icon
            )
        ),
        BottomNavItem(
            stringResource(R.string.didis_item_text_plural),
            if (prefRepo.getLoggedInUserType() == UPCM_USER) HomeScreens.DIDI_TAB_SCREEN.route else HomeScreens.DIDI_SCREEN.route,
            painterResource(R.drawable.didi_icon)
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
                    prefRepo = prefRepo
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
    prefRepo: PrefRepo
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
            prefRepo.saveFromPage(ARG_FROM_HOME)
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        }
    )
}
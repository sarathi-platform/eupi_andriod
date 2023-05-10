package com.patsurvey.nudge.activities

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.navigation.HomeScreenFlowNavigation
import com.patsurvey.nudge.navigation.ScreenRoutes
import com.patsurvey.nudge.utils.BottomNavItem


@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,

    ) {
    val homeScreenNavController = rememberNavController()
    val stepsNavController = rememberNavController()

    val activity = LocalContext.current as? Activity

    BackHandler {
        activity?.finish()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(languageItemActiveBg)
            .then(modifier),
        bottomBar = {
            BottomNavigationBar(
                modifier = Modifier
                    .background(Color.White)
                    .then(modifier),
                items = listOf(
                    BottomNavItem(
                        stringResource(R.string.progress_item_text),
                        ScreenRoutes.PROGRESS_SCREEN.route,
                        painterResource(R.drawable.progress_icon)
                    ),
                    BottomNavItem(
                        stringResource(R.string.didis_item_text),
                        ScreenRoutes.DIDI_SCREEN.route,
                        painterResource(R.drawable.didi_icon)
                    ),
//                    BottomNavItem(
//                        stringResource(R.string.more_item_text),
//                        ScreenRoutes.MORE_SCREEN.route,
//                        painterResource(R.drawable.more_icon)
//                    )
                ),
                navController = homeScreenNavController,
                onItemClick = {
                    homeScreenNavController.navigate(it.route)
                }
            )
        }
    ) {
        HomeScreenFlowNavigation(
            homeScreenNavController = homeScreenNavController,
            stepsNavHostController = stepsNavController,
            modifier = Modifier
                .padding(bottom = it.calculateBottomPadding())

        )

    }
}

@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClick: (BottomNavItem) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    BottomNavigation(
        modifier = modifier,
        backgroundColor = Color.White,
        elevation = 5.dp
    ) {
        items.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            BottomNavigationItem(
                selected = selected,
                onClick = { onItemClick(item) },
                alwaysShowLabel = true,
                icon = {
                    Column(horizontalAlignment = CenterHorizontally) {

                        Icon(
                            painter = item.icon,
                            contentDescription = item.name,
                            tint = if (selected) greenActiveIcon else blueDark
                        )
                        Text(
                            text = item.name,
                            textAlign = TextAlign.Center,
                            style = smallestTextStyle,
                            color = blueDark
                        )
                    }
                }
            )
        }
    }
}
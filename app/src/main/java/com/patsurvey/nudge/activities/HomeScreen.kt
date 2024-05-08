package com.patsurvey.nudge.activities

import android.annotation.SuppressLint
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
import com.nudge.navigationmanager.graphs.HomeScreens
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.navigation.ScreenRoutes
import com.patsurvey.nudge.utils.ARG_FROM_HOME
import com.patsurvey.nudge.utils.BottomNavItem


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
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
                        HomeScreens.PROGRESS_SEL_SCREEN.route,
                        painterResource(R.drawable.progress_icon)
                    ),
                    BottomNavItem(
                        stringResource(R.string.didi_endorsed_text_plural),
                        HomeScreens.DIDI_SEL_SCREEN.route,
                        painterResource(R.drawable.didi_icon)
                    ),
                ),
                navController = homeScreenNavController,
                onItemClick = {
                    if(it.route.equals(ScreenRoutes.DIDI_SCREEN.route,true)){
                        homeScreenNavController.navigate("didi_screen/$ARG_FROM_HOME")
                    }else homeScreenNavController.navigate(it.route)
                }
            )
        }
    ) {
//       HomeNavGraph(homeScreenNavController = homeScreenNavController, modifier = Modifier)
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
        elevation = 5.dp,
    ) {
        items.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            BottomNavigationItem(
                selected = selected,
                onClick = { onItemClick(item) },
                alwaysShowLabel = true,
                icon = {
                    Column(horizontalAlignment = CenterHorizontally) {
                        Spacer(modifier = Modifier.height(26.dp)
                            .background( blueDark))
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
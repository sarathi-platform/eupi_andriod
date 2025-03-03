package com.patsurvey.nudge.navigation.selection

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.nudge.core.TabsCore
import com.nudge.core.enums.TabsEnum
import com.nudge.core.helper.LocalTranslationHelper
import com.nudge.core.isOnline
import com.nudge.navigationmanager.graphs.HomeScreens
import com.nudge.navigationmanager.graphs.NudgeNavigationGraph
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.greenActiveIcon
import com.patsurvey.nudge.activities.ui.theme.smallestTextStyle
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.utils.ARG_FROM_HOME
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.BPC_USER_TYPE
import com.patsurvey.nudge.utils.BottomNavItem
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_NAME
import com.patsurvey.nudge.utils.UPCM_USER
import com.sarathi.dataloadingmangement.ui.component.ShowCustomDialog
import com.sarathi.missionactivitytask.navigation.MATHomeScreens

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeNavScreen(navController: NavHostController = rememberNavController(), prefRepo: PrefRepo) {
    Scaffold(
        bottomBar = {
            BottomBar(navController = navController, prefRepo)
        }
    ) {
        Log.d("TAG", "HomeNavScreen: ${prefRepo.getPref(PREF_KEY_TYPE_NAME, BLANK_STRING)}")
        NavHomeGraph(navController = navController, prefRepo)

    }
}

@Composable
fun BottomBar(navController: NavHostController, prefRepo: PrefRepo) {

    val translationHelper = LocalTranslationHelper.current

    val dataNotLoadedDialog = remember {
        mutableStateOf(false)
    }
    if (dataNotLoadedDialog.value) {
        ShowCustomDialog(
            message = stringResource(id = com.sarathi.missionactivitytask.R.string.data_not_Loaded_for_tab),
            positiveButtonTitle = stringResource(id = com.sarathi.missionactivitytask.R.string.ok),
            onNegativeButtonClick = {
                dataNotLoadedDialog.value = false
            },
            onPositiveButtonClick = {
                dataNotLoadedDialog.value = false
            }
        )
    }
    var screenList: MutableList<BottomNavItem> = mutableListOf<BottomNavItem>()
    if (prefRepo.getPref(PREF_KEY_TYPE_NAME, BLANK_STRING).equals(UPCM_USER)) {
        screenList.add(
            BottomNavItem(
                translationHelper.stringResource(R.string.mission),
                MATHomeScreens.MissionScreen.route,
                painterResource(R.drawable.ic_mission_icon),
                TabsEnum.MissionTab
            )
        )
        if (prefRepo.isDataTabVisible()) {
            screenList.add(
                BottomNavItem(
                    translationHelper.stringResource(R.string.data),
                    HomeScreens.DATA_TAB_SCREEN.route,
                    painterResource(id = R.drawable.data_tab_icon),
                    TabsEnum.DataTab
                )
            )
        }
        screenList.add(
            BottomNavItem(
                translationHelper.stringResource(R.string.didis_item_text_plural),
                HomeScreens.DIDI_TAB_SCREEN.route,
                painterResource(R.drawable.didi_icon),
                TabsEnum.DidiUpcmTab
            )
        )
    } else {
        screenList = mutableListOf(
            BottomNavItem(
                translationHelper.stringResource(R.string.progress_item_text),
                if ((prefRepo.getPref(PREF_KEY_TYPE_NAME, "") ?: "").equals(BPC_USER_TYPE, true))
                    HomeScreens.BPC_PROGRESS_SEL_SCREEN.route
                else
                    HomeScreens.PROGRESS_SEL_SCREEN.route,
                painterResource(R.drawable.progress_icon),
                TabsEnum.ProgressTab
            ),
            BottomNavItem(
                translationHelper.stringResource(R.string.didis_item_text_plural),
                HomeScreens.DIDI_SEL_SCREEN.route,
                painterResource(R.drawable.didi_icon),
                TabsEnum.DidiCrpTab
            )
        )
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomBarDestination = screenList.any { it.route == currentDestination?.route }
    if (bottomBarDestination) {
        BottomNavigation(
            modifier = Modifier.shadow(
                elevation = 10.dp
            ),
            backgroundColor = Color.White
        ) {
            screenList.forEach { screen ->
                AddItem(
                    screen = screen,
                    currentDestination = currentDestination,
                    navController = navController,
                    prefRepo = prefRepo,
                    dataNotLoadedDialog
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
    prefRepo: PrefRepo,
    dataNotLoadedDialog: MutableState<Boolean>
) {
    val context = LocalContext.current

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
            if (screen.route.equals(MATHomeScreens.MissionScreen.route)) {
                // TODO Fix issue where on click of Missions Tab it opens Mission screen from Baseline module.
                navController.navigate(NudgeNavigationGraph.MAT_GRAPH)
            }

            if ((!isOnline(context) &&
                        ((screen.tabItem == TabsEnum.DataTab && !prefRepo.isDataTabDataLoaded()) ||
                                (screen.tabItem == TabsEnum.DidiUpcmTab && !prefRepo.isDidiTabDataLoaded())))
            ) {
                dataNotLoadedDialog.value = true

            } else {
                navController.navigate(screen.route) {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
                TabsCore.setTabIndex(screen.tabItem.tabIndex)
            }
        }
    )
}
package com.sarathi.missionactivitytask.ui.grant_activity_screen.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.missionactivitytask.ui.grant_activity_screen.viewmodel.ActivityScreenViewModel
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent

@Composable
fun ActivityScreen(
    navController: NavController = rememberNavController(),
    viewModel: ActivityScreenViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.onEvent(InitDataEvent.InitDataState)
    }
    ToolBarWithMenuComponent(
        title = "CSG disbursement to Didi",
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        onBackIconClick = { navController.popBackStack() },
        isSearch = true,
        isDataAvailable = viewModel.activityList.value.isEmpty(),
        onSearchValueChange = {

        },
        onBottomUI = {
        },
        isFilterSelected = {},
        tabBarView = {},
        onContentUI = {
            if (viewModel.activityList.value.isNotEmpty()) {
                ActivityRowCard(
                    activities = viewModel.activityList.value,
                    contents = getContent()
                )
            }
        }
    )
}

fun getContent(): List<BasicContent> {
    val basicContentResponse1 = BasicContent("IMAGE", "ContentResponse Image")
    val basicContent2 = BasicContent("VIDEO", "ContentResponse Video")
    val basicContent3 = BasicContent("FILE", "ContentResponse File")
    val basicContent4 = BasicContent("IMAGE", "ContentResponse Image")
    val basicContent5 = BasicContent("IMAGE", "ContentResponse Image")
    val basicContent6 = BasicContent("IMAGE", "ContentResponse Image")
    val basicContent7 = BasicContent("IMAGE", "ContentResponse Image")
    val basicContent8 = BasicContent("IMAGE", "ContentResponse Image")
    val basicContent9 = BasicContent("IMAGE", "ContentResponse Image")
    val basicContent10 = BasicContent("IMAGE", "ContentResponse Image")
    return listOf(
        basicContentResponse1,
        basicContent2,
        basicContent3,
        basicContent4,
        basicContent5,
        basicContent6,
        basicContent7,
        basicContent8,
        basicContent9,
        basicContent10
    )
}
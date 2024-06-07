package com.sarathi.surveymanager.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sarathi.surveymanager.ui.component.ToolBarWithMenuComponent
import com.sarathi.surveymanager.viewmodels.DisbursementSummaryScreenViewModel

@Composable
fun DisbursementSummaryScreen(
    navController: NavController = rememberNavController(),
    viewModel: DisbursementSummaryScreenViewModel,
    onSettingClick: () -> Unit,
    surveyId: Int,
    sectionId: Int,
    taskId: Int,
    subjectType: String,
    subjectName: String,
    activityConfigId: Int,
    onNavigateSurveyScreen: () -> Unit,
) {

    LaunchedEffect(key1 = true) {
        viewModel.setPreviousScreenData(surveyId, sectionId, taskId, subjectType, activityConfigId)
    }
    ToolBarWithMenuComponent(
        title = subjectName,
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        onBackIconClick = { navController.popBackStack() },
        isSearch = false,
        isDataAvailable = false,
        onSearchValueChange = {

        },
        onBottomUI = {
        },
        onContentUI = { paddingValues ->
            CollapsibleCard(
                summaryCount = 10, onClick = {
                    onNavigateSurveyScreen()
                },
                onContentUI = {
                    DisbursementCard()
                })

        },
        onSettingClick = onSettingClick
    )
}
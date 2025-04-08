package com.sarathi.dataloadingmangement.ui.component.api_failed_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.database.entities.api.ApiCallJournalEntity
import com.nudge.core.ui.commonUi.BasicCardView
import com.nudge.core.ui.commonUi.ToolBarWithMenuComponent
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.didiDetailLabelStyle
import com.nudge.core.ui.theme.redOffline
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent


@Composable
fun ApiFailedListScreen(
    screenName: String,
    moduleName: String,
    onSettingClick: () -> Unit,
    navController: NavController = rememberNavController(),
    viewModel: ApiFailedListScreenViewModel,
) {
    LaunchedEffect(key1 = true) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.onEvent(
            InitDataEvent.InitApiFailedScreenState(
                screenName = screenName,
                moduleName = moduleName
            )
        )
    }

    ToolBarWithMenuComponent(
        title = "ApiFailedListScreen",
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        onBackIconClick = { navController.popBackStack() },
        isSearch = false,
        onRetry = {
        },
        onSearchValueChange = {
        },
        onBottomUI = {
        },
        onContentUI = { paddingValues, isSearch, onSearchValueChanged ->
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(viewModel.apiFailedList.value) { failedApi ->
                    FailedApiItem(failedApi)
                }
            }
        },
        onSettingClick = onSettingClick
    )
}

@Composable
fun FailedApiItem(failedApi: ApiCallJournalEntity) {
    BasicCardView(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Screen: ${failedApi.screenName}",
                style = didiDetailLabelStyle.copy(color = blueDark)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "API: ${failedApi.apiUrl}",
                style = didiDetailLabelStyle.copy(color = blueDark)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Error: ${failedApi.errorMsg}",
                style = didiDetailLabelStyle.copy(color = redOffline)
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewApiFailedListScreen() {
//    ApiFailedListScreen(failedApis = listOf(
//        FailedApi("HomeScreen", "GET /users", "Timeout Error"),
//        FailedApi("ProfileScreen", "POST /updateProfile", "Network Error"),
//        FailedApi("DashboardScreen", "GET /stats", "Server Error")
//    ))
//}

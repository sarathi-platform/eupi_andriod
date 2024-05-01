package com.nrlm.baselinesurvey.ui.mission_screen.presentation

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.activity.MainActivity
import com.nrlm.baselinesurvey.ui.common_components.ButtonPositive
import com.nrlm.baselinesurvey.ui.common_components.SearchWithFilterViewComponent
import com.nrlm.baselinesurvey.ui.common_components.common_events.SearchEvent
import com.nrlm.baselinesurvey.ui.mission_screen.viewmodel.MissionViewModel
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.defaultTextStyle
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.showCustomToast
import com.nudge.navigationmanager.graphs.NudgeNavigationGraph
import com.nudge.navigationmanager.routes.MISSION_SUMMARY_SCREEN_ROUTE_NAME

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun MissionScreen_1(
    navController: NavController = rememberNavController(),
    viewModel: MissionViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val filteredMissionList = viewModel.filterMissionList

    val loaderState = viewModel.loaderState.value
    val pullRefreshState = rememberPullRefreshState(
        viewModel.loaderState.value.isLoaderVisible,
        {
            if (BaselineCore.isOnline.value) {
                viewModel.refreshData()
            } else {
                showCustomToast(
                    context,
                    context.getString(R.string.refresh_failed_please_try_again)
                )

            }

        })

    LaunchedEffect(key1 = true) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.init()
    }
    BackHandler {
        (context as? MainActivity)?.finish()
    }
    Scaffold(
        modifier = Modifier,
        containerColor = white,
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_sarathi_logo),
                            contentDescription = "more action button",
                            tint = blueDark,
                            modifier = Modifier
                                .padding(vertical = 10.dp)
                        )
                        Text(
                            text = "SARATHI",
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp, color = textColorDark,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(NudgeNavigationGraph.SETTING_GRAPH)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.more_icon),
                            contentDescription = "more action button",
                            tint = blueDark,
                            modifier = Modifier
                                .padding(10.dp)
                        )
                    }
                },
                backgroundColor = Color.White,
                elevation = 10.dp
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp)
        ) {
            SearchWithFilterViewComponent(placeholderString = stringResource(id = R.string.search),
                filterSelected = false,
                modifier = Modifier.padding(horizontal = 10.dp),
                showFilter = false,
                onFilterSelected = {},
                onSearchValueChange = { queryTerm ->
                    viewModel.onEvent(SearchEvent.PerformSearch(queryTerm, false, ""))
                })

            Box(modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState))
            {
                PullRefreshIndicator(
                    refreshing = viewModel.loaderState.value.isLoaderVisible,
                    state = pullRefreshState,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .zIndex(1f),
                    contentColor = blueDark,
                )
                if (!loaderState.isLoaderVisible) {

                if (filteredMissionList.value.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .align(Alignment.Center),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            stringResource(R.string.not_able_to_load),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = defaultTextStyle,
                            color = textColorDark
                        )
                        Spacer(modifier = Modifier.padding(vertical = 10.dp))
                        ButtonPositive(
                            buttonTitle = stringResource(id = R.string.retry),
                            isActive = true,
                            isArrowRequired = false,
                            onClick = {
                                viewModel.refreshData()
                            })
                            

                    }
                } else {
                    LazyColumn {
                        items(filteredMissionList.value) { mission ->

                            val pendingTaskCountForMission =
                                viewModel.getPendingActivityCountForMissionLive(mission.missionId)
                                    .observeAsState().value ?: 0

                            MissonRowScreen_1(
                                mission = mission,
                                missionDueDate = mission.startDate,
                                viewModel = viewModel,
                                pendingCount = pendingTaskCountForMission,
                                onViewStatusClick = {},
                                onStartClick = {
                                    navController.navigate("${MISSION_SUMMARY_SCREEN_ROUTE_NAME}/${mission.missionId}/${mission.missionName}/${mission.endDate}")
                                })
                        }

                    }
                }
                }

            }
        }
    }

}

@Preview
@Composable
fun MissionScreen_1Perview() {
    MissionScreen_1()
}

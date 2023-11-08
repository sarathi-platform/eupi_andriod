package com.nrlm.baselinesurvey.ui.surveyee_screen.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nrlm.baselinesurvey.ALL_TAB
import com.nrlm.baselinesurvey.ARG_DIDI_ID
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.THIS_WEEK_TAB
import com.nrlm.baselinesurvey.navigation.home.HomeScreens
import com.nrlm.baselinesurvey.ui.common_components.LoaderComponent
import com.nrlm.baselinesurvey.ui.common_components.SearchWithFilterViewComponent
import com.nrlm.baselinesurvey.ui.common_components.common_events.SearchEvent
import com.nrlm.baselinesurvey.ui.surveyee_screen.viewmodel.SurveyeeScreenViewModel
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.borderGreyLight
import com.nrlm.baselinesurvey.ui.theme.defaultCardElevation
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.progressIndicatorColor
import com.nrlm.baselinesurvey.ui.theme.red
import com.nrlm.baselinesurvey.ui.theme.smallTextStyle
import com.nrlm.baselinesurvey.ui.theme.smallTextStyleWithNormalWeight
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.trackColor
import com.nrlm.baselinesurvey.ui.theme.unselectedTabColor
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.FilterListState
import com.nrlm.baselinesurvey.utils.showCustomToast

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SurveyeeListScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: SurveyeeScreenViewModel
) {

    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.init()
    }

    val loaderState = viewModel.loaderState.value
    val surveyeeList = viewModel.filteredSurveyeeListState.value

    val surveyeeListWithTolaFilter = viewModel.tolaMapSurveyeeListState.value

    val isFilterAppliedState = remember {
        mutableStateOf(FilterListState())
    }

    var selectedTabIndex = remember { mutableIntStateOf(1) }

    val tabs = listOf(THIS_WEEK_TAB, ALL_TAB)

    val pullRefreshState = rememberPullRefreshState(
        loaderState.isLoaderVisible,
        {
            if (BaselineCore.isOnline.value) {
                if (selectedTabIndex.intValue == tabs.indexOf(THIS_WEEK_TAB)) {
                    //Handle API Call
                } else {
                    //Handle API/DB Call
                }
            } else {
                showCustomToast(context, context.getString(R.string.refresh_failed_please_try_again))
            }

        })

    Scaffold(
        topBar = {
            TabRow(
                selectedTabIndex = selectedTabIndex.intValue,
                containerColor = Color.White,
                contentColor = textColorDark,
                modifier = Modifier
                    .shadow(defaultCardElevation)
                    .zIndex(1f),
            ) {
                tabs.forEachIndexed { tabIndex, tab ->
                    Tab(
                        selected = tabIndex == selectedTabIndex.intValue,
                        onClick = { selectedTabIndex.intValue = tabIndex },
                        text = {
                            Text(
                                text = if (tab == THIS_WEEK_TAB) stringResource(R.string.this_week_tab_title) else stringResource(R.string.all_tab_title),
                                style = if (tabIndex == selectedTabIndex.intValue) smallTextStyle else smallTextStyleWithNormalWeight
                            )
                        },
                        selectedContentColor = textColorDark,
                        unselectedContentColor = unselectedTabColor
                    )
                }

            }
        },
        containerColor = white
    ) {

        Box {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .align(Alignment.TopCenter)
                .pullRefresh(pullRefreshState)
            ) {

                LoaderComponent(visible = loaderState.isLoaderVisible)

                if (!loaderState.isLoaderVisible) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(dimen_8_dp),
                        modifier = Modifier
                            .padding(horizontal = dimen_16_dp, vertical = dimen_16_dp)
                            .background(
                                white
                            )
                    ) {
                        item {
                            SearchWithFilterViewComponent(
                                placeholderString = stringResource(id = R.string.search_didis),
                                filterSelected = isFilterAppliedState.value.isFilterApplied,
                                onFilterSelected = {
                                    if (surveyeeList.isNotEmpty()) {
                                        isFilterAppliedState.value = isFilterAppliedState.value.copy(
                                            isFilterApplied = !it
                                        )
                                        viewModel.onEvent(SearchEvent.FilterList)
                                    }
                                },
                                onSearchValueChange = { queryTerm ->
                                    viewModel.onEvent(SearchEvent.PerformSearch(queryTerm, isFilterAppliedState.value.isFilterApplied))
                                }
                            )
                        }

                        item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(dimen_8_dp)
                                        .padding(top = 1.dp)
                                        .clip(RoundedCornerShape(14.dp)),
                                    color = progressIndicatorColor,
                                    trackColor = trackColor,
                                    progress = 0.2f
                                )
                                Spacer(modifier = Modifier.width(dimen_8_dp))
                                Text(text = "2/4", color = textColorDark, style = smallTextStyle)
                            }

                        }

                        if (!isFilterAppliedState.value.isFilterApplied) {
                            itemsIndexed(items = surveyeeList) { index, item ->
                                SurveyeeCardComponent(surveyeeState = item) { buttonName, surveyeeId ->
                                    handleButtonClick(buttonName, surveyeeId, navController)
                                }
                            }
                        } else {
                            itemsIndexed(items = surveyeeListWithTolaFilter.keys.toList()) { index, key ->
                                SurveyeeCardWithTolaFilterComponent(
                                    tolaName = key,
                                    surveyeeStateList = surveyeeListWithTolaFilter[key]
                                        ?: emptyList(),
                                    buttonClicked = { buttonName, surveyeeId ->
                                        handleButtonClick(buttonName, surveyeeId, navController)
                                    }
                                )
                                if (index < surveyeeListWithTolaFilter.keys.size - 1) {
                                    Divider(
                                        color = borderGreyLight,
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(
                                            top = 22.dp,
                                            bottom = 1.dp
                                        )
                                    )
                                }
                            }
                        }
                        item { 
                            Spacer(modifier = Modifier.fillMaxWidth().height(dimen_10_dp))
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = loaderState.isLoaderVisible,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                contentColor = blueDark,
            )
        }
    }
}

fun handleButtonClick(buttonName: ButtonName, surveyeeId: Int, navController: NavController) {
    when (buttonName) {
        is ButtonName.START_BUTTON -> {
            navController.navigate("section_screen/$surveyeeId")
        }

        is ButtonName.NEGATIVE_BUTTON -> {

        }

        is ButtonName.SHOW_BUTTON -> {

        }
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SurveyeeListScreenPreview(
    modifier: Modifier = Modifier
) {
    SurveyeeListScreen(navController = rememberNavController(), viewModel = hiltViewModel())
}
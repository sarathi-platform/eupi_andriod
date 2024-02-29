package com.nrlm.baselinesurvey.ui.surveyee_screen.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.THIS_WEEK_TAB
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.ui.common_components.LoaderComponent
import com.nrlm.baselinesurvey.ui.common_components.MoveSurveyeeUpdateBannerComponent
import com.nrlm.baselinesurvey.ui.common_components.SearchWithFilterViewComponent
import com.nrlm.baselinesurvey.ui.common_components.common_events.SearchEvent
import com.nrlm.baselinesurvey.ui.surveyee_screen.viewmodel.SurveyeeScreenViewModel
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.borderGreyLight
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.progressIndicatorColor
import com.nrlm.baselinesurvey.ui.theme.smallTextStyle
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.trackColor
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.states.FilterListState
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nrlm.baselinesurvey.utils.states.SurveyState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ThisWeekSurvyeeListTab(
    paddingValues: PaddingValues,
    loaderState: LoaderState,
    pullRefreshState: PullRefreshState,
    viewModel: SurveyeeScreenViewModel,
    navController: NavController,
    activityId: Int,
    onActionEvent: (surveyeeListScreenActions: SurveyeeListScreenActions) -> Unit,
    modifier: Modifier = Modifier
) {

    val surveyeeList = viewModel.thisWeekSurveyeeListState.value

    val surveyeeListWithTolaFilter = viewModel.thisWeekTolaMapSurveyeeListState.value

    val isFilterAppliedState = remember {
        mutableStateOf(FilterListState())
    }

    val isSelectionEnabled = remember {
        mutableStateOf(false)
    }

    val linearProgress = remember {
        mutableStateOf(0.0f)
    }

    val showMoveSurveyeeBannerVisibilityPair = remember {
        mutableStateOf(Pair<Boolean, SurveyeeEntity?>(false, null))
    }

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .align(Alignment.TopCenter)
                .pullRefresh(pullRefreshState)
                .then(modifier)
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
                                    onActionEvent(
                                        SurveyeeListScreenActions.IsFilterApplied(
                                            isFilterAppliedState.value
                                        )
                                    )
                                    viewModel.onEvent(SearchEvent.FilterList(THIS_WEEK_TAB))
                                }
                            },
                            onSearchValueChange = { queryTerm ->
                                viewModel.onEvent(
                                    SearchEvent.PerformSearch(
                                        queryTerm,
                                        isFilterAppliedState.value.isFilterApplied,
                                        THIS_WEEK_TAB
                                    )
                                )
                            }
                        )
                    }

                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            linearProgress.value =
                                (surveyeeList.filter { it.surveyeeDetails.surveyStatus == SurveyState.COMPLETED.ordinal }.size.toFloat()
                                        / if (surveyeeList.isNotEmpty()) surveyeeList.size.toFloat() else 0.0F)
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(dimen_8_dp)
                                    .padding(top = 1.dp)
                                    .clip(RoundedCornerShape(14.dp)),
                                color = progressIndicatorColor,
                                trackColor = trackColor,
                                progress = linearProgress.value
                            )
                            Spacer(modifier = Modifier.width(dimen_8_dp))
                            Text(
                                text = "${surveyeeList.filter { it.surveyeeDetails.surveyStatus == SurveyState.COMPLETED.ordinal }.size}/${surveyeeList.size}",
                                color = textColorDark,
                                style = smallTextStyle
                            )
                        }
                    }

                    item {
                        MoveSurveyeeUpdateBannerComponent(showBanner = showMoveSurveyeeBannerVisibilityPair.value.first, surveyeeEntity = showMoveSurveyeeBannerVisibilityPair.value.second)
                    }

                    if (!isFilterAppliedState.value.isFilterApplied) {
                        itemsIndexed(items = surveyeeList) { index, item ->
                            SurveyeeCardComponent(
                                surveyeeState = item,
                                showCheckBox = !isSelectionEnabled.value,
                                fromScreen = THIS_WEEK_TAB,
                                checkBoxChecked = { surveyeeEntity, isChecked ->
                                    onActionEvent(SurveyeeListScreenActions.CheckBoxClicked(isChecked, surveyeeEntity))
                                },
                                moveDidiToThisWeek = { surveyeeCardState, moveToThisWeek ->
                                    viewModel.onEvent(
                                        SurveyeeListEvents.MoveDidiToThisWeek(
                                            surveyeeCardState.surveyeeDetails.didiId ?: -1,
                                            moveToThisWeek
                                        )
                                    )
                                    showMoveSurveyeeBannerVisibilityPair.value = Pair(true, surveyeeCardState.surveyeeDetails)
                                },
                                buttonClicked = { buttonName, surveyeeId ->
                                    handleButtonClick(
                                        buttonName,
                                        surveyeeId,
                                        activityId,
                                        navController
                                    )
                                }
                            )
                        }
                    } else {
                        itemsIndexed(items = surveyeeListWithTolaFilter.keys.toList()) { index, key ->
                            SurveyeeCardWithTolaFilterComponent(
                                tolaName = key,
                                surveyeeStateList = surveyeeListWithTolaFilter[key]
                                    ?: emptyList(),
                                showCheckBox = isSelectionEnabled.value,
                                fromScreen = THIS_WEEK_TAB,
                                buttonClicked = { buttonName, surveyeeId ->
                                    handleButtonClick(
                                        buttonName,
                                        surveyeeId,
                                        activityId,
                                        navController
                                    )
                                },
                                checkBoxChecked = { surveyeeEntity, isChecked ->
                                    onActionEvent(SurveyeeListScreenActions.CheckBoxClicked(isChecked, surveyeeEntity))

                                },
                                moveDidiToThisWeek = { surveyeeCardState, moveToThisWeek ->
                                    viewModel.onEvent(
                                        SurveyeeListEvents.MoveDidiToThisWeek(
                                            surveyeeCardState.surveyeeDetails.didiId ?: -1,
                                            moveToThisWeek
                                        )
                                    )
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
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(dimen_10_dp)
                        )
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
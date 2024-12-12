package com.sarathi.missionactivitytask.ui.grant_activity_screen.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.isOnline
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.brownDark
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.download_manager.FileType
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.navigation.navigateToMediaPlayerScreen
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.missionactivitytask.ui.grant_activity_screen.viewmodel.ActivityScreenViewModel
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
import com.sarathi.surveymanager.ui.component.ButtonPositive
import com.sarathi.surveymanager.ui.component.ShowCustomDialog
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ActivityScreen(
    navController: NavController = rememberNavController(),
    viewModel: ActivityScreenViewModel = hiltViewModel(),
    programId: Int,
    missionId: Int,
    missionName: String,
    missionSubTitle: String,
    missionTitleDetail: String,
    isMissionCompleted: Boolean,
    onSettingClick: () -> Unit
) {
    LaunchedEffect(key1 = true) {
        viewModel.setMissionDetail(missionId, isMissionCompleted, programId)
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.onEvent(InitDataEvent.InitDataState)
    }
    val context = LocalContext.current
    val pullRefreshState = rememberPullRefreshState(
        viewModel.loaderState.value.isLoaderVisible,
        {
            if (isOnline(context)) {
                viewModel.refreshData()
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.refresh_failed_please_try_again),
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    ToolBarWithMenuComponent(
        title = missionName,
        subTitle = missionSubTitle,
        subTitleColorId = brownDark,
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        onBackIconClick = { navController.popBackStack() },
        isSearch = false,
        onRetry = {
            viewModel.refreshData()
        },
        isDataNotAvailable = !viewModel.loaderState.value.isLoaderVisible && viewModel.activityList.value.isEmpty(),
        onSearchValueChange = {

        },
        onBottomUI = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(white)
                    .padding(10.dp)
            ) {
                ButtonPositive(
                    buttonTitle = stringResource(R.string.complete_mission),
                    isActive = viewModel.isButtonEnable.value,
                    isArrowRequired = false,
                    onClick = {
                        viewModel.showDialog.value = true
                    }
                )
            }
        },
        onContentUI = { paddingValues, isSearch, onSearchValueChanged ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState)
            )
            {
                PullRefreshIndicator(
                    refreshing = viewModel.loaderState.value.isLoaderVisible,
                    state = pullRefreshState,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .zIndex(1f),
                    contentColor = blueDark,
                )
                Spacer(modifier = Modifier.height(dimen_10_dp))
                if (viewModel.activityList.value.isNotEmpty()) {
                    ActivityRowCard(
                        missionId = missionId,
                        activities = viewModel.activityList.value,
                        programId = programId,
                        navController = navController,
                        missionSubTitle = missionTitleDetail
                    ) { contentValue, contentKey, contentType, contentTitle ->

                        if (viewModel.isFilePathExists(contentValue) || contentType.uppercase(Locale.getDefault()) == FileType.TEXT.name) {
                            navigateToMediaPlayerScreen(
                                navController = navController,
                                contentKey = contentKey,
                                contentType = contentType,
                                contentTitle = contentTitle
                            )
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.file_not_exists),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                if (viewModel.showDialog.value) {
                    ShowCustomDialog(
                        message = stringResource(R.string.not_be_able_to_make_changes_after_completing_this_mission),
                        negativeButtonTitle = stringResource(com.sarathi.surveymanager.R.string.cancel),
                        positiveButtonTitle = stringResource(com.sarathi.surveymanager.R.string.ok),
                        onNegativeButtonClick = {
                            viewModel.showDialog.value = false
                        },
                        onPositiveButtonClick = {
                            viewModel.markMissionCompleteStatus()
                            navController.popBackStack()
                            viewModel.showDialog.value = false
                        }
                    )
                }
            }
        },
        onSettingClick = onSettingClick
    )
}

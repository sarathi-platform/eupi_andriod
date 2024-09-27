package com.sarathi.missionactivitytask.ui.grant_activity_screen.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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

@Composable
fun ActivityScreen(
    navController: NavController = rememberNavController(),
    viewModel: ActivityScreenViewModel = hiltViewModel(),
    missionId: Int,
    missionName: String,
    isMissionCompleted: Boolean,
    onSettingClick: () -> Unit
) {
    LaunchedEffect(key1 = true) {
        viewModel.setMissionDetail(missionId, isMissionCompleted)
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.onEvent(InitDataEvent.InitDataState)
    }
    val context = LocalContext.current
    ToolBarWithMenuComponent(
        title = missionName,
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        onBackIconClick = { navController.popBackStack() },
        isSearch = false,
        onRetry = {},
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
            if (viewModel.activityList.value.isNotEmpty()) {
                ActivityRowCard(
                    missionId = missionId,
                    activities = viewModel.activityList.value,
                    navController = navController,
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

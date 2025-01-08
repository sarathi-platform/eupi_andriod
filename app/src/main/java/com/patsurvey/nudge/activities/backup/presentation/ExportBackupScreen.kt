package com.patsurvey.nudge.activities.backup.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.model.datamodel.CommonSettingScreenConfig
import com.nrlm.baselinesurvey.ui.common_components.common_setting.CommonSettingScreen
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.patsurvey.nudge.activities.backup.viewmodel.ExportBackupScreenViewModel
import com.patsurvey.nudge.activities.settings.domain.SettingTagEnum

@Composable
fun ExportBackupScreen(
    viewModel: ExportBackupScreenViewModel = hiltViewModel(),
    navController: NavController) {
    val context=LocalContext.current
    val settingConfig = CommonSettingScreenConfig(
        isSyncEnable = false,
        mobileNumber = viewModel.getMobileNumber(),
        lastSyncTime = 0L,
        title = viewModel.stringResource(R.string.export_data),
        isScreenHaveLogoutButton = false,
        optionList = viewModel.exportOptionList.value,
        versionText = BLANK_STRING
    )
    CommonSettingScreen(
        settingScreenConfig = settingConfig,
        onBackClick = {navController.popBackStack()},
        onItemClick = { _, settingOptionModel ->
            BaselineLogger.d("ExportImportScreen","${settingOptionModel.tag} :: ${settingOptionModel.title} Click")
            when(settingOptionModel.tag){
                SettingTagEnum.EXPORT_DATABASE.name -> {
                    viewModel.exportLocalDatabase(true) {
                        viewModel.onEvent(LoaderEvent.UpdateLoaderState(false))
                        viewModel.exportDatabaseAnalytic()
                    }
                }
                SettingTagEnum.EXPORT_IMAGES.name -> {
                    viewModel.exportLocalImages()
                    viewModel.exportImageAnalytic()
                }

                SettingTagEnum.EXPORT_EVENT_FILE.name -> {
                    viewModel.compressEventData(viewModel.getString(R.string.export_event_file))
                }
                SettingTagEnum.EXPORT_LOG_FILE.name -> {
                    viewModel.exportOnlyLogFile(context)
                }
                SettingTagEnum.EXPORT_BASELINE_QNA.name -> {
                    viewModel.exportOldAndNewBaselineQnA(context)
                }
            }
        },
        onLogoutClick = {},
        isLoaderVisible = false,
        onSyncDataClick = {}
    )

    if (viewModel.loaderState.value.isLoaderVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = blueDark,
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.Center)
            )
        }
    }
}
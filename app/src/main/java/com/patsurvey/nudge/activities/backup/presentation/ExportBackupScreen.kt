package com.patsurvey.nudge.activities.backup.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.model.datamodel.CommonSettingScreenConfig
import com.nrlm.baselinesurvey.ui.common_components.common_setting.CommonSettingScreen
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
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
        title = stringResource(id = R.string.export_data),
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
                    }
                }
                SettingTagEnum.EXPORT_IMAGES.name -> {
                    viewModel.exportLocalImages()
                }

                SettingTagEnum.EXPORT_EVENT_FILE.name -> {
                    viewModel.compressEventData(context.getString(R.string.export_event_file))
                }
                SettingTagEnum.EXPORT_LOG_FILE.name -> {
                    viewModel.exportOnlyLogFile(context)
                }
            }
        },
        onLogoutClick = {},
        isLoaderVisible = false,
        onSyncDataClick = {}
    )
}
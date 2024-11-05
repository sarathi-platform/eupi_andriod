package com.patsurvey.nudge.activities.backup.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.common_components.common_setting.CommonSettingScreen
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.backup.viewmodel.ExportBackupScreenViewModel
import com.patsurvey.nudge.activities.settings.domain.SettingTagEnum

@Composable
fun ExportBackupScreen(
    viewModel: ExportBackupScreenViewModel = hiltViewModel(),

    navController: NavController) {
        val context=LocalContext.current
        val filePicker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            viewModel.showRestartAppDialog.value=false
            it?.let { uri->
                if(uri != Uri.EMPTY){

                    viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
                    BaselineLogger.d("ExportImportScreen","Selected File :${uri.path}")
                    viewModel.importSelectedDB(uri){
                       viewModel.onEvent(LoaderEvent.UpdateLoaderState(false))
                       viewModel.showRestartAppDialog.value=false
                       viewModel.restartApp(context, MainActivity::class.java)
                   }
                }
            }

        }

    CommonSettingScreen(
        userType = viewModel.loggedInUserType.value,
        title = stringResource(id = R.string.export_data),
        versionText = BLANK_STRING,
        optionList = viewModel.exportOptionList.value,
        onBackClick = {navController.popBackStack()},
        isScreenHaveLogoutButton = false,
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
        onParticularFormClick = {index->},
        isLoaderVisible = false,
        expanded = false,
        activityForm = listOf()
    )
}
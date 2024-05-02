package com.nrlm.baselinesurvey.ui.backup.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.activity.MainActivity
import com.nrlm.baselinesurvey.navigation.AuthScreen
import com.nrlm.baselinesurvey.navigation.navgraph.Graph
import com.nrlm.baselinesurvey.ui.backup.viewmodel.ExportImportViewModel
import com.nrlm.baselinesurvey.ui.common_components.common_setting.CommonSettingScreen
import com.nrlm.baselinesurvey.ui.setting.domain.SettingTagEnum
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.ShowCustomDialog

@Composable
fun ExportImportScreen(
    viewModel: ExportImportViewModel = hiltViewModel(),
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
        title = stringResource(id = R.string.backup_recovery),
        versionText = BLANK_STRING,
        optionList = viewModel.optionList.value,
        onBackClick = {navController.popBackStack()},
        isScreenHaveLogoutButton = false,
        onItemClick = { index, settingOptionModel ->
            BaselineLogger.d("ExportImportScreen","${settingOptionModel.tag} :: ${settingOptionModel.title} Click")
            when(settingOptionModel.tag){
                SettingTagEnum.LOAD_SERVER_DATA.name -> {
                    viewModel.showLoadConfirmationDialog.value=true
                }

                SettingTagEnum.EXPORT_DATABASE.name -> {
                    viewModel.exportLocalDatabase(true) {
                        viewModel.onEvent(LoaderEvent.UpdateLoaderState(false))
                    }
                }

                SettingTagEnum.EXPORT_IMAGES.name -> {
                    viewModel.exportLocalImages()
                }

                SettingTagEnum.EXPORT_BACKUP_FILE.name -> {
                    viewModel.compressEventData(context.getString(R.string.export_event_file))
                }

                SettingTagEnum.IMPORT_DATA.name ->{
                    viewModel.showRestartAppDialog.value=true
                }

                SettingTagEnum.EXPORT_LOG_FILE.name ->{
                    viewModel.exportOnlyLogFile(context)
                }

                SettingTagEnum.REGENERATE_EVENTS.name -> {
                    viewModel.regenerateEvents(context.getString(R.string.share_export_file))
                }
                SettingTagEnum.EXPORT_BASELINE_QNA.name -> {
                    viewModel.exportBaseLineQnA(context)
                }
            }
        },
        onLogoutClick = {}
    )

    if(viewModel.showLoadConfirmationDialog.value){
        ShowCustomDialog(
            title = stringResource(id = R.string.are_you_sure),
            message = stringResource(id = R.string.are_you_sure_you_want_to_load_data_from_server),
            positiveButtonTitle = stringResource(id = R.string.yes_text),
            negativeButtonTitle = stringResource(id = R.string.option_no),
            onNegativeButtonClick = {
                BaselineLogger.d("ExportImportScreen","Load Server Data Dialog No Click")
                viewModel.showLoadConfirmationDialog.value =false
                                    },
            onPositiveButtonClick = {
                BaselineLogger.d("ExportImportScreen","Load Server Data Dialog YES Click")
                viewModel.exportLocalDatabase(isNeedToShare = false){
                    viewModel.clearLocalDatabase{
                        viewModel.onEvent(LoaderEvent.UpdateLoaderState(false))
                        viewModel.showLoadConfirmationDialog.value =false
                        navController.navigate(route = Graph.HOME){
                            launchSingleTop=true
                            popUpTo(AuthScreen.START_SCREEN.route){
                                inclusive=true
                            }
                        }
                    }
                }
            })
    }

        if(viewModel.showRestartAppDialog.value){
        ShowCustomDialog(
            title = stringResource(id = R.string.are_you_sure),
            message = stringResource(id = R.string.import_restart_dialog_message),
            positiveButtonTitle = stringResource(id = R.string.proceed),
            negativeButtonTitle = stringResource(id = R.string.cancel_text),
            onNegativeButtonClick = {
                BaselineLogger.d("ExportImportScreen","Restart Dialog Cancel Click")
                viewModel.showRestartAppDialog.value=false
                                    },
            onPositiveButtonClick = {
                BaselineLogger.d("ExportImportScreen","Restart Dialog Proceed Click")
                filePicker.launch("*/*")
            })
    }
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
@Preview(showBackground = true)
@Composable
fun ExportImportScreenPreview(){
    ExportImportScreen(navController = rememberNavController())
}
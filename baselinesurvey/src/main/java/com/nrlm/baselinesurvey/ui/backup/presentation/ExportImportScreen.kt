package com.nrlm.baselinesurvey.ui.backup.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
                    BaselineLogger.d("ExportImportScreen","Selected File :${uri.path}")
                   viewModel.importSelectedDB(uri){
                       BaselineLogger.d("ExportImportScreen","Restart Dialog Open")
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
                    viewModel.exportLocalDatabase(true) { }
                }

                SettingTagEnum.EXPORT_IMAGES.name -> {
                    viewModel.exportLocalImages()
                }

                SettingTagEnum.EXPORT_BACKUP_FILE.name -> {
                    viewModel.compressEventData("Export Event FIle")
                }

                SettingTagEnum.IMPORT_DATA.name ->{
                    viewModel.showRestartAppDialog.value=true
                }

                SettingTagEnum.EXPORT_LOG_FILE.name ->{
                    viewModel.exportOnlyLogFile(context)
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
                BaselineLogger.d("ExportImportScreen","No Click")
                viewModel.showLoadConfirmationDialog.value =false
                                    },
            onPositiveButtonClick = {
                BaselineLogger.d("ExportImportScreen","YES Click")
                viewModel.exportLocalDatabase(isNeedToShare = false){
                    viewModel.clearLocalDatabase{
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
                BaselineLogger.d("ExportImportScreen","Cancel Click")
                viewModel.showRestartAppDialog.value=false
                                    },
            onPositiveButtonClick = {
                BaselineLogger.d("ExportImportScreen","Proceed Click")
                filePicker.launch("*/*")
            })
    }

}
@Preview(showBackground = true)
@Composable
fun ExportImportScreenPreview(){
    ExportImportScreen(navController = rememberNavController())
}
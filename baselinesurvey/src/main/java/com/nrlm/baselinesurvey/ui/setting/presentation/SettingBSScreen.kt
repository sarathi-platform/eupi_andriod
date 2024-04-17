package com.nrlm.baselinesurvey.ui.setting.presentation


import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.BuildConfig
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.activity.MainActivity
import com.nrlm.baselinesurvey.navigation.AuthScreen
import com.nrlm.baselinesurvey.navigation.home.SettingBSScreens
import com.nrlm.baselinesurvey.navigation.navgraph.Graph
import com.nrlm.baselinesurvey.ui.common_components.common_setting.CommonSettingScreen
import com.nrlm.baselinesurvey.ui.setting.domain.SettingTagEnum
import com.nrlm.baselinesurvey.ui.setting.viewmodel.SettingBSViewModel
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.ShowCustomDialog
import com.nrlm.baselinesurvey.utils.showCustomToast
import com.nudge.core.model.SettingOptionModel
import java.util.Locale

@Composable
fun SettingBSScreen(
    viewModel: SettingBSViewModel = hiltViewModel(),
    navController: NavController
) {
    val list = ArrayList<SettingOptionModel>()
    val context = LocalContext.current

    val loaderState = viewModel.loaderState

    val filePicker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            viewModel.showRestartAppDialog.value=false
            it?.let { uri->
                if(uri != Uri.EMPTY){
                   viewModel.importSelectedDB(uri){
                        viewModel.showRestartAppDialog.value=false
                       viewModel.restartApp(context,MainActivity::class.java)
                   }
                }
            }

        }

    LaunchedEffect(key1 = true){
        list.add(
            SettingOptionModel(
                2,
                context.getString(R.string.profile),
                BLANK_STRING,
                SettingTagEnum.PROFILE.name
            )
        )
        list.add(
            SettingOptionModel(
                3,
                context.getString(R.string.language_text),
                BLANK_STRING,
                SettingTagEnum.LANGUAGE.name
            )
        )
        list.add(
            SettingOptionModel(
                4,
                context.getString(R.string.share_logs),
                BLANK_STRING,
                SettingTagEnum.SHARE_LOGS.name
            )
        )
        list.add(
            SettingOptionModel(
                5,
                context.getString(R.string.export_file),
                BLANK_STRING,
                SettingTagEnum.EXPORT_FILE.name
            )
        )

        list.add(
            SettingOptionModel(
                6,
                context.getString(R.string.load_server_data),
                BLANK_STRING,
                SettingTagEnum.LOAD_SERVER_DATA.name
            )
        )

        list.add(
            SettingOptionModel(
                6,
                context.getString(R.string.import_data),
                BLANK_STRING,
                SettingTagEnum.IMPORT_DATA.name
            )
        )
        viewModel._optionList.value = list
    }

    if(viewModel.showLoadConfirmationDialog.value){
        ShowCustomDialog(
            title = stringResource(id = R.string.are_you_sure),
            message =stringResource(id = R.string.are_you_sure_you_want_to_load_data_from_server),
            positiveButtonTitle = stringResource(id = R.string.yes_text),
            negativeButtonTitle = stringResource(id = R.string.option_no),
            onNegativeButtonClick = {viewModel.showLoadConfirmationDialog.value =false},
            onPositiveButtonClick = {
                viewModel.exportDbAndImages{
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
            message ="After Importing the data from file app needs to be restart.",
            positiveButtonTitle = "Proceed",
            negativeButtonTitle = "Cancel",
            onNegativeButtonClick = {viewModel.showRestartAppDialog.value=false},
            onPositiveButtonClick = {
                filePicker.launch("*/*")
            })
    }

    if (loaderState.value.isLoaderVisible) {
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

    CommonSettingScreen(
        title = stringResource(id = R.string.settings_screen_title),
        versionText = " ${BuildConfig.FLAVOR.uppercase(Locale.getDefault())} v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
        optionList = viewModel.optionList.value ?: emptyList(),
        onBackClick = {
            navController.popBackStack()
        },
        onItemClick = { index, option ->
            when (option.tag) {
                SettingTagEnum.LANGUAGE.name -> {
                    viewModel.saveLanguagePageFrom()
                    navController.navigate(SettingBSScreens.LANGUAGE_SCREEN.route)
                }

                SettingTagEnum.PROFILE.name -> {
                    navController.navigate(SettingBSScreens.PROFILE_SCREEN.route)

                }

                SettingTagEnum.SHARE_LOGS.name -> {
                    viewModel.buildAndShareLogs()
                }

                SettingTagEnum.EXPORT_FILE.name -> {

                    viewModel.exportDbAndImages {
                        viewModel.compressEventData(context.getString(R.string.share_export_file))
                    }
                }

                SettingTagEnum.LOAD_SERVER_DATA.name -> {
                    viewModel.showLoadConfirmationDialog.value=true
                }

                SettingTagEnum.IMPORT_DATA.name ->{
                    viewModel.showRestartAppDialog.value=true
                }
            }
       },
       onLogoutClick = {
           viewModel.performLogout {
               if (it)
                   navController.navigate(Graph.LOGOUT_GRAPH)
               else showCustomToast(context, context.getString(R.string.something_went_wrong))
           }
       }
   )
}
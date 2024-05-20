package com.nrlm.baselinesurvey.ui.setting.presentation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.BuildConfig
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.navigation.home.SettingBSScreens
import com.nrlm.baselinesurvey.navigation.navgraph.Graph
import com.nrlm.baselinesurvey.ui.common_components.common_setting.CommonSettingScreen
import com.nrlm.baselinesurvey.ui.setting.domain.SettingTagEnum
import com.nrlm.baselinesurvey.ui.setting.viewmodel.SettingBSViewModel
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.ConnectionMonitor
import com.nrlm.baselinesurvey.utils.ShowCustomDialog
import com.nrlm.baselinesurvey.utils.showCustomToast
import com.nudge.core.json
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

    LaunchedEffect(key1 = true){
        list.add(
            SettingOptionModel(
                1,
                context.getString(R.string.profile),
                BLANK_STRING,
                SettingTagEnum.PROFILE.name
            )
        )
        list.add(
            SettingOptionModel(
                2,
                context.getString(R.string.language_text),
                BLANK_STRING,
                SettingTagEnum.LANGUAGE.name
            )
        )

        list.add(
            SettingOptionModel(
                3,
                context.getString(R.string.export_backup_file),
                BLANK_STRING,
                SettingTagEnum.EXPORT_BACKUP_FILE.name
            )
        )
        list.add(
            SettingOptionModel(
                5,
                context.getString(R.string.backup_recovery),
                BLANK_STRING,
                SettingTagEnum.BACKUP_RECOVERY.name
            )
        )
        list.add(
            SettingOptionModel(
                6,
                context.getString(R.string.sync_up),
                BLANK_STRING,
                SettingTagEnum.SYNC_DATA_NOW.name
            )
        )
        viewModel._optionList.value = list
    }
    if(viewModel.showLogoutConfirmationDialog.value){
        ShowCustomDialog(title = stringResource(id = R.string.logout),
            message = stringResource(id = R.string.logout_confirmation),
            positiveButtonTitle = stringResource(id = R.string.yes_text),
            negativeButtonTitle  = stringResource(id = R.string.option_no),
            onPositiveButtonClick = {
                BaselineLogger.d("SettingScreen","Logout Button Click")
                viewModel.performLogout {
                    viewModel.showLogoutConfirmationDialog.value=false
                    if (it)
                        navController.navigate(Graph.LOGOUT_GRAPH)
                    else showCustomToast(context, context.getString(R.string.something_went_wrong))
                }
            }, onNegativeButtonClick = {
                viewModel.showLogoutConfirmationDialog.value=false

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

            BaselineLogger.d("SettingScreen","${option.tag} :: ${option.title} :: Click")
            when (option.tag) {
                SettingTagEnum.LANGUAGE.name -> {
                    viewModel.saveLanguagePageFrom()
                    navController.navigate(SettingBSScreens.LANGUAGE_SCREEN.route)
                }

                SettingTagEnum.PROFILE.name -> {
                    navController.navigate(SettingBSScreens.PROFILE_SCREEN.route)

                }

                SettingTagEnum.EXPORT_BACKUP_FILE.name -> {
                    viewModel.compressEventData(context.getString(R.string.share_export_file))
                }

                SettingTagEnum.BACKUP_RECOVERY.name -> {
                    navController.navigate(SettingBSScreens.BACKUP_RECOVERY_SCREEN.route)
                }

                SettingTagEnum.SYNC_DATA_NOW.name ->{
                    navController.navigate(SettingBSScreens.SYNC_EVENT_SCREEN.route)
                }

            }
       },
       onLogoutClick = {
           viewModel.showLogoutConfirmationDialog.value=true
       }
   )
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
}
package com.patsurvey.nudge.activities.settings.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.model.datamodel.CommonSettingScreenConfig
import com.nrlm.baselinesurvey.ui.common_components.common_setting.CommonSettingScreen
import com.nrlm.baselinesurvey.utils.showCustomToast
import com.nudge.core.value
import com.nudge.navigationmanager.graphs.AuthScreen
import com.nudge.navigationmanager.graphs.NudgeNavigationGraph
import com.nudge.navigationmanager.graphs.SettingScreens
import com.patsurvey.nudge.BuildConfig
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.settings.domain.SettingTagEnum
import com.patsurvey.nudge.activities.settings.viewmodel.SettingBSViewModel
import com.patsurvey.nudge.utils.PageFrom
import com.patsurvey.nudge.utils.showCustomDialog
import java.util.Locale

@Composable
fun SettingBSScreen(
    viewModel: SettingBSViewModel = hiltViewModel(),
    navController: NavController
) {

    val context = LocalContext.current
    val loaderState = viewModel.loaderState

    LaunchedEffect(key1 = true) {
        viewModel.initOptions(context)
    }

    if (viewModel.showLogoutDialog.value) {
        if (viewModel.syncWorkerRunning().value())
        {
            showCustomDialog(
                title = context.getString(R.string.logout),
                message = context.getString(R.string.sync_running),
                positiveButtonTitle = stringResource(id = R.string.ok),
                onPositiveButtonClick = {
                    viewModel.showLogoutDialog.value = false
                },
                onNegativeButtonClick = {}
            )
        }else {
            showCustomDialog(
                title = context.getString(com.patsurvey.nudge.R.string.logout),
                message = context.getString(R.string.logout_confirmation),
                positiveButtonTitle = stringResource(id = com.patsurvey.nudge.R.string.logout),
                negativeButtonTitle = stringResource(id = com.patsurvey.nudge.R.string.cancel),
                onNegativeButtonClick = {
                    viewModel.showLogoutDialog.value = false
                },
                onPositiveButtonClick = {
                    viewModel.showLogoutDialog.value = false
                    viewModel.showLoader.value = true
                    viewModel.performLogout(context) {
                        if (it) {
                            if (viewModel.prefRepo.settingOpenFrom() == PageFrom.VILLAGE_PAGE.ordinal) {
                                navController.navigate(AuthScreen.LOGIN.route)
                            } else {
                                if (navController.graph.route == NudgeNavigationGraph.ROOT) {
                                    navController.navigate(AuthScreen.LOGIN.route)
                                } else {
                                    navController.navigate(NudgeNavigationGraph.LOGOUT_GRAPH)
                                }
                            }
                        } else showCustomToast(
                            context,
                            context.getString(R.string.something_went_wrong)
                        )
                    }
                })

        }
    }

    if (!loaderState.value.isLoaderVisible) {
        val settingConfig = CommonSettingScreenConfig(
            isSyncEnable = viewModel.isSyncEnable.value,
            mobileNumber = viewModel.getUserMobileNumber(),
            lastSyncTime = viewModel.lastSyncTime.value,
            title = stringResource(id = R.string.settings_screen_title),
            isScreenHaveLogoutButton = true,
            optionList = viewModel.optionList.value ?: emptyList(),
            versionText = " ${BuildConfig.FLAVOR.uppercase(Locale.getDefault())} v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        )
        CommonSettingScreen(
            settingScreenConfig = settingConfig,
            isLoaderVisible = viewModel.showLoader.value,
            onBackClick = {
                navController.popBackStack()
            },
            onItemClick = { _, option ->
                when (option.tag) {
                    SettingTagEnum.LANGUAGE.name -> {
                        viewModel.saveLanguagePageFrom()
                        navController.navigate(SettingScreens.LANGUAGE_SCREEN.route)
                    }

                    SettingTagEnum.PROFILE.name -> {
                        navController.navigate(SettingScreens.PROFILE_SCREEN.route)

                    }

                    SettingTagEnum.FORMS.name -> {
                        navController.navigate(SettingScreens.SETTING_FORMS_SCREEN.route)
                    }

                    SettingTagEnum.EXPORT_DATA_BACKUP_FILE.name -> {

                        navController.navigate(SettingScreens.EXPORT_BACKUP_FILE_SCREEN.route)
                    }

                    SettingTagEnum.TRAINING_VIDEOS.name -> {
                        navController.navigate(SettingScreens.VIDEO_LIST_SCREEN.route)
                    }

                    SettingTagEnum.BACKUP_RECOVERY.name -> {
                        navController.navigate(SettingScreens.BACKUP_RECOVERY_SCREEN.route)
                    }
                    SettingTagEnum.EXPORT_BACKUP_FILE.name ->{

                        viewModel.compressEventData(context.getString(R.string.share_export_file))
                    }

                    SettingTagEnum.SHARE_LOGS.name -> {
                        viewModel.exportOnlyLogFile(context)
                    }

                }
            },
            onLogoutClick = {
                viewModel.showLogoutDialog.value = true
            },
            onSyncDataClick = {
                if (viewModel.syncEventCount.value > 0)
                    navController.navigate(SettingScreens.SYNC_DATA_NOW_SCREEN.route)
                else showCustomToast(
                    context,
                    context.getString(R.string.data_is_not_available_for_sync_please_perform_some_action)
                )
            }
        )
    }
}

fun isFromEAvailable(fomIndex: Int, pairFromEList: List<Pair<Int, Boolean>>): Boolean {
    val pairData = pairFromEList.filter { it.first == fomIndex && it.second }
    return pairData.isNotEmpty()

}


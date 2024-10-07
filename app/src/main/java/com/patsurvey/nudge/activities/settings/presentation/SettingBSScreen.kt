package com.patsurvey.nudge.activities.settings.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.ui.common_components.common_setting.CommonSettingScreen
import com.nrlm.baselinesurvey.utils.showCustomToast
import com.nudge.core.BLANK_STRING
import com.nudge.core.UPCM_USER
import com.nudge.navigationmanager.graphs.AuthScreen
import com.nudge.navigationmanager.graphs.NudgeNavigationGraph
import com.nudge.navigationmanager.graphs.SettingScreens
import com.patsurvey.nudge.BuildConfig
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.settings.domain.DigitalFormEnum
import com.patsurvey.nudge.activities.settings.domain.SettingTagEnum
import com.patsurvey.nudge.activities.settings.viewmodel.SettingBSViewModel
import com.patsurvey.nudge.utils.PageFrom
import com.patsurvey.nudge.utils.showCustomDialog
import com.patsurvey.nudge.utils.showToast
import com.sarathi.missionactivitytask.navigation.navigateToDisbursmentSummaryScreen
import java.util.Locale

@Composable
fun SettingBSScreen(
    viewModel: SettingBSViewModel = hiltViewModel(),
    navController: NavController
) {

    val context = LocalContext.current
    val expanded = remember {
        mutableStateOf(false)
    }

    val loaderState = viewModel.loaderState

    LaunchedEffect(key1 = true) {
        viewModel.initOptions(context)
    }


    if (viewModel.showLogoutDialog.value) {
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

    if (!loaderState.value.isLoaderVisible) {
        CommonSettingScreen(
            userType = viewModel.userType,
            title = stringResource(id = R.string.settings_screen_title),
            versionText = " ${BuildConfig.FLAVOR.uppercase(Locale.getDefault())} v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
            optionList = viewModel.optionList.value ?: emptyList(),
            isLoaderVisible = viewModel.showLoader.value,
            onBackClick = {
                navController.popBackStack()
            },
            expanded = expanded.value,
            activityForm = viewModel.activityFormGenerateList.value,
            formEName = viewModel.activityFormGenerateNameMap,
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
                        expanded.value = !expanded.value
                    }


                    SettingTagEnum.EXPORT_BACKUP_FILE.name -> {
                        navController.navigate(SettingScreens.EXPORT_BACKUP_FILE_SCREEN.route)

//                        viewModel.compressEventData(context.getString(R.string.share_export_file))
                    }

                    SettingTagEnum.TRAINING_VIDEOS.name -> {
                        navController.navigate(SettingScreens.VIDEO_LIST_SCREEN.route)
                    }

                    SettingTagEnum.BACKUP_RECOVERY.name -> {
                        navController.navigate(SettingScreens.BACKUP_RECOVERY_SCREEN.route)
                    }

                    SettingTagEnum.SHARE_LOGS.name -> {
                        viewModel.exportOnlyLogFile(context)
                    }
                }
            },
            onLogoutClick = {
                viewModel.showLogoutDialog.value = true
            },
            onParticularFormClick = { formIndex ->
                if (viewModel.userType == UPCM_USER) {
                    if (isFromEAvailable(
                            fomIndex = formIndex,
                            pairFromEList = viewModel.formEAvailableList.value
                        ) && viewModel.activityFormGenerateList.value.isNotEmpty()
                    ) {
                        navigateToDisbursmentSummaryScreen(
                            navController = navController,
                            missionId = viewModel.activityFormGenerateList.value[formIndex].missionId,
                            activityId = viewModel.activityFormGenerateList.value[formIndex].activityId,
                            taskIdList = BLANK_STRING,
                            isFromSettingScreen = true
                        )
                    } else {
                        showToast(
                            context,
                            context.getString(R.string.no_data_form_e_not_generated_text)
                        )
                    }

                } else {
                    when (formIndex) {
                        DigitalFormEnum.DIGITAL_FORM_A.ordinal -> {
                            viewModel.showLoaderForTime(500)
                            if (viewModel.formAAvailable.value)
                                navController.navigate(SettingScreens.FORM_A_SCREEN.route)
                            else
                                showToast(
                                    context,
                                    context.getString(com.patsurvey.nudge.R.string.no_data_form_a_not_generated_text)
                                )
                        }

                        DigitalFormEnum.DIGITAL_FORM_B.ordinal -> {
                            viewModel.showLoaderForTime(500)
                            if (viewModel.formBAvailable.value)
                                navController.navigate(SettingScreens.FORM_B_SCREEN.route)
                            else
                                showToast(
                                    context,
                                    context.getString(com.patsurvey.nudge.R.string.no_data_form_b_not_generated_text)
                                )
                        }

                        DigitalFormEnum.DIGITAL_FORM_C.ordinal -> {
                            viewModel.showLoaderForTime(500)
                            if (viewModel.formCAvailable.value)
                                navController.navigate(SettingScreens.FORM_C_SCREEN.route)
                            else
                                showToast(
                                    context,
                                    context.getString(com.patsurvey.nudge.R.string.no_data_form_c_not_generated_text)
                                )
                        }
                    }
                }

            }
        )
    }
}

private fun isFromEAvailable(fomIndex: Int, pairFromEList: List<Pair<Int, Boolean>>): Boolean {
    val pairData = pairFromEList.filter { it.first == fomIndex && it.second }
    return pairData.isNotEmpty()

}


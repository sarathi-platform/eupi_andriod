package com.patsurvey.nudge.activities.forms.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.model.datamodel.CommonSettingScreenConfig
import com.nrlm.baselinesurvey.ui.common_components.common_setting.CommonSettingScreen
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nudge.core.UPCM_USER
import com.nudge.navigationmanager.graphs.SettingScreens
import com.patsurvey.nudge.activities.forms.viewmodel.SettingFormsViewModel
import com.patsurvey.nudge.activities.settings.domain.DigitalFormEnum
import com.patsurvey.nudge.activities.settings.presentation.isFromEAvailable
import com.patsurvey.nudge.utils.showToast
import com.sarathi.missionactivitytask.navigation.navigateToDisbursmentSummaryScreen

@Composable
fun SettingFormsScreen(
    viewModel: SettingFormsViewModel = hiltViewModel(),
    navController: NavController
) {

    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.initOptions()
    }
    val settingConfig = CommonSettingScreenConfig(
        isSyncEnable = false,
        mobileNumber = viewModel.getUserMobileNumber(),
        lastSyncTime = 0L,
        title = stringResource(id = R.string.forms),
        isScreenHaveLogoutButton = false,
        optionList = viewModel.optionList.value,
        versionText = BLANK_STRING,
        isItemCard = true,
        errorMessage = stringResource(id = R.string.no_form_available_yet_text)
    )
    CommonSettingScreen(
        settingScreenConfig = settingConfig,
        onBackClick = { navController.popBackStack() },
        onItemClick = { formIndex, settingOptionModel ->
            BaselineLogger.d(
                "SettingFormsScreen",
                "${settingOptionModel.tag} :: ${settingOptionModel.title} Click"
            )
            if (viewModel.userType == UPCM_USER) {
                if (isFromEAvailable(
                        fomIndex = formIndex,
                        pairFromEList = viewModel.formEAvailableList.value
                    ) && viewModel.activityGenerateFormsList.value.isNotEmpty()
                ) {
                    navigateToDisbursmentSummaryScreen(
                        navController = navController,
                        missionId = viewModel.activityGenerateFormsList.value[formIndex].missionId,
                        activityId = viewModel.activityGenerateFormsList.value[formIndex].activityId,
                        taskIdList = com.nudge.core.BLANK_STRING,
                        isFromSettingScreen = true
                    )
                } else {
                    showToast(
                        context,
                        context.getString(com.patsurvey.nudge.R.string.no_data_form_e_not_generated_text)
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

        },
        onLogoutClick = {},
        isLoaderVisible = false,
        onSyncDataClick = {}
    )
}
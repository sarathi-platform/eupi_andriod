package com.patsurvey.nudge.activities.settings.presentation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.BuildConfig
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.common_components.common_setting.CommonSettingScreen
import com.patsurvey.nudge.activities.settings.domain.SettingTagEnum
import com.patsurvey.nudge.activities.settings.viewmodel.SettingBSViewModel
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.utils.showCustomToast
import com.nudge.core.ui.navigation.CoreGraph
import com.nudge.core.ui.navigation.SettingScreens
import com.patsurvey.nudge.activities.settings.domain.DigitalFormEnum
import com.patsurvey.nudge.utils.UPCM_USER
import com.patsurvey.nudge.utils.showToast
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

    LaunchedEffect(key1 = true){
     viewModel.initOptions(context)
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
        expanded = expanded.value,
        onItemClick = { index, option ->
            when (option.tag) {
                SettingTagEnum.LANGUAGE.name -> {
                    viewModel.saveLanguagePageFrom()
                    navController.navigate(SettingScreens.LANGUAGE_SCREEN.route)
                }

                SettingTagEnum.PROFILE.name -> {
                    navController.navigate(SettingScreens.PROFILE_SCREEN.route)

                }
                SettingTagEnum.FORMS.name ->{
                    expanded.value= !expanded.value
                }

                SettingTagEnum.SHARE_LOGS.name -> {
                    if(viewModel.userType == UPCM_USER)
                        viewModel.buildAndShareLogs()
                    else viewModel.buildAndShareLogsForSelection()
                }

                SettingTagEnum.EXPORT_FILE.name -> {
                    viewModel.compressEventData(context.getString(R.string.share_export_file))
                }
                SettingTagEnum.TRAINING_VIDEOS.name ->{
                    navController.navigate(SettingScreens.VIDEO_LIST_SCREEN.route)
                }
            }
       },
       onLogoutClick = {
           viewModel.performLogout {
               if (it)
                   navController.navigate(CoreGraph.LOGOUT_GRAPH)
               else showCustomToast(context, context.getString(R.string.something_went_wrong))
           }
       },
        onParticularFormClick = { formIndex->
            when(formIndex){
                DigitalFormEnum.DIGITAL_FORM_A.ordinal->{
                    viewModel.showLoaderForTime(500)
                    if (viewModel.formAAvailable.value)
                        navController.navigate(SettingScreens.FORM_A_SCREEN.route)
                    else
                        showToast(
                            context,
                            context.getString(com.patsurvey.nudge.R.string.no_data_form_a_not_generated_text)
                        )
                }
                DigitalFormEnum.DIGITAL_FORM_B.ordinal->{
                    viewModel.showLoaderForTime(500)
                    if (viewModel.formBAvailable.value)
                        navController.navigate(SettingScreens.FORM_B_SCREEN.route)
                    else
                        showToast(
                            context,
                            context.getString(com.patsurvey.nudge.R.string.no_data_form_b_not_generated_text)
                        )
                }
                DigitalFormEnum.DIGITAL_FORM_C.ordinal->{
                    viewModel.showLoaderForTime(500)
                    if (viewModel.formBAvailable.value)
                        navController.navigate(SettingScreens.FORM_C_SCREEN.route)
                    else
                        showToast(
                            context,
                            context.getString(com.patsurvey.nudge.R.string.no_data_form_c_not_generated_text)
                        )
                }
            }
        }
   )
}


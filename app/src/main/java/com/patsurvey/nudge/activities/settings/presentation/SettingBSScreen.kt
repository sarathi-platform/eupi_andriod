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
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.patsurvey.nudge.activities.settings.domain.SettingTagEnum
import com.patsurvey.nudge.activities.settings.viewmodel.SettingBSViewModel
import com.nrlm.baselinesurvey.utils.showCustomToast
import com.nudge.navigationmanager.graphs.AuthScreen
import com.nudge.navigationmanager.graphs.NudgeNavigationGraph
import com.nudge.navigationmanager.graphs.SettingScreens
import com.patsurvey.nudge.BuildConfig
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.settings.domain.DigitalFormEnum
import com.patsurvey.nudge.utils.showCustomDialog
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
                viewModel.showLoader.value=true
                viewModel.performLogout(context) {
                    if (it)
                        navController.navigate(NudgeNavigationGraph.LOGOUT_GRAPH)
                    else showCustomToast(context, context.getString(R.string.something_went_wrong))
                }
            })


    }

    if(viewModel.showLoadConfimationDialog.value){
        showCustomDialog(
            title = stringResource(id = R.string.are_you_sure),
            message =stringResource(id = R.string.are_you_sure_you_want_to_load_data_from_server),
            positiveButtonTitle = stringResource(id = R.string.yes_text),
            negativeButtonTitle = stringResource(id = R.string.option_no),
            onNegativeButtonClick = {viewModel.showLoadConfimationDialog.value =false},
            onPositiveButtonClick = {
                viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
                viewModel.exportDbAndImages{
                    viewModel.clearSelectionLocalDatabase{
                        viewModel.onEvent(LoaderEvent.UpdateLoaderState(false))
                        when(navController.graph.route){
                            NudgeNavigationGraph.ROOT-> navController.navigate(AuthScreen.VILLAGE_SELECTION_SCREEN.route)
                            NudgeNavigationGraph.HOME-> navController.navigate(AuthScreen.VILLAGE_SELECTION_SCREEN.route)
                          else -> navController.navigate(NudgeNavigationGraph.LOGOUT_GRAPH)
                        }
                    }
                }
            })
    }
  if(!loaderState.value.isLoaderVisible) {
      CommonSettingScreen(
          title = stringResource(id = R.string.settings_screen_title),
          versionText = " ${BuildConfig.FLAVOR.uppercase(Locale.getDefault())} v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
          optionList = viewModel.optionList.value ?: emptyList(),
          isLoaderVisible = viewModel.showLoader.value,
          onBackClick = {
              navController.popBackStack()
          },
          expanded = expanded.value,
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
                      viewModel.compressEventData(context.getString(R.string.share_export_file))
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

                  SettingTagEnum.EXPORT_FILE.name -> {
                      viewModel.compressEventData(context.getString(R.string.share_export_file))
                  }

                  SettingTagEnum.LOAD_SERVER_DATA.name -> {
                      if ((context as MainActivity).isOnline.value) {
                          viewModel.showLoadConfimationDialog.value = true
                      }else{
                          showToast(
                              context,
                              context.getString(R.string.logout_no_internet_error_message)
                          )
                      }
                  }
              }
          },
          onLogoutClick = {
              viewModel.showLogoutDialog.value = true
          },
          onParticularFormClick = { formIndex ->
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
      )
  }
}


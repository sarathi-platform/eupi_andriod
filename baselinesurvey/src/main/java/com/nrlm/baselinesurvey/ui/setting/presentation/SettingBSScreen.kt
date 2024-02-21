package com.nrlm.baselinesurvey.ui.setting.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.BuildConfig
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.common_components.common_setting.CommonSettingScreen
import com.nrlm.baselinesurvey.ui.setting.viewmodel.SettingBSViewModel
import com.nudge.core.model.SettingOptionModel
import java.util.Locale

@Composable
fun SettingBSScreen(
    viewModel:SettingBSViewModel?=null,
    navController: NavController
) {
    val list = ArrayList<SettingOptionModel>()
    val context = LocalContext.current

    LaunchedEffect(key1 = true){
        list.add(SettingOptionModel(1, context.getString(R.string.profile), BLANK_STRING))
        list.add(SettingOptionModel(2, context.getString(R.string.training_videos), BLANK_STRING))
        list.add(SettingOptionModel(3, context.getString(R.string.language_text), BLANK_STRING))
        list.add(SettingOptionModel(4, context.getString(R.string.share_logs), BLANK_STRING))
        viewModel?._optionList?.value=list
    }

   CommonSettingScreen(title = stringResource(id = R.string.settings_screen_title),
       versionText = " ${BuildConfig.FLAVOR.uppercase(Locale.getDefault())} v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
       optionList = viewModel?.optionList?.value ?: emptyList(),
       onBackClick = {

       },
       onItemClick = { index,option->

       }
   )
}
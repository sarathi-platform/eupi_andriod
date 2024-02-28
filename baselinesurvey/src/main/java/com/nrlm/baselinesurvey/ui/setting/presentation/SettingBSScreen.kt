package com.nrlm.baselinesurvey.ui.setting.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.BuildConfig
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.common_components.common_setting.CommonSettingScreen
import com.nrlm.baselinesurvey.ui.setting.domain.SettingTagEnum
import com.nrlm.baselinesurvey.ui.setting.viewmodel.SettingBSViewModel
import com.nudge.core.model.SettingOptionModel
import com.nrlm.baselinesurvey.navigation.AuthScreen
import com.nrlm.baselinesurvey.navigation.home.HomeScreens
import com.nrlm.baselinesurvey.navigation.home.SettingBSScreens
import com.nrlm.baselinesurvey.navigation.navgraph.Graph
import com.nrlm.baselinesurvey.utils.showCustomToast
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SettingBSScreen(
    viewModel:SettingBSViewModel?=null,
    navController: NavController
) {
    val list = ArrayList<SettingOptionModel>()
    val context = LocalContext.current


    LaunchedEffect(key1 = true){
        val lastSyncTimeInMS = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US)
        val lastSyncTime = if (lastSyncTimeInMS != 0L) dateFormat.format(lastSyncTimeInMS) else ""
//        list.add(
//            SettingOptionModel(
//                1,
//                context.getString(R.string.sync_up),
//                context.getString(R.string.last_syncup_text)
//                    .replace("{LAST_SYNC_TIME}", lastSyncTime.toString())
//            ,SettingTagEnum.SYNC_NOW.name)
//        )
        list.add(SettingOptionModel(2, context.getString(R.string.profile), BLANK_STRING,SettingTagEnum.PROFILE.name))
        list.add(SettingOptionModel(3, context.getString(R.string.language_text), BLANK_STRING,SettingTagEnum.LANGUAGE.name))
        list.add(SettingOptionModel(4, context.getString(R.string.share_logs), BLANK_STRING,SettingTagEnum.SHARE_LOGS.name))
//        list.add(SettingOptionModel(5, context.getString(R.string.export_file), BLANK_STRING,SettingTagEnum.EXPORT_FILE.name))
        viewModel?._optionList?.value=list
    }

   CommonSettingScreen(title = stringResource(id = R.string.settings_screen_title),
       versionText = " ${BuildConfig.FLAVOR.uppercase(Locale.getDefault())} v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
       optionList = viewModel?.optionList?.value ?: emptyList(),
       onBackClick = {
           navController.popBackStack()
       },
       onItemClick = { index, option->
            when(option.tag){
                SettingTagEnum.LANGUAGE.name -> {
                    viewModel?.saveLanguagePageFrom()
                    navController.navigate(SettingBSScreens.LANGUAGE_SCREEN.route)
                }
                SettingTagEnum.PROFILE.name ->{
                navController.navigate(SettingBSScreens.PROFILE_SCREEN.route)

                }
                SettingTagEnum.SHARE_LOGS.name,
//                SettingTagEnum.EXPORT_FILE.name //TODO Temp for training purpose only, remove this when backup file code is added.
                -> {
                    viewModel?.buildAndShareLogs()
                }
            }
       },
       onLogoutClick = {
            viewModel?.performLogout {
                if(it)
                navController.navigate(Graph.LOGOUT_GRAPH)
                else showCustomToast(context,context.getString(R.string.something_went_wrong))
            }
       }
   )
}
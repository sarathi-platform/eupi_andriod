package com.nrlm.baselinesurvey.ui.setting.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nrlm.baselinesurvey.BaselineApplication
import com.nrlm.baselinesurvey.LANGUAGE_OPEN_FROM_SETTING
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.entity.MissionEntity
import com.nrlm.baselinesurvey.ui.setting.domain.use_case.SettingBSUserCase
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.LogWriter
import com.nudge.core.model.SettingOptionModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingBSViewModel @Inject constructor(
    private val settingBSUserCase: SettingBSUserCase,
    val prefRepo: PrefRepo
):BaseViewModel(){
     val _optionList = mutableStateOf<List<SettingOptionModel>>(emptyList())
     val optionList: State<List<SettingOptionModel>> get() = _optionList


    fun performLogout(onLogout:(Boolean)->Unit){
        CoroutineScope(Dispatchers.IO).launch {
            val settingUseCaseResponse=settingBSUserCase.logoutUseCase.invoke()
            withContext(Dispatchers.Main){
                onLogout(settingUseCaseResponse)
            }

        }
    }

    fun saveLanguagePageFrom(){
       settingBSUserCase.saveLanguageScreenOpenFromUseCase.invoke()
    }
    fun buildAndShareLogs() {
        BaselineLogger.d("SettingBSViewModel", "buildAndShareLogs---------------")
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            LogWriter.buildSupportLogAndShare(prefRepo)
        }
    }

    override fun <T> onEvent(event: T) {

    }
}
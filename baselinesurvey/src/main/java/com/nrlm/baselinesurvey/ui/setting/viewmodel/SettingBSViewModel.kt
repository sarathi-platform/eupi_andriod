package com.nrlm.baselinesurvey.ui.setting.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.database.entity.MissionEntity
import com.nrlm.baselinesurvey.ui.setting.domain.use_case.SettingBSUserCase
import com.nudge.core.model.SettingOptionModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingBSViewModel @Inject constructor(
    private val settingBSUserCase: SettingBSUserCase
):BaseViewModel(){
     val _optionList = mutableStateOf<List<SettingOptionModel>>(emptyList())
     val optionList: State<List<SettingOptionModel>> get() = _optionList


    override fun <T> onEvent(event: T) {

    }
}
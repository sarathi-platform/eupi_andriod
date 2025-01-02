package com.nrlm.baselinesurvey.model.datamodel

import com.nudge.core.BLANK_STRING
import com.nudge.core.model.SettingOptionModel

data class CommonSettingScreenConfig(
    val title: String,
    val versionText: String,
    val optionList: List<SettingOptionModel>,
    val isSyncEnable: Boolean = false,
    val isScreenHaveLogoutButton: Boolean = true,
    val lastSyncTime: Long? = 0L,
    val mobileNumber: String,
    val isItemCard: Boolean = false,
    val errorMessage: String = BLANK_STRING
)

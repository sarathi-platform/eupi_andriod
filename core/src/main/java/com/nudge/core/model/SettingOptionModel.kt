package com.nudge.core.model


data class SettingOptionModel(
    val id: Int,
    val title: String,
    val subTitle: String,
    val tag: String,
    val isShareOption: Boolean = false,
    val icon: Int? = null
)

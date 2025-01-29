package com.nudge.core.model

import com.nudge.core.R


data class SettingOptionModel(
    val id: Int,
    val title: String,
    val subTitle: String,
    val tag: String,
    val leadingIcon: Int? = null,
    val trailingIcon: Int = R.drawable.ic_arrow_forward_ios_24
)

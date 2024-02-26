package com.nudge.core.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SettingOptionModel(
    @SerializedName("id")
    @Expose
    val id:Int,

    @SerializedName("title")
    @Expose
    val title:String,

    @SerializedName("subTitle")
    @Expose
    val subTitle:String
)

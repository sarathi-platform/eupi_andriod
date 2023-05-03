package com.patsurvey.nudge.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ConfigResponseModel(
    @SerializedName("languageList")
    @Expose
    val languageList:List<String>
)

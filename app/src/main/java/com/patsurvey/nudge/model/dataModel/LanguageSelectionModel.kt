package com.patsurvey.nudge.model.dataModel

import com.google.gson.annotations.SerializedName

data class LanguageSelectionModel(
        @SerializedName("language")
        val language : String,
        @SerializedName("code")
        val code : String?,
        @SerializedName("isSelected")
        var isSelected : Boolean
)

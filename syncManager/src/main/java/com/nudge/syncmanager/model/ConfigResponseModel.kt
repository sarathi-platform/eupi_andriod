package com.nudge.syncmanager.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.database.entities.language.LanguageEntity

data class ConfigResponseModel(
    @SerializedName("languageList")
    @Expose
    val languageList: List<LanguageEntity>,

    @SerializedName("questionImageUrlList")
    @Expose
    val imageProfileLink: List<String>,

    )
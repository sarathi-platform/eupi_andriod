package com.patsurvey.nudge.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.database.LanguageEntity

data class ConfigResponseModel(
    @SerializedName("languageList")
    @Expose
    val languageList:List<LanguageEntity>,

    @SerializedName("questionImageUrlList")
    @Expose
    val image_profile_link:List<String>
)

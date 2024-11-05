package com.nudge.core.model.response.language

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.database.entities.language.LanguageEntity

data class LanguageConfigModel(
    @SerializedName("languageList")
    @Expose
    val languageList: List<LanguageEntity>,

    @SerializedName("questionImageUrlList")
    @Expose
    val image_profile_link: List<String>,

    @SerializedName("bpcSuveryPercentage")
    @Expose
    val bpcSurveyPercentage: List<BpcScorePercentageResponse>
)

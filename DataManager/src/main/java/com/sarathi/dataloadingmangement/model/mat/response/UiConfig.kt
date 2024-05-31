package com.sarathi.dataloadingmangement.model.mat.response


import com.google.gson.annotations.SerializedName

data class UiConfig(
//    @SerializedName("form")
//    val form:Boolean,
    @SerializedName("language_attributes")
    val languageAttributes: List<LanguageAttributes>,
)
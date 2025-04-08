package com.sarathi.dataloadingmangement.model.mat.response


import com.google.gson.annotations.SerializedName

data class Language(
    @SerializedName("description")
    val description: String,
    @SerializedName("language")
    var language: String
)

data class MissionLivelihoodMission(
    @SerializedName("livelihood")
    val livelihood: String?,
    @SerializedName("languageCode")
    val languageCode: String?
)
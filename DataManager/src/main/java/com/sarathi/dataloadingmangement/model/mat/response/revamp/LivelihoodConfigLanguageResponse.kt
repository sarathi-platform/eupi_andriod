package com.sarathi.dataloadingmangement.model.mat.response.revamp

import com.google.gson.annotations.SerializedName

data class LivelihoodConfigLanguageResponse(
    @SerializedName("language_code")
    val languageCode: String?,
    @SerializedName("livelihood_type")
    val livelihoodType: String?
)
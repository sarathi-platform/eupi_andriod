package com.sarathi.dataloadingmangement.model.mat.response.revamp

import com.google.gson.annotations.SerializedName

data class LivelihoodConfigLanguageResponse(
    @SerializedName("languageCode")
    val languageCode: String?,
    @SerializedName("livelihoodType")
    val livelihoodType: String?,
    @SerializedName("livelihoodOrder")
    val livelihoodOrder: Int?
)
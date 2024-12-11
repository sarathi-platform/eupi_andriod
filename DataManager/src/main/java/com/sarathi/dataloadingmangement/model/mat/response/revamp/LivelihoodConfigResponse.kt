package com.sarathi.dataloadingmangement.model.mat.response.revamp

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LivelihoodConfigResponse(
    @SerializedName("livelihood_order")
    val livelihoodOrder: Int?,
    @SerializedName("languages")
    @Expose
    val languages: List<LivelihoodConfigLanguageResponse?>?
)


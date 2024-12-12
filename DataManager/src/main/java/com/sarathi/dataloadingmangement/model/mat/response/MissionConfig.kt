package com.sarathi.dataloadingmangement.model.mat.response

import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.model.mat.response.revamp.LivelihoodConfigLanguageResponse

data class MissionConfig(
    @SerializedName("content")
    val contents: List<ContentResponse>,
    @SerializedName("missionType")
    val missionType: String?,
    @SerializedName("livelihoodConfig")
    val livelihoodConfig: List<LivelihoodConfigLanguageResponse>?
)

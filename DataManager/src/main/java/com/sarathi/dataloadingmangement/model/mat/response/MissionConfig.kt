package com.sarathi.dataloadingmangement.model.mat.response

import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.model.mat.response.revamp.LivelihoodConfigResponse

data class MissionConfig(
    @SerializedName("content")
    val contents: List<ContentResponse>,
    @SerializedName("mission_type")
    val missionType: String?,
    @SerializedName("livelihood_config")
    val livelihoodConfig: List<LivelihoodConfigResponse>?
)

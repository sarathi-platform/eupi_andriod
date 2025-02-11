package com.sarathi.dataloadingmangement.model.mat.response.revamp

import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.model.mat.response.MissionLivelihoodMission

data class LivelihoodConfigLanguageResponse(
    @SerializedName("livelihoodType")
    val livelihoodType: String?,
    @SerializedName("livelihoodOrder")
    val livelihoodOrder: Int?,
    @SerializedName("program_livelihood_reference_id")
    val program_livelihood_reference_id: List<Int>? = emptyList(),
    @SerializedName("languages")
    val languages: List<MissionLivelihoodMission>?,
)
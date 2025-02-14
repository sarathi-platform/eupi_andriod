package com.sarathi.dataloadingmangement.domain.use_case.livelihood

import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING

data class LivelihoodOptionResponse(

    @SerializedName("didiId")
    val didiId: Int,
    @SerializedName("livelihoodDTO")
    val livelihoodDTO: ArrayList<LivelihoodDTO>
)

data class LivelihoodDTO(
    @SerializedName("programLivelihoodId")
    val programLivelihoodId: Int,
    @SerializedName("order")
    val order: Int,
    @SerializedName("type")
    val type: String? = BLANK_STRING
)

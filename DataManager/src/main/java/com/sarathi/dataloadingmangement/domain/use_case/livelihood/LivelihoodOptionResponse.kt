package com.sarathi.dataloadingmangement.domain.use_case.livelihood

import com.google.gson.annotations.SerializedName

data class LivelihoodOptionResponse(

    @SerializedName("didiId")
    val didiId: Int,
    @SerializedName("livelihoodDTO")
    val livelihoodDTO: ArrayList<LivelihoodDTO>
)

data class LivelihoodDTO(
    @SerializedName("livelihoodId")
    val livelihoodId: Int,
    @SerializedName("order")
    val order: Int,
)

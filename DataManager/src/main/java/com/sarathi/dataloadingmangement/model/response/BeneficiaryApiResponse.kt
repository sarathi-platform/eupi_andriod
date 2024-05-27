package com.sarathi.dataloadingmangement.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BeneficiaryApiResponse(
    @SerializedName("didiList")
    @Expose
    val didiList: List<DidiDetailList>
)
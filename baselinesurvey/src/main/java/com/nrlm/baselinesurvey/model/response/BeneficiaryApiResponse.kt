package com.nrlm.baselinesurvey.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.model.datamodel.DidiDetailList

data class BeneficiaryApiResponse(
    @SerializedName("didiList")
    @Expose
    val didiList:List<DidiDetailList>
)

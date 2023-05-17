package com.patsurvey.nudge.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.database.DidiEntity

data class BeneficiaryApiResponse(
    @SerializedName("beneficiaryList")
    @Expose
    val didiList:List<DidiDetailList>
)

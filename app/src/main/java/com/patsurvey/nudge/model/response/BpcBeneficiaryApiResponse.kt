package com.patsurvey.nudge.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BpcBeneficiaryApiResponse(
    @SerializedName("not_selected")
    @Expose
    val not_selected: List<DidiDetailList>,
    @SerializedName("selected")
    @Expose
    val selected: List<DidiDetailList>
)

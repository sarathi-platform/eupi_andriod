package com.patsurvey.nudge.model.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BpcUpdateSelectedDidiRequest(
    @SerializedName("newBeneficiaryIdSelected")
    @Expose
    val newBeneficiaryIdSelected: List<Int>,
    @SerializedName("oldBeneficiaryIdSelected")
    @Expose
    val oldBeneficiaryIdSelected: List<Int>,
    @SerializedName("villageId")
    @Expose
    val villageId: Int
)
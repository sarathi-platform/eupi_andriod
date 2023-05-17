package com.patsurvey.nudge.database.converters

import com.google.gson.annotations.SerializedName

data class BeneficiaryProcessStatusModel(
    @SerializedName("name") var name: String,
    @SerializedName("status") var status: String
)

package com.patsurvey.nudge.model.response

import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.database.converters.BeneficiaryProcessStatusModel

data class DidiDetailList(
    @SerializedName("id") var id: Int,
    @SerializedName("castId") var castId: Int,
    @SerializedName("cohortId") var cohortId: Int,
    @SerializedName("name") var name: String,
    @SerializedName("guardianName") var guardianName: String,
    @SerializedName("address") var address: String,
    @SerializedName("relationship") var relationship: String,
    @SerializedName("beneficiaryProcessStatus") var beneficiaryProcessStatus: List<BeneficiaryProcessStatusModel>,

)

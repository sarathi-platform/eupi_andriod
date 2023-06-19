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
    @SerializedName("createdDate") var createdDate: Long,
    @SerializedName("modifiedDate") var modifiedDate: Long,
    @SerializedName("localCreatedDate") var localCreatedDate: Long,
    @SerializedName("localModifiedDate") var localModifiedDate: Long,
    @SerializedName("score") var score: Double,
    @SerializedName("comment") var comment: String,
    @SerializedName("beneficiaryProcessStatus") var beneficiaryProcessStatus: List<BeneficiaryProcessStatusModel>,

)

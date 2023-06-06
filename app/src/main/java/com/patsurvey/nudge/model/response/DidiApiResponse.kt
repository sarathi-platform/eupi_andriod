package com.patsurvey.nudge.model.response

import com.google.gson.annotations.SerializedName

data class DidiApiResponse(
    @SerializedName("createdDate") var createdDate: Long,
    @SerializedName("modifiedDate") var modifiedDate: Long,
    @SerializedName("createdBy") var createdBy: Int,
    @SerializedName("modifiedBy") var modifiedBy: Int,
    @SerializedName("id") var id: Int,
    @SerializedName("transactionId") var transactionId: String,
    @SerializedName("status") var status: Int,
    @SerializedName("villageId") var villageId: Int,
    @SerializedName("address") var address: String,
    @SerializedName("guardianName") var guardianName: String,
    @SerializedName("name") var name: String,
    @SerializedName("relationship") var relationship : String,
    @SerializedName("castName") var castName : String,
    @SerializedName("castId") var castId : Int,
    @SerializedName("cohortId") var cohortId : Int
)

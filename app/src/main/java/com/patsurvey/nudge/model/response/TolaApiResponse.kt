package com.patsurvey.nudge.model.response

import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.model.dataModel.StepsListModal

data class TolaApiResponse(
    @SerializedName("createdDate") var createdDate: Long,
    @SerializedName("modifiedDate") var modifiedDate: Long,
    @SerializedName("createdBy") var createdBy: Int,
    @SerializedName("modifiedBy") var modifiedBy: Int,
    @SerializedName("id") var id: Int,
    @SerializedName("transactionId") var transactionId: String,
    @SerializedName("status") var status: Int,
    @SerializedName("name") var name: String,
    @SerializedName("type") var type: String,
    @SerializedName("villageId") var villageId: Int,
    @SerializedName("latitude") var latitude: Double,
    @SerializedName("longitude") var longitude: Double,
)

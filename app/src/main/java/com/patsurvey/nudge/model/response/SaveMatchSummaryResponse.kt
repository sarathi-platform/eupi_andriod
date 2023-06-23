package com.patsurvey.nudge.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SaveMatchSummaryResponse(
    @SerializedName("createdDate")
    @Expose
    val createdDate: Long,
    @SerializedName("modifiedDate")
    @Expose
    val modifiedDate: Long,
    @SerializedName("createdBy")
    @Expose
    val createdBy: Int,
    @SerializedName("modifiedBy")
    @Expose
    val modifiedBy: Int,
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("villageId")
    @Expose
    val villageId: Int,
    @SerializedName("programId")
    @Expose
    val programId: Int,
    @SerializedName("scorePercentage")
    @Expose
    val scorePercentage: Int,
    @SerializedName("reverificationScore")
    @Expose
    val reverificationScore: Double?,
    @SerializedName("status")
    @Expose
    val status: String?,
)
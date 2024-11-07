package com.nudge.core.datamodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FederationDetailModel(
    @SerializedName("blockId")
    @Expose
    val blockId: Int?,

    @SerializedName("blockName")
    @Expose
    val blockName: String?,

    @SerializedName("districtId")
    @Expose
    val districtId: Int?,

    @SerializedName("districtName")
    @Expose
    val districtName: String?,

    @SerializedName("stateId")
    @Expose
    val stateId: Int?,

    @SerializedName("stateName")
    @Expose
    val stateName: String?,
)

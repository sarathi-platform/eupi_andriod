package com.sarathi.dataloadingmangement.network.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING

data class FederationDetail(
    @SerializedName("blockId")
    @Expose
    val blockId: Int? = 0,

    @SerializedName("blockName")
    @Expose
    val blockName: String? = BLANK_STRING,

    @SerializedName("districtId")
    @Expose
    val districtId: Int? = 0,

    @SerializedName("districtName")
    @Expose
    val districtName: String? = BLANK_STRING,

    @SerializedName("stateId")
    @Expose
    val stateId: Int? = 0,

    @SerializedName("stateName")
    @Expose
    val stateName: String? = BLANK_STRING,

    )
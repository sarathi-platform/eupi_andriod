package com.nudge.core.model.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VillageDetailsFromLokOsResponseModel(
    @SerializedName("@odata.context")
    @Expose
    val odataContext: String,
    @SerializedName("@odata.count")
    @Expose
    val odataCount: Int,
    @SerializedName("value")
    @Expose
    val value: List<VillageDetailsFromLokOs>
)


data class VillageDetailsFromLokOs(
    @SerializedName("block_name")
    @Expose
    val blockName: String,
    @SerializedName("cbo_code")
    @Expose
    val cboCode: String,
    @SerializedName("cbo_name")
    @Expose
    val cboName: String,
    @SerializedName("district_name")
    @Expose
    val districtName: String,
    @SerializedName("guid")
    @Expose
    val guid: String,
    @SerializedName("state_name")
    @Expose
    val stateName: String,
    @SerializedName("user_id")
    @Expose
    val userId: String,
    @SerializedName("village_name")
    @Expose
    val villageName: String
)
package com.nudge.core.model.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LokOsStateCodeResponseModel(
    @SerializedName("category")
    @Expose
    val category: String,
    @SerializedName("kyc_flag")
    @Expose
    val kycFlag: String,
    @SerializedName("lgd_code")
    @Expose
    val lgdCode: String,
    @SerializedName("state_code")
    @Expose
    val stateCode: String,
    @SerializedName("state_id")
    @Expose
    val stateId: Int,
    @SerializedName("state_name_en")
    @Expose
    val stateNameEn: String,
    @SerializedName("state_name_hi")
    @Expose
    val stateNameHi: String,
    @SerializedName("state_name_local")
    @Expose
    val stateNameLocal: String,
    @SerializedName("state_short_name_en")
    @Expose
    val stateShortNameEn: String
)
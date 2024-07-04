package com.sarathi.dataloadingmangement.model.mat.response


import com.google.gson.annotations.SerializedName

data class GrantConfigResponse(
    @SerializedName("grantComponent")
    val grantComponent: String,
    @SerializedName("grantId")
    val grantId: Int,
    @SerializedName("mode")
    val grantMode: String,
    @SerializedName("grantName")
    val grantName: String,
    @SerializedName("nature")
    val grantNature: String,
    @SerializedName("grantType")
    val grantType: String
)